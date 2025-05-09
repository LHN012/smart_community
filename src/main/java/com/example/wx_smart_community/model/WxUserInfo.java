package com.example.wx_smart_community.model;

import lombok.Data;

@Data
public class WxUserInfo {
    private String openid;
    private String nickname;
    private String avatarUrl;
    private String gender;
    private String country;
    private String province;
    private String city;
    private String language;
    private Long userId;
    private String realName;
    private String phone;
    private String email;
} 