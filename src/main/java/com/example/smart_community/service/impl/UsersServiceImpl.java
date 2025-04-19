package com.example.smart_community.service.impl;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:48
 */
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.Users;
import com.example.smart_community.mapper.UsersMapper;
import com.example.smart_community.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

    @Override
    public Users getByUsername(String username) {
        logger.info("根据用户名查询用户: {}", username);
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Users user = getOne(queryWrapper);
        logger.info("查询结果: {}", user);
        return user;
    }

    @Override
    public List<Users> listAdmins() {
        logger.info("查询管理员列表");
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role", 2, 3);  // 2=管理员, 3=超级管理员
        List<Users> admins = list(queryWrapper);
        logger.info("查询到{}个管理员", admins.size());
        return admins;
    }

    @Override
    public void saveUser(Users user) {
        // 调用 MyBatis-Plus 的 save 方法保存用户信息
        this.save(user);
    }

    @Override
    public boolean updateById(Users user) {
        logger.info("尝试更新用户: {}, ID: {}", user.getUsername(), user.getUserId());
        try {
            boolean result = super.updateById(user);
            if (result) {
                logger.info("用户更新成功: {}, ID: {}", user.getUsername(), user.getUserId());
            } else {
                logger.warn("用户更新失败: {}, ID: {}", user.getUsername(), user.getUserId());
            }
            return result;
        } catch (Exception e) {
            logger.error("更新用户时发生错误: {}, ID: {}, 错误: {}", user.getUsername(), user.getUserId(), e.getMessage(), e);
            throw e;
        }
    }
}