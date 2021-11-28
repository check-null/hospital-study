package com.sub.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Europa
 */
@MapperScan("com.sub.user.mapper")
@ComponentScan(basePackages = "com.sub")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.sub")
@SpringBootApplication
public class ServiceUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}
