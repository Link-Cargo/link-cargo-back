package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.Entity.Chat;
import com.example.linkcargo.domain.chat.Entity.ChatRoom;
import com.example.linkcargo.domain.chat.dto.response.ChatEnterResponse;
import com.example.linkcargo.domain.chat.dto.request.ChatRequest;
import com.example.linkcargo.domain.chat.dto.response.ChatContentResponse;
import com.example.linkcargo.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void handleChatMessage(Message<?> message, ChatRequest chatRequest) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        Long chatRoomId = chatRequest.chatRoomId();
        Long userId = (Long) accessor.getSessionAttributes().get("userId");
        String sessionId = accessor.getSessionId();

        // 채팅방 입장 메시지를 보낸 경우 (프론트엔드가 먼저 "/user/queue/reply" 를 구독한 상태)
        if (chatRequest.messageType().equals(ChatRequest.MessageType.ENTER)) {
            log.info("ENTER message, chatRequest: {}", chatRequest);
            ChatEnterResponse chatEnterResponse = handleEnterMessage(chatRequest, userId);
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/reply", chatEnterResponse, createHeaders(sessionId));
            log.info("Message sent to /user/queue/reply for session: {}", sessionId);
        }

        // 채팅 전송 메시지를 보낸 경우 (프론트엔드가 먼저 "/sub/chatroom/{id}" 를 구독한 상태)
        if (chatRequest.messageType().equals(ChatRequest.MessageType.CHAT)) {
            log.info("CHAT message, chatRequest: {}", chatRequest);
            ChatContentResponse chatContentResponseResponse = handleChatMessage(chatRequest, chatRoomId, userId);
            messagingTemplate.convertAndSend("/sub/chatroom/" + chatRoomId,
                chatContentResponseResponse);
        }
    }

    private ChatEnterResponse handleEnterMessage(ChatRequest chatRequest, Long userId) {
        Long targetUserId = chatRequest.targetUserId();
        ChatRoom chatRoom = chatService.createOrGetChatRoom(userId, targetUserId);

        return new ChatEnterResponse(chatRoom.getId());
    }

    private ChatContentResponse handleChatMessage(ChatRequest chatRequest, Long chatRoomId, Long userId) {
        Chat chat = Chat.builder()
            .chatRoom(chatService.getChatRoom(chatRoomId))
            .sender(userService.getUser(userId))
            .content(chatRequest.content())
            .build();
        chatService.saveChat(chat);

        // 채팅방 ID, 작성자 ID, 내용
        return new ChatContentResponse(chatRoomId, userId, chatRequest.content());
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}

