package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.entity.PaymentRecord;
import com.example.wx_smart_community.service.PaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payment/records")
public class PaymentRecordController {
    @Autowired
    private PaymentRecordService paymentRecordService;

    @GetMapping("/list")
    public List<PaymentRecord> getRecords(@RequestParam Integer houseId, @RequestParam(required = false) String month) {
        return paymentRecordService.getRecordsByHouseId(houseId, month);
    }
} 