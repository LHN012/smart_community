package com.example.wx_smart_community.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxLoginResponse {
    private String token;
    private WxUserInfo user;
} 