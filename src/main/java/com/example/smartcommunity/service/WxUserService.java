package com.example.smartcommunity.service;

import com.example.smartcommunity.model.WxUser;

public interface WxUserService {
    /**
     * 根据openid查找微信用户
     */
    WxUser findByOpenid(String openid);

    /**
     * 保存或更新微信用户信息
     */
    WxUser saveOrUpdate(WxUser wxUser);

    /**
     * 根据userId查找微信用户
     */
    WxUser findByUserId(Integer userId);

    /**
     * 更新微信用户信息
     */
    boolean updateWxUser(WxUser wxUser);
} 