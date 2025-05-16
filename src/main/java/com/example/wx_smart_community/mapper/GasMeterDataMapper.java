package com.example.wx_smart_community.mapper;

import com.example.wx_smart_community.entity.GasMeterData;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GasMeterDataMapper {
    
    @Insert("INSERT INTO gas_meter_data (device_id, timestamp, flow_rate, total_volume, pressure, temperature, " +
            "battery_level, signal_strength, created_at) " +
            "VALUES (#{deviceId}, #{timestamp}, #{flowRate}, #{totalVolume}, #{pressure}, #{temperature}, " +
            "#{batteryLevel}, #{signalStrength}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GasMeterData data);

    @Insert("<script>" +
            "INSERT INTO gas_meter_data (device_id, timestamp, flow_rate, total_volume, pressure, temperature, " +
            "battery_level, signal_strength, created_at) VALUES " +
            "<foreach collection='list' item='data' separator=','>" +
            "(#{data.deviceId}, #{data.timestamp}, #{data.flowRate}, #{data.totalVolume}, #{data.pressure}, " +
            "#{data.temperature}, #{data.batteryLevel}, #{data.signalStrength}, #{data.createdAt})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<GasMeterData> dataList);

    @Select("SELECT * FROM gas_meter_data WHERE device_id = #{deviceId} " +
            "AND timestamp >= #{startTime} AND timestamp <= #{endTime} " +
            "ORDER BY timestamp DESC")
    List<GasMeterData> findByDeviceIdAndTimeRange(@Param("deviceId") String deviceId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM gas_meter_data WHERE device_id = #{deviceId} " +
            "AND timestamp >= #{startTime} ORDER BY timestamp DESC")
    List<GasMeterData> findHistoricalData(@Param("deviceId") String deviceId, 
                                         @Param("startTime") LocalDateTime startTime);

    @Select("SELECT * FROM gas_meter_data WHERE device_id = #{deviceId} " +
            "ORDER BY timestamp DESC LIMIT 1")
    GasMeterData findLatestByDeviceId(@Param("deviceId") String deviceId);

    @Select("SELECT DISTINCT device_id FROM gas_meter_data WHERE " +
            "timestamp >= #{startTime}")
    List<String> findActiveDeviceIds(@Param("startTime") LocalDateTime startTime);
} 