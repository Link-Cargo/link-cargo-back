package com.example.linkcargo.global.security;


import com.example.linkcargo.global.jwt.JwtAuthorizationFilter;
import com.example.linkcargo.global.jwt.JwtProvider;
import com.example.linkcargo.global.jwt.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth ->
                auth.anyRequest().permitAll()
            ).csrf((csrfConfig) ->
                csrfConfig.disable()
            )
            // JWT 필터
            .addFilterBefore(new JwtAuthorizationFilter(jwtProvider, jwtValidator),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
