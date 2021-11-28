package com.sub.msm.component;

import com.sub.common.helper.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
public class SmsComponent {

    private String appkey;

    /**
     * 发送短信
     *
     * @param code  {@link String} code:xxxx
     * @param phone {@link String} 手机号
     */
    public void send(String code, String phone) {
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = appkey;
        Map<String, String> headers = new HashMap<>(16);
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<>();
        Map<String, String> bodys = new HashMap<>();
        bodys.put("content", code);
        bodys.put("phone_number", phone);
        bodys.put("template_id", "TPL_0000");


        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
