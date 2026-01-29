package com.example.runnig_back.repository;

import com.example.runnig_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    // 이메일로 유저 찾기
    Optional<User> findByEmail(String email);

    // 이메일 존재 여부 확인
    Boolean existsUserByEmail(String email);

}
