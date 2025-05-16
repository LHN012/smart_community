package com.example.wx_smart_community.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ElectricMeterData {
    private Long id;
    private String deviceId;
    private LocalDateTime timestamp;
    private BigDecimal currentPower;
    private BigDecimal totalEnergy;
    private BigDecimal voltage;
    private BigDecimal current;
    private BigDecimal powerFactor;
    private Integer signalStrength;
    private LocalDateTime createdAt;
} 