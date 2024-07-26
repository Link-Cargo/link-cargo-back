package com.example.linkcargo.domain.user;

import com.example.linkcargo.domain.user.dto.request.UserLoginRequest;
import com.example.linkcargo.domain.user.dto.request.UserRegisterRequest;
import com.example.linkcargo.domain.user.refreshToken.RefreshTokenService;
import com.example.linkcargo.global.jwt.JwtProvider;
import com.example.linkcargo.global.jwt.TokenDTO;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import com.example.linkcargo.global.security.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

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
        refreshTokenService.save(customUserDetail.getId(), tokenDTO.refreshToken());

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
            throw new UsersHandler(ErrorStatus.USER_EXISTS_EMAIL);
        }
    }

    private void validateBusinessNumber(String businessNumber) {
        if (userRepository.existsByBusinessNumber(businessNumber)) {
            throw new UsersHandler(ErrorStatus.USER_EXISTS_BUSINESS_NUMBER);
        }
    }

}
