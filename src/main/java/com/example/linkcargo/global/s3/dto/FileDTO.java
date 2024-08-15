package com.example.linkcargo.global.s3.dto;

public record FileDTO(
    String name,
    String url,
    String createdAt
) {
}