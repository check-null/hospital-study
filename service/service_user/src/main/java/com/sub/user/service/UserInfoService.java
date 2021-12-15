package com.sub.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sub.model.user.UserInfo;
import com.sub.vo.user.LoginVo;
import com.sub.vo.user.UserAuthVo;
import com.sub.vo.user.UserInfoQueryVo;
import me.zhyd.oauth.model.AuthUser;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> loginUser(LoginVo vo);

    String saveQqAuth(AuthUser data);

    UserInfo getByOpenId(String openId);

    UserInfo getByUnionId(String unionId);

    UserInfo getByPhone(String phone);

    void userAuth(Long userId, UserAuthVo vo);

    IPage<UserInfo> selectPage(Page<UserInfo> infoPage, UserInfoQueryVo vo);

    boolean lock(Long userId, Integer status);

    Map<String, Object> show(Long userId);

    boolean approval(Long userId, Integer authStatus);
}
