package com.example.wx_smart_community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wx_smart_community.entity.MaintenanceRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface MaintenanceRequestMapper extends BaseMapper<MaintenanceRequest> {
    
    @Select("SELECT * FROM maintenancerequests WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<MaintenanceRequest> selectByUserId(@Param("userId") Integer userId);
} 