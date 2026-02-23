package com.example.runrankback.repository;

import com.example.runrankback.entity.Jelly;
import com.example.runrankback.entity.JellyColor;
import com.example.runrankback.entity.JellyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JellyRepository extends JpaRepository<Jelly, Long> {
    List<Jelly> findByUserId(Long userId);
    Optional<Jelly> findByUserIdAndTypeAndColor(Long userId, JellyType type, JellyColor color);
}
