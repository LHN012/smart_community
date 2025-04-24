package com.example.smart_community.controller;

import com.example.smart_community.entity.MaintenanceRequests;
import com.example.smart_community.service.MaintenanceRequestService;
import com.example.smart_community.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@RestController
@RequestMapping("/api/maintenance/requests")
public class MaintenanceRequestController {

    @Autowired
    private MaintenanceRequestService maintenanceRequestService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ResponseResult<Map<String, Object>>> getAllRequests() {
        List<MaintenanceRequests> requests = maintenanceRequestService.list();
        List<Integer> userIds = requests.stream()
                .map(MaintenanceRequests::getUserId)
                .collect(Collectors.toList());
        List<Integer> updateUserIds = requests.stream()
                .map(MaintenanceRequests::getUpdateUserId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        userIds.addAll(updateUserIds);
        
        Map<Integer, String> usersInfo = maintenanceRequestService.getUsersInfo(userIds);
        
        Map<String, Object> result = new HashMap<>();
        result.put("requests", requests);
        result.put("usersInfo", usersInfo);
        
        return ResponseEntity.ok(new ResponseResult<>(200, "获取成功", result));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseResult<Map<String, Object>>> getUserRequests(@PathVariable Integer userId) {
        List<MaintenanceRequests> requests = maintenanceRequestService.getUserRequests(userId);
        List<Integer> updateUserIds = requests.stream()
                .map(MaintenanceRequests::getUpdateUserId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        Map<Integer, String> usersInfo = maintenanceRequestService.getUsersInfo(updateUserIds);
        
        Map<String, Object> result = new HashMap<>();
        result.put("requests", requests);
        result.put("usersInfo", usersInfo);
        
        return ResponseEntity.ok(new ResponseResult<>(200, "获取成功", result));
    }

    @PostMapping
    public ResponseEntity<ResponseResult<MaintenanceRequests>> createRequest(@RequestBody MaintenanceRequests request) {
        try {
            boolean success = maintenanceRequestService.save(request);
            if (success) {
                return ResponseEntity.ok(new ResponseResult<>(200, "创建成功", request));
            }
            return ResponseEntity.badRequest().body(new ResponseResult<>(400, "创建失败", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseResult<>(400, "创建失败：" + e.getMessage(), null));
        }
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<ResponseResult<MaintenanceRequests>> updateRequest(
            @PathVariable Integer requestId,
            @RequestBody MaintenanceRequests request) {
        try {
            request.setRequestId(requestId);
            boolean success = maintenanceRequestService.updateById(request);
            if (success) {
                return ResponseEntity.ok(new ResponseResult<>(200, "更新成功", request));
            }
            return ResponseEntity.badRequest().body(new ResponseResult<>(400, "更新失败", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseResult<>(400, "更新失败：" + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<ResponseResult<Void>> deleteRequest(@PathVariable Integer requestId) {
        try {
            boolean success = maintenanceRequestService.removeById(requestId);
            if (success) {
                return ResponseEntity.ok(new ResponseResult<>(200, "删除成功", null));
            }
            return ResponseEntity.badRequest().body(new ResponseResult<>(400, "删除失败", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseResult<>(400, "删除失败：" + e.getMessage(), null));
        }
    }
} 