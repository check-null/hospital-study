package com.sub.order.service;

import com.sub.model.order.OrderInfo;

import java.util.Map;

public interface AlipayService {

    /**
     * 根据订单号下单，生成支付链接
     * @return
     */
    String createNative(Long orderId);

    /**
     * 根据订单号去微信第三方查询支付状态
     */
    String queryPayStatus(Long orderId, String paymentType);

    String close(OrderInfo orderInfo);

    Map<String, Object> refund(Long orderId);

    boolean payQuery(Map<String, String> map);
}
