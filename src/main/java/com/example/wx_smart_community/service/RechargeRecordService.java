package com.example.wx_smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wx_smart_community.entity.RechargeRecord;

import java.util.List;

public interface RechargeRecordService extends IService<RechargeRecord> {
    List<RechargeRecord> getRecordsByHouseId(Integer houseId, String month);
} 