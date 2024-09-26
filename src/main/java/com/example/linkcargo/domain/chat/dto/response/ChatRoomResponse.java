package com.example.linkcargo.domain.chat.dto.response;

import com.example.linkcargo.domain.chat.Entity.RoomStatus;

public record ChatRoomResponse(
    Long chatRoomId,
    Long targetUserId,
    String targetUserName, // 포워더 직원 이름
    String targetUserPosition,// 포워더 직원의 직책
    String targetUserCompany, // 포워더 직원의 회사
    String schedule, // 포워더 회사에게 문의한 스케줄
    String title, //채팅방 제목
    String latestContent, // 가장 최근 대화
    RoomStatus status
) {

}
