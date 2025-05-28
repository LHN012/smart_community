package com.example.wx_smart_community.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WxUserInfo {
    private String openid;
    private String nickname;
    private Integer userId;
    private String realName;
    private String phone;
    private String email;
    private Integer role;
    private String address;
    private String username;
    private LocalDateTime createdAt;
} 