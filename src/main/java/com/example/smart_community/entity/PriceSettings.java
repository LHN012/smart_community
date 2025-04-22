package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("pricesettings")
public class PriceSettings {
    @TableId(type = IdType.AUTO)
    private Integer priceId;
    
    private String type;
    
    private BigDecimal unitPrice;
    
    private LocalDate effectiveDate;
} 