package com.example.wx_smart_community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wx_smart_community.model.House;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HouseMapper extends BaseMapper<House> {
    @Select("SELECT h.* FROM houses h " +
            "INNER JOIN userhouses uh ON h.house_id = uh.house_id " +
            "WHERE uh.user_id = #{userId}")
    List<House> getHousesByUserId(Integer userId);
} 