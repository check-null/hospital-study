package com.sub.order.api;

import com.sub.common.result.Result;
import com.sub.model.order.OrderInfo;
import com.sub.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("auth/getOrders/{orderId}")
    public Result<Object> getOrders(@PathVariable String orderId) {
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }

}
