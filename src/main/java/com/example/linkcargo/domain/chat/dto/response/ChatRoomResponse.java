package com.example.linkcargo.domain.chat.dto.response;

import com.example.linkcargo.domain.chat.Entity.RoomStatus;

public record ChatRoomResponse(
    Long chatRoomId,
    Long targetUserId,
    String targetUserName,
    String title,
    String latestContent,
    RoomStatus status
) {

}
