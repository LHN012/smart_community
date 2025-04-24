package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.MaintenanceRequests;
import com.example.smart_community.entity.Users;
import com.example.smart_community.mapper.MaintenanceRequestsMapper;
import com.example.smart_community.mapper.UsersMapper;
import com.example.smart_community.service.MaintenanceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
public class MaintenanceRequestServiceImpl extends ServiceImpl<MaintenanceRequestsMapper, MaintenanceRequests> implements MaintenanceRequestService {
    
    @Autowired
    private UsersMapper usersMapper;
    
    @Override
    public List<MaintenanceRequests> getUserRequests(Integer userId) {
        QueryWrapper<MaintenanceRequests> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return list(queryWrapper);
    }

    @Override
    public Map<Integer, String> getUsersInfo(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        List<Users> users = usersMapper.selectList(queryWrapper);
        return users.stream()
                .collect(Collectors.toMap(
                        Users::getUserId,
                        Users::getUsername,
                        (existing, replacement) -> existing
                ));
    }
} 