package com.sub.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


/**
 * @author Europa
 */
@Configuration
public class CorsConfig {

    /**
     * 跨域配置
     *
     * @return {@link CorsWebFilter} gateway的跨域配置不能使用CorsFilter,要用这个
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        // p100
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //配置跨域 请求头
        corsConfiguration.addAllowedHeader("*");
        //请求方式
        corsConfiguration.addAllowedMethod("*");
        //请求来源
        corsConfiguration.addAllowedOrigin("*");
        //cookie
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }
}
