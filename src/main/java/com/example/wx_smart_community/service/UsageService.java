package com.example.wx_smart_community.service;

import com.example.wx_smart_community.entity.Device;
import com.example.wx_smart_community.entity.MeterData;
import com.example.wx_smart_community.mapper.DeviceMapper;
import com.example.wx_smart_community.mapper.MeterDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsageService {
    private static final Logger logger = LoggerFactory.getLogger(UsageService.class);

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private MeterDataMapper meterDataMapper;

    // 获取房屋的设备列表
    public List<Device> getHouseDevices(Integer houseId) {
        logger.info("开始获取房屋设备列表，houseId: {}", houseId);
        List<Device> devices = deviceMapper.findByHouseId(houseId);
        logger.info("获取到设备列表: {}", devices);
        return devices;
    }

    // 获取设备最新数据
    public MeterData getDeviceLatestData(String deviceId, String type, String month) {
        logger.info("开始获取设备最新数据，deviceId: {}, type: {}, month: {}", deviceId, type, month);
        String tableName = getTableNameByType(type);
        logger.info("对应的表名: {}", tableName);
        
        MeterData data = meterDataMapper.findLatestByDeviceIdAndMonth(tableName, deviceId, month);
        logger.info("获取到设备数据: {}", data);
        return data;
    }

    // 根据设备类型获取对应的表名
    private String getTableNameByType(String type) {
        String tableName;
        switch (type) {
            case "water":
                tableName = "water_meter_data";
                break;
            case "electric":
                tableName = "electric_meter_data";
                break;
            case "gas":
                tableName = "gas_meter_data";
                break;
            default:
                logger.error("无效的设备类型: {}", type);
                throw new IllegalArgumentException("Invalid device type: " + type);
        }
        return tableName;
    }
} 