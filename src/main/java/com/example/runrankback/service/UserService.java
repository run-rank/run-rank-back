package com.example.runrankback.service;

import com.example.runrankback.dto.request.UpdateProfileRequest;
import com.example.runrankback.dto.response.UserProfileResponse;
import com.example.runrankback.entity.User;
import com.example.runrankback.exception.CustomException;
import com.example.runrankback.exception.ErrorCode;
import com.example.runrankback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 내 프로필 조회
     * @param user CustomUserDetails에서 가져온 User 엔티티
     */
    public UserProfileResponse getMyProfile(User user) {
        return UserProfileResponse.from(user);
    }

    /**
     * 프로필 이미지 업로드/수정 (로컬 회원 전용)
     * @param user CustomUserDetails에서 가져온 User 엔티티
     */
    @Transactional
    public UserProfileResponse updateProfileImage(User user, MultipartFile file) {
        // 영속성 컨텍스트에 다시 연결 (Detached 상태 해결)
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 카카오 회원은 프로필 이미지 수정 불가
        if (managedUser.isKakaoUser()) {
            throw new CustomException(ErrorCode.KAKAO_PROFILE_CANNOT_MODIFY);
        }

        // 기존 이미지가 있으면 S3에서 삭제
        if (managedUser.getProfileImageUrl() != null) {
            s3Service.deleteProfileImage(managedUser.getProfileImageUrl());
        }

        // 새 이미지 업로드
        String newImageUrl = s3Service.uploadProfileImage(file, managedUser.getId());

        // DB 업데이트
        managedUser.updateProfileImage(newImageUrl);

        log.info("프로필 이미지 업데이트 완료 - userId: {}, url: {}", managedUser.getId(), newImageUrl);

        return UserProfileResponse.from(managedUser);
    }

    /**
     * 프로필 이미지 삭제 (로컬 회원 전용)
     * @param user CustomUserDetails에서 가져온 User 엔티티
     */
    @Transactional
    public UserProfileResponse deleteProfileImage(User user) {
        // 영속성 컨텍스트에 다시 연결
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 카카오 회원은 프로필 이미지 삭제 불가
        if (managedUser.isKakaoUser()) {
            throw new CustomException(ErrorCode.KAKAO_PROFILE_CANNOT_MODIFY);
        }

        // S3에서 이미지 삭제
        if (managedUser.getProfileImageUrl() != null) {
            s3Service.deleteProfileImage(managedUser.getProfileImageUrl());
        }

        // DB에서 URL 제거
        managedUser.updateProfileImage(null);

        log.info("프로필 이미지 삭제 완료 - userId: {}", managedUser.getId());

        return UserProfileResponse.from(managedUser);
    }

    /**
     * 프로필 정보 수정 (닉네임, 비밀번호 통합)
     * - 로컬 회원 전용
     * - 변경하고 싶은 필드만 값을 넣으면 해당 필드만 업데이트됨
     * @param user CustomUserDetails에서 가져온 User 엔티티
     */
    @Transactional
    public UserProfileResponse updateProfile(User user, UpdateProfileRequest request) {
        // 영속성 컨텍스트에 다시 연결
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 카카오 사용자는 회원정보 수정 불가
        if (managedUser.isKakaoUser()) {
            throw new CustomException(ErrorCode.KAKAO_USER_CANNOT_MODIFY);
        }

        // 닉네임 변경 요청이 있는 경우
        if (request.hasUserNameUpdate()) {
            managedUser.updateUserName(request.getUserName());
            log.info("닉네임 변경 완료 - userId: {}, newUserName: {}", managedUser.getId(), request.getUserName());
        }

        // 비밀번호 변경 요청이 있는 경우
        if (request.hasPasswordUpdate()) {
            // 현재 비밀번호 필수 확인
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
                throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
            }

            // 현재 비밀번호 일치 여부 확인
            if (!passwordEncoder.matches(request.getCurrentPassword(), managedUser.getPassword())) {
                throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
            }

            // 새 비밀번호로 업데이트
            managedUser.updatePassword(passwordEncoder.encode(request.getNewPassword()));
            log.info("비밀번호 변경 완료 - userId: {}", managedUser.getId());
        }

        return UserProfileResponse.from(managedUser);
    }
}
