package com.example.runrankback.controller;

import com.example.runrankback.dto.request.UpdateProfileRequest;
import com.example.runrankback.dto.response.UserProfileResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        String email = getCurrentUserEmail();
        UserProfileResponse response = userService.getMyProfile(email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 정보 수정",
            description = "닉네임, 비밀번호를 수정합니다. 변경하고 싶은 필드만 값을 넣으면 됩니다. (로컬 회원 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "현재 비밀번호 불일치 또는 카카오 회원"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        String email = getCurrentUserEmail();
        UserProfileResponse response = userService.updateProfile(email, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 이미지 업로드/수정",
            description = "프로필 이미지를 업로드하거나 수정합니다. (로컬 회원 전용, 카카오 회원 불가)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 성공",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 카카오 회원"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> updateProfileImage(
            @RequestPart("file") MultipartFile file) {
        String email = getCurrentUserEmail();
        UserProfileResponse response = userService.updateProfileImage(email, file);
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
    public ResponseEntity<UserProfileResponse> deleteProfileImage() {
        String email = getCurrentUserEmail();
        UserProfileResponse response = userService.deleteProfileImage(email);
        return ResponseEntity.ok(response);
    }

    /**
     * SecurityContext에서 현재 로그인한 유저의 이메일을 가져옵니다.
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
