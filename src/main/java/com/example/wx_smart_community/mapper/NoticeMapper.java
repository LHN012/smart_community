package com.example.wx_smart_community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wx_smart_community.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Date;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
    @Select("SELECT n.*, COALESCE(nr.is_read, 0) as is_read FROM notifications n " +
            "INNER JOIN notification_receivers nr ON n.id = nr.notification_id " +
            "WHERE nr.receiver_user_id = #{userId} " +
            "AND (#{type} = '全部' OR n.notification_type = #{type}) " +
            "AND n.send_time >= #{startTime} " +
            "ORDER BY CASE WHEN n.notification_type = '紧急通知' THEN 0 ELSE 1 END, n.send_time DESC")
    List<Notice> getNoticeList(@Param("userId") Integer userId, 
                              @Param("type") String type,
                              @Param("startTime") Date startTime);

    @Select("SELECT COUNT(*) FROM notifications n " +
            "LEFT JOIN notification_receivers nr ON n.id = nr.notification_id " +
            "WHERE nr.receiver_user_id = #{userId} AND nr.is_read = 0")
    int getUnreadCount(@Param("userId") Integer userId);

    @Select("SELECT n.*, COALESCE(nr.is_read, 0) as is_read " +
            "FROM notifications n " +
            "INNER JOIN notification_receivers nr ON n.id = nr.notification_id " +
            "WHERE n.id = #{id} AND nr.receiver_user_id = #{userId}")
    Notice selectById(@Param("id") Integer id, @Param("userId") Integer userId);
} 