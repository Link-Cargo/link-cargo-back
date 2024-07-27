package com.example.linkcargo.global.response.code.resultCode;

import com.example.linkcargo.global.response.code.BaseErrorCode;
import com.example.linkcargo.global.response.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// Enum Naming Format : {주체}_{이유}
// Message format : 동사 명사형으로 마무리
@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // Global
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL501", "서버 오류"),

    // User
    USER_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "USER401", "중복된 이메일입니다."),
    USER_EXISTS_BUSINESS_NUMBER(HttpStatus.BAD_REQUEST, "USER402", "중복된 사업자번호입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER403", "해당 정보의 유저를 찾을 수 없습니다."),

    // JWT
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401", "유효하지 않은 ACCESS 토큰입니다."),
    EXPIRED_MEMBER_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH402", "만료된 JWT 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH403", "지원되지 않는 JWT 토큰입니다."),
    ILLEGAL_ARGUMENT_TOKEN(HttpStatus.BAD_REQUEST, "AUTH404", "잘못된 JWT 토큰입니다."),
    JWT_NO_USER_INFO(HttpStatus.UNAUTHORIZED, "AUTH405", "토큰에 사용자 정보가 없습니다."),
    JWT_NO_AUTH_INFO(HttpStatus.UNAUTHORIZED, "AUTH406", "권한 정보가 없는 토큰입니다."),

    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH408", "유효하지 않은 REFRESH 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH409", "REFRESH 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH410", "REFRESH 토큰이 만료되었습니다."),

    // Schedule
    SCHEDULE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SCHEDULE401", "이미 존재하는 선박정보 입니다."),
    SCHEDULE_CREATED_FAIL(HttpStatus.NOT_FOUND, "SCHEDULE402", "선박 정보 생성에 실패하였습니다."),

    // Port
    IMPORT_PORT_NOT_FOUND(HttpStatus.NOT_FOUND, "PORT401", "존재 하지 않는 수입항 입니다."),
    EXPORT_PORT_NOT_FOUND(HttpStatus.NOT_FOUND, "PORT402", "존재 하지 않는 수출항 입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .httpStatus(httpStatus)
            .build();
    }
}
