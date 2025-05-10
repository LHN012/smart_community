package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.entity.MaintenanceRequest;
import com.example.wx_smart_community.service.MaintenanceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-requests")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
public class MaintenanceRequestController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceRequestController.class);

    @Autowired
    private MaintenanceRequestService maintenanceRequestService;

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody MaintenanceRequest request) {
        try {
            logger.info("创建报修请求: {}", request);
            MaintenanceRequest savedRequest = maintenanceRequestService.createRequest(request);
            return ResponseEntity.ok(savedRequest);
        } catch (Exception e) {
            logger.error("创建报修请求失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("创建报修请求失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRequestsByUserId(@PathVariable("userId") Integer userId) {
        try {
            logger.info("获取用户报修请求列表, userId: {}", userId);
            if (userId == null) {
                return ResponseEntity.badRequest().body("用户ID不能为空");
            }
            List<MaintenanceRequest> requests = maintenanceRequestService.getRequestsByUserId(userId);
            logger.info("获取到 {} 条报修请求", requests.size());
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("获取用户报修请求列表失败, userId: {}, error: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("获取报修请求失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Integer id) {
        try {
            logger.info("获取报修请求详情, id: {}", id);
            MaintenanceRequest request = maintenanceRequestService.getRequestById(id);
            if (request == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            logger.error("获取报修请求详情失败, id: {}, error: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("获取报修请求失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        try {
            logger.info("更新报修状态, id: {}, status: {}", id, status);
            MaintenanceRequest updatedRequest = maintenanceRequestService.updateRequestStatus(id, status);
            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            logger.error("更新报修状态失败, id: {}, status: {}, error: {}", id, status, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("更新报修状态失败：" + e.getMessage());
        }
    }
} 