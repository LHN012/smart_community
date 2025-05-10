package com.example.wx_smart_community.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("houses")
public class House {
    @TableId(value = "house_id", type = IdType.AUTO)
    private Integer houseId;
    private Integer areaId;
    private Integer buildingId;
    private Integer unitId;
    private String name;
    private Double price;
    private Double size;
    private BigDecimal balance;
} 