package com.example.wx_smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer userId;
    private String username;
    private String password;
    private Integer role;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private String realName;
    private String address;
    private String avatar;
} 