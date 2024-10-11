package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.Entity.Chat;
import com.example.linkcargo.domain.chat.dto.request.ChatRequest.MessageType;
import com.example.linkcargo.domain.chat.dto.request.ChatRequest;
import com.example.linkcargo.domain.chat.dto.response.ChatContentResponse;
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

        // 채팅 "일반" 전송 메시지를 보낸 경우 (프론트엔드가 먼저 "/sub/chatroom/{id}" 를 구독한 상태)
        if (chatRequest.messageType().equals(ChatRequest.MessageType.CHAT)) {
            log.info("CHAT message, chatRequest: {}", chatRequest);
            ChatContentResponse chatContentResponseResponse = handleChatMessage(chatRequest, chatRoomId, userId);
            // 채팅방 최근 메시지 시간 업데이트
            chatService.updateChatRoomMessageUpdatedTime(chatRoomId);
            // 구독자들에게 메시지 송신
            messagingTemplate.convertAndSend("/sub/chatroom/" + chatRoomId,
                chatContentResponseResponse);
        }

        // 채팅 "파일" 전송 메시지를 보낸 경우 (마찬가지로, 프론트엔드가 먼저 "/sub/chatroom/{id}" 를 구독한 상태)
        if (chatRequest.messageType().equals(ChatRequest.MessageType.FILE)) {
            log.info("FILE message, chatRequest: {}", chatRequest);
            ChatContentResponse chatContentResponse = handleFileMessage(chatRequest, chatRoomId, userId);
            // 채팅방 최근 메시지 시간 업데이트
            chatService.updateChatRoomMessageUpdatedTime(chatRoomId);
            // 구독자들에게 메시지 송신
            messagingTemplate.convertAndSend("/sub/chatroom/" + chatRoomId,
                chatContentResponse);
        }

    }

    /**
     * CHAT 타입 메시지 전송 시
     */
    private ChatContentResponse handleChatMessage(ChatRequest chatRequest, Long chatRoomId, Long userId) {
        Chat chat = Chat.builder()
            .chatRoom(chatService.getChatRoom(chatRoomId))
            .sender(userService.getUser(userId))
            .messageType(MessageType.CHAT)
            .content(chatRequest.content())
            .fileName("")
            .fileUrl("")
            .isRead(false)
            .build();
        Chat savedChat = chatService.saveChat(chat);

        // 채팅 ID, 채팅방 ID, 작성자 ID, 내용
        return new ChatContentResponse(savedChat.getId(), chatRoomId, userId, MessageType.CHAT, chatRequest.content(), "", "", savedChat.getCreatedAt());
    }

    /**
     * FILE 타입 메시지 전송 시
     */
    private ChatContentResponse handleFileMessage(ChatRequest chatRequest, Long chatRoomId, Long userId) {
        Chat chat = Chat.builder()
            .chatRoom(chatService.getChatRoom(chatRoomId))
            .sender(userService.getUser(userId))
            .messageType(MessageType.FILE)
            .content("")
            .fileName(chatRequest.fileName())
            .fileUrl(chatRequest.fileUrl())
            .isRead(false)
            .build();
        Chat savedChat = chatService.saveChat(chat);

        // 채팅방 ID, 작성자 ID, 내용
        return new ChatContentResponse(savedChat.getId(), chatRoomId, userId, MessageType.FILE, "", chatRequest.fileName(), chatRequest.fileUrl(), savedChat.getCreatedAt());
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}

