package com.baolo.study_room_rservation_system.Tool;

import com.baolo.study_room_rservation_system.Exception.CustomizeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    // 密钥（自己随便写一串长字符串）
    @Value("${jwt.secret-key}")
    private   String SECRET_KEY;
    // 令牌有效期 2 小时
    @Value("${jwt.expiration}")
    private  long EXPIRATION;

    // 生成密钥
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param studentId 学号
     * @return 令牌字符串
     */
    public String generateToken(Long userId, String studentId) {
        // 设置令牌Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("studentId", studentId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 校验令牌合法性
     * @param token 令牌
     * @return 校验结果
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 令牌过期
            throw new CustomizeException(401, "令牌已过期");
        } catch (Exception e) {
            // 令牌格式错误/签名错误/参数异常
            throw new CustomizeException(401, "令牌无效");
        }
    }

    /**
     * 解析令牌获取用户信息
     * @param token 令牌
     * @return 用户Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从Claims中获取用户ID
     * @param claims 令牌载荷
     * @return 用户ID
     */
    public Long getUserId(Claims claims) {
        return claims.get("userId", Long.class);
    }

    /**
     * 从Claims中获取学号
     * @param claims 令牌载荷
     * @return 学号
     */
    public String getStudentId(Claims claims) {
        return claims.get("studentId", String.class);
    }
}
