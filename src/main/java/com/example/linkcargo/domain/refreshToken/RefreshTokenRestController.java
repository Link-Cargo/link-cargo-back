package com.example.linkcargo.domain.refreshToken;

import com.example.linkcargo.global.exception.BusinessException;
import com.example.linkcargo.global.jwt.JwtProvider;
import com.example.linkcargo.global.response.ResponseMaker;
import com.example.linkcargo.global.response.ResultResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.net.BindException;
import java.net.http.HttpHeaders;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/refresh")
public class RefreshTokenRestController {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    @GetMapping
    public ResponseEntity<ResultResponseDto<String>> refresh(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        if(Objects.isNull(refreshToken)){
            throw new BusinessException(HttpStatus.BAD_REQUEST, "리프레시 토큰이 존재하지 않습니다.");
        }

        Long userId = jwtProvider.getId(refreshToken);
        String email = jwtProvider.getEmail(refreshToken);

        refreshTokenService.validate(userId, refreshToken); // 리프레시 토큰 DB 에 있는지 검사

        String accessToken = jwtProvider.generateAccessToken(userId, email);

        return ResponseMaker.createResponse(HttpStatus.OK, "리프레시 토큰 확인 성공", accessToken);
    }

}
