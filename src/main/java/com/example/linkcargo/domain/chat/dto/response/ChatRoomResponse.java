package com.example.linkcargo.domain.chat.dto.response;

import com.example.linkcargo.domain.chat.Entity.RoomStatus;

public record ChatRoomResponse(
    Long chatRoomId,
    String title,
    RoomStatus status
) {

}
