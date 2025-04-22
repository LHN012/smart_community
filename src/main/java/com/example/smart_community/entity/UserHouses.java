package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("userhouses")
public class UserHouses {
    @TableId(type = IdType.AUTO)
    private Integer relationId;
    
    private Integer userId;
    
    private Integer houseId;
    
    private String relationType;

    // 关联字段
    private String username;
    private String realName;
    private String phoneNumber;
    private String email;
    private String areaName;
    private String buildingName;
    private String unitName;
    private String houseName;
} 