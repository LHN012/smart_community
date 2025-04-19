package com.example.smart_community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许特定域名，不能使用通配符*
        config.addAllowedOrigin("http://localhost:5173");
        // 允许携带认证信息（cookies等）
        config.setAllowCredentials(true);
        // 允许的请求头
        config.addAllowedHeader("*");
        // 允许的请求方法
        config.addAllowedMethod("*");

        // 允许暴露的响应头
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}