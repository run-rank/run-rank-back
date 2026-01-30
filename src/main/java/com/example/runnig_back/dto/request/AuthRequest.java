package com.example.runnig_back.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthRequest {
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Schema(example = "test123@naver.com")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$",
            message = "비밀번호는 영문과 숫자를 포함하여 8~20자여야 합니다.")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Schema(example = "password123")
    private String password;

    @NotBlank(message = "사용자 이름은 필수 입력 값입니다.")
    @Schema(example = "김덕철")
    private String userName;
}
