package com.example.wx_smart_community.mapper;

import com.example.wx_smart_community.entity.Notifications;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface NotificationsMapper {
    @Insert("INSERT INTO notifications (notification_type, title, content, sender_id, receiver_type, receiver_id, send_time) " +
            "VALUES (#{notificationType}, #{title}, #{content}, #{senderId}, #{receiverType}, #{receiverId}, #{sendTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notifications notification);
} 