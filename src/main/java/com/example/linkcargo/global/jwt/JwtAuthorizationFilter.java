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
        // JWT 필터를 거치지 않을 주소
        List<String> excludePaths = Arrays.asList(
            "/api/v1/users/register",
            "/api/v1/users/login",
            "/api/v1/users/refresh",
            "/swagger-ui/",
            "/v3/api-docs", 
            "/swagger-resources", 
            "/swagger-ui.html", 
            "/webjars/"
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

        log.info("authentication = {}", authentication);

        filterChain.doFilter(request, response);
    }
}
