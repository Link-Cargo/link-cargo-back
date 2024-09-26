package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.Entity.ChatRoom;
import com.example.linkcargo.domain.chat.dto.request.ChatRoomIdRequest;
import com.example.linkcargo.domain.chat.dto.response.ChatRoomIdResponse;
import com.example.linkcargo.domain.chat.dto.response.ChatContentResponse;
import com.example.linkcargo.domain.chat.dto.response.ChatContentsResponse;
import com.example.linkcargo.domain.chat.dto.response.ChatRoomResponse;
import com.example.linkcargo.domain.chat.dto.response.ChatRoomsResponse;
import com.example.linkcargo.domain.user.dto.response.FilesResponse;
import com.example.linkcargo.domain.user.UserS3Service;
import com.example.linkcargo.domain.user.dto.response.FileResponse;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "6. Chat", description = "채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatRestController {

    private final ChatService chatService;
    private final UserS3Service userS3Service;

    @Operation(summary = "특정 상대와의 채팅방 조회/생성", description = "기존 채팅방이 있으면 해당 채팅방 ID를, 없으면 생성 후 ID 반환합니다.")
    @PostMapping("/rooms")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ChatRoomIdResponse> getChatRoomId(
        @Valid @RequestBody ChatRoomIdRequest chatRoomIdRequest,
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        ChatRoom chatRoom = chatService.createOrGetChatRoom(userDetail.getId(), chatRoomIdRequest.targetUserId());
        return ApiResponse.onSuccess(new ChatRoomIdResponse(chatRoom.getId()));
    }


    @Operation(summary = "채팅방 메시지 목록 조회", description = "특정 채팅방의 메시지 목록을 조회합니다.")
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

    @Operation(summary = "채팅방의 S3에 파일 저장", description = "파일을 저장한 후 S3 객체 주소를 반환 받습니다.")
    @PostMapping("/{chatRoomId}/file")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHAT401", description = "파일 업로드에 실패했습니다.")
    })
    public ApiResponse<FileResponse> sendFileToChatRoom(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @PathVariable("chatRoomId") Long chatRoomId,
        @Valid @RequestParam("file") MultipartFile file
    ) {
        FileResponse fileResponse = userS3Service.uploadFile(file, chatRoomId, userDetail.getId());
        return ApiResponse.onSuccess(fileResponse);
    }

    @Operation(summary = "채팅방에 업로드 된 파일 목록 조회", description = "채팅방에 업로드된 파일 목록을 조회합니다.")
    @GetMapping("/{chatRoomId}/file")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<FilesResponse> getAllFilesInChatRoom(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @PathVariable("chatRoomId") Long chatRoomId
    ) {
        FilesResponse filesResponse = userS3Service.getAllObjectsInChatRoom(chatRoomId);
        return ApiResponse.onSuccess(filesResponse);
    }
}
