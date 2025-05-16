package com.example.wx_smart_community.service.impl;

import com.example.wx_smart_community.entity.ElectricMeterData;
import com.example.wx_smart_community.service.ElectricMeterDataCollectionService;
import com.example.wx_smart_community.mapper.ElectricMeterDataMapper;
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
public class ElectricMeterDataCollectionServiceImpl implements ElectricMeterDataCollectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(ElectricMeterDataCollectionServiceImpl.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ElectricMeterDataMapper electricMeterDataMapper;
    
    // 设备连接状态缓存
    private final Map<String, Boolean> deviceStatus = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        logger.info("初始化电表数据采集服务");
    }
    
    @Override
    public ElectricMeterData collectRealTimeData(String deviceId) {
        try {
            return electricMeterDataMapper.findLatestByDeviceId(deviceId);
        } catch (Exception e) {
            logger.error("从设备{}采集数据失败", deviceId, e);
            return null;
        }
    }
    
    @Override
    public List<ElectricMeterData> batchCollectRealTimeData(List<String> deviceIds) {
        List<ElectricMeterData> results = new ArrayList<>();
        for (String deviceId : deviceIds) {
            ElectricMeterData data = collectRealTimeData(deviceId);
            if (data != null) {
                results.add(data);
            }
        }
        return results;
    }
    
    @Override
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void scheduleDataCollection() {
        logger.info("开始定时采集电表数据...");
        List<String> deviceIds = electricMeterDataMapper.findActiveDeviceIds(
            LocalDateTime.now().minusHours(1)
        );
        
        for (String deviceId : deviceIds) {
            if (!deviceStatus.getOrDefault(deviceId, false)) {
                logger.warn("设备{}离线", deviceId);
            }
        }
        
        logger.info("完成电表数据采集检查，共检查{}个设备", deviceIds.size());
    }
    
    @Override
    public void handleDeviceReport(String deviceId, String payload) {
        try {
            logger.info("开始处理电表{}的数据报告", deviceId);
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);
            logger.debug("解析后的数据: {}", data);
            
            ElectricMeterData electricMeterData = convertToElectricMeterData(deviceId, data);
            logger.debug("转换后的设备数据: {}", electricMeterData);
            
            if (validateData(electricMeterData)) {
                deviceStatus.put(deviceId, true);
                electricMeterDataMapper.insert(electricMeterData);
                logger.info("设备{}数据已保存到数据库", deviceId);
            } else {
                logger.error("设备{}数据验证失败", deviceId);
            }
        } catch (Exception e) {
            logger.error("处理设备{}上报数据失败: {}", deviceId, e.getMessage());
        }
    }
    
    @Override
    public boolean validateData(ElectricMeterData data) {
        if (data == null) {
            return false;
        }
        
        // 检查必要字段
        if (data.getDeviceId() == null || data.getTimestamp() == null) {
            return false;
        }
        
        // 检查数值范围
        if (data.getVoltage() != null) {
            BigDecimal voltage = data.getVoltage();
            if (voltage.compareTo(new BigDecimal("180")) < 0 || voltage.compareTo(new BigDecimal("250")) > 0) {
                return false; // 电压范围检查（180-250V）
            }
        }
        
        if (data.getCurrentPower() != null) {
            BigDecimal power = data.getCurrentPower();
            if (power.compareTo(BigDecimal.ZERO) < 0) {
                return false; // 功率不能为负
            }
        }
        
        return true;
    }
    
    private ElectricMeterData convertToElectricMeterData(String deviceId, Map<String, Object> rawData) {
        try {
            Map<String, Object> deviceData = (Map<String, Object>) rawData.get("data");
            logger.debug("设备原始数据: {}", deviceData);
            
            ElectricMeterData data = new ElectricMeterData();
            data.setDeviceId(deviceId);
            data.setTimestamp(LocalDateTime.parse(
                (String) rawData.get("timestamp"),
                DateTimeFormatter.ISO_DATE_TIME
            ));
            
            // 设置基本数据
            data.setCurrentPower(new BigDecimal(deviceData.get("current_power").toString()));
            data.setTotalEnergy(new BigDecimal(deviceData.get("total_energy").toString()));
            data.setVoltage(new BigDecimal(deviceData.get("voltage").toString()));
            data.setCurrent(new BigDecimal(deviceData.get("current").toString()));
            data.setPowerFactor(new BigDecimal(deviceData.get("power_factor").toString()));
            data.setSignalStrength((Integer) deviceData.get("signal_strength"));
            
            // 设置创建时间
            data.setCreatedAt(LocalDateTime.now());
            
            return data;
        } catch (Exception e) {
            logger.error("数据转换失败: {}", e.getMessage());
            return null;
        }
    }
} 