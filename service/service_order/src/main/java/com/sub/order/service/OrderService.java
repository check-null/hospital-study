package com.sub.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sub.model.order.OrderInfo;

public interface OrderService extends IService<OrderInfo> {
    Long saveOrder(String scheduleId, Long patientId);
}
