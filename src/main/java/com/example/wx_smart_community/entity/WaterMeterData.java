package com.example.wx_smart_community.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WaterMeterData {
    private Long id;
    private String deviceId;
    private LocalDateTime timestamp;
    private BigDecimal flowRate;
    private BigDecimal totalVolume;
    private BigDecimal pressure;
    private BigDecimal temperature;
    private BigDecimal batteryLevel;
    private Integer signalStrength;
    private LocalDateTime createdAt;
} 