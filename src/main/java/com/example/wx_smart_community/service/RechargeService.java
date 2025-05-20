package com.example.wx_smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wx_smart_community.entity.RechargeRecord;
import java.util.Map;

public interface RechargeService extends IService<RechargeRecord> {
    Map<String, Object> createRecharge(Map<String, Object> params);
    void handlePayNotify(String xmlData);
    RechargeRecord getRechargeByOrderNo(String orderNo);
} 