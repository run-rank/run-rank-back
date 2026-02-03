package com.example.runrankback.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j // 로그를 찍기 위해 추가 (Lombok)
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("CustomException 발생: {}", errorCode.getMessage()); // 서버 콘솔에 로그 남기기

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        // 500 에러 방지를 위해 HttpStatusCode.valueOf()를 쓰거나
        // 직접 숫자를 넘기는 대신 아래와 같이 작성 권장
        return ResponseEntity
                .status(errorCode.getStatus()) // int 값을 바로 넘겨도 ResponseEntity가 처리해줍니다.
                .body(errorResponse);
    }

    // [중요] CustomException 외에 다른 모든 예외를 잡아서 로그를 찍어줌
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상치 못한 에러 발생: ", e); // 여기서 500 에러의 진짜 범인이 찍힙니다!

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("SERVER_ERROR")
                .message("서버 내부 오류가 발생했습니다.")
                .build();

        return ResponseEntity.status(500).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");

        // 에러 메시지 중 첫 번째 것만 가져오기
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        body.put("message", errorMessage);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
