package com.example.runrankback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfo {

    private String id;           // 카카오 사용자 고유 ID
    private String email;        // 이메일
    private String userName;     // 닉네임
    private String profileImage; // 프로필 이미지 URL
}
