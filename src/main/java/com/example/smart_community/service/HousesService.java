package com.example.smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_community.entity.Houses;

import java.util.List;

public interface HousesService extends IService<Houses> {

    /**
     * 根据单元ID获取房屋列表
     * @param unitId 单元ID
     * @return 房屋列表
     */
    List<Houses> getHousesByUnitId(Integer unitId);

    /**
     * 检查指定单元下的房屋编号是否唯一
     * @param unitId 单元ID
     * @param houseNo 房屋编号
     * @return 如果唯一返回true，否则返回false
     */
    boolean isHouseNoUniqueInUnit(Integer unitId, String houseNo);
}    