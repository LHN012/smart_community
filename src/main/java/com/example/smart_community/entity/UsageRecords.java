package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("usagerecords")
public class UsageRecords {
    @TableId(type = IdType.AUTO)
    private Integer usageId;
    
    private Integer houseId;
    
    private String type;
    
    private BigDecimal usageAmount;
    
    private LocalDate month;
} 