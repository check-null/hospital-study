package com.sub.oss.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:thirdparty.properties")
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssComponent {

    String endpoint;

    String accessKeyId;

    String accessKeySecret;

    String bucketName;


}
