package com.example.linkcargo.domain.user;

import com.example.linkcargo.domain.user.dto.request.LoginRequestDTO;
import com.example.linkcargo.domain.user.dto.request.RegisterRequestDTO;
import com.example.linkcargo.domain.user.dto.response.LoginResponseDTO;
import com.example.linkcargo.domain.user.dto.response.RegisterResponseDTO;
import com.example.linkcargo.global.response.ResponseMaker;
import com.example.linkcargo.global.response.ResultResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserService userService;

    /**
     * 회원 가입
     */
    @PostMapping("/register")
    public ResponseEntity<ResultResponseDto<RegisterResponseDTO>> register(
        @Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        User joinedUser = userService.join(registerRequestDTO);

        RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO(joinedUser.getId(),
            joinedUser.getEmail());
        return ResponseMaker.createResponse(HttpStatus.OK, "회원 가입 성공", registerResponseDTO);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ResultResponseDto<LoginResponseDTO>> login(
        @Valid @RequestBody LoginRequestDTO loginRequestDTO
    ) {
        String jwt = userService.login(loginRequestDTO);

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(jwt);

        return ResponseMaker.createResponse(HttpStatus.OK, "로그인 성공", loginResponseDTO);
    }
}
