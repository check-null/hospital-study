package com.sub.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sub.common.exception.YyghException;
import com.sub.common.helper.JwtHelper;
import com.sub.common.result.ResultCodeEnum;
import com.sub.model.user.UserInfo;
import com.sub.user.mapper.UserInfoMapper;
import com.sub.user.service.UserInfoService;
import com.sub.vo.user.LoginVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Override
    public Map<String, Object> loginUser(LoginVo vo) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", vo.getPhone());
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        if (userInfo == null) {
            UserInfo user = new UserInfo();
            user.setName("");
            user.setPhone(vo.getPhone());
            user.setStatus(1);
            baseMapper.insert(user);
            userInfo = user;
        }

        if (userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }

        String token = JwtHelper.createToken(userInfo.getId(), name);

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("name", name);
        map.put("token", token);

        return map;
    }

}
