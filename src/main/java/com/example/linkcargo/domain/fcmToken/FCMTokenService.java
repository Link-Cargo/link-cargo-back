package com.example.linkcargo.domain.fcmToken;

import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FCMTokenService {

    private final FCMTokenRepository fcmTokenRepository;
    private final UserService userService;

    @Transactional
    public void save(Long userId, String token) {
        User user = userService.getUser(userId);
        Optional<FCMToken> fcmToken = fcmTokenRepository.findByUser(user);
        // 기존에 FCM 토큰이 존재하지 않는 경우 -> 새로 저장
        if(fcmToken.isEmpty()){
            fcmTokenRepository.save(new FCMToken(user, token));
            return;
        }
        // 기존에 FCM 토큰이 존재하는 경우 -> 업데이트
        fcmToken.get().update(token);
    }
}
