package com.example.runrankback.controller;

import com.example.runrankback.dto.request.UpdateProfileRequest;
import com.example.runrankback.dto.response.UserProfileResponse;
import com.example.runrankback.security.CustomUserDetails;
import com.example.runrankback.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "사용자 정보 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 유저의 프로필 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse response = userService.getMyProfile(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 정보 수정",
            description = "닉네임, 비밀번호, 프로필 이미지를 수정합니다. 변경하고 싶은 필드만 값을 넣으면 됩니다. (로컬 회원 전용)\n\n" +
                    "- 닉네임: userName 필드에 값 입력\n" +
                    "- 비밀번호: newPassword + confirmPassword 입력\n" +
                    "- 프로필 이미지: profileImage 파일 첨부 (없으면 기존 이미지 유지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치 또는 카카오 회원"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        UpdateProfileRequest request = new UpdateProfileRequest(userName, newPassword, confirmPassword);
        UserProfileResponse response = userService.updateProfile(userDetails.getUser(), request, profileImage);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 이미지 삭제",
            description = "프로필 이미지를 삭제합니다. (로컬 회원 전용, 카카오 회원 불가)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 이미지 삭제 성공",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "카카오 회원은 삭제 불가"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping("/me/profile-image")
    public ResponseEntity<UserProfileResponse> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse response = userService.deleteProfileImage(userDetails.getUser());
        return ResponseEntity.ok(response);
    }
}
