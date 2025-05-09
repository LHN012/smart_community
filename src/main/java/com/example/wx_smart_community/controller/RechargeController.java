package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.model.RechargeRecord;
import com.example.wx_smart_community.service.RechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recharge")
public class RechargeController {

    @Autowired
    private RechargeService rechargeService;

    @PostMapping("/record")
    public ResponseEntity<?> recordRecharge(@RequestBody RechargeRecord record) {
        try {
            rechargeService.recordRecharge(record);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 