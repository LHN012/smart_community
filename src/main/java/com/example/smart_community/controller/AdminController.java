package com.example.smart_community.controller;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:38
 */
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.smart_community.common.Result;
import com.example.smart_community.entity.Users;
import com.example.smart_community.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UsersService usersService;

    @GetMapping("/list")
    public Result<List<Users>> list() {
        List<Users> admins = usersService.listAdmins();
        return Result.success(admins);
    }

    @GetMapping("/normal-users")
    public Result<List<Users>> listNormalUsers() {
        try {
            List<Users> users = usersService.listNormalUsers();
            logger.info("获取到普通用户列表，数量: {}", users.size());
            users.forEach(user -> logger.info("用户ID: {}, 用户名: {}", user.getUserId(), user.getUsername()));
            return Result.success(users);
        } catch (Exception e) {
            logger.error("获取普通用户列表失败", e);
            return Result.error("获取普通用户列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/normal-users/search")
    public Result<List<Users>> searchNormalUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String phoneNumber) {
        try {
            List<Users> users = usersService.listNormalUsersByCondition(keyword, phoneNumber);
            logger.info("搜索到普通用户列表，数量: {}", users.size());
            return Result.success(users);
        } catch (Exception e) {
            logger.error("搜索普通用户列表失败", e);
            return Result.error("搜索普通用户列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/check-username/{username}")
    public Result<Boolean> checkUsername(@PathVariable String username) {
        Users user = usersService.getByUsername(username);
        return Result.success(user != null);
    }

    @PostMapping("/create")
    public Result<Integer> create(@RequestBody Users user) {
        try {
            if (usersService.save(user)) {
                return Result.success(user.getUserId());
            }
            return Result.error("创建管理员失败");
        } catch (Exception e) {
            logger.error("创建管理员失败", e);
            return Result.error("创建管理员失败: " + e.getMessage());
        }
    }

    @PostMapping("/create-normal-user")
    public Result<Integer> createNormalUser(@RequestBody Users user) {
        try {
            // 设置角色为普通用户
            user.setRole(1);
            if (usersService.save(user)) {
                return Result.success(user.getUserId());
            }
            return Result.error("创建普通用户失败");
        } catch (Exception e) {
            logger.error("创建普通用户失败", e);
            return Result.error("创建普通用户失败: " + e.getMessage());
        }
    }

    @PutMapping("/update/{userId}")
    public Result<Boolean> update(@PathVariable Integer userId, @RequestBody Users user) {
        try {
            logger.info("开始更新用户信息, userId: {}, 请求数据: {}", userId, user);
            
            // 先检查用户是否存在
            Users existingUser = usersService.getById(userId);
            if (existingUser == null) {
                logger.warn("用户不存在, userId: {}", userId);
                return Result.error("用户不存在");
            }
            logger.info("找到现有用户: {}", existingUser);

            // 保持原有的一些字段不变
            user.setUserId(userId);
            user.setUsername(existingUser.getUsername()); // 不允许修改用户名
            user.setRole(existingUser.getRole()); // 不允许修改角色
            
            // 如果密码为空，保持原密码不变
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }

            logger.info("准备更新用户信息: {}", user);
            boolean success = usersService.updateById(user);
            
            if (success) {
                logger.info("用户更新成功, userId: {}", userId);
                return Result.success(true);
            } else {
                logger.warn("用户更新失败, userId: {}", userId);
                return Result.error("更新管理员失败");
            }
        } catch (Exception e) {
            logger.error("更新管理员失败, userId: {}, 错误: {}", userId, e.getMessage(), e);
            return Result.error("更新管理员失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{userId}")
    public Result<Boolean> delete(@PathVariable Integer userId) {
        try {
            // 先检查用户是否存在
            Users existingUser = usersService.getById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }

            // 检查是否是超级管理员
            if (existingUser.getRole() == 3) {
                return Result.error("超级管理员不可删除");
            }

            if (usersService.removeById(userId)) {
                return Result.success(true);
            }
            return Result.error("删除管理员失败");
        } catch (Exception e) {
            logger.error("删除管理员失败", e);
            return Result.error("删除管理员失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-normal-user/{userId}")
    public Result<Boolean> deleteNormalUser(@PathVariable Integer userId) {
        try {
            // 先检查用户是否存在
            Users existingUser = usersService.getById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }

            // 检查是否是普通用户
            if (existingUser.getRole() != 1) {
                return Result.error("只能删除普通用户");
            }

            if (usersService.removeById(userId)) {
                return Result.success(true);
            }
            return Result.error("删除普通用户失败");
        } catch (Exception e) {
            logger.error("删除普通用户失败", e);
            return Result.error("删除普通用户失败: " + e.getMessage());
        }
    }
}
