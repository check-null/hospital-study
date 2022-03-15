package com.sub.order.receiver;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.rabbitmq.client.Channel;
import com.sub.enums.AlipayStatusEnum;
import com.sub.model.order.OrderInfo;
import com.sub.model.order.PaymentInfo;
import com.sub.order.component.AlipayComponent;
import com.sub.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class OrderReceiver {

    @Resource
    AlipayComponent alipayComponent;

    @Resource
    OrderService orderService;

    /**
     * 订单超时取消
     *
     * @param order
     * @param message
     * @param channel
     */
    public void orderTimeout(OrderInfo order, Message message, Channel channel) throws AlipayApiException {
        if (order == null) {
            return;
        }
        // 先查询支付状态
        String queryString = alipayComponent.query(order);
        JSONObject jsonQuery = JSONObject.parseObject(queryString);
        JSONObject alipayQuery = jsonQuery.getJSONObject("alipay_trade_query_response");
        Integer i = 10000;
        Integer code = alipayQuery.getInteger("code");
        if (!i.equals(code)) {
            throw new AlipayApiException("订单查询失败: " + alipayQuery.toString());
        }
        // 等待付款阶段
        String tradeStatus = alipayQuery.getString("trade_status");
        if (AlipayStatusEnum.WAIT_BUYER_PAY.getStatus().equals(tradeStatus)) {
            // 关闭订单
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOutTradeNo(order.getOutTradeNo());
            String closeString = alipayComponent.close(paymentInfo);
            JSONObject jsonClose = JSONObject.parseObject(closeString);
            JSONObject alipayClose = jsonClose.getJSONObject("alipay_trade_close_response");
            Integer closeInteger = alipayClose.getInteger("code");
            if (!i.equals(closeInteger)) {
                throw new AlipayApiException("订单关闭失败: " + alipayClose.toString());
            }
            // 关闭订单
            order.setOrderStatus(-1);
            orderService.updateById(order);
        }

    }
}
