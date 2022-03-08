package com.sub.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sub.common.exception.YyghException;
import com.sub.common.result.ResultCodeEnum;
import com.sub.enums.OrderStatusEnum;
import com.sub.enums.PaymentTypeEnum;
import com.sub.enums.RefundStatusEnum;
import com.sub.model.order.OrderInfo;
import com.sub.model.order.PaymentInfo;
import com.sub.model.order.RefundInfo;
import com.sub.order.component.AlipayComponent;
import com.sub.order.service.AlipayService;
import com.sub.order.service.OrderService;
import com.sub.order.service.PaymentService;
import com.sub.order.service.RefundInfoService;
import org.joda.time.DateTime;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlipayServiceImpl implements AlipayService {

    @Resource
    OrderService orderService;

    @Resource
    PaymentService paymentService;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    AlipayComponent alipayComponent;

    @Resource
    RefundInfoService refundInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createNative(Long orderId) {
        String payMap = (String) redisTemplate.opsForValue().get(orderId.toString());
        if (null != payMap) {
            return payMap;
        }
        //根据id获取订单信息
        OrderInfo order = orderService.getById(orderId);
        // 保存交易记录
        paymentService.savePaymentInfo(order, PaymentTypeEnum.ALI_PAY.getStatus());
        order.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(order);
        // 生成交易form
        // todo 先判断结果再保存记录
        return alipayComponent.pay(order);
    }

    @Override
    public String queryPayStatus(Long orderId, String paymentType) {
        OrderInfo orderInfo = orderService.getById(orderId);
        return alipayComponent.query(orderInfo);
    }

    @Override
    public Boolean close(Long orderId) {
        OrderInfo orderInfo = orderService.getById(orderId);
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if (quitTime.isBeforeNow()) {
            throw new YyghException(ResultCodeEnum.CANCEL_ORDER_NO);
        }

        orderInfo.setOrderStatus(-1);
        alipayComponent.close(orderInfo);
        return orderService.updateById(orderInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> refund(Long orderId) {
        // 查支付记录
        PaymentInfo paymentInfoQuery = paymentService.getPaymentInfo(orderId, PaymentTypeEnum.ALI_PAY.getStatus());
        // 判断是否退款成功
        RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfoQuery);
        HashMap<String, Object> map = new HashMap<>(8);
        map.put("code", 10000);
        map.put("msg", "Success");
        if (refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()) {

            return map;
        }
        // 支付宝退款
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setAmount(paymentInfoQuery.getTotalAmount());
        orderInfo.setOutTradeNo(paymentInfoQuery.getOutTradeNo());
        String refund = alipayComponent.refund(orderInfo);

        JSONObject alipayJson = JSONObject.parseObject(refund);
        // 判断退款状态
        Integer code = alipayJson.getInteger("code");
        if (code.equals(10000)) {
            refundInfo.setCallbackTime(new Date());
            refundInfo.setTradeNo(alipayJson.getString("trade_no"));
            refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
            refundInfo.setCallbackContent(refund);
            refundInfoService.updateById(refundInfo);
            return map;
        }

        HashMap<String, Object> hashMap = new HashMap<>(8);
        map.put("code", code);
        map.put("msg", alipayJson.getString("msg"));
        return hashMap;
    }


}
