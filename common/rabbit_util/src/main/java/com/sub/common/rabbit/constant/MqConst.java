package com.sub.common.rabbit.constant;

public class MqConst {
    /**
     * 预约下单
     */
    public static final String EXCHANGE_DIRECT_ORDER = "exchange.direct.order";
    public static final String ROUTING_ORDER = "order";
    /**
     * 队列
     */
    public static final String QUEUE_ORDER = "queue.order";
    /**
     * 短信
     */
    public static final String EXCHANGE_DIRECT_MSM = "exchange.direct.msm";
    public static final String ROUTING_MSM_ITEM = "msm.item";
    /**
     * 队列
     */
    public static final String QUEUE_MSM_ITEM = "queue.msm.item";
    /**
     * 延迟队列
     */
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ROUTING_DELAY_ORDER = "order.delay.item";
    public static final String EXCHANGE_DIRECT_DELAY = "exchange.direct.delay";


    public static final String DELAYED_QUEUE_NAME = "delayed.queue";
    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";
}
