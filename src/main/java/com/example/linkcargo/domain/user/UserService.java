package com.example.linkcargo.domain.user;

import com.example.linkcargo.domain.user.dto.request.LoginRequestDTO;
import com.example.linkcargo.domain.user.dto.request.RegisterRequestDTO;
import com.example.linkcargo.global.jwt.JwtProvider;
import com.example.linkcargo.global.security.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;

    public User join(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new RuntimeException("해당 이메일의 회원이 이미 존재합니다.");
        }

        String encodedPw = bCryptPasswordEncoder.encode(registerRequestDTO.getPassword());
        User user = new User(registerRequestDTO.getEmail(), encodedPw);

        User savedUser = userRepository.save(user);

        return savedUser;
    }

    public String login(LoginRequestDTO loginRequestDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginRequestDTO.getEmail(),
            loginRequestDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        Long userId = customUserDetail.getId();
        String email = customUserDetail.getUsername();

        String jwt = jwtProvider.generateToken(userId, email);
        return jwt;
    }
}
