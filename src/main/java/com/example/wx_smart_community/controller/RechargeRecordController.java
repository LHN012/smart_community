package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.entity.RechargeRecord;
import com.example.wx_smart_community.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recharge")
public class RechargeRecordController {
    @Autowired
    private RechargeRecordService rechargeRecordService;

    @GetMapping("/records")
    public List<RechargeRecord> getRecords(@RequestParam Integer houseId, @RequestParam(required = false) String month) {
        return rechargeRecordService.getRecordsByHouseId(houseId, month);
    }
} 