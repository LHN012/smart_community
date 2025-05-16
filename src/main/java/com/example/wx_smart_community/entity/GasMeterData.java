package com.example.wx_smart_community.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class GasMeterData {
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