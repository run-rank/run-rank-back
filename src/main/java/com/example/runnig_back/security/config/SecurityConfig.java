package com.example.runnig_back.security.config; // 본인 패키지명에 맞게 수정

import com.example.runnig_back.security.jwt.JwtAuthenticationFilter;
import com.example.runnig_back.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 설정을 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()       // 회원가입, 로그인 API 경로 허용
                        .requestMatchers("/api/auth/signup", "/api/auth/login").permitAll() // 회원가입, 로그인 API 경로 허용
                        .requestMatchers("/h2-console/**").permitAll()     // H2 데이터베이스 콘솔 허용
                        .requestMatchers("/login", "/signup").permitAll()  // 나중에 만들 커스텀 HTML 페이지 경로 허용
                        .requestMatchers("/css/**", "/js/**").permitAll()// 페이지에 필요한 정적 리소스(CSS, JS) 허용
                        .requestMatchers("/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll() // Swagger API 문서 경로 허용
                        .anyRequest().authenticated()                      // 그 외 모든 요청은 인증 필요
                )

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

    // 비밀번호 암호화 도구 빈 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}