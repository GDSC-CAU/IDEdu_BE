package com.gdg.backend.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7; // 7일 (기본값, 필요시 변경)

    public JwtTokenProvider(@Value("${jwt.secret.key}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT Secret Key must be at least 32 characters long");
        }
        log.info("🔹 Loaded JWT Secret from Environment");
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Long memberId) {
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey) // 알고리즘 생략
                .compact();
    }

    public String getMemberId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long validateToken(String token) {
        try {
            return Long.parseLong(Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject());
        } catch (ExpiredJwtException e) {
            log.error("❌ JWT 토큰이 만료되었습니다.");
            throw new JwtException("JWT 토큰이 만료되었습니다.");
        } catch (MalformedJwtException | SignatureException e) {
            log.error("❌ 유효하지 않은 JWT 토큰입니다.");
            throw new JwtException("유효하지 않은 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("❌ JWT 검증 오류: {}", e.getMessage());
            throw new JwtException("JWT 검증 오류");
        }
    }
}
