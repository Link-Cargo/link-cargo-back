package com.example.linkcargo.user;


import com.example.linkcargo.domain.token.RefreshTokenService;
import com.example.linkcargo.domain.user.Role;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserService;
import com.example.linkcargo.domain.token.dto.request.UserRegisterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    RefreshTokenService refreshTokenService;

//    @Test
//    void 정상_회원가입() {
//        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
//            Role.CONSIGNOR, // 예: Role.USER
//            "John", // 첫 이름
//            "Doe", // 마지막 이름
//            "john.doe@example.com", // 이메일
//            "password123", // 비밀번호
//            "+1234567890", // 전화번호
//            "TechCorp", // 회사 이름
//            "Software Engineer", // 직책
//            "1234567890" // 사업자 번호
//        );
//        User joinedUser = refreshTokenService.join(userRegisterRequest);
//        Assertions.assertAll(
//            () -> org.assertj.core.api.Assertions.assertThat(joinedUser.getFirstName()).isEqualTo(userRegisterRequest.firstName()),
//            () -> org.assertj.core.api.Assertions.assertThat(joinedUser.getLastName()).isEqualTo(userRegisterRequest.lastName()),
//            () -> org.assertj.core.api.Assertions.assertThat(joinedUser.getEmail()).isEqualTo(userRegisterRequest.email()),
//            () -> org.assertj.core.api.Assertions.assertThat(joinedUser.getPhoneNumber()).isEqualTo(userRegisterRequest.phoneNumber()),
//            () -> org.assertj.core.api.Assertions.assertThat(joinedUser.getCompanyName()).isEqualTo(userRegisterRequest.companyName()),
//            () -> org.assertj.core.api.Assertions.assertThat(joinedUser.getJobTitle()).isEqualTo(userRegisterRequest.jobTitle()),
//            () -> org.assertj.core.api.Assertions.assertThat(joinedUser.getBusinessNumber()).isEqualTo(userRegisterRequest.businessNumber())
//        );
//
//    }
}
