package com.example.linkcargo.domain.user;

import com.example.linkcargo.domain.user.dto.request.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User join(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new RuntimeException("해당 이메일의 회원이 이미 존재합니다.");
        }

        String encodedPw = bCryptPasswordEncoder.encode(registerRequestDTO.getPassword());
        User user = new User(registerRequestDTO.getEmail(), encodedPw);

        User savedUser = userRepository.save(user);
        log.info("savedUser = {}", savedUser);

        return savedUser;
    }
}
