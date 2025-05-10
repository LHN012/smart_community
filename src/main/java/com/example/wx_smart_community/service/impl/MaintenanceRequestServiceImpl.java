package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wx_smart_community.entity.MaintenanceRequest;
import com.example.wx_smart_community.mapper.MaintenanceRequestMapper;
import com.example.wx_smart_community.service.MaintenanceRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MaintenanceRequestServiceImpl extends ServiceImpl<MaintenanceRequestMapper, MaintenanceRequest>
        implements MaintenanceRequestService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceRequestServiceImpl.class);

    @Override
    @Transactional
    public MaintenanceRequest createRequest(MaintenanceRequest request) {
        try {
            request.setStatus("待处理");
            save(request);
            return request;
        } catch (Exception e) {
            logger.error("创建报修请求失败", e);
            throw new RuntimeException("创建报修请求失败：" + e.getMessage());
        }
    }

    @Override
    public List<MaintenanceRequest> getRequestsByUserId(Integer userId) {
        try {
            logger.info("正在查询用户ID为 {} 的报修请求", userId);
            List<MaintenanceRequest> requests = lambdaQuery()
                    .eq(MaintenanceRequest::getUserId, userId)
                    .orderByDesc(MaintenanceRequest::getCreatedAt)
                    .list();
            logger.info("查询到 {} 条报修请求", requests.size());
            return requests;
        } catch (Exception e) {
            logger.error("获取用户报修请求列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取报修请求列表失败：" + e.getMessage());
        }
    }

    @Override
    public MaintenanceRequest getRequestById(Integer id) {
        try {
            return getById(id);
        } catch (Exception e) {
            logger.error("获取报修请求详情失败", e);
            throw new RuntimeException("获取报修请求详情失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public MaintenanceRequest updateRequestStatus(Integer id, String status) {
        try {
            MaintenanceRequest request = getById(id);
            if (request == null) {
                throw new RuntimeException("报修请求不存在");
            }
            request.setStatus(status);
            updateById(request);
            return request;
        } catch (Exception e) {
            logger.error("更新报修状态失败", e);
            throw new RuntimeException("更新报修状态失败：" + e.getMessage());
        }
    }
} 