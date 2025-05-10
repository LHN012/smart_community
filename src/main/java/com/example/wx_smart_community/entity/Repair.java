package com.example.wx_smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("repair")
public class Repair {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userId;
    private String title;
    private String description;
    private String location;
    private String images;
    private Integer status; // 0:待处理 1:处理中 2:已完成 3:已取消
    private String contactPhone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
} 