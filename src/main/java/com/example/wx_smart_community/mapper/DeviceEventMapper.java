package com.example.wx_smart_community.mapper;

import com.example.wx_smart_community.entity.DeviceEvent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface DeviceEventMapper {
    
    @Insert("INSERT INTO device_events (device_id, event_type, event_data, timestamp) " +
            "VALUES (#{deviceId}, #{eventType}, #{eventData}, #{timestamp})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DeviceEvent event);
} 