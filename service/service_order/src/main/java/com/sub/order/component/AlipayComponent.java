package com.sub.order.component;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.sub.model.order.OrderInfo;
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

    private AlipayConfig getConfig() {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(url);
        alipayConfig.setAppId(appId);
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setPrivateKey(rsaPrivateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setCharset("utf-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }

    /**
     * 生成支付信息代码
     *
     * @return 支付信息代码
     */
    public String pay(OrderInfo order) {

        String data = null;

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(getConfig());
            //创建API对应的request
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl("");
            alipayRequest.setNotifyUrl("http://admin.shop.com/api/order/alipay/notify");
            //填充业务参数
            JSONObject bizContent = new JSONObject();
            // 订单号
            bizContent.put("out_trade_no", order.getOutTradeNo());
            // 金额
            bizContent.put("total_amount", order.getAmount().toString());
            // 订单标题
            String subject = order.getReserveDate() + "就诊" + order.getDepname();
            bizContent.put("subject", subject);
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
            bizContent.put("qr_pay_mode", "4");
            bizContent.put("qrcode_width", 200);
            alipayRequest.setBizContent(bizContent.toString());

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


    public String query(OrderInfo order) {
        String query = null;
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(getConfig());
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", order.getOutTradeNo());
            request.setBizContent(bizContent.toString());
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            query = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return query;
    }

    public String close(OrderInfo info) {
        String query = null;
        try {
            AlipayClient client = new DefaultAlipayClient(getConfig());
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("trade_no", info.getOutTradeNo());
            request.setBizContent(bizContent.toString());
            AlipayTradeCloseResponse response = client.execute(request);
            query = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return query;
    }

    public boolean refund(Long orderId) {
        return false;
    }
}
