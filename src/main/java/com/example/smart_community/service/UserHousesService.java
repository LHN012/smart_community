package com.example.smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_community.entity.UserHouses;
import java.util.List;

public interface UserHousesService extends IService<UserHouses> {
    /**
     * 获取用户房屋关系列表，包含用户和房屋信息
     */
    List<UserHouses> listWithUserAndHouseInfo();

    /**
     * 检查用户是否已经绑定过该房屋
     * @param userId 用户ID
     * @param houseId 房屋ID
     * @return 是否存在绑定关系
     */
    boolean checkUserHouseExists(Integer userId, Integer houseId);
} 