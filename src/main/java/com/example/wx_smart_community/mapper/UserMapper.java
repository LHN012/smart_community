package com.example.wx_smart_community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wx_smart_community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User findById(Integer userId);
} 