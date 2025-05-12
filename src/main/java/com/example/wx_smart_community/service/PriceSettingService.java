package com.example.wx_smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wx_smart_community.entity.PriceSetting;

import java.util.List;

public interface PriceSettingService extends IService<PriceSetting> {
    
    /**
     * 获取当前有效的价格设置
     * @param type 类型（水、电、燃气、物业）
     * @return 价格设置列表
     */
    List<PriceSetting> getCurrentValidSettings(String type);
    
    /**
     * 获取历史价格设置
     * @param type 类型（水、电、燃气、物业）
     * @return 历史价格设置列表
     */
    List<PriceSetting> getHistorySettings(String type);
    
    /**
     * 启用价格设置
     * @param priceId 价格设置ID
     * @return 是否成功
     */
    boolean enableSetting(Integer priceId);
    
    /**
     * 禁用价格设置
     * @param priceId 价格设置ID
     * @return 是否成功
     */
    boolean disableSetting(Integer priceId);
} 