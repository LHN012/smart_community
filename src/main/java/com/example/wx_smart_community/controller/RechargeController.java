package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.model.RechargeRecord;
import com.example.wx_smart_community.service.RechargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService rechargeService;

    @PostMapping("/recharge")
    public ResponseEntity<Map<String, Object>> createRecharge(@RequestBody Map<String, Object> params) {
        try {
            log.info("创建充值订单，参数：{}", params);
            Map<String, Object> result = rechargeService.createRecharge(params);
            log.info("创建充值订单结果：{}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("创建充值订单失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "创建充值订单失败：" + e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    @PostMapping("/recharge/record")
    public ResponseEntity<Map<String, Object>> recordRecharge(@RequestBody Map<String, Object> params) {
        try {
            log.info("记录充值，参数：{}", params);
            // 确保参数包含必要的字段
            if (!params.containsKey("userId") || !params.containsKey("houseId") || !params.containsKey("amount")) {
                throw new RuntimeException("缺少必要参数");
            }
            Map<String, Object> result = rechargeService.createRecharge(params);
            log.info("记录充值结果：{}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("记录充值失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "记录充值失败：" + e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    @PostMapping("/payment/wechat")
    public ResponseEntity<Map<String, Object>> wechatPay(@RequestBody Map<String, Object> params) {
        try {
            log.info("微信支付，参数：{}", params);
            // 确保参数包含必要的字段
            if (!params.containsKey("userId") || !params.containsKey("houseId") || !params.containsKey("amount")) {
                throw new RuntimeException("缺少必要参数");
            }

            // 构建支付参数
            Map<String, String> payParams = new HashMap<>();
            payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            payParams.put("nonceStr", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32));
            payParams.put("package", "prepay_id=wx" + System.currentTimeMillis());
            payParams.put("signType", "MD5");
            payParams.put("paySign", "dummy_sign");

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", payParams);
            log.info("微信支付参数生成成功：{}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("微信支付失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "微信支付失败：" + e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    @PostMapping("/payment/alipay")
    public ResponseEntity<Map<String, Object>> alipay(@RequestBody Map<String, Object> params) {
        try {
            log.info("支付宝支付，参数：{}", params);
            // 确保参数包含必要的字段
            if (!params.containsKey("userId") || !params.containsKey("houseId") || !params.containsKey("amount")) {
                throw new RuntimeException("缺少必要参数");
            }
            params.put("payType", "支付宝");
            Map<String, Object> result = rechargeService.createRecharge(params);
            log.info("支付宝支付结果：{}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("支付宝支付失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "支付宝支付失败：" + e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    @PostMapping("/recharge/notify")
    public String handlePayNotify(@RequestBody String xmlData) {
        log.info("收到支付回调通知：{}", xmlData);
        try {
            rechargeService.handlePayNotify(xmlData);
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        }
    }

    @GetMapping("/status/{orderNo}")
    public ResponseEntity<RechargeRecord> getRechargeStatus(@PathVariable String orderNo) {
        log.info("查询充值订单状态，orderNo: {}", orderNo);
        RechargeRecord record = rechargeService.getRechargeByOrderNo(orderNo);
        return ResponseEntity.ok(record);
    }
} 