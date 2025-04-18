package com.example.smart_community.controller;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:49
 */
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.smart_community.entity.*;
import com.example.smart_community.service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/house")
public class HouseController {

    @Resource
    private AreasService areasService;
    @Resource
    private BuildingsService buildingsService;
    @Resource
    private UnitsService unitsService;
    @Resource
    private HousesService housesService;

    // 获取区域列表
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/area/list")
    public Map<String, Object> listAreas() {
        List<Areas> areaList = areasService.list();
        Map<String, Object> result = new HashMap<>();
        result.put("data", areaList);
        return result;
    }

    // 获取楼栋列表（根据区域ID）
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/building/list")
    public Map<String, Object> listBuildings(@RequestParam Integer areaId) {
        QueryWrapper<Buildings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("areaId", areaId);
        List<Buildings> buildingList = buildingsService.list(queryWrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("data", buildingList);
        return result;
    }

    // 获取单元列表（根据楼栋ID）
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/unit/list")
    public Map<String, Object> listUnits(@RequestParam Integer buildingId) {
        QueryWrapper<Units> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buildingId", buildingId);
        List<Units> unitList = unitsService.list(queryWrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("data", unitList);
        return result;
    }

    // 获取房屋列表
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/list")
    public Map<String, Object> listHouses(@RequestParam(required = false) String keyword) {
        QueryWrapper<Houses> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(qw -> qw.like("houseNo", keyword).or().like("unitId", keyword));
        }
        List<Houses> houseList = housesService.list(queryWrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("data", houseList);
        result.put("total", houseList.size());
        return result;
    }

    // 新建房屋
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/create")
    public Map<String, Object> createHouse(@RequestBody Houses house) {
        Map<String, Object> result = new HashMap<>();
        // 检查单元是否存在
        if (unitsService.getById(house.getUnitId()) == null) {
            result.put("code", 400);
            result.put("msg", "单元不存在");
            return result;
        }
        boolean success = housesService.save(house);
        if (success) {
            result.put("code", 200);
            result.put("msg", "新建成功");
        } else {
            result.put("code", 500);
            result.put("msg", "新建失败");
        }
        return result;
    }

    // 更新房屋信息
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PutMapping("/update/{houseId}")
    public Map<String, Object> updateHouse(@PathVariable Integer houseId, @RequestBody Houses house) {
        Map<String, Object> result = new HashMap<>();
        Houses dbHouse = housesService.getById(houseId);
        if (dbHouse == null) {
            result.put("code", 404);
            result.put("msg", "房屋不存在");
            return result;
        }
        // 更新字段
        dbHouse.setUnitId(house.getUnitId());
        dbHouse.setSize(house.getSize());
        dbHouse.setBalance(house.getBalance());
        boolean success = housesService.updateById(dbHouse);
        if (success) {
            result.put("code", 200);
            result.put("msg", "更新成功");
        } else {
            result.put("code", 500);
            result.put("msg", "更新失败");
        }
        return result;
    }

    // 删除房屋
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @DeleteMapping("/delete/{houseId}")
    public Map<String, Object> deleteHouse(@PathVariable Integer houseId) {
        Map<String, Object> result = new HashMap<>();
        Houses dbHouse = housesService.getById(houseId);
        if (dbHouse == null) {
            result.put("code", 404);
            result.put("msg", "房屋不存在");
            return result;
        }
        boolean success = housesService.removeById(houseId);
        if (success) {
            result.put("code", 200);
            result.put("msg", "删除成功");
        } else {
            result.put("code", 500);
            result.put("msg", "删除失败");
        }
        return result;
    }
}

