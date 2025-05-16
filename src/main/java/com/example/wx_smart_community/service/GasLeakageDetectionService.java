package com.example.wx_smart_community.service;

import com.example.wx_smart_community.algorithm.GasLeakageDetector.DetectionResult;
import com.example.wx_smart_community.entity.GasMeterData;
import com.example.wx_smart_community.entity.WarningRule;
import java.util.List;

public interface GasLeakageDetectionService {
    
    // 执行所有检测规则
    List<DetectionResult> detectLeakage(String deviceId);
    
    // 获取设备的历史数据
    List<GasMeterData> getHistoricalData(String deviceId, int hours);
    
    // 获取设备的预警规则
    List<WarningRule> getDeviceWarningRules(String deviceId);
    
    // 保存检测结果
    void saveDetectionResult(String deviceId, DetectionResult result);
    
    // 更新设备状态
    void updateDeviceStatus(String deviceId, String status);
} 