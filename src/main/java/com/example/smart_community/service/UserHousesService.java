package com.example.smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_community.entity.UserHouses;
import java.util.List;

public interface UserHousesService extends IService<UserHouses> {
    /**
     * 获取用户房屋关系列表，包含用户和房屋信息
     */
    List<UserHouses> listWithUserAndHouseInfo();
} 