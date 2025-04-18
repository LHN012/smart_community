package com.example.smart_community.controller;

import com.example.smart_community.entity.Users;
import com.example.smart_community.security.JwtTokenUtil;
import com.example.smart_community.service.UsersService;
import com.example.smart_community.utils.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsersService usersService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Users user) {
        logger.info("收到登录请求 - 用户名: {}, 密码长度: {}", 
            user.getUsername(), 
            user.getPassword() != null ? user.getPassword().length() : 0);
            
        try {
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成token
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);
            
            // 获取用户信息
            Users userInfo = usersService.getByUsername(user.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", userInfo);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("data", data);
            return result;
        } catch (BadCredentialsException e) {
            logger.error("登录失败 - 用户名或密码错误: {}", user.getUsername());
            return ResultUtils.error("用户名或密码错误");
        } catch (DisabledException e) {
            logger.error("登录失败 - 用户已被禁用: {}", user.getUsername());
            return ResultUtils.error("用户已被禁用");
        } catch (UsernameNotFoundException e) {
            logger.error("登录失败 - 用户不存在: {}", user.getUsername());
            return ResultUtils.error("用户不存在");
        } catch (Exception e) {
            logger.error("登录失败 - 未知错误: {}", e.getMessage(), e);
            return ResultUtils.error("登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Users user) {
        try {
            if (usersService.getByUsername(user.getUsername()) != null) {
                return ResultUtils.error("用户名已存在");
            }
            usersService.saveUser(user);
            return ResultUtils.success("注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("注册失败: " + e.getMessage());
        }
    }
}    