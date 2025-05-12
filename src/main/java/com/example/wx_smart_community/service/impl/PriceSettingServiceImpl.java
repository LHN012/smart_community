package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wx_smart_community.entity.PriceSetting;
import com.example.wx_smart_community.mapper.PriceSettingMapper;
import com.example.wx_smart_community.service.PriceSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PriceSettingServiceImpl extends ServiceImpl<PriceSettingMapper, PriceSetting> implements PriceSettingService {

    @Override
    public List<PriceSetting> getCurrentValidSettings(String type) {
        Date now = new Date();
        LambdaQueryWrapper<PriceSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PriceSetting::getType, type)
                .eq(PriceSetting::getEnableStatus, 1)
                .le(PriceSetting::getEffectiveDate, now)
                .and(w -> w.isNull(PriceSetting::getEndDate)
                        .or()
                        .gt(PriceSetting::getEndDate, now))
                .orderByAsc(PriceSetting::getLevel);
        return list(wrapper);
    }

    @Override
    public List<PriceSetting> getHistorySettings(String type) {
        Date now = new Date();
        LambdaQueryWrapper<PriceSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PriceSetting::getType, type)
                .lt(PriceSetting::getEffectiveDate, now)
                .and(w -> w.isNotNull(PriceSetting::getEndDate)
                        .and(w2 -> w2.lt(PriceSetting::getEndDate, now)))
                .orderByDesc(PriceSetting::getEffectiveDate);
        return list(wrapper);
    }

    @Override
    @Transactional
    public boolean enableSetting(Integer priceId) {
        PriceSetting setting = getById(priceId);
        if (setting == null) {
            return false;
        }
        setting.setEnableStatus(1);
        return updateById(setting);
    }

    @Override
    @Transactional
    public boolean disableSetting(Integer priceId) {
        PriceSetting setting = getById(priceId);
        if (setting == null) {
            return false;
        }
        setting.setEnableStatus(0);
        return updateById(setting);
    }
} 