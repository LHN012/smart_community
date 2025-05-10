package com.example.wx_smart_community.service.impl;

import com.example.wx_smart_community.service.PayStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WxPayStrategy implements PayStrategy {
    
    @Override
    public Map<String, Object> pay(Map<String, Object> params) {
        try {
            log.info("开始处理微信支付，参数：{}", params);
            
            // 验证参数
            if (!params.containsKey("amount") || !params.containsKey("description")) {
                throw new RuntimeException("缺少必要参数");
            }

            // 生成订单号
            String orderNo = "WX" + System.currentTimeMillis();
            
            // 构建支付参数
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "微信支付成功");
            result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            result.put("nonceStr", String.valueOf(System.currentTimeMillis()));
            result.put("package", "prepay_id=wx" + System.currentTimeMillis());
            result.put("signType", "MD5");
            result.put("paySign", "dummy_sign");
            result.put("transactionId", "wx_" + System.currentTimeMillis());
            result.put("status", "SUCCESS");
            result.put("payType", "微信");
            result.put("rechargeChannel", "线上");
            result.put("paymentMethod", "微信");
            result.put("orderNo", orderNo);
            
            log.info("微信支付处理完成，返回结果：{}", result);
            return result;
        } catch (Exception e) {
            log.error("微信支付处理失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "微信支付失败：" + e.getMessage());
            return error;
        }
    }

    @Override
    public String getPayType() {
        return "微信";
    }
} 