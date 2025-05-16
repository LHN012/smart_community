package com.example.wx_smart_community.service.impl;

import com.example.wx_smart_community.entity.GasMeterData;
import com.example.wx_smart_community.service.GasMeterDataCollectionService;
import com.example.wx_smart_community.mapper.GasMeterDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GasMeterDataCollectionServiceImpl implements GasMeterDataCollectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(GasMeterDataCollectionServiceImpl.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private GasMeterDataMapper gasMeterDataMapper;
    
    // 设备连接状态缓存
    private final Map<String, Boolean> deviceStatus = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        logger.info("初始化气表数据采集服务");
    }
    
    @Override
    public GasMeterData collectRealTimeData(String deviceId) {
        try {
            return gasMeterDataMapper.findLatestByDeviceId(deviceId);
        } catch (Exception e) {
            logger.error("从设备{}采集数据失败", deviceId, e);
            return null;
        }
    }
    
    @Override
    public List<GasMeterData> batchCollectRealTimeData(List<String> deviceIds) {
        List<GasMeterData> results = new ArrayList<>();
        for (String deviceId : deviceIds) {
            GasMeterData data = collectRealTimeData(deviceId);
            if (data != null) {
                results.add(data);
            }
        }
        return results;
    }
    
    @Override
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void scheduleDataCollection() {
        logger.info("开始定时采集气表数据...");
        List<String> deviceIds = gasMeterDataMapper.findActiveDeviceIds(
            LocalDateTime.now().minusHours(1)
        );
        
        for (String deviceId : deviceIds) {
            if (!deviceStatus.getOrDefault(deviceId, false)) {
                logger.warn("设备{}离线", deviceId);
            }
        }
        
        logger.info("完成气表数据采集检查，共检查{}个设备", deviceIds.size());
    }
    
    @Override
    public void handleDeviceReport(String deviceId, String payload) {
        try {
            logger.info("开始处理气表{}的数据报告", deviceId);
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);
            logger.debug("解析后的数据: {}", data);
            
            GasMeterData gasMeterData = convertToGasMeterData(deviceId, data);
            logger.debug("转换后的设备数据: {}", gasMeterData);
            
            if (validateData(gasMeterData)) {
                deviceStatus.put(deviceId, true);
                gasMeterDataMapper.insert(gasMeterData);
                logger.info("设备{}数据已保存到数据库", deviceId);
                
                // 检查是否是异常数据
                if (data.containsKey("data") && ((Map)data.get("data")).containsKey("is_abnormal")) {
                    boolean isAbnormal = (boolean) ((Map)data.get("data")).get("is_abnormal");
                    if (isAbnormal) {
                        logger.warn("⚠️ 设备{}报告异常状态！", deviceId);
                        logger.warn("异常数据: 流量={}, 压力={}, 温度={}", 
                            gasMeterData.getFlowRate(),
                            gasMeterData.getPressure(),
                            gasMeterData.getTemperature());
                        // TODO: 触发异常处理流程
                    }
                }
            } else {
                logger.error("设备{}数据验证失败", deviceId);
            }
        } catch (Exception e) {
            logger.error("处理设备{}上报数据失败: {}", deviceId, e.getMessage());
        }
    }
    
    @Override
    public boolean validateData(GasMeterData data) {
        if (data == null) {
            return false;
        }
        
        // 检查必要字段
        if (data.getDeviceId() == null || data.getTimestamp() == null) {
            return false;
        }
        
        // 检查数值范围
        if (data.getPressure() != null) {
            BigDecimal pressure = data.getPressure();
            if (pressure.compareTo(BigDecimal.ZERO) < 0 || pressure.compareTo(new BigDecimal("1000")) > 0) {
                return false; // 压力范围检查（0-1000kPa）
            }
        }
        
        if (data.getFlowRate() != null) {
            BigDecimal flowRate = data.getFlowRate();
            if (flowRate.compareTo(BigDecimal.ZERO) < 0) {
                return false; // 流量不能为负
            }
        }
        
        return true;
    }
    
    private GasMeterData convertToGasMeterData(String deviceId, Map<String, Object> rawData) {
        try {
            Map<String, Object> deviceData = (Map<String, Object>) rawData.get("data");
            logger.debug("设备原始数据: {}", deviceData);
            
            GasMeterData data = new GasMeterData();
            data.setDeviceId(deviceId);
            data.setTimestamp(LocalDateTime.parse(
                (String) rawData.get("timestamp"),
                DateTimeFormatter.ISO_DATE_TIME
            ));
            
            // 设置基本数据
            data.setFlowRate(new BigDecimal(deviceData.get("flow_rate").toString()));
            data.setTotalVolume(new BigDecimal(deviceData.get("total_volume").toString()));
            data.setPressure(new BigDecimal(deviceData.get("pressure").toString()));
            data.setTemperature(new BigDecimal(deviceData.get("temperature").toString()));
            
            // 设置设备状态数据
            if (deviceData.containsKey("battery_level")) {
                data.setBatteryLevel(new BigDecimal(deviceData.get("battery_level").toString()));
            }
            
            if (deviceData.containsKey("signal_strength")) {
                data.setSignalStrength((Integer) deviceData.get("signal_strength"));
            }
            
            // 设置创建时间
            data.setCreatedAt(LocalDateTime.now());
            
            return data;
        } catch (Exception e) {
            logger.error("数据转换失败: {}", e.getMessage());
            return null;
        }
    }
} 