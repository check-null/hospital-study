package com.sub.user.controller;

import com.sub.common.result.Result;
import com.sub.user.service.UserInfoService;
import com.sub.vo.user.LoginVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Europa
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Resource
    UserInfoService userInfoService;

    @PostMapping("login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginVo vo) {
        Map<String, Object> map = userInfoService.loginUser(vo);
        return Result.ok(map);
    }
}
