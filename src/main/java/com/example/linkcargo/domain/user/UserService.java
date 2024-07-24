package com.example.linkcargo.domain.user;

import com.example.linkcargo.domain.user.dto.request.UserLoginRequest;
import com.example.linkcargo.domain.user.dto.request.UserRegisterRequest;
import com.example.linkcargo.global.jwt.JwtProvider;
import com.example.linkcargo.global.jwt.TokenDTO;
import com.example.linkcargo.global.security.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;

    public User join(UserRegisterRequest userRegisterRequest) {
        validateEmail(userRegisterRequest.email());
        validateBusinessNumber(userRegisterRequest.businessNumber());

        String encodedPw = passwordEncoder.encode(userRegisterRequest.password());
        User user = userRegisterRequest.toEntity();
        user.updatePassword(encodedPw);

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public TokenDTO login(UserLoginRequest userLoginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userLoginRequest.email(), userLoginRequest.password());

        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TokenDTO tokenDTO = createTokens(customUserDetail);

        return tokenDTO;
    }

    private TokenDTO createTokens(CustomUserDetail customUserDetail) {
        String accessToken = jwtProvider.generateAccessToken(customUserDetail.getId(),
            customUserDetail.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(customUserDetail.getId(),
            customUserDetail.getUsername());

        return new TokenDTO(accessToken, refreshToken);
    }


    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("해당 이메일의 회원이 이미 존재합니다.");
        }
    }
    private void validateBusinessNumber(String businessNumber) {
        if (userRepository.existsByBusinessNumber(businessNumber)) {
            throw new RuntimeException("해당 사업자 번호의 회원이 이미 존재합니다.");
        }
    }

}
