package com.example.wx_smart_community.service;

import com.example.wx_smart_community.model.WxLoginResponse;
import com.example.wx_smart_community.model.WxUserInfo;
import com.example.wx_smart_community.entity.WxUser;

public interface WxUserService {
    WxLoginResponse login(String code);
    WxUserInfo getUserInfo(String openid, String userId);
    WxUserInfo bindWechat(String code, String userId);
    WxUser getUserByOpenid(String openid);
    WxUser getUserByUserId(Long userId);
    void updateUserInfo(WxUser wxUser);
} 