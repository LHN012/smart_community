package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:00
 */

@TableName("houses")
@Data
public class Houses {
    @TableId(value = "house_id", type = IdType.AUTO)
    private Integer houseId;

    @TableField("area_id")
    private Integer areaId;

    @TableField("building_id")
    private Integer buildingId;

    @TableField("unit_id")
    private Integer unitId;

    @TableField("name")
    private String name;

    @TableField("price")
    private Double price;

    @TableField("size")
    private Double size;

    @TableField("balance")
    private Double balance;
}
