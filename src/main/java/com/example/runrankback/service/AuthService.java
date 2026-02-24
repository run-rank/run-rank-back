package com.example.runrankback.service;

import com.example.runrankback.dto.request.AuthRequest;
import com.example.runrankback.dto.request.KakaoLoginRequest;
import com.example.runrankback.dto.request.LoginRequest;
import com.example.runrankback.dto.response.AuthResponse;
import com.example.runrankback.dto.response.KakaoUserInfo;
import com.example.runrankback.entity.RefreshToken;
import com.example.runrankback.entity.User;
import com.example.runrankback.exception.CustomException;
import com.example.runrankback.exception.ErrorCode;
import com.example.runrankback.repository.RefreshTokenRepository;
import com.example.runrankback.repository.UserRepository;
import com.example.runrankback.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final KakaoService kakaoService;

    @Transactional
    public void signup(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userName(request.getUserName())
                .build();

        userRepository.save(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_INPUT_INVALID));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_INPUT_INVALID);
        }

        String accessToken = jwtProvider.createToken(user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());

        // 4. 리프레시 토큰 DB 저장 (기존 토큰이 있으면 업데이트, 없으면 신규 생성)
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser_Email(user.getEmail())
                .map(token -> {
                    token.updateRefreshToken(refreshToken);
                    return token;
                })
                .orElse(RefreshToken.builder()
                        .user(user)
                        .refreshToken(refreshToken)
                        .build());

        refreshTokenRepository.save(refreshTokenEntity);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .userName(user.getUserName())
                .build();
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        // 1. 리프레시 토큰 유효성 검사 (JWT 자체 검증)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 2. 토큰에서 이메일 추출
        String email = jwtProvider.getEmail(refreshToken);

        // user 존재 여부 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 3. DB에 저장된 토큰과 일치하는지 확인 (로그아웃 여부나 변조 확인)
        RefreshToken savedToken = refreshTokenRepository.findByUser_Email(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        if (!savedToken.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 4. 새로운 액세스 토큰 생성
        String newAccessToken = jwtProvider.createToken(email);

        // 5. 응답 (리프레시는 그대로 전달하거나, 보안을 위해 같이 갱신 가능)
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 기존 리프레시 토큰 유지
                .email(email)
                .userName(user.getUserName())
                .build();
    }

    /**
     * 카카오(OAuth2) 로그인 성공 시 호출: 액세스/리프레시 토큰 발급 및 저장
     */
    @Transactional
    public AuthResponse loginByOAuth2(User user) {
        String accessToken = jwtProvider.createToken(user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());

        // 리프레시 토큰 DB 저장 (기존 토큰이 있으면 업데이트, 없으면 신규 생성)
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser_Email(user.getEmail())
                .map(token -> {
                    token.updateRefreshToken(refreshToken);
                    return token;
                })
                .orElse(RefreshToken.builder()
                        .user(user)
                        .refreshToken(refreshToken)
                        .build());

        refreshTokenRepository.save(refreshTokenEntity);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .userName(user.getUserName())
                .build();
    }

    /**
     * 카카오 토큰으로 로그인 (앱 SDK 방식)
     * - 프론트에서 카카오 액세스 토큰을 전달받아 처리
     */
    @Transactional
    public AuthResponse loginWithKakaoToken(KakaoLoginRequest request) {
        // 1. 카카오 토큰으로 사용자 정보 조회
        KakaoUserInfo kakaoUserInfo = kakaoService.getUserInfo(request.getAccessToken());

        // 2. 이메일 필수 체크
        if (kakaoUserInfo.getEmail() == null || kakaoUserInfo.getEmail().isBlank()) {
            throw new CustomException(ErrorCode.KAKAO_EMAIL_NOT_PROVIDED);
        }

        // 3. 기존 회원인지 확인, 없으면 자동 회원가입
        User user = userRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseGet(() -> {
                    // 신규 회원가입
                    User newUser = User.builder()
                            .email(kakaoUserInfo.getEmail())
                            .userName(kakaoUserInfo.getUserName())
                            .password(null)  // 카카오 로그인은 비밀번호 없음
                            .provider("kakao")
                            .profileImageUrl(kakaoUserInfo.getProfileImage())
                            .build();
                    return userRepository.save(newUser);
                });

        // 4. 기존 회원이면 프로필 이미지 업데이트 (카카오에서 변경했을 수 있음)
        if ("kakao".equals(user.getProvider()) && kakaoUserInfo.getProfileImage() != null) {
            user.updateProfileImage(kakaoUserInfo.getProfileImage());
        }

        // 5. JWT 토큰 발급
        String accessToken = jwtProvider.createToken(user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());

        // 6. 리프레시 토큰 저장
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser_Email(user.getEmail())
                .map(token -> {
                    token.updateRefreshToken(refreshToken);
                    return token;
                })
                .orElse(RefreshToken.builder()
                        .user(user)
                        .refreshToken(refreshToken)
                        .build());

        refreshTokenRepository.save(refreshTokenEntity);

        // 7. 응답 반환
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .userName(user.getUserName())
                .build();
    }

    /**
     * 로그아웃 - DB에서 리프레시 토큰 삭제
     */
    @Transactional
    public void logout(String email) {
        refreshTokenRepository.deleteByUser_Email(email);
    }
}
