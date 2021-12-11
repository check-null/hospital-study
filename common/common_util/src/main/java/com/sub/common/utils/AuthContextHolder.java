package com.sub.common.utils;

import com.sub.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

public class AuthContextHolder {

    public static Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        return JwtHelper.getUserId(token);
    }

    public static String getUserName(HttpServletRequest request) {
        String token = request.getHeader("token");
        return JwtHelper.getUserName(token);
    }
}
