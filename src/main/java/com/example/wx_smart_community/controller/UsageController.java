package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.entity.Device;
import com.example.wx_smart_community.entity.MeterData;
import com.example.wx_smart_community.service.UsageService;
import com.example.wx_smart_community.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsageController {
    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);

    @Autowired
    private UsageService usageService;

    // 获取房屋设备列表
    @GetMapping("/devices/house/{houseId}")
    public Result<List<Device>> getHouseDevices(@PathVariable Integer houseId) {
        logger.info("获取房屋设备列表，houseId: {}", houseId);
        try {
            List<Device> devices = usageService.getHouseDevices(houseId);
            logger.info("获取到设备列表: {}", devices);
            return Result.success(devices);
        } catch (Exception e) {
            logger.error("获取设备列表失败", e);
            return Result.error("获取设备列表失败：" + e.getMessage());
        }
    }

    // 获取设备最新数据
    @GetMapping("/meter/{type}/latest")
    public Result<MeterData> getDeviceLatestData(
            @PathVariable String type,
            @RequestParam String deviceId,
            @RequestParam String month) {
        logger.info("获取设备最新数据，type: {}, deviceId: {}, month: {}", type, deviceId, month);
        try {
            MeterData data = usageService.getDeviceLatestData(deviceId, type, month);
            logger.info("获取到设备数据: {}", data);
            return Result.success(data);
        } catch (Exception e) {
            logger.error("获取设备数据失败", e);
            return Result.error("获取设备数据失败：" + e.getMessage());
        }
    }
} 