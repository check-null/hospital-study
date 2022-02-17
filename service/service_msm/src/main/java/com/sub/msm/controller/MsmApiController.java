package com.sub.msm.controller;

import com.sub.common.result.Result;
import com.sub.msm.component.SmsComponent;
import com.sub.msm.service.MsmService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("send/{phone}")
    public Result<String> sendCode(@PathVariable String phone) {
        String code = redisTemplate.opsForValue().get(phone);
        Long expire = redisTemplate.opsForValue().getOperations().getExpire(phone);
        // 1分钟内不能再次发送
        if (!StringUtils.isEmpty(code) && expire != null && expire > 240) {
            Result.fail("请勿频繁发送短信");
        }

        if (!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        double random = Math.random();
        code = String.valueOf(random).substring(2, 8);
        boolean isSend = smsComponent.sendMsg(code, phone);
        if (isSend) {
            redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);
        } else {
            Result.fail("服务器错误,请联系客服");
        }

        return Result.ok();
    }

    @GetMapping("send-sms/{phone}")
    public Result<String> sendSms(@PathVariable String phone) {
        String code = redisTemplate.opsForValue().get(phone);
        Long expire = redisTemplate.opsForValue().getOperations().getExpire(phone);
        // 1分钟内不能再次发送
        if (!StringUtils.isEmpty(code) && expire != null && expire > 240) {
            Result.fail("请勿频繁发送短信");
        }

        if (!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        double random = Math.random();
        code = String.valueOf(random).substring(2, 8);
        boolean isSend = smsComponent.sendSms(code, phone);
        if (isSend) {
            redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);
        } else {
            Result.fail("服务器错误,请联系客服");
        }

        return Result.ok();
    }

    @GetMapping("send_sms/{phone}")
    public Result<String> sendMsg(@PathVariable String phone) {
        String code = redisTemplate.opsForValue().get(phone);
        Long expire = redisTemplate.opsForValue().getOperations().getExpire(phone);
        // 1分钟内不能再次发送
        if (!StringUtils.isEmpty(code) && expire != null && expire > 240) {
            Result.fail("请勿频繁发送短信");
        }

        if (!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        double random = Math.random();
        code = String.valueOf(random).substring(2, 8);
        boolean isSend = smsComponent.sendMsg(code, phone);
        if (isSend) {
            redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);
        } else {
            Result.fail("服务器错误,请联系客服");
        }

        return Result.ok();
    }


}
