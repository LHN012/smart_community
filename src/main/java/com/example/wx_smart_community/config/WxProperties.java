package com.example.wx_smart_community.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx")
public class WxProperties {
    private String appid;
    private String secret;
} 