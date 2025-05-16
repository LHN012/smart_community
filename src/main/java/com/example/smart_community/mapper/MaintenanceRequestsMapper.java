package com.example.smart_community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smart_community.entity.MaintenanceRequests;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface MaintenanceRequestsMapper extends BaseMapper<MaintenanceRequests> {
    /**
     * 根据用户ID查询报修请求列表
     * @param userId 用户ID
     * @return 报修请求列表
     */
    @Select("SELECT request_id, user_id, location, description, status, created_at, update_user_id, updated_at " +
           "FROM maintenancerequests " +
           "WHERE user_id = #{userId} " +
           "ORDER BY created_at DESC")
    List<MaintenanceRequests> selectByUserId(Long userId);
}