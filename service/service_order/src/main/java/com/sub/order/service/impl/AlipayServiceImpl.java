package com.sub.order.service.impl;

import com.sub.enums.PaymentTypeEnum;
import com.sub.model.order.OrderInfo;
import com.sub.order.service.AlipayService;
import com.sub.order.service.OrderService;
import com.sub.order.service.PaymentService;
import org.joda.time.DateTime;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
        try {
            Map<String, Object> payMap = (Map<String, Object>) redisTemplate.opsForValue().get(orderId.toString());
            if(null != payMap) {
                return payMap;
            }


            //根据id获取订单信息
            OrderInfo order = orderService.getById(orderId);
            // 保存交易记录
            paymentService.savePaymentInfo(order, PaymentTypeEnum.ALI_PAY.getStatus());
            //1、设置参数
            //3、返回第三方的数据
            //4、封装返回结果集
//            Map<String, Object> map = new HashMap<>();
//            map.put("orderId", orderId);
//            map.put("totalFee", order.getAmount());
//            map.put("resultCode", resultMap.get("result_code"));
//            map.put("codeUrl", resultMap.get("code_url"));
//            if(null != resultMap.get("result_code")) {
//                //微信支付二维码2小时过期，可采取2小时未支付取消订单
//                redisTemplate.opsForValue().set(orderId.toString(), map, 1000, TimeUnit.MINUTES);
//            }
//            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
        return null;
    }
}
