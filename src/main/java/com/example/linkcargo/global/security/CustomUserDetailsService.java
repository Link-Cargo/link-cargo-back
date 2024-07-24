package com.example.linkcargo.global.security;

import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetail loadUserByUsername(String email) throws UsernameNotFoundException {
        User findUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("로그인 정보가 올바르지 않습니다."));

        if (findUser != null) {
            return new CustomUserDetail(findUser);
        }

        return null;
    }
}