package com.example.smart_community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 13:59
 */

@Data
@TableName("areas")
public class Areas {

    private Integer areaId;
    private String name;
    private LocalDateTime createdAt;
}
