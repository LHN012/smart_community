package com.example.smart_community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_community.entity.Units;

import java.util.List;

public interface UnitsService extends IService<Units> {

    /**
     * 根据楼栋ID获取单元列表
     * @param buildingId 楼栋ID
     * @return 单元列表
     */
    List<Units> getUnitsByBuildingId(Integer buildingId);

    /**
     * 检查指定楼栋下的单元编号是否唯一
     * @param buildingId 楼栋ID
     * @param unitNo 单元编号
     * @return 如果唯一返回true，否则返回false
     */
    boolean isUnitNoUniqueInBuilding(Integer buildingId, String unitNo);
}    