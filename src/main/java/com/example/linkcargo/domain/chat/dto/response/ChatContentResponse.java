package com.example.linkcargo.domain.chat.dto.response;

import java.time.LocalDateTime;

public record ChatContentResponse(
    Long chatRoomId,
    Long senderId,
    String content,
    LocalDateTime createdAt
) {
}
