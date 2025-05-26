package com.example.smart_community.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "recharge_records")
public class RechargeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "house_id", nullable = false)
    private Integer houseId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "balance_before", nullable = false)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false)
    private BigDecimal balanceAfter;

    @Column(name = "recharge_amount", nullable = false)
    private BigDecimal rechargeAmount;

    @Column(name = "recharge_channel", nullable = false)
    @Enumerated(EnumType.STRING)
    private RechargeChannel rechargeChannel;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "recharge_time")
    private LocalDateTime rechargeTime;

    public enum RechargeChannel {
        线上, 线下
    }

    public enum PaymentMethod {
        微信, 支付宝, 银行卡, 现金
    }
} 