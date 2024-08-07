package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.Entity.Chat;
import com.example.linkcargo.global.security.CustomUserDetail;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatRestController {

    private final ChatService chatService;

    /**
     * 채팅방 메시지 목록 조회(최근순)
     */
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<Chat>> getMessages(@PathVariable Long chatRoomId) {
        List<Chat> messages = chatService.getChatsByRoomId(chatRoomId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 채팅방 목록 조회
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getAllChatRooms(
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        List<ChatRoom> chatRooms = chatService.getChatRooms(userDetail.getId());
        return ResponseEntity.ok(chatRooms);
    }
}
