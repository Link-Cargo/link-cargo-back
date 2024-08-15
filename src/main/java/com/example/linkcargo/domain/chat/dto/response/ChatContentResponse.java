package com.example.linkcargo.domain.chat.dto.response;

import com.example.linkcargo.domain.chat.dto.request.ChatRequest.MessageType;
import java.time.LocalDateTime;

public record ChatContentResponse(
    Long chatRoomId,
    Long senderId,
    MessageType messageType,
    String content,
    String fileName,
    String fileUrl,
    LocalDateTime createdAt
) {
}
