package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.Entity.Chat;
import com.example.linkcargo.domain.chat.dto.ChatRequest;
import com.example.linkcargo.domain.chat.dto.ChatRequest.MessageType;
import com.example.linkcargo.domain.chat.dto.ChatResponse;
import com.example.linkcargo.domain.user.UserService;
import com.example.linkcargo.global.security.CustomUserDetail;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

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
        CustomUserDetail userDetail = (CustomUserDetail) accessor.getSessionAttributes().get("user");

        Long chatRoomId = chatRequest.chatRoomId();
        Long userId = userDetail.getId();

        if (chatRequest.messageType().equals(ChatRequest.MessageType.ENTER)) {
            ChatResponse chatResponse = handleEnterMessage(chatRequest, userId);
            messagingTemplate.convertAndSendToUser(userId.toString(), "/sub/enterResponse", chatResponse);
        }

        if (chatRequest.messageType().equals(ChatRequest.MessageType.CHAT)) {
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

    private Long getCurrentUserId() {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal();
        return userDetail.getId(); // Assuming username is the user ID
    }
}

