package com.example.smart_community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smart_community.entity.Notifications;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface NotificationsMapper extends BaseMapper<Notifications> {
    
    @Select("SELECT n.*, " +
            "u.username as sender_name, " +
            "CASE " +
            "WHEN n.receiver_type = '全体业主' THEN '全体业主' " +
            "WHEN n.receiver_type = '物业员工' THEN '物业员工' " +
            "WHEN n.receiver_type = '特定房间业主' THEN '特定房间业主' " +
            "END as receiverTypeName, " +
            "CASE " +
            "WHEN n.receiver_type = '全体业主' THEN '全部' " +
            "WHEN n.receiver_type = '物业员工' THEN '全部' " +
            "WHEN n.receiver_type = '特定房间业主' THEN " +
            "CONCAT(" +
            "(SELECT a.name FROM areas a " +
            "JOIN houses h ON a.area_id = h.area_id " +
            "WHERE h.house_id = n.receiver_id), " +
            "'-', " +
            "(SELECT b.name FROM buildings b " +
            "JOIN houses h ON b.building_id = h.building_id " +
            "WHERE h.house_id = n.receiver_id), " +
            "'-', " +
            "(SELECT u.name FROM units u " +
            "JOIN houses h ON u.unit_id = h.unit_id " +
            "WHERE h.house_id = n.receiver_id), " +
            "'-', " +
            "(SELECT h.name FROM houses h " +
            "WHERE h.house_id = n.receiver_id)) " +
            "END as receiverInfo " +
            "FROM notifications n " +
            "LEFT JOIN users u ON n.sender_id = u.user_id " +
            "ORDER BY n.send_time DESC")
    List<Map<String, Object>> selectNotificationsWithSenderAndReceiver();
} 