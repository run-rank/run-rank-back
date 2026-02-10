package com.example.runrankback.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카카오 로그인 요청")
public class KakaoLoginRequest {

    @NotBlank(message = "카카오 액세스 토큰은 필수입니다.")
    @Schema(description = "카카오에서 발급받은 액세스 토큰", example = "ACCESS_TOKEN_FROM_KAKAO")
    private String accessToken;
}
