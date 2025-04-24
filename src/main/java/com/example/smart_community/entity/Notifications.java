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
    private Integer id;
    
    private String notificationType;
    private String title;
    private String content;
    private Integer senderId;
    private String receiverType;
    private Integer receiverId;
    private LocalDateTime sendTime;
} 