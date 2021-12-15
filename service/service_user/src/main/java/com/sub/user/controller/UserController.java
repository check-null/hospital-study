package com.sub.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sub.common.result.Result;
import com.sub.model.user.UserInfo;
import com.sub.user.service.UserInfoService;
import com.sub.vo.user.UserInfoQueryVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Resource
    UserInfoService userInfoService;

    @GetMapping("{page}/{limit}")
    public Result<Object> list(@PathVariable Long page,
                               @PathVariable Long limit,
                               UserInfoQueryVo vo) {
        Page<UserInfo> infoPage = new Page<>(page, limit);
        IPage<UserInfo> iPage = userInfoService.selectPage(infoPage, vo);
        return Result.ok(iPage);
    }

    @GetMapping("lock/{userId}/{status}")
    public Result<Object> lock(@PathVariable Long userId, @PathVariable Integer status) {
        boolean lock = userInfoService.lock(userId, status);
        return Result.ok(lock);
    }

    @GetMapping("show/{userId}")
    public Result<Object> show(@PathVariable Long userId) {
        Map<String, Object> map = userInfoService.show(userId);
        return Result.ok(map);
    }

    @GetMapping("approval/{userId}/{authStatus}")
    public Result<Object> approval(@PathVariable Long userId,@PathVariable Integer authStatus) {
        boolean result = userInfoService.approval(userId, authStatus);
        return Result.ok(result);
    }
}
