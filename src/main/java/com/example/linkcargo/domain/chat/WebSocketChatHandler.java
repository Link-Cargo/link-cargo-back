package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.dto.request.ChatRequest;
import com.example.linkcargo.domain.chat.dto.request.ChatRequest.MessageType;
import com.example.linkcargo.domain.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final ChatService chatService;
    private final UserService userService;

    private final Set<WebSocketSession> sessions = new HashSet<>();
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 연결됨", session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

        ChatRequest chatRequest = mapper.readValue(payload, ChatRequest.class);
        log.info("session {}", chatRequest.toString());

        Long chatRoomId = chatRequest.chatRoomId();
        Long userId = chatRequest.senderId();

        if (chatRequest.messageType().equals(MessageType.ENTER)) {
            handleEnterMessage(session, chatRequest, userId);
        }

        if (chatRequest.messageType().equals(MessageType.CHAT)) {
            handleChatMessage(chatRequest, chatRoomId, userId);
        }
    }

    /**
     * 채팅방 입장 메시지
     */
    private void handleEnterMessage(WebSocketSession session, ChatRequest chatRequest, Long userId) throws IOException {
        Long targetUserId = chatRequest.targetUserId();

        ChatRoom chatRoom = chatService.createOrGetChatRoom(userId, targetUserId);

        chatRoomSessionMap.computeIfAbsent(chatRoom.getId(), k -> new HashSet<>());
        Set<WebSocketSession> chatRoomSession = chatRoomSessionMap.get(chatRoom.getId());

        if (!chatRoomSession.contains(session)) {
            chatRoomSession.add(session);

            if (!chatService.isUserInChatRoom(userId, chatRoom.getId())) {
                chatService.addUserToChatRoom(userId, chatRoom.getId());
            }

            if (!chatService.isUserInChatRoom(targetUserId, chatRoom.getId())) {
                chatService.addUserToChatRoom(targetUserId, chatRoom.getId());
            }
        }

        // 채팅방 ID를 클라이언트로 전송
        Map<String, Object> response = new HashMap<>();
        response.put("messageType", MessageType.ENTER);
        response.put("chatRoomId", chatRoom.getId());
        sendMessage(session, response); // 클라이언트 세션에게 채팅방 ID 전송
    }

    /**
     * 채팅 전송 메시지
     */
    private void handleChatMessage(ChatRequest chatRequest, Long chatRoomId, Long userId) throws IOException {
        Chat chat = Chat.builder()
            .chatRoom(chatService.getChatRoom(chatRoomId))
            .sender(userService.getUser(userId))
            .content(chatRequest.content())
            .build();
        chatService.saveChat(chat);

        Set<WebSocketSession> chatRoomSession = chatRoomSessionMap.get(chatRoomId);
        sendMessageToChatRoom(chatRequest, chatRoomSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());
        sessions.remove(session);
        for (Set<WebSocketSession> chatRoomSessions : chatRoomSessionMap.values()) {
            chatRoomSessions.remove(session);
        }
    }

    private void sendMessageToChatRoom(ChatRequest chatRequest, Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.parallelStream().forEach(sess -> sendMessage(sess, chatRequest));
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}