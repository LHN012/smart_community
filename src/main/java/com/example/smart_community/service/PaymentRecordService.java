package com.example.smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_community.entity.PaymentRecords;
import java.util.List;

public interface PaymentRecordService extends IService<PaymentRecords> {
    List<PaymentRecords> findAll();
} 