package com.example.wx_smart_community.mapper;

import com.example.wx_smart_community.entity.NotificationReceiver;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface NotificationReceiverMapper {
    @Insert("INSERT INTO notification_receivers (receiver_user_id, notification_id, is_read) " +
            "VALUES (#{receiverUserId}, #{notificationId}, #{isRead})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NotificationReceiver receiver);
} 