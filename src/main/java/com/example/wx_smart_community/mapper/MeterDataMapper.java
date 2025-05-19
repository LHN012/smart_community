package com.example.wx_smart_community.mapper;

import com.example.wx_smart_community.entity.MeterData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper
public interface MeterDataMapper {
    Logger logger = LoggerFactory.getLogger(MeterDataMapper.class);

    @Select("SELECT * FROM ${tableName} WHERE device_id = #{deviceId} " +
            "AND DATE_FORMAT(created_at, '%Y-%m') = #{month} " +
            "ORDER BY created_at DESC LIMIT 1")
    MeterData findLatestByDeviceIdAndMonth(
            @Param("tableName") String tableName,
            @Param("deviceId") String deviceId,
            @Param("month") String month);
} 