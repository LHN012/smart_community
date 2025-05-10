package com.example.wx_smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wx_smart_community.model.House;
import java.util.List;

public interface HouseService extends IService<House> {
    List<House> getHousesByUserId(Integer userId);
} 