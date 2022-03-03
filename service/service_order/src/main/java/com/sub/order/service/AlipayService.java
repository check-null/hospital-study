package com.sub.order.service;

import java.util.Map;

public interface AlipayService {

    /**
     * 根据订单号下单，生成支付链接
     * @return
     */
    String createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId, String name);
}
