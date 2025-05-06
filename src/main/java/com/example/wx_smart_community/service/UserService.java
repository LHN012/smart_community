package com.example.wx_smart_community.service;

import com.example.wx_smart_community.entity.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    User login(String username, String password);
    ResponseEntity<?> getUserInfo(Integer userId);
} 