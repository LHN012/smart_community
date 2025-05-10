package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wx_smart_community.mapper.RechargeRecordMapper;
import com.example.wx_smart_community.model.RechargeRecord;
import com.example.wx_smart_community.service.PayStrategy;
import com.example.wx_smart_community.service.RechargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeServiceImpl extends ServiceImpl<RechargeRecordMapper, RechargeRecord> implements RechargeService {

    private final List<PayStrategy> payStrategies;
    private final DefaultPayStrategy defaultPayStrategy;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Map<String, Object> createRecharge(Map<String, Object> params) {
        try {
            Integer userId = (Integer) params.get("userId");
            Integer houseId = (Integer) params.get("houseId");
            BigDecimal amount = new BigDecimal(params.get("amount").toString());
            String payType = (String) params.get("payType");

            // 获取当前房屋余额
            String balanceSql = "SELECT balance FROM houses WHERE house_id = ?";
            BigDecimal currentBalance = jdbcTemplate.queryForObject(balanceSql, BigDecimal.class, houseId);
            if (currentBalance == null) {
                throw new RuntimeException("房屋不存在");
            }

            // 创建充值记录
            RechargeRecord record = new RechargeRecord();
            record.setHouseId(houseId);
            record.setUserId(userId);
            record.setBalanceBefore(currentBalance);
            record.setBalanceAfter(currentBalance.add(amount));
            record.setRechargeAmount(amount);
            record.setRechargeChannel("线上");
            record.setPaymentMethod(payType);
            record.setRechargeTime(LocalDateTime.now());
            
            // 保存充值记录
            boolean saved = save(record);
            if (!saved) {
                throw new RuntimeException("保存充值记录失败");
            }
            log.info("创建充值记录成功：{}", record);

            // 获取支付策略
            PayStrategy payStrategy = payStrategies.stream()
                    .filter(strategy -> strategy.getPayType().equals(payType))
                    .findFirst()
                    .orElse(defaultPayStrategy);

            // 调用支付
            Map<String, Object> payParams = new HashMap<>();
            payParams.put("amount", amount);
            payParams.put("description", "物业费充值");
            Map<String, Object> payResult = payStrategy.pay(payParams);

            // 处理支付结果
            if ((Boolean) payResult.get("success")) {
                // 更新房屋余额
                String updateSql = "UPDATE houses SET balance = ? WHERE house_id = ?";
                int updated = jdbcTemplate.update(updateSql, record.getBalanceAfter(), houseId);
                if (updated <= 0) {
                    throw new RuntimeException("更新房屋余额失败");
                }
                log.info("更新房屋余额成功：houseId={}, newBalance={}", houseId, record.getBalanceAfter());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "充值成功");
            result.put("record", record);
            return result;
        } catch (Exception e) {
            log.error("充值失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "充值失败：" + e.getMessage());
            return error;
        }
    }

    @Override
    public RechargeRecord getRechargeByOrderNo(String orderNo) {
        return null; // 不再需要此方法
    }

    @Override
    @Transactional
    public void handlePayNotify(String xmlData) {
        log.info("收到支付回调通知：{}", xmlData);
        // 默认实现直接返回成功
    }
} 