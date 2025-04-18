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
        logger.info("尝试查询用户: {}", username);
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Users user = this.getOne(queryWrapper);
        if (user != null) {
            logger.info("找到用户: {}, ID: {}", username, user.getUserId());
        } else {
            logger.warn("未找到用户: {}", username);
        }
        return user;
    }

    @Override
    public List<Users> listAdmins() {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("role", 1);  // 只查询管理员和超级管理员
        return list(queryWrapper);
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