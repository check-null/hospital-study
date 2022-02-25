package com.sub.order.component;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:thirdparty.properties")
public class AlipayComponent {
    /**
     * 商户appid
     */
    @Value("${alipay.app_id}")
    private String appId;
    /**
     * 支付宝公钥
     */
    @Value("${alipay.alipay_public_key}")
    private String alipayPublicKey;
    /**
     * 私钥 pkcs8格式的
     */
    @Value("${alipay.rsa_private_key}")
    private String rsaPrivateKey;
    /**
     * 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     */
    @Value("${alipay.notify_url}")
    private String notifyUrl;
    /**
     * 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
     */
    @Value("${alipay.return_url}")
    private String returnUrl;
    /**
     * 请求网关地址
     */
    @Value("${alipay.url}")
    private String url;
    /**
     * 编码
     */
    private String charset = "UTF-8";
    /**
     * 返回格式
     */
    private String format = "json";
    /**
     * 日志记录目录定义在 logFile 中
     */
    private String logPath = "/log";
    /**
     * RSA2
     */
    private String signType = "RSA2";

    public String pay() {
        AlipayClient alipayClient = new DefaultAlipayClient(url, appId, rsaPrivateKey, format, charset, alipayPublicKey, signType);
        //创建API对应的request
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl("");
        alipayRequest.setNotifyUrl("");
        //填充业务参数
        JSONObject bizContent = new JSONObject();
        // 订单号
        bizContent.put("out_trade_no", "20210817010101004");
        // 金额
        bizContent.put("total_amount", 0.01);
        // 订单标题
        bizContent.put("subject", "测试商品");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        alipayRequest.setBizContent(bizContent.toString());
        String data = null;
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);
            if (response.isSuccess()) {
                System.out.println("成功");
                data = response.getBody();
            } else {
                System.out.println("失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return data;
    }
}
