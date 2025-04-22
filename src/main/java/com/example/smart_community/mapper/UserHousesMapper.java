package com.example.smart_community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smart_community.entity.UserHouses;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserHousesMapper extends BaseMapper<UserHouses> {
    
    @Select("SELECT uh.relation_id, uh.user_id, uh.house_id, uh.relation_type, " +
            "u.username, u.real_name, u.phone_number, u.email, " +
            "a.name as area_name, b.name as building_name, u2.name as unit_name, " +
            "h.name as house_name " +
            "FROM userhouses uh " +
            "LEFT JOIN users u ON uh.user_id = u.user_id " +
            "LEFT JOIN houses h ON uh.house_id = h.house_id " +
            "LEFT JOIN areas a ON h.area_id = a.area_id " +
            "LEFT JOIN buildings b ON h.building_id = b.building_id " +
            "LEFT JOIN units u2 ON h.unit_id = u2.unit_id")
    List<UserHouses> selectListWithUserAndHouseInfo();
} 