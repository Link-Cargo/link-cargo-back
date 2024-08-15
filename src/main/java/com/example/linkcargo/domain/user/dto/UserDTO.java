package com.example.linkcargo.domain.user.dto;

import com.example.linkcargo.domain.user.Role;
import com.example.linkcargo.domain.user.Status;

import java.math.BigDecimal;

public record UserDTO(
    Long id,
    Role role,
    String firstName,
    String lastName,
    String email,
    String password,
    String profile,
    String phoneNumber,
    String companyName,
    String jobTitle,
    String businessNumber,
    Status status,
    BigDecimal totalPrice
) {

    public record ForwardingDTO(
        String companyName,
        String businessNumber
    ) {
    }

}
