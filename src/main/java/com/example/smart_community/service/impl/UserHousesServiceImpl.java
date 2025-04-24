package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.UserHouses;
import com.example.smart_community.mapper.UserHousesMapper;
import com.example.smart_community.service.UserHousesService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserHousesServiceImpl extends ServiceImpl<UserHousesMapper, UserHouses> implements UserHousesService {

    @Override
    public List<UserHouses> listWithUserAndHouseInfo() {
        return baseMapper.selectListWithUserAndHouseInfo();
    }

    @Override
    public boolean checkUserHouseExists(Integer userId, Integer houseId) {
        LambdaQueryWrapper<UserHouses> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserHouses::getUserId, userId)
               .eq(UserHouses::getHouseId, houseId);
        return count(wrapper) > 0;
    }
} 