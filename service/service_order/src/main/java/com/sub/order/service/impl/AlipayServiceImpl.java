package com.sub.order.service.impl;

import com.sub.order.service.AlipayService;
import com.sub.order.service.OrderService;
import com.sub.order.service.PaymentService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class AlipayServiceImpl implements AlipayService {

    @Resource
    OrderService orderService;

    @Resource
    PaymentService paymentService;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public Map<String, Object> createNative(Long orderId) {

        return null;
    }
}
