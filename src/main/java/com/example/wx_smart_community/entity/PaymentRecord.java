package com.example.wx_smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_records")
public class PaymentRecord {
    @TableId(type = IdType.AUTO)
    private Integer recordId;
    private Integer houseId;
    private String type;
    private BigDecimal deductedAmount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private Integer userId;
    private LocalDateTime createDate;
} 