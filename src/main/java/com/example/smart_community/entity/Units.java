package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:00
 */
@Data
@TableName("units")
public class Units {
    @TableId(type = IdType.AUTO)
    private Integer unitId;
    
    private String name;
}
