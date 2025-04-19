package com.example.smart_community.mapper;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 13:54
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smart_community.entity.Users;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    @Select("SELECT * FROM Users WHERE username = #{username}")
    Users selectByUsername(String username);
}
