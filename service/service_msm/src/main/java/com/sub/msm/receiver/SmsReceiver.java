package com.sub.msm.receiver;

import com.rabbitmq.client.Channel;
import com.sub.common.rabbit.constant.MqConst;
import com.sub.msm.component.SmsComponent;
import com.sub.msm.service.MsmService;
import com.sub.vo.msm.MsmVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SmsReceiver {

    @Resource
    SmsComponent smsComponent;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
            key = {MqConst.ROUTING_MSM_ITEM}
    ))
    public void send(MsmVo msmVo, Message message, Channel channel) {
        smsComponent.sendMq(msmVo);
    }

}
