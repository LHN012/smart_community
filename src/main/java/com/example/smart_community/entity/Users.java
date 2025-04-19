package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;
    
    @TableField("username")
    private String username;
    
    @TableField("password")
    private String password;
    
    @TableField("role")
    private Integer role;       // 1=普通用户, 2=管理员, 3=超级管理员
    
    @TableField("email")
    private String email;
    
    @TableField("phone_number")
    private String phoneNumber;
    
    @TableField("real_name")
    private String realName;
    
    @TableField("address")
    private String address;
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}
