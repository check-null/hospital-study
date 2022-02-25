package com.sub.order.service;

import java.util.Map;

public interface AlipayService {

    /**
     * 根据订单号下单，生成支付链接
     */
    Map<String, Object> createNative(Long orderId);

}
