package com.example.linkcargo.domain.chat.dto.response;

import java.util.List;

public record ChatRoomsResponse(
    List<ChatRoomResponse> chatRooms
) {

}
