package com.example.smart_community.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getSubject);
        } catch (Exception e) {
            logger.error("从token中获取用户名失败", e);
            return null;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getExpiration);
        } catch (Exception e) {
            logger.error("从token中获取过期时间失败", e);
            return null;
        }
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("解析token失败", e);
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            if (expiration == null) {
                return true;
            }
            boolean isExpired = expiration.before(new Date());
            logger.info("Token过期时间: {}, 当前时间: {}, 是否过期: {}", 
                expiration, new Date(), isExpired);
            return isExpired;
        } catch (Exception e) {
            logger.error("检查token是否过期时发生错误", e);
            return true;
        }
    }

    public String generateToken(UserDetails userDetails) {
        try {
            Map<String, Object> claims = new HashMap<>();
            // 获取用户ID
            Integer userId = ((CustomUserDetails) userDetails).getUserId();
            claims.put("userId", userId);
            
            // 修正角色存储方式（原代码会覆盖角色）
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            claims.put("roles", roles);
            
            // 添加用户名到claims中
            claims.put("sub", userDetails.getUsername());
            
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();
            
            logger.info("生成token成功: {}", token);
            return token;
        } catch (Exception e) {
            logger.error("生成token失败", e);
            throw e;
        }
    }

    public Integer getUserIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.get("userId", Integer.class);
        } catch (Exception e) {
            logger.error("从token中获取用户ID失败", e);
            return null;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            boolean isValid = (username != null && 
                             username.equals(userDetails.getUsername()) && 
                             !isTokenExpired(token));
            logger.info("验证token结果: username={}, isValid={}", username, isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("验证token失败", e);
            return false;
        }
    }
} 