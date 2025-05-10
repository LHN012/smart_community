package com.example.wx_smart_community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("maintenancerequests")
public class MaintenanceRequest {
    @TableId(value = "request_id", type = IdType.AUTO)
    private Integer requestId;

    @TableField("user_id")
    private Integer userId;

    @TableField("location")
    private String location;

    @TableField("description")
    private String description;

    @TableField("status")
    private String status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField("update_user_id")
    private Integer updateUserId;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String userName;
} 