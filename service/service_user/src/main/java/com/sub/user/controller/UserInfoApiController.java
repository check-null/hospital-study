package com.sub.user.controller;

import com.sub.common.result.Result;
import com.sub.common.utils.AuthContextHolder;
import com.sub.model.user.UserInfo;
import com.sub.user.service.UserInfoService;
import com.sub.vo.user.LoginVo;
import com.sub.vo.user.UserAuthVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("auth/userAuth")
    public Result<Object> userAuth(@RequestBody UserAuthVo vo, HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        userInfoService.userAuth(userId, vo);
        return Result.ok();
    }

    @GetMapping("auth/getUserInfo")
    public Result<Object> getUserInfo(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }
}
