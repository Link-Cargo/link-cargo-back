package com.example.linkcargo.domain.token;


import com.example.linkcargo.domain.token.dto.request.UserLoginRequest;
import com.example.linkcargo.domain.token.dto.request.UserRegisterRequest;
import com.example.linkcargo.domain.token.dto.response.TokenResponse;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "0. Register, Login, Refresh", description = "회원 가입, 로그인, 리프레시 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;


    @Operation(summary = "회원 가입", description = "회원 가입을 수행합니다. UserRegisterRequest 사용")
    @PostMapping("/register")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "이미 존재하는 이메일 입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER402", description = "이미 존재하는 사업자 번호 입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<Long> join(
        @Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        User joinedUser = refreshTokenService.join(userRegisterRequest);
        return ApiResponse.onSuccess(joinedUser.getId());
    }

    @Operation(summary = "로그인", description = "로그인을 수행합니다. UserLoginRequest 사용")
    @PostMapping("/login")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER403", description = "해당 정보의 유저를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<TokenResponse> login(
        @Valid @RequestBody UserLoginRequest userLoginRequest) {
        TokenResponse tokenResponse = refreshTokenService.login(userLoginRequest);
        return ApiResponse.onSuccess(tokenResponse);
    }

    @Operation(summary = "토큰 재발급", description = "토큰을 재발급합니다. 헤더의 Refresh-Token 사용")
    @GetMapping("/refresh")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH408", description = "유효하지 않은 REFRESH 토큰입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<TokenResponse> refresh(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        TokenResponse tokenResponse = refreshTokenService.reCreateTokens(refreshToken);
        return ApiResponse.onSuccess(tokenResponse);
    }

}
