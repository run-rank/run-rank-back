package com.example.runrankback.security.jwt;

import com.example.runrankback.exception.ErrorCode;
import com.example.runrankback.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // JSON 변환을 위한 Jackson 도구

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 1. 응답 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 세팅

        // 2. 우리가 만든 ErrorResponse 객체 생성
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message(ErrorCode.UNAUTHORIZED.getMessage())
                .build();

        // 3. JSON으로 변환하여 응답 본문에 작성
        String result = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(result);
    }
}
