package com.example.wx_smart_community.mapper;

import com.example.wx_smart_community.entity.ElectricMeterData;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ElectricMeterDataMapper {
    @Insert("INSERT INTO electric_meter_data (device_id, timestamp, current_power, total_energy, voltage, current, " +
            "power_factor, signal_strength, created_at) " +
            "VALUES (#{deviceId}, #{timestamp}, #{currentPower}, #{totalEnergy}, #{voltage}, #{current}, " +
            "#{powerFactor}, #{signalStrength}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ElectricMeterData data);

    @Select("SELECT * FROM electric_meter_data WHERE device_id = #{deviceId} " +
            "ORDER BY timestamp DESC LIMIT 1")
    ElectricMeterData findLatestByDeviceId(@Param("deviceId") String deviceId);

    @Select("SELECT DISTINCT device_id FROM electric_meter_data WHERE " +
            "timestamp >= #{startTime}")
    List<String> findActiveDeviceIds(@Param("startTime") LocalDateTime startTime);
} 