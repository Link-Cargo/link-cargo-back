package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.dto.response.ChatContentResponse;
import com.example.linkcargo.domain.chat.dto.response.ChatContentsResponse;
import com.example.linkcargo.domain.chat.dto.response.ChatRoomResponse;
import com.example.linkcargo.domain.chat.dto.response.ChatRoomsResponse;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "6. Chat", description = "채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatRestController {

    private final ChatService chatService;

    // TODO 이거 오래된 순으로 바꿔야 할 듯
    @Operation(summary = "채팅방 메시지 목록 조회(최근순)", description = "특정 채팅방의 메시지 목록을 조회합니다.")
    @GetMapping("/{chatRoomId}/messages")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ChatContentsResponse> getMessages(@PathVariable Long chatRoomId) {
        List<ChatContentResponse> chatContentResponses = chatService.getChatsByRoomId(chatRoomId);
        return ApiResponse.onSuccess(new ChatContentsResponse(chatContentResponses));
    }

    @Operation(summary = "채팅방 목록 조회", description = "유저의 채팅방의 목록을 조회합니다.")
    @GetMapping("/rooms")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ChatRoomsResponse> getAllChatRooms(
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        List<ChatRoomResponse> chatRoomResponses = chatService.getChatRooms(userDetail.getId());
        return ApiResponse.onSuccess(new ChatRoomsResponse(chatRoomResponses));
    }
}
