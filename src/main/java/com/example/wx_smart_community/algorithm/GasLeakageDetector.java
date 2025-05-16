package com.example.wx_smart_community.algorithm;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Component
public class GasLeakageDetector {
    
    // 检测结果类
    @Data
    public static class DetectionResult {
        private boolean isLeakageDetected;
        private String alertType;
        private String description;
        private double abnormalValue;
        private LocalDateTime detectionTime;
    }

    // 压力阈值检测
    public DetectionResult detectPressureAnomaly(double currentPressure, double thresholdHigh, double thresholdLow) {
        DetectionResult result = new DetectionResult();
        result.setDetectionTime(LocalDateTime.now());
        
        if (currentPressure > thresholdHigh) {
            result.setLeakageDetected(true);
            result.setAlertType("压力过高警告");
            result.setDescription("当前压力超过上限阈值");
            result.setAbnormalValue(currentPressure);
        } else if (currentPressure < thresholdLow) {
            result.setLeakageDetected(true);
            result.setAlertType("压力过低警告");
            result.setDescription("当前压力低于下限阈值");
            result.setAbnormalValue(currentPressure);
        } else {
            result.setLeakageDetected(false);
        }
        
        return result;
    }

    // 流量突变检测
    public DetectionResult detectFlowRateMutation(List<Double> historicalFlowRates, double currentFlowRate, double mutationRate) {
        DetectionResult result = new DetectionResult();
        result.setDetectionTime(LocalDateTime.now());
        
        if (historicalFlowRates.isEmpty()) {
            return result;
        }

        double avgFlowRate = historicalFlowRates.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double changeRate = Math.abs((currentFlowRate - avgFlowRate) / avgFlowRate);
        
        if (changeRate > mutationRate) {
            result.setLeakageDetected(true);
            result.setAlertType("流量突变警告");
            result.setDescription(String.format("流量变化率%.2f%%超过阈值%.2f%%", changeRate * 100, mutationRate * 100));
            result.setAbnormalValue(changeRate);
        }
        
        return result;
    }

    // 长期微小流量检测
    public DetectionResult detectContinuousSmallFlow(List<Double> flowRates, double minThreshold, int timeWindowHours) {
        DetectionResult result = new DetectionResult();
        result.setDetectionTime(LocalDateTime.now());
        
        if (flowRates.size() < timeWindowHours) {
            return result;
        }

        long continuousSmallFlowCount = flowRates.stream()
                .filter(flow -> flow > 0 && flow < minThreshold)
                .count();

        if (continuousSmallFlowCount >= timeWindowHours * 0.8) { // 如果80%以上的时间都有微小流量
            result.setLeakageDetected(true);
            result.setAlertType("持续微小流量警告");
            result.setDescription("检测到长期持续的微小流量，可能存在泄漏");
            result.setAbnormalValue(continuousSmallFlowCount);
        }
        
        return result;
    }

    // 温度异常检测
    public DetectionResult detectTemperatureAnomaly(double currentTemp, double thresholdHigh, double thresholdLow) {
        DetectionResult result = new DetectionResult();
        result.setDetectionTime(LocalDateTime.now());
        
        if (currentTemp > thresholdHigh) {
            result.setLeakageDetected(true);
            result.setAlertType("温度过高警告");
            result.setDescription("当前温度超过上限阈值");
            result.setAbnormalValue(currentTemp);
        } else if (currentTemp < thresholdLow) {
            result.setLeakageDetected(true);
            result.setAlertType("温度过低警告");
            result.setDescription("当前温度低于下限阈值");
            result.setAbnormalValue(currentTemp);
        }
        
        return result;
    }
} 