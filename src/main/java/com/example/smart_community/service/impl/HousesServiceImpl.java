package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.Houses;
import com.example.smart_community.mapper.HousesMapper;
import com.example.smart_community.service.HousesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HousesServiceImpl extends ServiceImpl<HousesMapper, Houses> implements HousesService {

    @Override
    public List<Houses> getHousesByUnitId(Integer unitId) {
        LambdaQueryWrapper<Houses> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Houses::getUnitId, unitId);
        return list(wrapper);
    }

    @Override
    public boolean isHouseNameUniqueInUnit(Integer unitId, String name) {
        LambdaQueryWrapper<Houses> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Houses::getUnitId, unitId)
               .eq(Houses::getName, name);
        return count(wrapper) == 0;
    }

    @Override
    public List<Houses> getHousesByCondition(String name, Integer unitId) {
        LambdaQueryWrapper<Houses> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Houses::getName, name);
        }
        if (unitId != null) {
            wrapper.eq(Houses::getUnitId, unitId);
        }
        return list(wrapper);
    }

    @Override
    public boolean isAreaExists(Integer areaId) {
        LambdaQueryWrapper<Houses> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Houses::getAreaId, areaId);
        return count(wrapper) > 0;
    }

    @Override
    public boolean isBuildingExists(Integer buildingId) {
        LambdaQueryWrapper<Houses> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Houses::getBuildingId, buildingId);
        return count(wrapper) > 0;
    }
}    