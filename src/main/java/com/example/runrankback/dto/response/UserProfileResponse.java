package com.example.runrankback.dto.response;

import com.example.runrankback.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 프로필 응답")
public class UserProfileResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "프로필 이미지 URL", example = "https://bucket.s3.amazonaws.com/profiles/1/uuid.jpg")
    private String profileImageUrl;

    @Schema(description = "로그인 제공자 (local/kakao)", example = "local")
    private String provider;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .profileImageUrl(user.getProfileImageUrl())
                .provider(user.getProvider())
                .build();
    }
}
