package com.sub.order.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:thirdparty.properties")
public class AlipayComponent {
    /**
     * 商户appid
     */
    @Value("${alipay.app_id}")
    public String appId;
    /**
     * 支付宝公钥
     */
    @Value("${alipay.alipay_public_key}")
    public String alipayPublicKey;
    /**
     * 私钥 pkcs8格式的
     */
    @Value("${alipay.rsa_private_key}")
    public String rsaPrivateKey;
    /**
     * 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     */
    @Value("${alipay.notify_url}")
    public String notifyUrl;
    /**
     * 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
     */
    @Value("${alipay.return_url}")
    public String returnUrl;
    /**
     * 请求网关地址
     */
    @Value("${alipay.url}")
    public String url;
    /**
     * 编码
     */
    public String charset = "UTF-8";
    /**
     * 返回格式
     */
    public String format = "json";
    /**
     * 日志记录目录定义在 logFile 中
     */
    public String logPath = "/log";
    /**
     * RSA2
     */
    public String signtype = "RSA2";
}
