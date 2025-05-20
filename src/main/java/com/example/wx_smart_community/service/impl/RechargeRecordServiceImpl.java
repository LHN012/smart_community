package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wx_smart_community.entity.RechargeRecord;
import com.example.wx_smart_community.mapper.RechargeRecordMapper;
import com.example.wx_smart_community.service.RechargeRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RechargeRecordServiceImpl extends ServiceImpl<RechargeRecordMapper, RechargeRecord> implements RechargeRecordService {
    @Override
    public List<RechargeRecord> getRecordsByHouseId(Integer houseId, String month) {
        LambdaQueryWrapper<RechargeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RechargeRecord::getHouseId, houseId);
        
        if (month != null && !month.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            LocalDateTime startDate = LocalDateTime.parse(month + "-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
            
            wrapper.between(RechargeRecord::getRechargeTime, startDate, endDate);
        }
        
        wrapper.orderByDesc(RechargeRecord::getRechargeTime);
        return list(wrapper);
    }
} 