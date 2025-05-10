package com.example.wx_smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wx_smart_community.entity.MaintenanceRequest;
import java.util.List;

public interface MaintenanceRequestService extends IService<MaintenanceRequest> {
    MaintenanceRequest createRequest(MaintenanceRequest request);
    List<MaintenanceRequest> getRequestsByUserId(Integer userId);
    MaintenanceRequest getRequestById(Integer id);
    MaintenanceRequest updateRequestStatus(Integer id, String status);
} 