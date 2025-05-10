package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.entity.Repair;
import com.example.wx_smart_community.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @PostMapping("/submit")
    public Map<String, Object> submitRepair(@RequestBody Repair repair) {
        repair.setStatus(0); // 设置初始状态为待处理
        boolean success = repairService.save(repair);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return result;
    }

    @GetMapping("/list")
    public List<Repair> getUserRepairs(@RequestParam String userId) {
        return repairService.getUserRepairs(userId);
    }

    @PostMapping("/updateStatus")
    public Map<String, Object> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        boolean success = repairService.updateRepairStatus(id, status);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return result;
    }
} 