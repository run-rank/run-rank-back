package com.example.runrankback.security.jwt;

import com.example.runrankback.entity.User;
import com.example.runrankback.repository.UserRepository;
import com.example.runrankback.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String salt;

    private SecretKey secretKey;
    private static final long ACCESS_EXP = 1000L * 60 * 60 * 24; // 24시간
    private static final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7; // 7일

    @PostConstruct
    protected void init() {
        // yml에서 읽어온 secret으로 암호화 키 생성
        this.secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
    public String createToken(String email) {
        Claims claims = Jwts.claims().subject(email).build();
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_EXP))
                .signWith(secretKey)
                .compact();
    }

    // 리프레시 토큰 생성 메서드 추가
    public String createRefreshToken(String email) {
        Claims claims = Jwts.claims().subject(email).build();
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_EXP))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 인증 정보 조회 - CustomUserDetails 사용
     * 로컬 사용자, 카카오 사용자 모두 동일하게 처리됨 (JWT 기반)
     */
    public Authentication getAuthentication(String token) {
        String email = getEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다: " + email));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 이메일 추출
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰: {}", e.getMessage());
            return false;
        }
    }
}
