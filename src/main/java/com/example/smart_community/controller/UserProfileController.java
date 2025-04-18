package com.example.smart_community.controller;

import com.example.smart_community.common.Result;
import com.example.smart_community.entity.UserProfile;
import com.example.smart_community.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/{userId}")
    public Result<UserProfile> getByUserId(@PathVariable Integer userId) {
        UserProfile profile = userProfileService.getByUserId(userId);
        if (profile != null) {
            return Result.success(profile);
        }
        return Result.error("未找到用户个人信息");
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody UserProfile profile) {
        if (userProfileService.save(profile)) {
            return Result.success(true);
        }
        return Result.error("创建用户个人信息失败");
    }

    @PutMapping("/{userId}")
    public Result<Boolean> update(@PathVariable Integer userId, @RequestBody UserProfile profile) {
        profile.setUserId(userId);
        if (userProfileService.updateById(profile)) {
            return Result.success(true);
        }
        return Result.error("更新用户个人信息失败");
    }

    @DeleteMapping("/{userId}")
    public Result<Boolean> delete(@PathVariable Integer userId) {
        if (userProfileService.deleteByUserId(userId)) {
            return Result.success(true);
        }
        return Result.error("删除用户个人信息失败");
    }
} 