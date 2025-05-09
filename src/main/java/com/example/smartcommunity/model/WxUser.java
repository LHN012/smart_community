package com.example.smartcommunity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wx_user")
public class WxUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String openid;
    private String unionid;
    private String sessionKey;
    private String nickname;
    private Integer userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 