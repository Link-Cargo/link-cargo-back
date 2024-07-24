package com.example.linkcargo.domain.user;

import com.example.linkcargo.domain.user.dto.request.UserRegisterRequest;
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

    @PostMapping
    public void join(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        userService.join(userRegisterRequest);
    }
}
