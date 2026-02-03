package com.example.runrankback.controller;

import com.example.runrankback.entity.User;
import com.example.runrankback.exception.CustomException;
import com.example.runrankback.exception.ErrorCode;
import com.example.runrankback.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 정보 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @Operation(summary = "내 정보 조회", description = "토큰을 이용해 현재 로그인한 유저의 닉네임을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<String> getMyInfo() {
        // 1. SecurityContextHolder에서 필터가 저장해둔 인증 정보를 꺼냅니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 인증 정보에서 유저의 이메일(Principal)을 가져옵니다.
        String email = authentication.getName();

        // 3. DB에서 유저를 찾아 닉네임을 반환합니다.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return ResponseEntity.ok("안녕하세요, " + user.getUserName() + "님!");
    }
}
