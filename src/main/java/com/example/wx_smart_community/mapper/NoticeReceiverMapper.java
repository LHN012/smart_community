package com.example.wx_smart_community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wx_smart_community.entity.NoticeReceiver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NoticeReceiverMapper extends BaseMapper<NoticeReceiver> {
    @Update("UPDATE notification_receivers SET is_read = 1 " +
            "WHERE receiver_user_id = #{userId} AND notification_id = #{noticeId}")
    int markAsRead(@Param("userId") Integer userId, @Param("noticeId") Integer noticeId);

    @Update("UPDATE notification_receivers SET is_read = 1 " +
            "WHERE receiver_user_id = #{userId}")
    int markAllAsRead(@Param("userId") Integer userId);
} 