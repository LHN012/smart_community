package com.example.wx_smart_community.service.impl;

import com.example.wx_smart_community.algorithm.GasLeakageDetector;
import com.example.wx_smart_community.algorithm.GasLeakageDetector.DetectionResult;
import com.example.wx_smart_community.entity.GasMeterData;
import com.example.wx_smart_community.entity.WarningRule;
import com.example.wx_smart_community.service.GasLeakageDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GasLeakageDetectionServiceImpl implements GasLeakageDetectionService {

    @Autowired
    private GasLeakageDetector detector;

    // 这里需要注入相应的Mapper类
    // @Autowired
    // private GasMeterDataMapper gasMeterDataMapper;
    // @Autowired
    // private WarningRuleMapper warningRuleMapper;
    // @Autowired
    // private DeviceMapper deviceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DetectionResult> detectLeakage(String deviceId) {
        List<DetectionResult> results = new ArrayList<>();
        
        // 获取设备的预警规则
        List<WarningRule> rules = getDeviceWarningRules(deviceId);
        
        // 获取最近24小时的数据
        List<GasMeterData> historicalData = getHistoricalData(deviceId, 24);
        
        if (historicalData.isEmpty()) {
            return results;
        }
        
        // 获取最新数据
        GasMeterData latestData = historicalData.get(historicalData.size() - 1);
        
        // 对每个规则进行检测
        for (WarningRule rule : rules) {
            if (!rule.getEnableStatus()) {
                continue;
            }
            
            DetectionResult result = null;
            
            switch (rule.getRuleType()) {
                case "压力阈值":
                    result = detector.detectPressureAnomaly(
                        latestData.getPressure().doubleValue(),
                        rule.getThresholdHigh().doubleValue(),
                        rule.getThresholdLow().doubleValue()
                    );
                    break;
                    
                case "流量突变":
                    List<Double> flowRates = historicalData.stream()
                        .map(data -> data.getFlowRate().doubleValue())
                        .collect(Collectors.toList());
                    result = detector.detectFlowRateMutation(
                        flowRates,
                        latestData.getFlowRate().doubleValue(),
                        rule.getMutationRate().doubleValue()
                    );
                    break;
                    
                case "温度异常":
                    result = detector.detectTemperatureAnomaly(
                        latestData.getTemperature().doubleValue(),
                        rule.getThresholdHigh().doubleValue(),
                        rule.getThresholdLow().doubleValue()
                    );
                    break;
                    
                case "长期微小流量":
                    List<Double> historicalFlowRates = historicalData.stream()
                        .map(data -> data.getFlowRate().doubleValue())
                        .collect(Collectors.toList());
                    result = detector.detectContinuousSmallFlow(
                        historicalFlowRates,
                        rule.getThresholdLow().doubleValue(),
                        rule.getTimeWindow()
                    );
                    break;
            }
            
            if (result != null && result.isLeakageDetected()) {
                results.add(result);
                // 保存检测结果
                saveDetectionResult(deviceId, result);
                // 如果检测到泄漏，更新设备状态
                updateDeviceStatus(deviceId, "故障");
            }
        }
        
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GasMeterData> getHistoricalData(String deviceId, int hours) {
        // TODO: 实现从数据库获取历史数据的逻辑
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarningRule> getDeviceWarningRules(String deviceId) {
        // TODO: 实现从数据库获取预警规则的逻辑
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public void saveDetectionResult(String deviceId, DetectionResult result) {
        // TODO: 实现保存检测结果的逻辑
    }

    @Override
    @Transactional
    public void updateDeviceStatus(String deviceId, String status) {
        // TODO: 实现更新设备状态的逻辑
    }
} 