package com.example.linkcargo.domain.user;

import com.example.linkcargo.domain.user.dto.request.UserPasswordUpdateRequest;
import com.example.linkcargo.domain.user.dto.response.UserResponse;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));
    }

    public UserResponse getUserProfile(Long userId) {
        User user = getUser(userId);
        return user.toUserResponse();
    }

    public void changePassword(Long userId, UserPasswordUpdateRequest userPasswordUpdateRequest) {
        User user = getUser(userId);

        // 기존 비밀번호가 일치하는지 확인 
        if(!passwordEncoder.matches(userPasswordUpdateRequest.existPassword(), user.getPassword())){
            throw new UsersHandler(ErrorStatus.USER_NOT_FOUND);
        }
        // 일치하면 새 비밀번호로 변경
        String encodedNewPassword = passwordEncoder.encode(userPasswordUpdateRequest.newPassword());
        user.updatePassword(encodedNewPassword);
    }

    public List<User> findAllUsersByRole(Role role) {
        List<User> users = userRepository.findAllByRole(role);
        return users;
    }

    public String getEmailByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));
        return user.getEmail();
    }

    public List<String> getAllEmailByUserRole(Role role) {
        List<String> emails = userRepository.findAllByRole(role).stream().map(user -> user.getEmail()).toList();
        return emails;
    }
}
