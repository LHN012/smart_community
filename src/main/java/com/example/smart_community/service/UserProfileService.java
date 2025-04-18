package com.example.smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_community.entity.UserProfile;

public interface UserProfileService extends IService<UserProfile> {
    UserProfile getByUserId(Integer userId);
    boolean deleteByUserId(Integer userId);
    boolean updateProfile(UserProfile profile);
} 