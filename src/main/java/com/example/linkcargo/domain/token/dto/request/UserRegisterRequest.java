package com.example.linkcargo.domain.token.dto.request;

import com.example.linkcargo.domain.user.Role;
import com.example.linkcargo.domain.user.Status;
import com.example.linkcargo.domain.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;

public record UserRegisterRequest(

    @NotBlank(message = "Role is mandatory")
    Role role,

    @NotBlank(message = "First name is mandatory")
    String firstName,

    @NotBlank(message = "Last name is mandatory")
    String lastName,

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    String email,

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    String password,

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    String phoneNumber,

    @NotBlank(message = "Company name is mandatory")
    String companyName,

    @NotBlank(message = "Job title is mandatory")
    String jobTitle,

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Business number should be numeric and between 10 to 15 digits long")
    String businessNumber
) {

    public User toEntity() {
        return User.builder()
            .role(this.role)
            .firstName(this.firstName)
            .lastName(this.lastName)
            .email(this.email)
            .password(this.password)
            .phoneNumber(this.phoneNumber)
            .companyName(this.companyName)
            .jobTitle(this.jobTitle)
            .businessNumber(this.businessNumber)
            .status(Status.ENABLED) // 기본값 설정, 필요에 따라 조정 가능
            .totalPrice(BigDecimal.ZERO) // 기본값 설정, 필요에 따라 조정 가능
            .notifications(new ArrayList<>()) // 빈 리스트로 초기화
            .memberships(new ArrayList<>()) // 빈 리스트로 초기화
            .build();
    }

}
