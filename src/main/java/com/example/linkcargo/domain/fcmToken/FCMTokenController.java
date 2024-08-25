package com.example.linkcargo.domain.fcmToken;

import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm-token")
public class FCMTokenController {

    private final FCMTokenService fcmTokenService;

    @PostMapping
    @Operation(summary = "FCM 토큰 전송", description = "FCM 토큰을 전송, 중복을 검사하여 저장")
    public void saveFCMToken(
        @Valid @RequestBody FCMTokenRequest fcmTokenRequest,
        @AuthenticationPrincipal CustomUserDetail userDetail

    ) {
        fcmTokenService.save(userDetail.getId(), fcmTokenRequest.token());
    }
}
