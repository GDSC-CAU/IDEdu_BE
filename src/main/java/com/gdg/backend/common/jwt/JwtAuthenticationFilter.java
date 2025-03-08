package com.gdg.backend.common.jwt;

import com.gdg.backend.common.exception.handler.GeneralHandler;
import com.gdg.backend.common.response.status.ErrorCode;
import com.gdg.backend.domain.member.entity.Member;
import com.gdg.backend.domain.member.entity.UserPrincipal;
import com.gdg.backend.domain.member.repository.MemberRepository;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            Long memberId = jwtTokenProvider.validateToken(token);
            log.info("✅ JWT 검증 성공 - memberId={}", memberId);

            // 회원 정보 조회 (Member 객체는 실제 Student 인스턴스일 수 있음)
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new GeneralHandler(ErrorCode.MEMBER_NOT_FOUND));

            log.info(member.getUsername());

            // UserPrincipal 생성
            UserPrincipal userPrincipal = new UserPrincipal(member);

            // SecurityContext에 인증 정보 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 제거 후 토큰 반환
        }
        return null;
    }
}