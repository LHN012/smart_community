package com.example.wx_smart_community.mapper;

import com.example.wx_smart_community.entity.WaterMeterData;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WaterMeterDataMapper {
    @Insert("INSERT INTO water_meter_data (device_id, timestamp, flow_rate, total_volume, pressure, temperature, " +
            "battery_level, signal_strength, created_at) " +
            "VALUES (#{deviceId}, #{timestamp}, #{flowRate}, #{totalVolume}, #{pressure}, #{temperature}, " +
            "#{batteryLevel}, #{signalStrength}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WaterMeterData data);

    @Select("SELECT * FROM water_meter_data WHERE device_id = #{deviceId} " +
            "ORDER BY timestamp DESC LIMIT 1")
    WaterMeterData findLatestByDeviceId(@Param("deviceId") String deviceId);

    @Select("SELECT DISTINCT device_id FROM water_meter_data WHERE " +
            "timestamp >= #{startTime}")
    List<String> findActiveDeviceIds(@Param("startTime") LocalDateTime startTime);
} 