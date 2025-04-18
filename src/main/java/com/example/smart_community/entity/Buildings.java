package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:00
 */


@Data
@TableName("Buildings")
public class Buildings {
    private Integer buildingId;
    private Integer areaId;
    private String buildingNo;
}
