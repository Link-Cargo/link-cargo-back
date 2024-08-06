package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.dto.request.ChatRequest;
import com.example.linkcargo.domain.chat.dto.request.ChatRoomRequest;
import com.example.linkcargo.global.security.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    /**
     * 채팅방 생성
     */
    @PostMapping
    public ResponseEntity<ChatRoom> createChatRoom(
        @RequestBody ChatRoomRequest chatRoomRequest,
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        ChatRoom chatRoom = chatService.createOrGetChatRoom(userDetail.getId(),
            chatRoomRequest.targetUserId());

        // 현재 사용자가 채팅방에 추가되지 않았는지 확인
        boolean userAlreadyInChatRoom = chatService.isUserInChatRoom(userDetail.getId(), chatRoom.getId());
        if (!userAlreadyInChatRoom) {
            chatService.addUserToChatRoom(userDetail.getId(), chatRoom.getId());
        }

        // 대상 사용자가 채팅방에 추가되지 않았는지 확인
        boolean targetUserAlreadyInChatRoom = chatService.isUserInChatRoom(chatRoomRequest.targetUserId(), chatRoom.getId());
        if (!targetUserAlreadyInChatRoom) {
            chatService.addUserToChatRoom(chatRoomRequest.targetUserId(), chatRoom.getId());
        }

        return ResponseEntity.ok(chatRoom);
    }

//    /**
//     * 채팅방 메시지 조회
//     */
//    @GetMapping("/{chatRoomId}/messages")
//    public ResponseEntity<List<Chat>> getMessages(@PathVariable Long chatRoomId) {
//        List<Chat> messages = chatService.getChatsByRoomId(chatRoomId);
//        return ResponseEntity.ok(messages);
//    }

    /**
     * 채팅방 목록 조회
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getAllChatRooms(
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        List<ChatRoom> chatRooms = chatService.getAllChatRooms(userDetail.getId());
        return ResponseEntity.ok(chatRooms);
    }
}
