package com.sub.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sub.model.order.OrderInfo;
import com.sub.model.user.UserInfo;
import com.sub.vo.order.OrderQueryVo;

import java.util.Map;

public interface OrderService extends IService<OrderInfo> {
    Long saveOrder(String scheduleId, Long patientId);

    OrderInfo getOrder(String orderId);

    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    Map<String,Object> show(Long orderId);
}
