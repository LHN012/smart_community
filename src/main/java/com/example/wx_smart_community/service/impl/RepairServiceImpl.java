package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wx_smart_community.entity.Repair;
import com.example.wx_smart_community.mapper.RepairMapper;
import com.example.wx_smart_community.service.RepairService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {
    
    @Override
    public List<Repair> getUserRepairs(String userId) {
        QueryWrapper<Repair> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public boolean updateRepairStatus(Long id, Integer status) {
        Repair repair = getById(id);
        if (repair != null) {
            repair.setStatus(status);
            return updateById(repair);
        }
        return false;
    }
} 