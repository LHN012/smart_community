package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.entity.Device;
import com.example.wx_smart_community.entity.MeterData;
import com.example.wx_smart_community.model.HistoricalUsage;
import com.example.wx_smart_community.service.HistoricalUsageService;
import com.example.wx_smart_community.service.UsageService;
import com.example.wx_smart_community.utils.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UsageController {
    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);

    private final UsageService usageService;
    private final HistoricalUsageService historicalUsageService;

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

    @GetMapping("/usage/historical")
    public ResponseEntity<List<HistoricalUsage>> getHistoricalUsage(
            @RequestParam Integer houseId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String type) {
        log.info("获取历史用量数据，houseId: {}, month: {}, type: {}", houseId, month, type);
        List<HistoricalUsage> data = historicalUsageService.getHistoricalUsage(houseId, month, type);
        return ResponseEntity.ok(data);
    }
} 