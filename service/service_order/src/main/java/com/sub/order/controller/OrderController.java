package com.sub.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sub.common.result.Result;
import com.sub.enums.OrderStatusEnum;
import com.sub.model.order.OrderInfo;
import com.sub.order.service.OrderService;
import com.sub.vo.order.OrderQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "订单接口")
@RestController
@RequestMapping("/admin/order/orderInfo")
public class OrderController {

    @Resource
    private OrderService orderService;

    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result<Object> index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "orderCountQueryVo", value = "查询对象") OrderQueryVo orderQueryVo) {
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("getStatusList")
    public Result<Object> getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    @ApiOperation(value = "获取订单")
    @GetMapping("show/{id}")
    public Result<Object> get(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable Long id) {
        Map<String, Object> show = orderService.show(id);
        return Result.ok(show);
    }

}
