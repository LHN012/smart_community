package com.example.wx_smart_community.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeviceEvent {
    private Long id;
    private String deviceId;
    private String eventType;  // enum('online','offline','alert','maintenance','update')
    private String eventData;  // JSON格式的详细事件数据
    private LocalDateTime timestamp;  // 事件发生时间
    private LocalDateTime createdAt;  // 记录创建时间
} 