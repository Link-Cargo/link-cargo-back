package com.example.linkcargo.global.response;

/**
 * 메시지, "데이터" 전달
 */
public record ResultResponseDto<T>(String message, T data) {

}
