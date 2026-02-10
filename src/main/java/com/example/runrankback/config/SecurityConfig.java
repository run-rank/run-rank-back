package com.example.runrankback.config; // 본인 패키지명에 맞게 수정

import com.example.runrankback.security.jwt.JwtAuthenticationEntryPoint;
import com.example.runrankback.security.jwt.JwtAuthenticationFilter;
import com.example.runrankback.security.jwt.JwtProvider;
import com.example.runrankback.security.oauth.CustomOAuth2UserService;
import com.example.runrankback.security.oauth.OAuth2SucessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 설정을 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SucessHandler oAuth2SucessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) throws Exception {
        http
                // 기본 폼 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // oauth 내용
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/kakao")  // 기본 로그인 페이지 대신 카카오로 바로 이동
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2SucessHandler)
                        .failureHandler((request, response, exception) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(401);
                            response.getWriter().write("{\"error\": \"OAuth2 로그인 실패\", \"message\": \"" + exception.getMessage() + "\"}");
                        })
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/logout").authenticated()  // 로그아웃은 인증 필요
                        .requestMatchers("/api/auth/**").permitAll()       // 회원가입, 로그인 API 경로 허용
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()  // OAuth2 로그인 경로 허용
                        .requestMatchers("/h2-console/**").permitAll()     // H2 데이터베이스 콘솔 허용
                        .requestMatchers("/login", "/signup").permitAll()  // 나중에 만들 커스텀 HTML 페이지 경로 허용
                        .requestMatchers("/css/**", "/js/**").permitAll()// 페이지에 필요한 정적 리소스(CSS, JS) 허용
                        .requestMatchers("/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/error").permitAll() // Swagger API 문서 경로 허용
                        .anyRequest().authenticated()                      // 그 외 모든 요청은 인증 필요
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // 인증되지 않은 사용자가 보호된 페이지에 접근했을 때의 처리 (필요시 주석 해제)
                /*
                .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) ->
                        response.sendRedirect("/login"))               // 인증 없을 시 로그인 페이지로 강제 이동
                )
                */

                .headers(headers -> headers.frameOptions(options -> options.disable()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

}