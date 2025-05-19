package com.example.wx_smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("notifications")
public class Notice {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String notificationType;  // 通知类型
    private String title;
    private String content;
    private Integer senderId;
    private String receiverType;  // 接收通知的对象类型
    private Integer receiverId;
    private Date sendTime;
    private Boolean isRead;  // 是否已读
} 