package com.example.runrankback.service;

import com.example.runrankback.dto.response.KakaoUserInfo;
import com.example.runrankback.exception.CustomException;
import com.example.runrankback.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate;

    /**
     * 카카오 액세스 토큰으로 사용자 정보 조회
     */
    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        try {
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(kakaoAccessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // 카카오 API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new CustomException(ErrorCode.KAKAO_API_ERROR);
            }

            // 사용자 정보 추출
            String id = String.valueOf(body.get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
            String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

            Map<String, Object> profile = kakaoAccount != null
                    ? (Map<String, Object>) kakaoAccount.get("profile") : null;
            String nickname = profile != null ? (String) profile.get("nickname") : null;
            String profileImage = profile != null ? (String) profile.get("profile_image_url") : null;

            log.info("카카오 사용자 정보 조회 성공 - id: {}, email: {}", id, email);

            return KakaoUserInfo.builder()
                    .id(id)
                    .email(email)
                    .userName(nickname)
                    .profileImage(profileImage)
                    .build();

        } catch (HttpClientErrorException e) {
            log.error("카카오 API 호출 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_KAKAO_TOKEN);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 중 오류: {}", e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_API_ERROR);
        }
    }
}
