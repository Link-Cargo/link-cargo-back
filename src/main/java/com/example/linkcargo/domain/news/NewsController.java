package com.example.linkcargo.domain.news;

import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.response.code.resultCode.SuccessStatus;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
@Tag(name = "12. News", description = "뉴스 관련 API")
public class NewsController {

    private final NewsService newsService;

    @PostMapping("/generate")
    @Operation(summary = "뉴스 생성", description = "관심사(환율, 수입국(상하이), 운임지수에 관한 뉴스 엔티티를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ETC401", description = "외부 API 호출 오류.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> createNews(
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        newsService.createNewsAboutInterest();
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}
