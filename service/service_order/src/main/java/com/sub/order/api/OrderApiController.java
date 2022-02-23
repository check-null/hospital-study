package com.sub.order.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sub.common.result.Result;
import com.sub.common.utils.AuthContextHolder;
import com.sub.enums.OrderStatusEnum;
import com.sub.model.order.OrderInfo;
import com.sub.order.service.OrderService;
import com.sub.vo.order.OrderQueryVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    @GetMapping("auth/{page}/{limit}")
    public Result<Object> list(@PathVariable Long page,
                               @PathVariable Long limit,
                               OrderQueryVo orderQueryVo,
                               HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        orderQueryVo.setUserId(userId);
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> iPage = orderService.selectPage(pageParam, orderQueryVo);
        return Result.ok(iPage);
    }

    @GetMapping("auth/getStatusList")
    public Result<Object> getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }
}
