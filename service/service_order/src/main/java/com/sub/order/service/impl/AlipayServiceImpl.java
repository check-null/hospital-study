package com.sub.order.service.impl;

import com.sub.enums.PaymentTypeEnum;
import com.sub.model.order.OrderInfo;
import com.sub.order.component.AlipayComponent;
import com.sub.order.service.AlipayService;
import com.sub.order.service.OrderService;
import com.sub.order.service.PaymentService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Override
    public String createNative(Long orderId) {
        String payMap = (String) redisTemplate.opsForValue().get(orderId.toString());
        if (null != payMap) {
            return payMap;
        }
        //根据id获取订单信息
        OrderInfo order = orderService.getById(orderId);
        // 保存交易记录
        paymentService.savePaymentInfo(order, PaymentTypeEnum.ALI_PAY.getStatus());
        // 生成交易form
        return alipayComponent.pay(order);
    }

    @Override
    public Map<String, String> queryPayStatus(Long orderId, String name) {
        return null;
    }
}
