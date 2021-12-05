package com.sub.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sub.model.user.UserInfo;
import com.sub.vo.user.LoginVo;
import me.zhyd.oauth.model.AuthUser;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> loginUser(LoginVo vo);

    String saveQqAuth(AuthUser data);

    UserInfo getByOpenId(String openId);

    UserInfo getByUnionId(String unionId);

    UserInfo getByPhone(String phone);
}
