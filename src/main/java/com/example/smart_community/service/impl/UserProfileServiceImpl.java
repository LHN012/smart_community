package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.UserProfile;
import com.example.smart_community.mapper.UserProfileMapper;
import com.example.smart_community.service.UserProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    @Override
    public UserProfile getByUserId(Integer userId) {
        QueryWrapper<UserProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return getOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean deleteByUserId(Integer userId) {
        QueryWrapper<UserProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean updateProfile(UserProfile profile) {
        if (profile.getId() == null) {
            // 如果没有ID，先查询是否存在
            UserProfile existingProfile = getByUserId(profile.getUserId());
            if (existingProfile != null) {
                profile.setId(existingProfile.getId());
            }
        }
        return updateById(profile);
    }
} 