package com.example.linkcargo.domain.user;

import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.response.code.resultCode.SuccessStatus;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "7. User", description = "유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/profile")
public class UserController {

    private final UserS3Service userS3Service;

    @PostMapping
    @Operation(summary = "내 프로필 이미지 업로드/수정(기존 프로필 존재 시 제거)", description = "프로필을 업로드 합니다(기존 프로필 존재 시 제거)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "S3401", description = "잘못된 형식의 파일입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "프로필 업로드에 실패했습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<SuccessStatus> uploadProfileImage(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestParam("profile") MultipartFile file
    ) {
        // S3 서비스에서 프로필 이미지 업로드를 처리
        userS3Service.uploadProfileImage(userDetail.getId(), file);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @DeleteMapping
    @Operation(summary = "내 프로필 이미지 초기화", description = "프로필 이미지를 기본 이미지로 초기화")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER403", description = "해당 정보의 유저를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> resetProfileImage(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        // S3 서비스에서 기존 프로필 이미지를 삭제
        userS3Service.deleteExistingImage(userDetail.getId());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

}
