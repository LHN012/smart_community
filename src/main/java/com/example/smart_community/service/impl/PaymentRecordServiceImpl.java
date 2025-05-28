package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.PaymentRecords;
import com.example.smart_community.mapper.PaymentRecordMapper;
import com.example.smart_community.service.PaymentRecordService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecords> implements PaymentRecordService {
    
    @Override
    public List<PaymentRecords> findAll() {
        return list();
    }
} 