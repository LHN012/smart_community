package com.example.smart_community.service.impl;

import com.example.smart_community.entity.RechargeRecord;
import com.example.smart_community.repository.RechargeRecordRepository;
import com.example.smart_community.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordRepository rechargeRecordRepository;

    @Override
    public List<RechargeRecord> findAll() {
        return rechargeRecordRepository.findAll();
    }
} 