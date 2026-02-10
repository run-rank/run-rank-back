package com.example.runrankback.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users") // 테이블명 users로 지정
public class User {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column // 이메일-로그인시 id역할
    private String email;

    @Column // 비밀번호
    private String password;

    @Column // 이름
    private String userName;

    @Column // 로그인 제공자 (local, kakao)
    private String provider;

    @Column // 카카오 프로필 이미지 URL
    private String profileImageUrl;

    @Builder
    public User (String email, String password, String userName, String provider, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.provider = provider != null ? provider : "local";
        this.profileImageUrl = profileImageUrl;
    }

    // 프로필 이미지 업데이트 메서드
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // 카카오 사용자인지 확인
    public boolean isKakaoUser() {
        return "kakao".equals(this.provider);
    }

    // 닉네임 업데이트
    public void updateUserName(String userName) {
        this.userName = userName;
    }

    // 비밀번호 업데이트
    public void updatePassword(String password) {
        this.password = password;
    }
}
