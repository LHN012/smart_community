package com.example.wx_smart_community.mapper;

import com.example.wx_smart_community.entity.WarningRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface WarningRuleMapper {
    
    @Select("SELECT * FROM warning_rules WHERE device_id = #{deviceId} AND enable_status = 1")
    List<WarningRule> findByDeviceId(@Param("deviceId") String deviceId);
    
    @Select("SELECT * FROM warning_rules WHERE enable_status = 1")
    List<WarningRule> findAllEnabled();
} 