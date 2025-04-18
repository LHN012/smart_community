package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:00
 */
@Data
@TableName("Units")
public class Units {
    private Integer unitId;
    private Integer buildingId;
    private String unitNo;
}
