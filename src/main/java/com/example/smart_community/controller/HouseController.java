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
@RequestMapping("/api/house")
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
        Map<String, Object> result = new HashMap<>();
        try {
            List<Areas> areaList = areasService.list();
            result.put("code", 200);
            result.put("data", areaList);
        } catch (Exception e) {
            e.printStackTrace(); // 打印堆栈跟踪
            result.put("code", 500);
            result.put("msg", "获取区域列表失败：" + e.getMessage());
        }
        return result;
    }

    // 新建区域
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/area/create")
    public Map<String, Object> createArea(@RequestBody Areas area) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = areasService.save(area);
            if (success) {
                result.put("code", 200);
                result.put("msg", "新建成功");
            } else {
                result.put("code", 500);
                result.put("msg", "新建失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "新建失败：" + e.getMessage());
        }
        return result;
    }

    // 获取楼栋列表
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/building/list")
    public Map<String, Object> listBuildings() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Buildings> buildingList = buildingsService.list();
            result.put("code", 200);
            result.put("data", buildingList);
        } catch (Exception e) {
            e.printStackTrace(); // 打印堆栈跟踪
            result.put("code", 500);
            result.put("msg", "获取楼栋列表失败：" + e.getMessage());
        }
        return result;
    }

    // 新建楼栋
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/building/create")
    public Map<String, Object> createBuilding(@RequestBody Buildings building) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = buildingsService.save(building);
            if (success) {
                result.put("code", 200);
                result.put("msg", "新建成功");
            } else {
                result.put("code", 500);
                result.put("msg", "新建失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "新建失败：" + e.getMessage());
        }
        return result;
    }

    // 获取单元列表
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/unit/list")
    public Map<String, Object> listUnits() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Units> unitList = unitsService.list();
            result.put("code", 200);
            result.put("data", unitList);
        } catch (Exception e) {
            e.printStackTrace(); // 打印堆栈跟踪
            result.put("code", 500);
            result.put("msg", "获取单元列表失败：" + e.getMessage());
        }
        return result;
    }

    // 新建单元
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/unit/create")
    public Map<String, Object> createUnit(@RequestBody Units unit) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = unitsService.save(unit);
            if (success) {
                result.put("code", 200);
                result.put("msg", "新建成功");
            } else {
                result.put("code", 500);
                result.put("msg", "新建失败");
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印堆栈跟踪
            result.put("code", 500);
            result.put("msg", "新建失败：" + e.getMessage());
        }
        return result;
    }

    // 获取房屋列表
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/list")
    public Map<String, Object> listHouses(@RequestParam(required = false) String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            QueryWrapper<Houses> queryWrapper = new QueryWrapper<>();
            if (keyword != null && !keyword.isEmpty()) {
                queryWrapper.and(qw -> qw.like("name", keyword).or().like("unit_id", keyword));
            }
            List<Houses> houseList = housesService.list(queryWrapper);
            result.put("code", 200);
            result.put("data", houseList);
        } catch (Exception e) {
            e.printStackTrace(); // 打印堆栈跟踪
            result.put("code", 500);
            result.put("msg", "获取房屋列表失败：" + e.getMessage());
        }
        return result;
    }

    // 新建房屋
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/create")
    public Map<String, Object> createHouse(@RequestBody Houses house) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查区域是否存在
            if (areasService.getById(house.getAreaId()) == null) {
                result.put("code", 400);
                result.put("msg", "区域不存在");
                return result;
            }
            // 检查楼栋是否存在
            if (buildingsService.getById(house.getBuildingId()) == null) {
                result.put("code", 400);
                result.put("msg", "楼栋不存在");
                return result;
            }
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
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "新建失败：" + e.getMessage());
        }
        return result;
    }

    // 更新房屋信息
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PutMapping("/update/{houseId}")
    public Map<String, Object> updateHouse(@PathVariable Integer houseId, @RequestBody Houses house) {
        Map<String, Object> result = new HashMap<>();
        try {
            Houses dbHouse = housesService.getById(houseId);
            if (dbHouse == null) {
                result.put("code", 404);
                result.put("msg", "房屋不存在");
                return result;
            }
            // 检查区域是否存在
            if (areasService.getById(house.getAreaId()) == null) {
                result.put("code", 400);
                result.put("msg", "区域不存在");
                return result;
            }
            // 检查楼栋是否存在
            if (buildingsService.getById(house.getBuildingId()) == null) {
                result.put("code", 400);
                result.put("msg", "楼栋不存在");
                return result;
            }
            // 检查单元是否存在
            if (unitsService.getById(house.getUnitId()) == null) {
                result.put("code", 400);
                result.put("msg", "单元不存在");
                return result;
            }
            // 更新字段
            dbHouse.setAreaId(house.getAreaId());
            dbHouse.setBuildingId(house.getBuildingId());
            dbHouse.setUnitId(house.getUnitId());
            dbHouse.setName(house.getName());
            dbHouse.setSize(house.getSize());
            dbHouse.setBalance(house.getBalance());
            dbHouse.setPrice(house.getPrice());
            boolean success = housesService.updateById(dbHouse);
            if (success) {
                result.put("code", 200);
                result.put("msg", "更新成功");
            } else {
                result.put("code", 500);
                result.put("msg", "更新失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "更新失败：" + e.getMessage());
        }
        return result;
    }

    // 删除房屋
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @DeleteMapping("/delete/{houseId}")
    public Map<String, Object> deleteHouse(@PathVariable Integer houseId) {
        Map<String, Object> result = new HashMap<>();
        try {
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
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "删除失败：" + e.getMessage());
        }
        return result;
    }
}

