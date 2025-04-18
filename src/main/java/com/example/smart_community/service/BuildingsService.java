package com.example.smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_community.entity.Buildings;

import java.util.List;

public interface BuildingsService extends IService<Buildings> {

    /**
     * 根据区域ID获取楼栋列表
     * @param areaId 区域ID
     * @return 楼栋列表
     */
    List<Buildings> getBuildingsByAreaId(Integer areaId);

    /**
     * 检查指定区域下的楼栋编号是否唯一
     * @param areaId 区域ID
     * @param buildingNo 楼栋编号
     * @return 如果唯一返回true，否则返回false
     */
    boolean isBuildingNoUniqueInArea(Integer areaId, String buildingNo);
}    