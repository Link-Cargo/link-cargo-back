package com.example.linkcargo.domain.user;

import com.example.linkcargo.domain.user.dto.request.UserLoginRequest;
import com.example.linkcargo.domain.user.dto.request.UserRegisterRequest;
import com.example.linkcargo.domain.user.dto.response.UserLoginResponse;
import com.example.linkcargo.domain.user.dto.response.UserRegisterResponse;
import com.example.linkcargo.global.jwt.TokenDTO;
import com.example.linkcargo.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    @PostMapping("register")
    public ApiResponse<UserRegisterResponse> join(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        User joinedUser = userService.join(userRegisterRequest);
        return ApiResponse.onSuccess(new UserRegisterResponse(joinedUser.getId()));
    }

    @PostMapping("login")
    public ApiResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        TokenDTO token = userService.login(userLoginRequest);
        return ApiResponse.onSuccess(new UserLoginResponse(token));
    }
}
