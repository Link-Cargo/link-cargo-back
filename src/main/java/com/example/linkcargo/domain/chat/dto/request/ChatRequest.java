package com.example.linkcargo.domain.chat.dto.request;

public record ChatRequest(
    Long chatRoomId, // 채팅방 ID

    // TODO 어차피 발신자는 로그인한 사용자이므로, JWT 필터를 거쳐서 시큐리티 컨텍스트에 저장된 Authentication 객체를 조회하여 사용하는 방식으로 하자
    // 따라서 발신자 ID 필드는 필요없을 듯
    // 만약 JWT 필터를 안거치는 경우엔 발신자 ID 필드가 필요할 것 같음ㅡ 이 부분은 어떻게 구현할 지 생각해보자.
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