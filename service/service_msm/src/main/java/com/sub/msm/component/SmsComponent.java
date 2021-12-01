package com.sub.msm.component;

import com.sub.common.helper.HttpUtils;
import lombok.Data;
import okhttp3.*;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
     * @return
     */
    public boolean send(String code, String phone) {
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
        Map<String, String> bodys = new HashMap<>(16);
        bodys.put("content", "code:" + code);
        bodys.put("phone_number", phone);
        bodys.put("template_id", "TPL_0000");


        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendSms(String code, String phone) {
        String url = "https://dfsns.market.alicloudapi.com/data/send_sms";
        String appcode = appkey;
        // 设置请求体
        LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>(16);
        multiValueMap.add("content", "code:" + code);
        multiValueMap.add("phone_number", phone);
        multiValueMap.add("template_id", "TPL_0000");
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "APPCODE " + appcode);
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        RestTemplate restTemplate = new RestTemplate();
        String post = restTemplate.postForObject(url, new HttpEntity<>(multiValueMap, headers), String.class);
        System.out.println("post = " + post);
        return true;
    }

    public boolean sendMsg(String code, String phone) {
        String appcode = appkey;
        OkHttpClient client = new OkHttpClient();
        // request body
        FormBody requestBody = new FormBody.Builder()
                .add("content", "code: " + code)
                .add("phone_number", phone)
                .add("template_id", "TPL_0000")
                .build();

        Request request = new Request.Builder()
                .url("https://dfsns.market.alicloudapi.com/data/send_sms")
                .addHeader("Authorization", "APPCODE " + appcode)
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()){
            if (response.body() != null) {
                System.out.println(response.body().string());
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }
}
