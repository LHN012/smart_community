package com.example.wx_smart_community.service;

import com.example.wx_smart_community.entity.ElectricMeterData;
import java.util.List;

public interface ElectricMeterDataCollectionService {
    // 从设备采集实时数据
    ElectricMeterData collectRealTimeData(String deviceId);
    
    // 批量采集多个设备的实时数据
    List<ElectricMeterData> batchCollectRealTimeData(List<String> deviceIds);
    
    // 定时任务采集数据
    void scheduleDataCollection();
    
    // 处理设备上报的数据
    void handleDeviceReport(String deviceId, String payload);
    
    // 验证数据有效性
    boolean validateData(ElectricMeterData data);
} 