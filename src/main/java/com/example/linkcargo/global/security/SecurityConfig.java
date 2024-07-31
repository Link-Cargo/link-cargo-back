package com.example.linkcargo.global.security;

import com.example.linkcargo.global.jwt.JwtAuthorizationFilter;
import com.example.linkcargo.global.jwt.JwtProvider;
import com.example.linkcargo.global.jwt.JwtValidator;
import com.example.linkcargo.global.resolver.ExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable)
            // jwt filter
            .addFilterBefore(new JwtAuthorizationFilter(jwtProvider, jwtValidator),
                UsernamePasswordAuthenticationFilter.class)
            // jwt exception handler filter
            .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthorizationFilter.class);

        return http.build();
    }
}