package com.example.linkcargo.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;

    public JwtAuthorizationFilter(JwtProvider jwtProvider, JwtValidator jwtValidator) {
        this.jwtProvider = jwtProvider;
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String excludePath = "/api/v1/users";
        String path = request.getRequestURI();
        return path.startsWith(excludePath);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT 인가 필터 도착");

        // 토큰 추출
        String rawToken = request.getHeader("Authorization");
        log.info("rawToken  = {}, ", rawToken);
        String token = jwtValidator.validateFormAndRemoveBearer(rawToken);
        log.info("token = {}", token);

        // 토큰으로 사용자 정보 추출 & 시큐리티 컨텍스트에 저장
        Authentication authentication = jwtProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("authentication = " + authentication);

        // 다음 필터로 넘어감
        log.info("go to next filter");
        filterChain.doFilter(request, response);
    }

}
