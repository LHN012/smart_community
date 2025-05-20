package com.example.wx_smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wx_smart_community.model.HistoricalUsage;
import java.util.List;

public interface HistoricalUsageService extends IService<HistoricalUsage> {
    List<HistoricalUsage> getHistoricalUsage(Integer houseId, String month, String type);
} 