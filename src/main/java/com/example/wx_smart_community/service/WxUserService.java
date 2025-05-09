package com.example.wx_smart_community.service;

import com.example.wx_smart_community.entity.WxUser;
import com.example.wx_smart_community.model.WxLoginResponse;
import com.example.wx_smart_community.model.WxUserInfo;

public interface WxUserService {
    /**
     * 微信登录
     */
    WxLoginResponse login(String code);

    /**
     * 获取用户信息
     */
    WxUserInfo getUserInfo(String openid, String userId);

    /**
     * 根据openid查找微信用户
     */
    WxUser findByOpenid(String openid);

    /**
     * 保存或更新微信用户信息
     */
    boolean saveOrUpdate(WxUser wxUser);

    /**
     * 根据userId查找微信用户
     */
    WxUser findByUserId(Integer userId);

    /**
     * 更新微信用户信息
     */
    boolean updateWxUser(WxUser wxUser);

    /**
     * 绑定微信账号
     */
    WxUserInfo bindWechat(String code, String userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(WxUser wxUser);
} 