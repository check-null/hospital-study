package com.sub.user.component;

import lombok.Data;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@PropertySource("classpath:thirdparty.properties")
public class ConstantPropertiesUtil {

    @Value("${qq.appid}")
    private String appId;
    @Value("${qq.appkey}")
    private String appSecret;
    @Value("${qq.callback}")
    private String redirectUrl;
    @Value("${yygh.baseUrl}")
    private String yyghBaseUrl;

    public AuthRequest getAuthRequest() {
        List<String> scopes = new ArrayList<>(10);
        scopes.add("get_user_info");
        scopes.add("getUnionId");

        final AuthConfig build = AuthConfig.builder()
                .clientId(appId)
                .clientSecret(appSecret)
                .redirectUri(redirectUrl)
                .unionId(true)
                .scopes(scopes)
                .build();
        return new AuthQqRequest(build);
    }

}
