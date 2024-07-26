package com.example.linkcargo.domain.user;

import static java.util.Objects.isNull;

import com.example.linkcargo.domain.user.dto.request.UserLoginRequest;
import com.example.linkcargo.domain.user.dto.request.UserRegisterRequest;
import com.example.linkcargo.domain.user.dto.response.UserLoginResponse;
import com.example.linkcargo.domain.user.dto.response.UserRegisterResponse;
import com.example.linkcargo.domain.user.refreshToken.RefreshTokenResponse;
import com.example.linkcargo.domain.user.refreshToken.RefreshTokenService;
import com.example.linkcargo.global.jwt.JwtProvider;
import com.example.linkcargo.global.jwt.TokenDTO;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.JwtHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    public ApiResponse<UserRegisterResponse> join(
        @Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        User joinedUser = userService.join(userRegisterRequest);
        return ApiResponse.onSuccess(new UserRegisterResponse(joinedUser.getId()));
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(
        @Valid @RequestBody UserLoginRequest userLoginRequest) {
        TokenDTO token = userService.login(userLoginRequest);
        return ApiResponse.onSuccess(new UserLoginResponse(token));
    }

    @GetMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        if (isNull(refreshToken)) {
            throw new JwtHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getId(refreshToken);
        String email = jwtProvider.getEmail(refreshToken);
        refreshTokenService.validate(userId, refreshToken); // 리프레시 토큰 유효성 검사

        String accessToken = jwtProvider.generateAccessToken(userId, email);
        String newRefreshToken = reGenerateRefreshToken(userId, email);

        return ApiResponse.onSuccess(
            new RefreshTokenResponse(new TokenDTO(accessToken, newRefreshToken)));
    }

    @Transactional
    public String reGenerateRefreshToken(Long userId, String email) {
        refreshTokenService.deleteByUserId(userId); // 기존 리프레시 토큰 삭제
        String newRefreshToken = jwtProvider.generateRefreshToken(userId, email);
        return newRefreshToken;
    }
}
