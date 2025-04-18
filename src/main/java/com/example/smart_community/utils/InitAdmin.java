package com.example.smart_community.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class InitAdmin {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            // 检查是否已存在admin用户
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = 'admin'",
                Integer.class
            );

            if (count == null || count == 0) {
                // 插入超级管理员
                jdbcTemplate.update(
                    "INSERT INTO users (username, password, role, email, phone_number) VALUES (?, ?, ?, ?, ?)",
                    "admin",
                    "123456",  // 直接使用明文密码
                    3,  // 3表示超级管理员
                    "admin@example.com",
                    "13800138000"
                );

                System.out.println("超级管理员账号创建成功！用户名: admin, 密码: 123456");
            } else {
                System.out.println("超级管理员账号已存在！");
            }
        } catch (Exception e) {
            System.err.println("初始化管理员账号失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 