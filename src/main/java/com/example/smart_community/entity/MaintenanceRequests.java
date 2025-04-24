package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("maintenancerequests")
public class MaintenanceRequests {
    @TableId(type = IdType.AUTO)
    private Integer requestId;
    
    private Integer userId;
    
    private String description;
    
    private String status;
    
    private LocalDateTime createdAt;
    
    private Integer updateUserId;
    
    private LocalDateTime updatedAt;
} 