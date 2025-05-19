package com.example.wx_smart_community.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MeterData {
    private Long id;
    private String deviceId;
    private LocalDateTime timestamp;
    private BigDecimal totalVolume; // 累计用量
    private BigDecimal totalEnergy; // 累计电量
    private BigDecimal flowRate; // 流量
    private BigDecimal pressure; // 压力
    private BigDecimal temperature; // 温度
    private BigDecimal batteryLevel; // 电池电量
    private Integer signalStrength; // 信号强度
    private LocalDateTime createdAt; // 创建时间
} 