package com.example.wx_smart_community.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("historical_usage")
public class HistoricalUsage {
    @TableId(type = IdType.AUTO)
    private Integer usageId;
    private Integer houseId;
    private String type;
    private Date month;
    private BigDecimal totalUsage;
} 