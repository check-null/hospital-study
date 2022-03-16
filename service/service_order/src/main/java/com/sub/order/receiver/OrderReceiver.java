package com.sub.order.receiver;

import com.rabbitmq.client.Channel;
import com.sub.common.rabbit.constant.MqConst;
import com.sub.model.order.OrderInfo;
import com.sub.order.service.OrderService;
import org.joda.time.DateTime;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class OrderReceiver {

    @Resource
    OrderService orderService;

    /**
     * 订单超时取消
     *
     * @param order
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.ORDER_DELAY_QUEUE, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_DELAY),
            key = {MqConst.ROUTING_DELAY_ORDER}
    ))
    public void orderTimeout(OrderInfo order, Message message, Channel channel) {
        String now = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
        System.out.println(now + " 消费队列: " + order);
        // 预约后xx分钟后未支付直接关闭订单
        if (order != null) {
            // 关闭订单
            order.setOrderStatus(-1);
            order.setUpdateTime(new Date());
            orderService.updateById(order);
        }

    }
}
