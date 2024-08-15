package com.example.linkcargo.domain.chat.dto.request;

public record ChatRequest(
    Long chatRoomId, // 채팅방 ID
    Long targetUserId, // 수신자 ID(채팅방 생성 시 필요)
    String content,  // 메시지 내용
    String fileName, // 파일 이름
    String fileUrl, // 파일이 저장된 S3 주소
    MessageType messageType // 메시지 타입
) {

    public enum MessageType {
        ENTER, // 입장 메시지
        CHAT, // 일반 채팅 메시지
        FILE, // 파일이 저장된 S3 주소 메시지
    }
}

/**
 * 채팅방 입장 메시지 형식
 * {
 *     "messageType": "ENTER"
 *     "targetUserId": "Number",
 * }
 *
 * 채팅 전송 메시지 형식
 * {
 *     "messageType": "CHAT"
 *     "chatRoomId": "Number",
 *     "content": "String",
 * }
 */