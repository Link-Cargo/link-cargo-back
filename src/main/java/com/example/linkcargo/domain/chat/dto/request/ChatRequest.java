package com.example.linkcargo.domain.chat.dto.request;

public record ChatRequest(
    Long chatRoomId, // 채팅방 ID
    Long senderId,   // 발신자 ID
    Long targetUserId, // 수신자 ID(채팅방 생성 시 필요)
    String content,  // 메시지 내용
    MessageType messageType // 메시지 타입
) {

    public enum MessageType {
        ENTER, // 입장 메시지
        CHAT // 일반 채팅 메시지
    }
}