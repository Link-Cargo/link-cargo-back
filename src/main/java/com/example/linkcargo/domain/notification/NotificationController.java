package com.example.linkcargo.domain.notification;

import com.example.linkcargo.domain.notification.dto.response.NotificationPageResponse;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "11. Notification", description = "Notification 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 목록 조회 - 페이징", description = "알림 목록을 조회합니다. 페이징 파라미터 사용")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public NotificationPageResponse getNotifications(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호
        @RequestParam(value = "size", defaultValue = "10") int size // 페이지 크기
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Direction.DESC, "createdAt"));

        NotificationPageResponse notificationPageResponse = notificationService.getNotifications(
            userDetail.getId(), pageRequest);
        return notificationPageResponse;
    }

    @GetMapping("/unread")
    @Operation(summary = "읽지 않은 알림 목록 조회 - 페이징", description = "읽지 않은 알림 목록을 조회합니다. 페이징 파라미터 사용")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public NotificationPageResponse getUnReadNotifications(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호
        @RequestParam(value = "size", defaultValue = "10") int size // 페이지 크기
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Direction.DESC, "createdAt"));

        NotificationPageResponse notificationPageResponse = notificationService.getUnReadNotifications(
            userDetail.getId(), pageRequest);
        return notificationPageResponse;
    }

    @DeleteMapping
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @Operation(summary = "알림 목록 삭제", description = "알림을 모두 삭제합니다.")
    public void deleteAllAlarms(@AuthenticationPrincipal CustomUserDetail userDetail) {
        notificationService.deleteAllAlarms(userDetail.getId());
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "특정 알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION401", description = "해당 ID 의 NOTIFICATION 이 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public void markNotificationAsRead(@PathVariable("notificationId") Long notificationId) {
        notificationService.markAsRead(notificationId);
    }

    @PutMapping("/read")
    @Operation(summary = "모든 알림 읽음 처리", description = "모든 알림을 읽음 처리합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public void markAllNotificationAsRead(@AuthenticationPrincipal CustomUserDetail userDetail) {
        notificationService.markAllAsRead(userDetail.getId());
    }
}
