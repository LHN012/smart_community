package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_profiles")
public class UserProfile {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String realName;
    private String address;
    private String avatar;
} 