package com.example.wx_smart_community.service.impl;

import com.example.wx_smart_community.algorithm.GasLeakageDetector;
import com.example.wx_smart_community.algorithm.GasLeakageDetector.DetectionResult;
import com.example.wx_smart_community.entity.GasMeterData;
import com.example.wx_smart_community.entity.WarningRule;
import com.example.wx_smart_community.entity.DeviceEvent;
import com.example.wx_smart_community.entity.Notifications;
import com.example.wx_smart_community.entity.NotificationReceiver;
import com.example.wx_smart_community.service.GasLeakageDetectionService;
import com.example.wx_smart_community.mapper.GasMeterDataMapper;
import com.example.wx_smart_community.mapper.WarningRuleMapper;
import com.example.wx_smart_community.mapper.DeviceMapper;
import com.example.wx_smart_community.mapper.DeviceEventMapper;
import com.example.wx_smart_community.mapper.NotificationsMapper;
import com.example.wx_smart_community.mapper.NotificationReceiverMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class GasLeakageDetectionServiceImpl implements GasLeakageDetectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(GasLeakageDetectionServiceImpl.class);

    @Autowired
    private GasLeakageDetector detector;

    @Autowired
    private GasMeterDataMapper gasMeterDataMapper;
    
    @Autowired
    private WarningRuleMapper warningRuleMapper;
    
    @Autowired
    private DeviceMapper deviceMapper;
    
    @Autowired
    private DeviceEventMapper deviceEventMapper;
    
    @Autowired
    private NotificationsMapper notificationsMapper;
    
    @Autowired
    private NotificationReceiverMapper notificationReceiverMapper;

    @Override
    @Transactional
    public List<DetectionResult> detectLeakage(String deviceId) {
        List<DetectionResult> results = new ArrayList<>();
        
        try {
            // 获取设备的预警规则
            List<WarningRule> rules = getDeviceWarningRules(deviceId);
            logger.info("设备{}的预警规则数量: {}", deviceId, rules.size());
            
            // 获取最近24小时的数据
            List<GasMeterData> historicalData = getHistoricalData(deviceId, 24);
            logger.info("设备{}的历史数据数量: {}", deviceId, historicalData.size());
            
            if (historicalData.isEmpty()) {
                logger.warn("设备{}没有历史数据", deviceId);
                return results;
            }
            
            // 获取最新数据
            GasMeterData latestData = historicalData.get(historicalData.size() - 1);
            logger.debug("最新数据: 流量={}, 压力={}, 温度={}", 
                latestData.getFlowRate(),
                latestData.getPressure(),
                latestData.getTemperature());
            
            // 对每个规则进行检测
            for (WarningRule rule : rules) {
                if (!rule.getEnableStatus()) {
                    logger.debug("规则{}已禁用，跳过检测", rule.getRuleName());
                    continue;
                }
                
                logger.debug("开始执行规则: {}, 类型: {}", rule.getRuleName(), rule.getRuleType());
                DetectionResult result = null;
                
                try {
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
                            logger.debug("历史流量数据: {}", flowRates);
                            logger.debug("当前流量: {}, 突变率阈值: {}", 
                                latestData.getFlowRate().doubleValue(),
                                rule.getMutationRate().doubleValue());
                            
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
                            
                        default:
                            logger.warn("未知的规则类型: {}", rule.getRuleType());
                            break;
                    }
                    
                    if (result != null && result.isLeakageDetected()) {
                        logger.warn("设备{}触发规则{}: {}", deviceId, rule.getRuleName(), result.getDescription());
                        results.add(result);
                        // 保存检测结果
                        saveDetectionResult(deviceId, result);
                        // 如果检测到泄漏，更新设备状态
                        updateDeviceStatus(deviceId, "故障");
                    } else {
                        logger.debug("规则{}检测结果正常", rule.getRuleName());
                    }
                } catch (Exception e) {
                    logger.error("执行规则{}时发生错误: {}", rule.getRuleName(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error("执行泄漏检测时发生错误: {}", e.getMessage(), e);
        }
        
        return results;
    }

    @Override
    @Transactional
    public List<GasMeterData> getHistoricalData(String deviceId, int hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        logger.debug("获取设备{}从{}开始的历史数据", deviceId, startTime);
        return gasMeterDataMapper.findHistoricalData(deviceId, startTime);
    }

    @Override
    @Transactional
    public List<WarningRule> getDeviceWarningRules(String deviceId) {
        logger.debug("获取设备{}的预警规则", deviceId);
        // 先获取设备特定的规则
        List<WarningRule> rules = warningRuleMapper.findByDeviceId(deviceId);
        // 再获取全局规则（deviceId = 0）
        List<WarningRule> globalRules = warningRuleMapper.findByDeviceId("0");
        // 合并规则，设备特定规则优先
        rules.addAll(globalRules);
        return rules;
    }

    @Override
    @Transactional
    public void saveDetectionResult(String deviceId, DetectionResult result) {
        logger.debug("保存设备{}的检测结果: {}", deviceId, result.getDescription());
        
        try {
            // 1. 保存设备事件
            DeviceEvent event = new DeviceEvent();
            event.setDeviceId(deviceId);
            event.setEventType("alert");
            
            String eventData = String.format(
                "{\"alertType\":\"%s\",\"description\":\"%s\",\"abnormalValue\":\"%s\"}",
                result.getAlertType(),
                result.getDescription(),
                result.getAbnormalValue()
            );
            event.setEventData(eventData);
            event.setTimestamp(result.getDetectionTime());
            
            logger.info("正在保存设备事件...");
            deviceEventMapper.insert(event);
            logger.info("设备事件保存成功");
            
            // 2. 获取设备关联的房屋ID
            logger.info("正在获取设备{}关联的房屋ID...", deviceId);
            Integer houseId = deviceMapper.getHouseIdByDeviceId(deviceId);
            logger.info("获取到房屋ID: {}", houseId);
            
            if (houseId == null) {
                logger.error("设备{}未关联房屋", deviceId);
                return;
            }
            
            // 3. 创建通知
            logger.info("正在创建通知...");
            Notifications notification = new Notifications();
            notification.setNotificationType("紧急通知");
            notification.setTitle("燃气安全预警紧急通知");
            notification.setContent("检测到设备" + deviceId + "出现异常：" + result.getDescription() + 
                                "\n异常类型：" + result.getAlertType() + 
                                "\n异常值：" + result.getAbnormalValue() + 
                                "\n检测时间：" + result.getDetectionTime());
            notification.setSenderId(1); // 默认系统发送者ID为1
            notification.setReceiverType("特定房间业主");
            notification.setReceiverId(houseId);
            notification.setSendTime(LocalDateTime.now());
            
            logger.info("正在保存通知...");
            int rows = notificationsMapper.insert(notification);
            logger.info("通知保存结果: {} 行受影响", rows);
            logger.info("生成的通知ID: {}", notification.getId());
            
            // 4. 获取该房屋绑定的所有用户ID
            logger.info("正在获取房屋{}的所有用户ID...", houseId);
            List<Integer> userIds = deviceMapper.getUserIdsByHouseId(houseId);
            logger.info("获取到{}个用户", userIds.size());
            
            // 5. 为每个用户创建通知接收记录
            logger.info("正在创建用户通知接收记录...");
            for (Integer userId : userIds) {
                try {
                    NotificationReceiver receiver = new NotificationReceiver();
                    receiver.setNotificationId(notification.getId());
                    receiver.setReceiverUserId(userId);
                    receiver.setIsRead(false);
                    notificationReceiverMapper.insert(receiver);
                    logger.info("已为用户{}创建通知接收记录", userId);
                } catch (Exception e) {
                    logger.error("为用户{}创建通知接收记录失败: {}", userId, e.getMessage(), e);
                }
            }
            logger.info("通知接收记录创建完成");
            
        } catch (Exception e) {
            logger.error("保存检测结果时发生错误: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void updateDeviceStatus(String deviceId, String status) {
        logger.debug("更新设备{}状态为: {}", deviceId, status);
        deviceMapper.updateStatus(deviceId, status);
    }
} 