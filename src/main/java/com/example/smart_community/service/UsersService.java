package com.example.smart_community.service;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:45
 */
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_community.entity.Users;
import com.example.smart_community.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsersService extends IService<Users> {
    @Autowired
    UsersMapper usersMapper = null;

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，如果未找到返回null
     */
    Users getByUsername(String username);
    /**
     * 保存用户信息
     * @param user 用户实体
     */
    void saveUser(Users user);

    List<Users> listAdmins();

    /**
     * 获取普通用户列表
     * @return 普通用户列表
     */
    List<Users> listNormalUsers();

    /**
     * 根据条件查询普通用户列表
     * @param keyword 关键词（用户名/姓名/邮箱）
     * @param phoneNumber 手机号
     * @return 用户列表
     */
    List<Users> listNormalUsersByCondition(String keyword, String phoneNumber);
}
