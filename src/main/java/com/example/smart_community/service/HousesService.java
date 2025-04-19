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
     * 检查房屋名称在单元内是否唯一
     * @param unitId 单元ID
     * @param name 房屋名称
     * @return 是否唯一
     */
    boolean isHouseNameUniqueInUnit(Integer unitId, String name);

    /**
     * 根据条件查询房屋列表
     * @param name 房屋名称
     * @param unitId 单元ID
     * @return 房屋列表
     */
    List<Houses> getHousesByCondition(String name, Integer unitId);

    /**
     * 检查区域是否存在
     * @param areaId 区域ID
     * @return 是否存在
     */
    boolean isAreaExists(Integer areaId);

    /**
     * 检查楼栋是否存在
     * @param buildingId 楼栋ID
     * @return 是否存在
     */
    boolean isBuildingExists(Integer buildingId);
}    