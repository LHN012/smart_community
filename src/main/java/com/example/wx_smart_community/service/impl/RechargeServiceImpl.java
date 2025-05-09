package com.example.wx_smart_community.service.impl;

import com.example.wx_smart_community.model.RechargeRecord;
import com.example.wx_smart_community.service.RechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RechargeServiceImpl implements RechargeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void recordRecharge(RechargeRecord record) throws Exception {
        // 获取当前房屋余额
        String balanceSql = "SELECT balance FROM houses WHERE house_id = ?";
        BigDecimal currentBalance = jdbcTemplate.queryForObject(balanceSql, BigDecimal.class, record.getHouseId());
        
        if (currentBalance == null) {
            throw new Exception("房屋不存在");
        }

        // 设置充值前余额
        record.setBalanceBefore(currentBalance);
        
        // 计算充值后余额
        BigDecimal newBalance = currentBalance.add(record.getRechargeAmount());
        record.setBalanceAfter(newBalance);
        
        // 设置充值时间
        record.setRechargeTime(LocalDateTime.now());

        // 调用存储过程记录充值
        String sql = "CALL insert_recharge_record(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            record.getHouseId(),
            record.getUserId(),
            record.getRechargeAmount(),
            record.getRechargeChannel(),
            record.getPaymentMethod()
        );
    }
} 