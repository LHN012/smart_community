package com.example.wx_smart_community.service.impl;

import com.example.wx_smart_community.service.PayStrategy;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultPayStrategy implements PayStrategy {
    
    @Override
    public Map<String, Object> pay(Map<String, Object> params) {
        // 默认实现直接返回支付成功
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "支付成功");
        result.put("orderNo", params.get("orderNo"));
        result.put("transactionId", "default_" + System.currentTimeMillis());
        return result;
    }

    @Override
    public String getPayType() {
        return "DEFAULT";
    }
} 