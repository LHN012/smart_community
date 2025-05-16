package com.example.wx_smart_community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceMapper {
    
    @Update("UPDATE devices SET status = #{status}, update_time = NOW() WHERE device_id = #{deviceId}")
    int updateStatus(@Param("deviceId") String deviceId, @Param("status") String status);
} 