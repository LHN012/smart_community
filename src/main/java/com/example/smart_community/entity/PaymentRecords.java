package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_records")
public class PaymentRecords {
    @TableId(type = IdType.AUTO)
    private Integer recordId;
    
    private Integer houseId;
    
    private String type;
    
    private BigDecimal deductedAmount;
    
    private BigDecimal balanceBefore;
    
    private BigDecimal balanceAfter;
    
    private Integer userId;
    
    private LocalDateTime createDate;
    
    public enum PaymentType {
        水, 电, 燃气, 物业, 提出余额
    }
} 