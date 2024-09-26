package com.example.linkcargo.domain.chat.dto.request;

public record ChatRoomIdRequest(
    Long targetUserId,
    String schedule
) {

}
