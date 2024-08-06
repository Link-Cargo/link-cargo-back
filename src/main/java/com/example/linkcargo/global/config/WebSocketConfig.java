package com.example.linkcargo.global.config;

import com.example.linkcargo.global.jwt.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 웹 소켓 인드포인트 설정
        // ws://localhost:8080/ws/chat 으로 요청이 들어오면 websocket 통신을 진행
        // 모든 IP 에서 접속 가능하도록 설정(모든 CORS) 요청 허용
        registry
            .addHandler(webSocketHandler, "/ws/chat")
            .setAllowedOrigins("*");
    }

}
