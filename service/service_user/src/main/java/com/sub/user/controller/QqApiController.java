package com.sub.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.sub.common.result.Result;
import com.sub.user.component.ConstantPropertiesUtil;
import com.sub.user.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("oauth")
public class QqApiController {

    @Resource
    ConstantPropertiesUtil constantPropertiesUtil;

    @Resource
    UserInfoService userInfoService;

    @ResponseBody
    @GetMapping("render/qq")
    public Result<String> render() {
        AuthRequest authRequest = constantPropertiesUtil.getAuthRequest();
        String state = AuthStateUtils.createState();
        String authorizeUrl = authRequest.authorize(state);
        log.info("重定向url: {}", authorizeUrl);
        return Result.ok(authorizeUrl);
    }

    @GetMapping("callback/qq")
    public String qqAuth(AuthCallback callback, HttpServletResponse resp) {
        log.info("进入callback：qq callback params：" + JSONObject.toJSONString(callback));
        AuthRequest authRequest = constantPropertiesUtil.getAuthRequest();
        AuthResponse<AuthUser> response = authRequest.login(callback);
        log.info(JSONObject.toJSONString(response));
        if (response.ok()) {
            return "redirect:" + userInfoService.saveQqAuth(response.getData());
        }
        return null;
    }

}
