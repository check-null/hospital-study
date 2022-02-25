package com.sub.order.controller;

import com.sub.common.result.Result;
import com.sub.order.component.AlipayComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/order/alipay")
public class AlipayController {

    @Resource
    AlipayComponent alipayComponent;

    @GetMapping("/pay")
    public Result<Object> pay() {
        String pay = alipayComponent.pay();

        return Result.ok(pay);
    }
}
