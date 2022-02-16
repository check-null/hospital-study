package com.sub.order.api;

import com.sub.common.result.Result;
import com.sub.order.service.OrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Europa
 */
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {

    @Resource
    OrderService orderService;

    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result<Object> saveOrders(@PathVariable String scheduleId, @PathVariable Long patientId) {
        Long orderId = orderService.saveOrder(scheduleId, patientId);
        return Result.ok(orderId);
    }

}
