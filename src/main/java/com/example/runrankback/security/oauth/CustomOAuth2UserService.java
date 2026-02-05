package com.example.runrankback.security.oauth;

import com.example.runrankback.entity.User;
import com.example.runrankback.repository.UserRepository;
import com.example.runrankback.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오에서 받은 정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

        // 이메일 동의 안 한 경우 예외 처리
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("카카오 이메일 정보가 필요합니다. 이메일 제공에 동의해주세요.");
        }

        Map<String, Object> properties = (Map<String, Object>) oAuth2User.getAttributes().get("properties");
        String nickname = properties != null ? (String) properties.get("nickname") : null;

        // DB에 회원이 없으면 회원가입, 있으면 로그인
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .userName(nickname)
                        .password("") // 소셜 로그인은 비밀번호 없음
                        .build()));

        // CustomUserDetails로 감싸서 반환 (attributes도 함께 전달)
        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }
}
