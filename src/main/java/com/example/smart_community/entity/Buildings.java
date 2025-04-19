package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:00
 */


@Data
@TableName("buildings")
public class Buildings {
    @TableId(value = "building_id", type = IdType.AUTO)
    private Integer buildingId;

    @TableField("name")
    private String name;
}
