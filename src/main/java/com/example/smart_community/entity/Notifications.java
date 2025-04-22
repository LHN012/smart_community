package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notifications")
public class Notifications {
    @TableId(type = IdType.AUTO)
    private Integer notificationId;
    
    private String content;
    
    private String type;
    
    private String status;
    
    private LocalDateTime createdAt;
} 