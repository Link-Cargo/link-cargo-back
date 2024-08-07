package com.example.linkcargo.domain.chat.dto;

public record ChatResponse(
    String status,       // 상태 메시지, 예: "Entered chat room", "Chat message sent"
    Long chatRoomId,     // 채팅방 ID (선택적)
    String content       // 메시지 내용 (선택적)
) {
    // 기본 생성자를 통해 필드 초기화
    public ChatResponse(String status) {
        this(status, null, null);
    }

    // 생성자 오버로딩을 통해 다른 경우도 처리
    public ChatResponse(String status, Long chatRoomId) {
        this(status, chatRoomId, null);
    }

    public ChatResponse(String status, String content) {
        this(status, null, content);
    }
}