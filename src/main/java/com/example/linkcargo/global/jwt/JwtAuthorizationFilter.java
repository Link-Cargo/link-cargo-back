package com.example.linkcargo.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // JWT 인증이 필요없는 URL (Security Context 에 사용자 정보 X)
        List<String> excludePaths = Arrays.asList(
            "/api/v1/users/register",
            "/api/v1/users/login",
            "/api/v1/users/refresh",
            "/api/swagger-ui/",
            "/api/v3/api-docs",
            "/api/swagger-resources",
            "/api/swagger-ui.html",
            "/api/webjars/", "/swagger-ui/index.html" ,"/v1/users/register","/v3/api-docs", "/swagger-ui/","/swagger-resources","swagger-ui.html",
            "/ws/chat"  // 웹소켓 엔드포인트
        );

        String path = request.getRequestURI();
        return excludePaths.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String rawToken = request.getHeader("Authorization");
        String token = jwtValidator.validateFormAndRemoveBearer(rawToken);

        Authentication authentication = jwtProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
