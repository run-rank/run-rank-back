package com.example.runrankback.security.oauth;

import com.example.runrankback.security.jwt.JwtProvider;
import com.example.runrankback.security.CustomUserDetails;
import com.example.runrankback.service.AuthService;
import com.example.runrankback.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SucessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            AuthResponse authResponse = authService.loginByOAuth2(userDetails.getUser());

            response.setContentType("application/json");
            response.getWriter().write(String.format(
                "{\"accessToken\": \"%s\", \"refreshToken\": \"%s\", \"email\": \"%s\", \"userName\": \"%s\"}",
                authResponse.getAccessToken(),
                authResponse.getRefreshToken(),
                authResponse.getEmail(),
                authResponse.getUserName()
            ));
        } else {
            // fallback: 기존 방식 (JWT만 발급)
            String email = authentication.getName();
            String token = jwtProvider.createToken(email);
            response.setContentType("application/json");
            response.getWriter().write("{\"token\": \"" + token + "\"}");
        }
    }
}
