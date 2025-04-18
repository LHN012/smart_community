package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.Buildings;
import com.example.smart_community.mapper.BuildingsMapper;
import com.example.smart_community.service.BuildingsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingsServiceImpl extends ServiceImpl<BuildingsMapper, Buildings> implements BuildingsService {

    @Override
    public List<Buildings> getBuildingsByAreaId(Integer areaId) {
        QueryWrapper<Buildings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("areaId", areaId);
        // 明确调用父类的 getBaseMapper() 方法
        return getBaseMapper().selectList(queryWrapper);
    }

    @Override
    public boolean isBuildingNoUniqueInArea(Integer areaId, String buildingNo) {
        QueryWrapper<Buildings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("areaId", areaId).eq("buildingNo", buildingNo);
        // 明确调用父类的 getBaseMapper() 方法
        return getBaseMapper().selectCount(queryWrapper) == 0;
    }
}    