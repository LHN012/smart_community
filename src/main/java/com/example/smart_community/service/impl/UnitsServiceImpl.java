package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.Units;
import com.example.smart_community.mapper.UnitsMapper;
import com.example.smart_community.service.UnitsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitsServiceImpl extends ServiceImpl<UnitsMapper, Units> implements UnitsService {

    @Override
    public List<Units> getUnitsByBuildingId(Integer buildingId) {
        QueryWrapper<Units> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buildingId", buildingId);
        return this.list(queryWrapper);
    }

    @Override
    public boolean isUnitNoUniqueInBuilding(Integer buildingId, String unitNo) {
        QueryWrapper<Units> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buildingId", buildingId).eq("unitNo", unitNo);
        return this.count(queryWrapper) == 0;
    }
}    