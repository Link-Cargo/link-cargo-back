package com.example.linkcargo.domain.user;

import com.example.linkcargo.domain.user.dto.request.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User join(UserRegisterRequest userRegisterRequest) {
        validateEmail(userRegisterRequest.email());
        validateBusinessNumber(userRegisterRequest.businessNumber());

        String encodedPw = passwordEncoder.encode(userRegisterRequest.password());
        User user = userRegisterRequest.toEntity();
        user.updatePassword(encodedPw);

        User savedUser = userRepository.save(user);
        return savedUser;
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
