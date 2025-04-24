package com.example.smart_community.controller;

import com.example.smart_community.common.Result;
import com.example.smart_community.entity.Houses;
import com.example.smart_community.service.HousesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/houses")
public class HousesController {

    private static final Logger logger = LoggerFactory.getLogger(HousesController.class);

    @Autowired
    private HousesService housesService;

    /**
     * 获取所有房屋信息列表
     */
    @GetMapping("/list")
    public Result<List<Houses>> list() {
        List<Houses> list = housesService.listWithAreaAndBuildingAndUnit();
        return Result.success(list);
    }

    /**
     * 根据条件查询房屋列表
     */
    @GetMapping("/search")
    public Result<List<Houses>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer unitId) {
        List<Houses> list = housesService.getHousesByCondition(name, unitId);
        return Result.success(list);
    }

    /**
     * 添加房屋
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Houses house) {
        if (!housesService.isHouseNameUniqueInUnit(house.getUnitId(), house.getName())) {
            return Result.error("该单元下已存在同名房屋");
        }
        if (!housesService.isAreaExists(house.getAreaId())) {
            return Result.error("区域不存在");
        }
        if (!housesService.isBuildingExists(house.getBuildingId())) {
            return Result.error("楼栋不存在");
        }
        boolean success = housesService.save(house);
        if (success) {
            return Result.success("添加成功");
        } else {
            return Result.error("添加失败");
        }
    }

    /**
     * 更新房屋信息
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Houses house) {
        if (!housesService.isHouseNameUniqueInUnit(house.getUnitId(), house.getName())) {
            return Result.error("该单元下已存在同名房屋");
        }
        if (!housesService.isAreaExists(house.getAreaId())) {
            return Result.error("区域不存在");
        }
        if (!housesService.isBuildingExists(house.getBuildingId())) {
            return Result.error("楼栋不存在");
        }
        boolean success = housesService.updateById(house);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 删除房屋
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        boolean success = housesService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 房屋充值
     */
    @PostMapping("/recharge")
    public Result<String> recharge(@RequestBody Map<String, Object> params) {
        try {
            logger.info("收到充值请求，参数：{}", params);
            
            Integer houseId = (Integer) params.get("houseId");
            Integer userId = (Integer) params.get("userId");
            Number rechargeAmountNumber = (Number) params.get("rechargeAmount");
            Double rechargeAmount = rechargeAmountNumber.doubleValue();
            String rechargeChannel = (String) params.get("rechargeChannel");
            String paymentMethod = (String) params.get("paymentMethod");
            Integer userRole = (Integer) params.get("userRole");

            // 如果是管理员（role=2），则充值方式固定为线下
            if (userRole != null && userRole == 2) {
                rechargeChannel = "线下";
            }

            logger.info("解析后的参数 - houseId: {}, userId: {}, rechargeAmount: {}, rechargeChannel: {}, paymentMethod: {}, userRole: {}", 
                    houseId, userId, rechargeAmount, rechargeChannel, paymentMethod, userRole);

            // 参数验证
            if (houseId == null || userId == null || rechargeAmount == null || 
                rechargeChannel == null || paymentMethod == null) {
                logger.error("参数不完整 - houseId: {}, userId: {}, rechargeAmount: {}, rechargeChannel: {}, paymentMethod: {}", 
                        houseId, userId, rechargeAmount, rechargeChannel, paymentMethod);
                return Result.error("参数不完整");
            }

            if (rechargeAmount <= 0) {
                logger.error("充值金额必须大于0 - rechargeAmount: {}", rechargeAmount);
                return Result.error("充值金额必须大于0");
            }

            boolean success = housesService.recharge(houseId, userId, rechargeAmount, rechargeChannel, paymentMethod);
            if (success) {
                logger.info("充值成功 - houseId: {}, userId: {}, rechargeAmount: {}", houseId, userId, rechargeAmount);
                return Result.success("充值成功");
            } else {
                logger.error("充值失败 - houseId: {}, userId: {}, rechargeAmount: {}", houseId, userId, rechargeAmount);
                return Result.error("充值失败：房屋不存在或更新失败");
            }
        } catch (Exception e) {
            logger.error("充值过程发生异常", e);
            return Result.error("充值失败：" + e.getMessage());
        }
    }
} 