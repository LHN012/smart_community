package com.example.wx_smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wx_smart_community.entity.Repair;
import java.util.List;
 
public interface RepairService extends IService<Repair> {
    List<Repair> getUserRepairs(String userId);
    boolean updateRepairStatus(Long id, Integer status);
} 