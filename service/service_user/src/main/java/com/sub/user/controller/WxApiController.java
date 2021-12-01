package com.sub.user.controller;

import com.sub.common.result.Result;
import com.sub.user.component.ConstantPropertiesUtil;
import com.sub.user.service.UserInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/ucenter/wx")
public class WxApiController {

    @Resource
    UserInfoService userInfoService;

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @ResponseBody
    @GetMapping("getLoginParam")
    public Result<Map<String, Object>> getQrContent(HttpSession httpSession) throws UnsupportedEncodingException {
        String redirectUrl = URLEncoder.encode(ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL, "utf-8");
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("redirectUrl", redirectUrl);
        map.put("scope", "snsapi_login");
        map.put("state", String.valueOf(System.currentTimeMillis()));

        return Result.ok(map);
    }
}
