package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.model.House;
import com.example.wx_smart_community.service.HouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    @GetMapping("/houses")
    public ResponseEntity<List<House>> getUserHouses(@RequestParam Integer userId) {
        log.info("获取用户房屋列表，userId: {}", userId);
        List<House> houses = houseService.getHousesByUserId(userId);
        return ResponseEntity.ok(houses);
    }
} 