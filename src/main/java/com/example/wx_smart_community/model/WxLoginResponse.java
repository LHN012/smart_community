package com.example.wx_smart_community.model;

import lombok.Data;

@Data
public class WxLoginResponse {
    private String token;
    private WxUserInfo user;
    
    public WxLoginResponse(String token, WxUserInfo user) {
        this.token = token;
        this.user = user;
    }
} 