package com.sub.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.sub.common.result.Result;
import com.sub.enums.AlipayStatusEnum;
import com.sub.enums.PaymentTypeEnum;
import com.sub.order.service.AlipayService;
import com.sub.order.service.PaymentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

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
    public Result<Object> alipayNotify(HttpServletRequest request) {
        request.getParameterMap().forEach((k,v) -> System.out.println("key: " + k + " value: " + Arrays.toString(v)));
        return Result.ok();
    }

    @ApiOperation(value = "查询支付状态")
    @GetMapping("/queryPayStatus/{orderId}")
    public Result<Object> queryPayStatus(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        //调用查询接口
        String resultMap = alipayService.queryPayStatus(orderId, PaymentTypeEnum.ALI_PAY.name());
        JSONObject queryResponse = JSONObject.parseObject(resultMap).getJSONObject("alipay_trade_query_response");

        Integer integer = 10000;
        if (!integer.equals(queryResponse.getInteger("code"))) {
            return Result.fail().message(queryResponse.getString("sub_msg"));
        }

        String tradeStatus = queryResponse.getString("trade_status");
        if (AlipayStatusEnum.TRADE_SUCCESS.getStatus().equals(tradeStatus)) {
            String outTradeNo = queryResponse.getString("out_trade_no");
            String tradeNo = queryResponse.getString("trade_no");

            paymentService.paySuccess(outTradeNo, PaymentTypeEnum.ALI_PAY.getStatus(), tradeNo);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }

    @GetMapping("/close/{orderId}")
    public Result<Object> close(@PathVariable Long orderId) {
        return Result.ok();
    }
}
