package com.baolo.study_room_rservation_system.Tool;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    // 密钥（自己随便写一串长字符串）
    private static final String SECRET_KEY = "mySecretKey12345678901234567890123456789012";
    // 令牌有效期 2 小时
    private static final long EXPIRATION = 1000 * 60 * 60 * 2L;

    // 获取密钥
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * 生成 JWT Token
     */
    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析 Token
     */
    public Claims extractToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取用户ID（从token里）
     */
    public Long getUserId(String token) {
        return Long.parseLong(extractToken(token).getSubject());
    }

    /**
     * 判断Token是否过期
     */
    public boolean isExpired(String token) {
        return extractToken(token).getExpiration().before(new Date());
    }
}
