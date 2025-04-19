package com.example.smart_community.security;

import com.example.smart_community.entity.Users;
import com.example.smart_community.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UsersService usersService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("尝试加载用户: {}", username);
        Users user = usersService.getByUsername(username);
        if (user == null) {
            logger.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在");
        }
        
        logger.info("找到用户: {}, 角色: {}", username, user.getRole());
        String role = "ROLE_" + (user.getRole() == 3 ? "SUPER_ADMIN" : (user.getRole() == 2 ? "ADMIN" : "USER"));
        logger.info("用户角色: {}", role);
        
        UserDetails userDetails = new User(user.getUsername(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role)));
        logger.info("用户详情创建成功: {}", userDetails);
        return userDetails;
    }
} 