package com.example.smart_community.controller;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:49
 */
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.smart_community.entity.*;
import com.example.smart_community.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    @Resource
    private HouseService houseService;

    private static final Logger logger = LoggerFactory.getLogger(HouseController.class);

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
    public Map<String, Object> listBuildings(@RequestParam(required = false) Integer areaId) {
        Map<String, Object> result = new HashMap<>();
        try {
            QueryWrapper<Buildings> queryWrapper = new QueryWrapper<>();
            if (areaId != null) {
                queryWrapper.eq("area_id", areaId);
            }
            List<Buildings> buildingList = buildingsService.list(queryWrapper);
            result.put("code", 200);
            result.put("data", buildingList);
        } catch (Exception e) {
            e.printStackTrace();
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
    public Map<String, Object> listHouses(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Integer unitId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Houses> houseList;
            if (unitId != null) {
                houseList = housesService.getHousesByUnitId(unitId);
            } else if (keyword != null && !keyword.isEmpty()) {
                houseList = housesService.getHousesByCondition(keyword, null);
            } else {
                houseList = housesService.listWithAreaAndBuildingAndUnit();
            }
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
            // 更新字段（不更新余额）
            dbHouse.setAreaId(house.getAreaId());
            dbHouse.setBuildingId(house.getBuildingId());
            dbHouse.setUnitId(house.getUnitId());
            dbHouse.setName(house.getName());
            dbHouse.setSize(house.getSize());
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

    @GetMapping("/areas")
    public Map<String, Object> getAreas() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> areas = houseService.getAreas();
            result.put("code", 200);
            result.put("data", areas);
        } catch (Exception e) {
            logger.error("获取区域列表失败", e);
            result.put("code", 500);
            result.put("msg", "获取区域列表失败：" + e.getMessage());
        }
        return result;
    }

    @GetMapping("/buildings/{areaId}")
    public Map<String, Object> getBuildingsByArea(@PathVariable Integer areaId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> buildings = houseService.getBuildingsByArea(areaId);
            result.put("code", 200);
            result.put("data", buildings);
        } catch (Exception e) {
            logger.error("获取楼栋列表失败", e);
            result.put("code", 500);
            result.put("msg", "获取楼栋列表失败：" + e.getMessage());
        }
        return result;
    }

    @GetMapping("/units/{buildingId}")
    public Map<String, Object> getUnitsByBuilding(@PathVariable Integer buildingId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> units = houseService.getUnitsByBuilding(buildingId);
            result.put("code", 200);
            result.put("data", units);
        } catch (Exception e) {
            logger.error("获取单元列表失败", e);
            result.put("code", 500);
            result.put("msg", "获取单元列表失败：" + e.getMessage());
        }
        return result;
    }

    @GetMapping("/houses/{unitId}")
    public Map<String, Object> getHousesByUnit(@PathVariable Integer unitId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> houses = houseService.getHousesByUnit(unitId);
            result.put("code", 200);
            result.put("data", houses);
        } catch (Exception e) {
            logger.error("获取房屋列表失败", e);
            result.put("code", 500);
            result.put("msg", "获取房屋列表失败：" + e.getMessage());
        }
        return result;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/extract")
    public Map<String, Object> extractBalance(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer houseId = (Integer) params.get("houseId");
            Integer userId = (Integer) params.get("userId");
            Double extractAmount = ((Number) params.get("extractAmount")).doubleValue();

            if (houseId == null || userId == null || extractAmount == null) {
                result.put("code", 400);
                result.put("msg", "参数不完整");
                return result;
            }

            boolean success = housesService.extractBalance(houseId, userId, new BigDecimal(extractAmount));
            if (success) {
                result.put("code", 200);
                result.put("msg", "提取成功");
            } else {
                result.put("code", 500);
                result.put("msg", "提取失败");
            }
        } catch (Exception e) {
            logger.error("提取房屋余额失败", e);
            result.put("code", 500);
            result.put("msg", "提取失败：" + e.getMessage());
        }
        return result;
    }
}

