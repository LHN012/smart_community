package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("paymentrecords")
public class PaymentRecords {
    @TableId(type = IdType.AUTO)
    private Integer recordId;
    
    private Integer houseId;
    
    private String type;
    
    private BigDecimal amount;
    
    private BigDecimal balanceBefore;
    
    private BigDecimal balanceAfter;
    
    private LocalDateTime transactionTime;
} 