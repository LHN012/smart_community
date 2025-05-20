package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wx_smart_community.entity.PaymentRecord;
import com.example.wx_smart_community.mapper.PaymentRecordMapper;
import com.example.wx_smart_community.service.PaymentRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements PaymentRecordService {
    @Override
    public List<PaymentRecord> getRecordsByHouseId(Integer houseId, String month) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getHouseId, houseId);
        
        if (month != null && !month.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            LocalDateTime startDate = LocalDateTime.parse(month + "-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
            
            wrapper.between(PaymentRecord::getCreateDate, startDate, endDate);
        }
        
        wrapper.orderByDesc(PaymentRecord::getCreateDate);
        return list(wrapper);
    }
} 