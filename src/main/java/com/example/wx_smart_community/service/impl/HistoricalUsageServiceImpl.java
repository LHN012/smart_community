package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wx_smart_community.mapper.HistoricalUsageMapper;
import com.example.wx_smart_community.model.HistoricalUsage;
import com.example.wx_smart_community.service.HistoricalUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoricalUsageServiceImpl extends ServiceImpl<HistoricalUsageMapper, HistoricalUsage> implements HistoricalUsageService {

    @Override
    public List<HistoricalUsage> getHistoricalUsage(Integer houseId, String month, String type) {
        log.info("查询历史用量数据，houseId: {}, month: {}, type: {}", houseId, month, type);
        
        LambdaQueryWrapper<HistoricalUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HistoricalUsage::getHouseId, houseId);
        
        // 如果指定了月份，添加月份条件
        if (month != null && !month.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                wrapper.eq(HistoricalUsage::getMonth, sdf.parse(month));
            } catch (ParseException e) {
                log.error("月份格式解析错误: {}", month, e);
            }
        }
        
        // 如果指定了类型，添加类型条件
        if (type != null && !type.isEmpty() && !type.equals("全部")) {
            wrapper.eq(HistoricalUsage::getType, type);
        }
        
        // 按月份降序排序
        wrapper.orderByDesc(HistoricalUsage::getMonth);
        
        return list(wrapper);
    }
} 