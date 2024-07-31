package com.example.linkcargo.global.resolver;

import com.example.linkcargo.global.security.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Login.class);
    }

    @Override
    public LoginInfo resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        CustomUserDetail userDetails = (CustomUserDetail) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        System.out.println("userDetails = " + userDetails);
        Long userId = userDetails.getId();
        String email = userDetails.getUsername();

        return new LoginInfo(userId, email);
    }
}
