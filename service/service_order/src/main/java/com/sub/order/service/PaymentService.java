package com.sub.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sub.model.order.OrderInfo;
import com.sub.model.order.PaymentInfo;

public interface PaymentService extends IService<PaymentInfo> {

    /**
     * 保存交易记录
     * @param order
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    void savePaymentInfo(OrderInfo order, Integer paymentType);

    void paySuccess(String outTradeNo, Integer status, String tradeNo);

    /**
     * 获取支付记录
     * @param orderId
     * @param paymentType
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);

    Boolean refund(Long orderId);
}
