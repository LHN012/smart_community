package com.example.smart_community.utils;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:59
 */
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
public class JwtUtils {
    private static final String SECRET_KEY = "smart-community-secret-key"; // 生产环境需替换为安全的密钥
    private static final long EXPIRE_TIME = 864000000L; // 10天（毫秒）

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT字符串
     */
    public static String generateToken(Integer userId, Integer role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role) // 添加角色信息到JWT
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param token JWT字符串
     * @return Claims对象
     * @throws JwtException 解析异常
     */
    public static Claims parseToken(String token) throws JwtException {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}
