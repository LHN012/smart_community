package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("notification_receivers")
public class NotificationReceiver {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer receiverUserId;
    private Integer notificationId;
    private Boolean isRead;
} 