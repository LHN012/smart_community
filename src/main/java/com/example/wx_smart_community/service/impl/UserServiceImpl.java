package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.wx_smart_community.entity.User;
import com.example.wx_smart_community.mapper.UserMapper;
import com.example.wx_smart_community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        try {
            log.info("尝试登录用户: username={}", username);
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("username", username)
                   .eq("password", password);
            User user = userMapper.selectOne(wrapper);
            if (user != null) {
                log.info("用户登录成功: username={}, userId={}", username, user.getUserId());
            } else {
                log.warn("用户登录失败: username={}", username);
            }
            return user;
        } catch (Exception e) {
            log.error("登录过程发生错误: username={}, error={}", username, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<?> getUserInfo(Integer userId) {
        log.info("获取用户信息: userId={}", userId);
        User user = userMapper.findById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
} 