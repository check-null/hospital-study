package com.sub.msm.controller;

import com.sub.common.result.Result;
import com.sub.msm.component.SmsComponent;
import com.sub.msm.service.MsmService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/msm")
public class MsmApiController {

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Resource
    SmsComponent smsComponent;

    @Resource
    MsmService msmService;

    @GetMapping("send/{phone}")
    public Result<String> sendCode(@PathVariable String phone) {
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        double random = Math.random();
        code = "code:"+String.valueOf(random).substring(2, 8);
//        smsComponent.send(code, phone);
        redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);

        return Result.ok();
    }
}
