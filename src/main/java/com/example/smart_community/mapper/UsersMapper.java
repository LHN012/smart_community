package com.example.smart_community.mapper;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 13:54
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smart_community.entity.Users;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    Users selectByUsername(String username);

    /**
     * 查询所有管理员（角色为2或3的用户）
     * @return 管理员列表
     */
    @Select("SELECT * FROM users WHERE role IN (2, 3)")
    List<Users> selectAdmins();

    /**
     * 查询所有普通用户（角色为1的用户）
     * @return 普通用户列表
     */
    @Select("SELECT * FROM users WHERE role = 1")
    List<Users> selectNormalUsers();

    /**
     * 根据房屋ID查询用户
     * @param houseId 房屋ID
     * @return 用户列表
     */
    @Select("SELECT u.* FROM users u " +
            "JOIN userhouses uh ON u.user_id = uh.user_id " +
            "WHERE uh.house_id = #{houseId}")
    List<Users> selectUsersByHouseId(Integer houseId);

    /**
     * 根据角色查询用户
     * @param role 角色
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE role = #{role}")
    List<Users> selectUsersByRole(Integer role);
}
