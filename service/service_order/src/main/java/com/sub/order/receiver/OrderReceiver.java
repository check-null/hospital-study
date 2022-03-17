package com.sub.order.receiver;

import com.rabbitmq.client.Channel;
import com.sub.common.rabbit.constant.MqConst;
import com.sub.enums.PaymentStatusEnum;
import com.sub.enums.PaymentTypeEnum;
import com.sub.model.order.OrderInfo;
import com.sub.model.order.PaymentInfo;
import com.sub.order.service.OrderService;
import com.sub.order.service.PaymentService;
import org.joda.time.DateTime;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class OrderReceiver {

    @Resource
    OrderService orderService;

    @Resource
    PaymentService paymentService;

    @RabbitListener(queues = MqConst.DELAYED_QUEUE_NAME)
    public void receiveDelayedQueue(OrderInfo order, Message message, Channel channel) {
        String now = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
        System.out.println(now + " 消费队列: " + order);
        if (order != null) {
            PaymentInfo paymentInfo = paymentService.getPaymentInfo(order.getId(), PaymentTypeEnum.ALI_PAY.getStatus());
            // 没有支付信息或者支付状态不是已支付就关闭订单
            if (paymentInfo == null || !PaymentStatusEnum.PAID.getStatus().equals(paymentInfo.getPaymentStatus())) {
                // 关闭订单
                order.setOrderStatus(-1);
                order.setUpdateTime(new Date());
                orderService.updateById(order);
            }
        }
    }
}
