package com.example.linkcargo.domain.token;

import static java.util.Objects.isNull;

import com.example.linkcargo.domain.token.dto.request.UserLoginRequest;
import com.example.linkcargo.domain.token.dto.request.UserRegisterRequest;
import com.example.linkcargo.domain.token.dto.response.TokenResponse;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserRepository;
import com.example.linkcargo.global.jwt.JwtProvider;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.JwtHandler;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import com.example.linkcargo.global.security.CustomUserDetail;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     * 회원가입
     */
    public User join(UserRegisterRequest userRegisterRequest) {
        validateEmail(userRegisterRequest.email());
        validateBusinessNumber(userRegisterRequest.businessNumber());

        String encodedPw = passwordEncoder.encode(userRegisterRequest.password());
        User user = userRegisterRequest.toEntity();
        user.updatePassword(encodedPw);

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    /**
     * 로그인
     */
    public TokenResponse login(UserLoginRequest userLoginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userLoginRequest.email(), userLoginRequest.password());

        try {
            Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TokenResponse tokenResponse = createTokens(customUserDetail);
            RefreshToken refreshToken = new RefreshToken(customUserDetail.getId(),
                tokenResponse.refreshToken());
            // 기존 리프레시 토큰 제거 
            refreshTokenRepository.deleteByUserId(customUserDetail.getId());
            // 새 리프레시 토큰 저장
            refreshTokenRepository.save(refreshToken);
            return tokenResponse;
        } catch (AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                throw new UsersHandler(ErrorStatus.USER_NOT_FOUND);
            }
            throw new UsersHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 토큰 생성
     */
    private TokenResponse createTokens(CustomUserDetail customUserDetail) {
        String accessToken = jwtProvider.generateAccessToken(customUserDetail.getId(),
            customUserDetail.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(customUserDetail.getId(),
            customUserDetail.getUsername());

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * 토큰 재생성(리프레시)
     */
    public TokenResponse reCreateTokens(String refreshToken) {
        if (isNull(refreshToken)) {
            throw new JwtHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getId(refreshToken);
        String email = jwtProvider.getEmail(refreshToken);
        validate(userId, refreshToken); // 리프레시 토큰 유효성 검사

        String accessToken = jwtProvider.generateAccessToken(userId, email);
        deleteByUserId(userId); // 기존 리프레시 토큰 삭제
        // 새 리프레시 토큰 생성 및 저장
        String newRefreshToken = jwtProvider.generateRefreshToken(userId, email);
        refreshTokenRepository.save(new RefreshToken(userId, newRefreshToken));
        return new TokenResponse(accessToken, newRefreshToken);
    }

    /**
     * 리프레시 토큰 삭제
     */
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * 리프레시 토큰 유효성 검증
     */
    public void validate(Long userId, String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByUserIdAndToken(userId, refreshToken)
            .orElseThrow(() -> new JwtHandler(ErrorStatus.REFRESH_TOKEN_NOT_FOUND));

        Date expirationDate = jwtProvider.getExpiration(tokenEntity.getToken());
        if (expirationDate.before(new Date())) {
            throw new JwtHandler(ErrorStatus.REFRESH_TOKEN_EXPIRED);
        }
    }

    /**
     * 이메일 중복되는지 검사
     */
    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UsersHandler(ErrorStatus.USER_EXISTS_EMAIL);
        }
    }

    /**
     * 사업자 번호 중복되는지 검사
     */
    private void validateBusinessNumber(String businessNumber) {
        if (userRepository.existsByBusinessNumber(businessNumber)) {
            throw new UsersHandler(ErrorStatus.USER_EXISTS_BUSINESS_NUMBER);
        }
    }
}
