package com.example.wx_smart_community.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class WarningRule {
    private Integer ruleId;
    private String ruleName;
    private Integer deviceId;
    private String ruleType;
    private String targetField;
    private BigDecimal thresholdHigh;
    private BigDecimal thresholdLow;
    private BigDecimal mutationRate;
    private Integer timeWindow;
    private Boolean enableStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 