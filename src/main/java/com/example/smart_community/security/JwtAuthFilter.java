package com.example.smart_community.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        logger.info("收到请求，Authorization header: {}", requestTokenHeader);

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                logger.info("从token中解析出用户名: {}", username);
            } catch (IllegalArgumentException e) {
                logger.error("无法获取JWT Token", e);
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token已过期", e);
            }
        } else {
            logger.warn("JWT Token不是以Bearer开头");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                logger.info("加载用户信息成功: {}", userDetails);

                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    logger.info("Token验证成功");
                    // 添加详细的角色解析
                    Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
                    List<String> roles = claims.get("roles", List.class);
                    logger.info("从token中解析出角色: {}", roles);

                    // 确保角色以ROLE_开头
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    logger.info("处理后的权限列表: {}", authorities);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("认证信息已设置到SecurityContext");
                } else {
                    logger.error("Token验证失败");
                }
            } catch (Exception e) {
                logger.error("处理认证时发生错误", e);
            }
        } else {
            logger.info("用户名为空或已存在认证信息");
        }
        chain.doFilter(request, response);
    }
}