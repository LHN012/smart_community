package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wx_smart_community.mapper.HouseMapper;
import com.example.wx_smart_community.model.House;
import com.example.wx_smart_community.service.HouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {

    @Override
    public List<House> getHousesByUserId(Integer userId) {
        log.info("获取用户房屋列表，userId: {}", userId);
        return baseMapper.getHousesByUserId(userId);
    }
} 