package com.example.wx_smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("price_settings")
public class PriceSetting {
    
    @TableId(type = IdType.AUTO)
    private Integer priceId;
    
    private String type;
    
    private Integer level;
    
    private BigDecimal price;
    
    private BigDecimal startUsage;
    
    private BigDecimal endUsage;
    
    private Date effectiveDate;
    
    private Date endDate;
    
    private Integer paymentCycle;
    
    private Integer enableStatus;
} 