package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.entity.PriceSetting;
import com.example.wx_smart_community.service.PriceSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price-settings")
public class PriceSettingController {

    @Autowired
    private PriceSettingService priceSettingService;

    @GetMapping("/{type}")
    public ResponseEntity<List<PriceSetting>> getCurrentValidSettings(@PathVariable String type) {
        return ResponseEntity.ok(priceSettingService.getCurrentValidSettings(type));
    }

    @GetMapping("/{type}/history")
    public ResponseEntity<List<PriceSetting>> getHistorySettings(@PathVariable String type) {
        return ResponseEntity.ok(priceSettingService.getHistorySettings(type));
    }

    @PostMapping
    public ResponseEntity<Boolean> createPriceSetting(@RequestBody PriceSetting priceSetting) {
        return ResponseEntity.ok(priceSettingService.save(priceSetting));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updatePriceSetting(@PathVariable("id") Integer id, 
                                                    @RequestBody PriceSetting priceSetting) {
        priceSetting.setPriceId(id);
        return ResponseEntity.ok(priceSettingService.updateById(priceSetting));
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<Boolean> enableSetting(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(priceSettingService.enableSetting(id));
    }

    @PostMapping("/{id}/disable")
    public ResponseEntity<Boolean> disableSetting(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(priceSettingService.disableSetting(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletePriceSetting(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(priceSettingService.removeById(id));
    }
} 