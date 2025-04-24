package com.example.smart_community.service;

import com.example.smart_community.mapper.HouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HouseService {
    @Autowired
    private HouseMapper houseMapper;

    public List<Map<String, Object>> getAreas() {
        return houseMapper.selectAreas();
    }

    public List<Map<String, Object>> getBuildingsByArea(Integer areaId) {
        return houseMapper.selectBuildingsByArea(areaId);
    }

    public List<Map<String, Object>> getUnitsByBuilding(Integer buildingId) {
        return houseMapper.selectUnitsByBuilding(buildingId);
    }

    public List<Map<String, Object>> getHousesByUnit(Integer unitId) {
        return houseMapper.selectHousesByUnit(unitId);
    }
} 