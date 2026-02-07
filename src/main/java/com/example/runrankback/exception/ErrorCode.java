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

    // Kakao (카카오 관련 오류)
    INVALID_KAKAO_TOKEN(401, "KAKAO001", "유효하지 않은 카카오 토큰입니다."),
    KAKAO_API_ERROR(500, "KAKAO002", "카카오 API 호출 중 오류가 발생했습니다."),
    KAKAO_EMAIL_NOT_PROVIDED(400, "KAKAO003", "카카오 계정에서 이메일 정보를 제공받지 못했습니다."),

    // Profile (프로필 관련 오류)
    FILE_NOT_FOUND(400, "PROFILE001", "파일이 존재하지 않습니다."),
    FILE_SIZE_EXCEEDED(400, "PROFILE002", "파일 크기가 5MB를 초과했습니다."),
    INVALID_FILE_TYPE(400, "PROFILE003", "지원하지 않는 파일 형식입니다. (JPEG, PNG, GIF, WEBP만 지원)"),
    FILE_UPLOAD_FAILED(500, "PROFILE004", "파일 업로드에 실패했습니다."),
    KAKAO_PROFILE_CANNOT_MODIFY(400, "PROFILE005", "카카오 로그인 사용자는 프로필 이미지를 직접 수정할 수 없습니다. 카카오에서 변경해주세요."),

    // Server (500: 서버 잘못)
    INTERNAL_SERVER_ERROR(500, "S001", "서버 내부 에러가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;




}
