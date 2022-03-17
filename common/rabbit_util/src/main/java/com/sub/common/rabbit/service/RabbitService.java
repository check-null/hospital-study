package com.sub.common.rabbit.service;

import com.sub.common.rabbit.constant.MqConst;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Europa
 */
@Service("RabbitService")
public class RabbitService {

    @Resource
    RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param exchange   交换机
     * @param routingKey 路由键
     * @param message    消息
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }

    public void delayCancelOrder(Object obj, Integer ttl) {
        rabbitTemplate.convertAndSend(MqConst.DELAYED_EXCHANGE_NAME, MqConst.DELAYED_ROUTING_KEY, obj, message -> {
            message.getMessageProperties().setDelay(ttl);
            return message;
        });
    }

}
