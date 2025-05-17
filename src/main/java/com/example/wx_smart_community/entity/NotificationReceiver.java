package com.example.wx_smart_community.entity;

import lombok.Data;

@Data
public class NotificationReceiver {
    private Integer id;
    private Integer receiverUserId;
    private Integer notificationId;
    private Boolean isRead;
} 