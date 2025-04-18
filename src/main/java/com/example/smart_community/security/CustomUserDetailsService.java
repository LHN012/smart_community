package com.example.smart_community.security;

import com.example.smart_community.entity.Users;
import com.example.smart_community.service.UsersService;
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

    @Autowired
    private UsersService usersService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("尝试加载用户: " + username);
        Users user = usersService.getByUsername(username);
        if (user == null) {
            System.out.println("用户不存在: " + username);
            throw new UsernameNotFoundException("用户不存在");
        }
        
        System.out.println("找到用户: " + username + ", 角色: " + user.getRole());
        String role = "ROLE_" + (user.getRole() == 3 ? "SUPER_ADMIN" : (user.getRole() == 2 ? "ADMIN" : "USER"));
        System.out.println("用户角色: " + role);
        
        UserDetails userDetails = new User(user.getUsername(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role)));
        System.out.println("用户详情创建成功: " + userDetails);
        return userDetails;
    }
} 