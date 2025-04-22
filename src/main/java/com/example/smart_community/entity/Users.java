package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 13:51
 */

@Data
@TableName("users")
public class Users {
    @TableId(type = IdType.AUTO)
    private Integer userId;
    
    private String username;
    
    private String password;
    
    private Integer role;       // 1=普通用户, 2=管理员, 3=超级管理员
    
    private String email;
    
    private String phoneNumber;
    
    private LocalDateTime createdAt;
    
    private String realName;
    
    private String address;
    
    private String avatar;
}
