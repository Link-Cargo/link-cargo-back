package com.example.linkcargo.global.response.code.resultCode;

import com.example.linkcargo.global.response.code.BaseCode;
import com.example.linkcargo.global.response.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    _OK(HttpStatus.OK, "COMMON200", "성공입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
            .message(message)
            .code(code)
            .isSuccess(true)
            .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
            .message(message)
            .code(code)
            .isSuccess(true)
            .httpStatus(httpStatus)
            .build();
    }
}