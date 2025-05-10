package com.example.wx_smart_community.service.impl;

import com.example.wx_smart_community.service.PayStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AlipayStrategy implements PayStrategy {
    
    @Override
    public Map<String, Object> pay(Map<String, Object> params) {
        try {
            log.info("开始处理支付宝支付，参数：{}", params);
            
            // 验证参数
            if (!params.containsKey("amount") || !params.containsKey("description")) {
                throw new RuntimeException("缺少必要参数");
            }

            // 生成订单号
            String orderNo = "ALI" + System.currentTimeMillis();
            
            // 构建支付参数
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "支付宝支付成功");
            result.put("orderStr", "alipay_" + System.currentTimeMillis());
            result.put("orderInfo", "支付宝支付订单信息");
            result.put("transactionId", "alipay_" + System.currentTimeMillis());
            result.put("status", "SUCCESS");
            result.put("payType", "支付宝");
            result.put("rechargeChannel", "线上");
            result.put("paymentMethod", "支付宝");
            result.put("orderNo", orderNo);
            
            log.info("支付宝支付处理完成，返回结果：{}", result);
            return result;
        } catch (Exception e) {
            log.error("支付宝支付处理失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "支付宝支付失败：" + e.getMessage());
            return error;
        }
    }

    @Override
    public String getPayType() {
        return "支付宝";
    }
} 