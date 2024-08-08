package com.example.linkcargo.domain.chat.dto.response;

public record ChatContentResponse(
    Long chatRoomId,
    Long senderId,
    String content
) {
}
