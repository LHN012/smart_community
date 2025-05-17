package com.example.wx_smart_community.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notifications {
    private Integer id;
    private String notificationType;
    private String title;
    private String content;
    private Integer senderId;
    private String receiverType;
    private Integer receiverId;
    private LocalDateTime sendTime;
} 