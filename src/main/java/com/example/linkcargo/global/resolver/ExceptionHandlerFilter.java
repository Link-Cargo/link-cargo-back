package com.example.linkcargo.global.resolver;

import com.example.linkcargo.global.response.code.BaseErrorCode;
import com.example.linkcargo.global.response.exception.handler.JwtHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtHandler e) {
            System.out.println(e.getCode());
            System.out.println(e.getCode().getReason());
            System.out.println(e.getCode().getReasonHttpStatus().getHttpStatus());

            setErrorResponse(response, e.getCode());
        }
    }

    private void setErrorResponse(
        HttpServletResponse response,
        BaseErrorCode errorCode
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(errorCode.getReasonHttpStatus().getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(false, errorCode.getReason().getCode(),
            errorCode.getReason().getMessage());
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public record ErrorResponse(
        boolean isSuccess,
        String code,
        String msg
    ) {

    }
}
