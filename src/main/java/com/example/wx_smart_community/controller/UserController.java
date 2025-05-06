package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.entity.User;
import com.example.wx_smart_community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    // 用于存储用户会话信息
    private static final Map<String, User> userSessions = new HashMap<>();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            log.info("收到登录请求: username={}", username);
            
            if (username == null || password == null) {
                log.warn("登录失败: 用户名或密码为空");
                return ResponseEntity.badRequest().body("用户名和密码不能为空");
            }

            User user = userService.login(username, password);
            if (user != null) {
                // 生成会话令牌
                String token = UUID.randomUUID().toString();
                userSessions.put(token, user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", user);
                
                log.info("登录成功: username={}, userId={}", username, user.getUserId());
                return ResponseEntity.ok(response);
            } else {
                log.warn("登录失败: 用户名或密码错误, username={}", username);
                return ResponseEntity.badRequest().body("用户名或密码错误");
            }
        } catch (Exception e) {
            log.error("登录过程发生错误: error={}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("登录失败：" + e.getMessage());
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("收到获取用户信息请求，Authorization header: {}", authHeader);
        
        if (authHeader == null || authHeader.isEmpty()) {
            log.warn("未提供Authorization header");
            return ResponseEntity.status(401).body("未登录");
        }
        
        // 处理Bearer token
        String token = authHeader;
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.info("处理后的token: {}", token);
        }
        
        // 打印所有会话信息用于调试
        log.info("当前所有会话信息: {}", userSessions);
        
        User user = userSessions.get(token);
        if (user == null) {
            log.warn("token无效或已过期: {}", token);
            return ResponseEntity.status(401).body("登录已过期");
        }
        
        log.info("成功获取用户信息: userId={}", user.getUserId());
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        if (token != null && !token.isEmpty()) {
            userSessions.remove(token);
        }
        return ResponseEntity.ok().body("退出登录成功");
    }
} 