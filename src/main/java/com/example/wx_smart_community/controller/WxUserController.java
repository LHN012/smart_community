package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.entity.WxUser;
import com.example.wx_smart_community.service.WxUserService;
import com.example.wx_smart_community.utils.JwtUtil;
import com.example.wx_smart_community.model.WxLoginResponse;
import com.example.wx_smart_community.model.WxUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/wx")
public class WxUserController {

    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> params) {
        try {
            log.info("收到微信登录请求，参数：{}", params);
            String code = (String) params.get("code");
            if (code == null || code.trim().isEmpty()) {
                log.error("微信登录失败：code为空");
                return ResponseEntity.badRequest().body("登录凭证不能为空");
            }

            log.info("开始处理微信登录，code: {}", code);
            WxLoginResponse response = wxUserService.login(code);
            log.info("微信登录成功，返回数据：{}", response);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("微信登录失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("登录失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo(@RequestParam(required = false) String openid,
                                       @RequestParam(required = false) String userId) {
        try {
            log.info("获取微信用户信息，openid: {}, userId: {}", openid, userId);
            if (openid == null && userId == null) {
                return ResponseEntity.badRequest().body("openid和userId不能同时为空");
            }
            
            WxUserInfo userInfo = wxUserService.getUserInfo(openid, userId);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            log.error("获取微信用户信息失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取用户信息失败：" + e.getMessage());
        }
    }

    @PutMapping("/user/info")
    public ResponseEntity<?> updateUserInfo(@RequestBody WxUser wxUser) {
        try {
            wxUserService.updateUserInfo(wxUser);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bind")
    public ResponseEntity<?> bindWechat(@RequestBody Map<String, Object> params) {
        try {
            log.info("收到微信绑定请求，参数：{}", params);
            String code = (String) params.get("code");
            String userId = (String) params.get("userId");
            
            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("code不能为空");
            }
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("userId不能为空");
            }

            log.info("开始绑定微信账号，code: {}, userId: {}", code, userId);
            WxUserInfo userInfo = wxUserService.bindWechat(code, userId);
            log.info("微信账号绑定成功，用户信息：{}", userInfo);
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            log.error("微信账号绑定失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("绑定失败：" + e.getMessage());
        }
    }
} 