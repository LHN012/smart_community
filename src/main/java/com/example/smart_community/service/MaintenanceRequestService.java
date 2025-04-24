package com.example.smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_community.entity.MaintenanceRequests;
import com.example.smart_community.entity.Users;

import java.util.List;
import java.util.Map;

public interface MaintenanceRequestService extends IService<MaintenanceRequests> {
    List<MaintenanceRequests> getUserRequests(Integer userId);
    Map<Integer, String> getUsersInfo(List<Integer> userIds);
} 