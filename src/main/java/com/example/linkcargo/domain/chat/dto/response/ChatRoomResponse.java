package com.example.linkcargo.domain.chat.dto.response;

import com.example.linkcargo.domain.chat.Entity.RoomStatus;

public record ChatRoomResponse(
    Long chatRoomId,
    Long targetUserId,
    String targetUserName,
    String targetUserCompany, // 포워더 직원의 회사
    String title,
    String schedule, // 포워더 회사에게 문의한 스케줄
    String latestContent,
    RoomStatus status
) {

}
