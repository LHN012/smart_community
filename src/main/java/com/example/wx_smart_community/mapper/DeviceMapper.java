package com.example.wx_smart_community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceMapper {
    
    @Update("UPDATE devices SET status = #{status} WHERE device_number = #{deviceId}")
    int updateStatus(@Param("deviceId") String deviceId, @Param("status") String status);
    
    @Select("SELECT house_id FROM devices WHERE device_number = #{deviceId}")
    Integer getHouseIdByDeviceId(String deviceId);
    
    @Select("SELECT user_id FROM userhouses WHERE house_id = #{houseId}")
    List<Integer> getUserIdsByHouseId(Integer houseId);
} 