package com.example.wx_smart_community.service;

import com.example.wx_smart_community.model.RechargeRecord;

public interface RechargeService {
    void recordRecharge(RechargeRecord record) throws Exception;
} 