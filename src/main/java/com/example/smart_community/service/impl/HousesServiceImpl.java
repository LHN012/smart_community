package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        QueryWrapper<Houses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unitId", unitId);
        return this.list(queryWrapper);
    }

    @Override
    public boolean isHouseNoUniqueInUnit(Integer unitId, String houseNo) {
        QueryWrapper<Houses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unitId", unitId).eq("houseNo", houseNo);
        return this.count(queryWrapper) == 0;
    }
}    