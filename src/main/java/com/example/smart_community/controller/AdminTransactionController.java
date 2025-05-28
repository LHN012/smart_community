package com.example.smart_community.controller;

import com.example.smart_community.entity.PaymentRecords;
import com.example.smart_community.entity.RechargeRecord;
import com.example.smart_community.service.PaymentRecordService;
import com.example.smart_community.service.RechargeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminTransactionController {
    private static final Logger logger = LoggerFactory.getLogger(AdminTransactionController.class);

    @Autowired
    private PaymentRecordService paymentRecordService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @GetMapping("/payment-records")
    public ResponseEntity<List<PaymentRecords>> getAllPaymentRecords() {
        logger.info("开始获取所有缴费记录");
        try {
            List<PaymentRecords> records = paymentRecordService.findAll();
            logger.info("成功获取缴费记录，数量: {}", records.size());
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            logger.error("获取缴费记录失败", e);
            throw e;
        }
    }

    @GetMapping("/recharge-records")
    public ResponseEntity<List<RechargeRecord>> getAllRechargeRecords() {
        logger.info("开始获取所有充值记录");
        try {
            List<RechargeRecord> records = rechargeRecordService.findAll();
            logger.info("成功获取充值记录，数量: {}", records.size());
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            logger.error("获取充值记录失败", e);
            throw e;
        }
    }
} 