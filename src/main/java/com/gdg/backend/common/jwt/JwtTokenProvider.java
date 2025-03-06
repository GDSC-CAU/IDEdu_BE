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
    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 24 * 24; // 24 * 24 * 24 시간 // TODO 배포 시 변경

    public JwtTokenProvider(@Value("${jwt.secret.key}") String secret) {
        log.info("🔹 Loaded JWT Secret from Environment: {}", secret);
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 🔑 JWT 토큰 생성 (회원 ID 기반)
     */
    public String generateToken(Long memberId) {

        return Jwts.builder()
                .setSubject(String.valueOf(memberId)) // 사용자 ID 저장
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명
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

    /**
     * 🔑 JWT 토큰 검증 및 회원 ID 반환
     */
    public Long validateToken(String token) {
        try {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());

        // TODO 토큰 검증 복구
        } catch (ExpiredJwtException e) {
            log.error("❌ JWT 토큰이 만료되었습니다.");
          throw new RuntimeException("JWT 토큰이 만료되었습니다.");
        } catch (MalformedJwtException | SignatureException e) {
            log.error("❌ 유효하지 않은 JWT 토큰입니다.");
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("❌ JWT 검증 오류: {}", e.getMessage());
            throw new RuntimeException("JWT 검증 오류");
        }
    }
}
