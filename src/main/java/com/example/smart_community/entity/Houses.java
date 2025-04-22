package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:00
 */

@Data
@TableName("houses")
public class Houses {
    @TableId(type = IdType.AUTO)
    private Integer houseId;
    
    private Integer areaId;
    
    private Integer buildingId;
    
    private Integer unitId;
    
    private String name;
    
    private Double price;
    
    private Double size;
    
    private Double balance;
}
