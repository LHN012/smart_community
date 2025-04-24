package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.Houses;
import com.example.smart_community.mapper.HousesMapper;
import com.example.smart_community.service.HousesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HousesServiceImpl extends ServiceImpl<HousesMapper, Houses> implements HousesService {

    private static final Logger logger = LoggerFactory.getLogger(HousesServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Houses> getHousesByUnitId(Integer unitId) {
        LambdaQueryWrapper<Houses> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Houses::getUnitId, unitId);
        return list(wrapper);
    }

    @Override
    public boolean isHouseNameUniqueInUnit(Integer unitId, String name) {
        LambdaQueryWrapper<Houses> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Houses::getUnitId, unitId)
               .eq(Houses::getName, name);
        return count(wrapper) == 0;
    }

    @Override
    public List<Houses> getHousesByCondition(String name, Integer unitId) {
        LambdaQueryWrapper<Houses> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Houses::getName, name);
        }
        if (unitId != null) {
            wrapper.eq(Houses::getUnitId, unitId);
        }
        return list(wrapper);
    }

    @Override
    public boolean isAreaExists(Integer areaId) {
        LambdaQueryWrapper<Houses> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Houses::getAreaId, areaId);
        return count(wrapper) > 0;
    }

    @Override
    public boolean isBuildingExists(Integer buildingId) {
        LambdaQueryWrapper<Houses> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Houses::getBuildingId, buildingId);
        return count(wrapper) > 0;
    }

    @Override
    public List<Houses> listWithAreaAndBuildingAndUnit() {
        return baseMapper.selectListWithAreaAndBuildingAndUnit();
    }

    @Override
    @Transactional
    public boolean recharge(Integer houseId, Integer userId, Double rechargeAmount, String rechargeChannel, String paymentMethod) {
        try {
            logger.info("开始充值操作 - 房屋ID: {}, 用户ID: {}, 充值金额: {}", houseId, userId, rechargeAmount);
            
            // 1. 获取房屋当前余额
            Houses house = baseMapper.selectBasicById(houseId);
            if (house == null) {
                logger.error("房屋不存在 - 房屋ID: {}", houseId);
                return false;
            }
            
            // 2. 获取充值金额
            // BigDecimal rechargeAmountDecimal = new BigDecimal(rechargeAmount.toString());

            
            logger.info("准备充值 - 房屋ID: {},  充值金额: {}", 
                    houseId, rechargeAmount);
            
            // 3. 调用存储过程插入充值记录（触发器会自动更新房屋余额）
            try {
                jdbcTemplate.update("CALL insert_recharge_record(?, ?, ?, ?, ?)",
                        houseId,
                        userId,
                        rechargeAmount.doubleValue(),
                        rechargeChannel,
                        paymentMethod);
                logger.info("充值记录插入成功 - 房屋ID: {}, 用户ID: {}", houseId, userId);
            } catch (Exception e) {
                logger.error("调用存储过程失败 - 房屋ID: {}, 用户ID: {}, 错误: {}", 
                        houseId, userId, e.getMessage(), e);
                throw e;
            }
            
            return true;
        } catch (Exception e) {
            logger.error("充值操作失败 - 房屋ID: {}, 用户ID: {}, 错误: {}", 
                    houseId, userId, e.getMessage(), e);
            throw new RuntimeException("充值失败: " + e.getMessage());
        }
    }
}    