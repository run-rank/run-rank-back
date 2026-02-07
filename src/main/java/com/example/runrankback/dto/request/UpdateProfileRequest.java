package com.example.runrankback.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로필 수정 요청 DTO
 * - 모든 필드는 선택적(Optional)
 * - null이 아닌 필드만 업데이트됨
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String userName;

    private String currentPassword;  // 비밀번호 변경 시 필수

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String newPassword;

    /**
     * 닉네임 변경 요청인지 확인
     */
    public boolean hasUserNameUpdate() {
        return userName != null && !userName.isBlank();
    }

    /**
     * 비밀번호 변경 요청인지 확인
     */
    public boolean hasPasswordUpdate() {
        return newPassword != null && !newPassword.isBlank();
    }
}
