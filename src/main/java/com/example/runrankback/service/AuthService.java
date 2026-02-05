package com.example.runrankback.service;

import com.example.runrankback.dto.request.AuthRequest;
import com.example.runrankback.dto.request.LoginRequest;
import com.example.runrankback.dto.response.AuthResponse;
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

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_INPUT_INVALID));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_INPUT_INVALID);
        }

        String accessToken = jwtProvider.createToken(user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());

        // 4. 리프레시 토큰 DB 저장 (기존 토큰이 있으면 업데이트, 없으면 신규 생성)
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserEmail(user.getEmail())
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
        RefreshToken savedToken = refreshTokenRepository.findByUserEmail(email)
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
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserEmail(user.getEmail())
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
}
