package com.example.smart_community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smart_community.entity.Houses;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface HousesMapper extends BaseMapper<Houses> {
    
    @Select("SELECT h.*, " +
            "a.name as area_name, " +
            "b.name as building_name, " +
            "u.name as unit_name " +
            "FROM houses h " +
            "LEFT JOIN areas a ON h.area_id = a.area_id " +
            "LEFT JOIN buildings b ON h.building_id = b.building_id " +
            "LEFT JOIN units u ON h.unit_id = u.unit_id")
    List<Houses> selectListWithAreaAndBuildingAndUnit();
    
    @Select("SELECT h.house_id, h.area_id, h.building_id, h.unit_id, h.name, h.price, h.size, h.balance, " +
            "a.name as area_name, " +
            "b.name as building_name, " +
            "u.name as unit_name " +
            "FROM houses h " +
            "LEFT JOIN areas a ON h.area_id = a.area_id " +
            "LEFT JOIN buildings b ON h.building_id = b.building_id " +
            "LEFT JOIN units u ON h.unit_id = u.unit_id " +
            "WHERE h.house_id = #{houseId}")
    Houses selectBasicById(Integer houseId);
}    