package com.sub.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sub.common.exception.YyghException;
import com.sub.common.helper.JwtHelper;
import com.sub.common.result.ResultCodeEnum;
import com.sub.enums.AuthStatusEnum;
import com.sub.model.user.Patient;
import com.sub.model.user.UserInfo;
import com.sub.user.component.ConstantPropertiesUtil;
import com.sub.user.mapper.UserInfoMapper;
import com.sub.user.service.PatientService;
import com.sub.user.service.UserInfoService;
import com.sub.vo.user.LoginVo;
import com.sub.vo.user.UserAuthVo;
import com.sub.vo.user.UserInfoQueryVo;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Resource
    ConstantPropertiesUtil constantPropertiesUtil;

    @Resource
    PatientService patientService;

    @Override
    public Map<String, Object> loginUser(LoginVo vo) {

        String code = redisTemplate.opsForValue().get(vo.getPhone());
        if (!vo.getCode().equals(code)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        UserInfo userInfo = getByPhone(vo.getPhone());
        // 先查该手机是否注册过
        if (userInfo == null) {
            // 再查一下是否绑定过qq
            userInfo = getByUnionId(vo.getUnionId());
            // 如果注册过,且绑定过qq,手机号还是空的就更新手机号
            if (userInfo != null && StringUtils.isEmpty(userInfo.getPhone())) {
                userInfo.setPhone(vo.getPhone());
                baseMapper.updateById(userInfo);
            } else {
                // 手机号没注册过,qq也没绑定就新增用户
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(vo.getPhone());
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }

        // 判断该用户是否被禁用
        if (userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }

        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }

        String token = JwtHelper.createToken(userInfo.getId(), name);

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("name", name);
        map.put("token", token);

        return map;
    }

    @Override
    public String saveQqAuth(AuthUser data) {
        AuthUser authUser = JSONObject.parseObject(JSONObject.toJSONString(data), AuthUser.class);
        // 判断是否有该用户
        UserInfo userInfo = getByUnionId(authUser.getToken().getUnionId());
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setHeadImg(authUser.getAvatar());
            userInfo.setOpenid(authUser.getToken().getOpenId());
            userInfo.setNickName(authUser.getNickname());
            userInfo.setUnionId(data.getToken().getUnionId());

            baseMapper.insert(userInfo);
        }

        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }

        String unionId;
        if (StringUtils.isEmpty(userInfo.getPhone())) {
            unionId = data.getToken().getUnionId();
        } else {
            unionId = "";
        }
        String token = JwtHelper.createToken(userInfo.getId(), userInfo.getName());
        try {
            return constantPropertiesUtil.getYyghBaseUrl() + "/qq/callback?token=" + token + "&unionId=" + unionId + "&name=" + URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UserInfo getByOpenId(String openId) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openId", openId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public UserInfo getByUnionId(String unionId) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("UnionId", unionId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public UserInfo getByPhone(String phone) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void userAuth(Long userId, UserAuthVo vo) {
        UserInfo userInfo = baseMapper.selectById(userId);
        if (userInfo == null) {
            return;
        }
        // 设置真实姓名
        userInfo.setName(vo.getName());
        // 证件信息
        userInfo.setCertificatesType(vo.getCertificatesType());
        userInfo.setCertificatesNo(vo.getCertificatesNo());
        userInfo.setCertificatesUrl(vo.getCertificatesUrl());
        // 认证中
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        // 更新
        baseMapper.updateById(userInfo);
    }

    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> infoPage, UserInfoQueryVo vo) {
        String name = vo.getKeyword();
        Integer status = vo.getStatus();
        Integer authStatus = vo.getAuthStatus();
        String createTimeBegin = vo.getCreateTimeBegin();
        String createTimeEnd = vo.getCreateTimeEnd();

        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if (name != null) {
            wrapper.like("name", name);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (authStatus != null) {
            wrapper.eq("auth_status", authStatus);
        }
        if (createTimeBegin != null) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (createTimeEnd != null) {
            wrapper.le("create_time", createTimeEnd);
        }
        Page<UserInfo> pages = baseMapper.selectPage(infoPage, wrapper);
        pages.getRecords().forEach(this::packUserInfo);
        return pages;
    }

    @Override
    public boolean lock(Long userId, Integer status) {
        // p147
        if (status == 0 || status == 1) {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(userId);
            userInfo.setStatus(status);
            userInfo.setUpdateTime(new Date());
            return baseMapper.updateById(userInfo) > 0;
        }
        return false;
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String, Object> map = new HashMap<>(16);
        UserInfo userInfo = packUserInfo(baseMapper.selectById(userId));
        List<Patient> list = patientService.findAllUserId(userId);

        map.put("userInfo", userInfo);
        map.put("patientList", list);
        return map;
    }

    @Override
    public boolean approval(Long userId, Integer authStatus) {
        // 2通过 -1不通过
        if (authStatus == 2 || authStatus == -1) {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(userId);
            userInfo.setAuthStatus(authStatus);
            return baseMapper.updateById(userInfo) > 0;
        }
        return false;
    }

    private UserInfo packUserInfo(UserInfo item) {
        item.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(item.getAuthStatus()));
        String statusString = item.getStatus() == 0 ? "锁定" : "正常";
        item.getParam().put("statusString", statusString);
        return item;
    }


}
