package com.example.smart_community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface HouseMapper {
    @Select("SELECT DISTINCT area_id as id, name FROM areas")
    List<Map<String, Object>> selectAreas();

    @Select("SELECT DISTINCT building_id as id, name FROM buildings WHERE area_id = #{areaId}")
    List<Map<String, Object>> selectBuildingsByArea(Integer areaId);

    @Select("SELECT DISTINCT unit_id as id, name FROM units WHERE building_id = #{buildingId}")
    List<Map<String, Object>> selectUnitsByBuilding(Integer buildingId);

    @Select("SELECT house_id as id, name FROM houses WHERE unit_id = #{unitId}")
    List<Map<String, Object>> selectHousesByUnit(Integer unitId);
} 