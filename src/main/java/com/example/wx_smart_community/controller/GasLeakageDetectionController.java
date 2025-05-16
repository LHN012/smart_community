package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.algorithm.GasLeakageDetector.DetectionResult;
import com.example.wx_smart_community.entity.GasMeterData;
import com.example.wx_smart_community.entity.WarningRule;
import com.example.wx_smart_community.service.GasLeakageDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gas-detection")
public class GasLeakageDetectionController {

    @Autowired
    private GasLeakageDetectionService gasLeakageDetectionService;

    @GetMapping("/detect/{deviceId}")
    public ResponseEntity<List<DetectionResult>> detectLeakage(@PathVariable String deviceId) {
        List<DetectionResult> results = gasLeakageDetectionService.detectLeakage(deviceId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/historical-data/{deviceId}")
    public ResponseEntity<List<GasMeterData>> getHistoricalData(
            @PathVariable String deviceId,
            @RequestParam(defaultValue = "24") int hours) {
        List<GasMeterData> data = gasLeakageDetectionService.getHistoricalData(deviceId, hours);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/rules/{deviceId}")
    public ResponseEntity<List<WarningRule>> getDeviceWarningRules(@PathVariable String deviceId) {
        List<WarningRule> rules = gasLeakageDetectionService.getDeviceWarningRules(deviceId);
        return ResponseEntity.ok(rules);
    }
} 