package com.sub.common.helper;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

public class JwtHelper {
    //过期时间
    private static final long TOKEN_EXPIRATION = 24 * 60 * 60 * 1000;
    //签名秘钥
    private static final String TOKEN_SIGN_KEY = "123456";

    /**
     * 根据参数生成token
     */
    public static String createToken(Long userId, String userName) {
        return Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, TOKEN_SIGN_KEY)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    /**
     * 根据token字符串得到用户id
     */
    public static Long getUserId(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(TOKEN_SIGN_KEY).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer) claims.get("userId");
        return userId.longValue();
    }

    /**
     * 根据token字符串得到用户名称
     */
    public static String getUserName(String token) {
        if (StringUtils.isEmpty(token)) {
            return "";
        }

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(TOKEN_SIGN_KEY).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("userName");
    }

    public static void main(String[] args) {
        String token = JwtHelper.createToken(1L, "lucy");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUserName(token));
    }
}
