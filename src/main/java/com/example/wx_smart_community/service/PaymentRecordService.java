package com.example.wx_smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wx_smart_community.entity.PaymentRecord;

import java.util.List;

public interface PaymentRecordService extends IService<PaymentRecord> {
    List<PaymentRecord> getRecordsByHouseId(Integer houseId, String month);
} 