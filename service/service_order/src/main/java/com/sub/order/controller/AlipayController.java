package com.sub.order.controller;

import com.sub.common.result.Result;
import com.sub.enums.PaymentTypeEnum;
import com.sub.order.component.AlipayComponent;
import com.sub.order.service.AlipayService;
import com.sub.order.service.PaymentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/order/alipay")
public class AlipayController {

    @Resource
    AlipayService alipayService;

    @Resource
    private PaymentService paymentService;

    @GetMapping("/createNative/{orderId}")
    public Result<Object> pay(@PathVariable Long orderId) {
        String pay = alipayService.createNative(orderId);
        return Result.ok(pay);
    }

    @GetMapping("/notify")
    public Result<Object> alipayNotify() {

        return Result.ok();
    }

    @ApiOperation(value = "查询支付状态")
    @GetMapping("/queryPayStatus/{orderId}")
    public Result<Object> queryPayStatus(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        //调用查询接口
        Map<String, String> resultMap = alipayService.queryPayStatus(orderId, PaymentTypeEnum.ALI_PAY.name());

        if (resultMap == null) {
            return Result.fail().message("支付出错");
        }

        if ("SUCCESS".equals(resultMap.get("trade_state"))) {
            //更改订单状态，处理支付结果
            String outTradeNo = resultMap.get("out_trade_no");
            paymentService.paySuccess(outTradeNo, PaymentTypeEnum.ALI_PAY.getStatus(), resultMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }

}
