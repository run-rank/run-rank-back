package com.example.runnig_back.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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

    @Builder
    public User (String email, String password, String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }



}
