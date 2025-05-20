package com.example.wx_smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recharge_records")
public class RechargeRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer houseId;
    private Integer userId;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private BigDecimal rechargeAmount;
    private String rechargeChannel;
    private String paymentMethod;
    private LocalDateTime rechargeTime;
} 