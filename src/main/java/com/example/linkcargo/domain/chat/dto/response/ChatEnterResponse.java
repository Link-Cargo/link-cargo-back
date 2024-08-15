package com.example.linkcargo.domain.chat.dto.response;

import com.example.linkcargo.domain.chat.dto.request.ChatRequest.MessageType;

public record ChatEnterResponse(
    MessageType messageType,
    Long chatRoomId
) {
}
