package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.Entity.Chat;
import com.example.linkcargo.domain.chat.dto.ChatRequest;
import com.example.linkcargo.domain.chat.dto.ChatResponse;
import com.example.linkcargo.domain.user.UserService;
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
public class ChatStompController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatStompController(ChatService chatService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void handleChatMessage(Message<?> message, ChatRequest chatRequest) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        Long chatRoomId = chatRequest.chatRoomId();
        Long userId = (Long) accessor.getSessionAttributes().get("userId");
        String sessionId = accessor.getSessionId();

        // 채팅방 입장 메시지를 보낸 경우 (프론트엔드가 "/user/queue/reply" 를 구독한 상태)
        if (chatRequest.messageType().equals(ChatRequest.MessageType.ENTER)) {
            log.info("ENTER message, chatRequest: {}", chatRequest);
            ChatResponse chatResponse = handleEnterMessage(chatRequest, userId);
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/reply", chatResponse, createHeaders(sessionId));
            log.info("Message sent to /user/queue/reply for session: {}", sessionId);
        }

        // 채팅 전송 메시지를 보낸 경우 (프론트엔드가 "/sub/chatroom/{id}" 를 구독한 상태)
        if (chatRequest.messageType().equals(ChatRequest.MessageType.CHAT)) {
            log.info("CHAT message, chatRequest: {}", chatRequest);
            ChatResponse chatResponse = handleChatMessage(chatRequest, chatRoomId, userId);
            messagingTemplate.convertAndSend("/sub/chatroom/" + chatRoomId, chatResponse);
        }
    }

    private ChatResponse handleEnterMessage(ChatRequest chatRequest, Long userId) {
        Long targetUserId = chatRequest.targetUserId();
        ChatRoom chatRoom = chatService.createOrGetChatRoom(userId, targetUserId);

        return new ChatResponse("Entered chat room", chatRoom.getId());
    }

    private ChatResponse handleChatMessage(ChatRequest chatRequest, Long chatRoomId, Long userId) {
        Chat chat = Chat.builder()
            .chatRoom(chatService.getChatRoom(chatRoomId))
            .sender(userService.getUser(userId))
            .content(chatRequest.content())
            .build();
        chatService.saveChat(chat);

        return new ChatResponse("Chat message sent", chatRoomId, chatRequest.content());
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}

