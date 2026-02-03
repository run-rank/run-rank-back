package com.example.runrankback.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Auth (400: 클라이언트 잘못)
    EMAIL_DUPLICATION(400, "AUTH001", "이미 사용 중인 이메일입니다."),
    LOGIN_INPUT_INVALID(401, "AUTH002", "이메일 또는 비밀번호가 잘못되었습니다."),
    UNAUTHORIZED(401, "AUTH003", "로그인이 필요한 서비스입니다."),
    INVALID_TOKEN(401, "AUTH004", "유효하지 않은 토큰입니다."),

    // User (404: 찾을 수 없음)
    USER_NOT_FOUND(404, "U001", "해당 사용자를 찾을 수 없습니다."),


    // Server (500: 서버 잘못)
    INTERNAL_SERVER_ERROR(500, "S001", "서버 내부 에러가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;




}
