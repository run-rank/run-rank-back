package com.example.runrankback.service;

import com.example.runrankback.dto.response.JellyResponseDto;
import com.example.runrankback.entity.Jelly;
import com.example.runrankback.entity.JellyColor;
import com.example.runrankback.entity.JellyType;
import com.example.runrankback.entity.User;
import com.example.runrankback.repository.JellyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class JellyService {

    private final JellyRepository jellyRepository;
    private final Random random = new Random();

    public Jelly createRandomJelly(User user) {
        int chance = random.nextInt(100); // 0 ~ 99
        JellyType type;
        JellyColor color;

        if (chance < 70) {
            // 70% 일반 젤리 (0 ~ 69)
            type = JellyType.NORMAL;
            color = getRandomColor();
        } else if (chance < 95) {
            // 25% 반짝이 젤리 (70 ~ 94)
            type = JellyType.GLITTER;
            color = getRandomColor();
        } else {
            // 5% 레어 젤리 (95 ~ 99)
            type = JellyType.RARE;
            color = JellyColor.RAINBOW; // 레어는 특별 색상
        }

        // 중복 체크
        Optional<Jelly> existingJelly = jellyRepository.findByUserIdAndTypeAndColor(user.getId(), type, color);
        if (existingJelly.isPresent()) {
            return null; // 이미 보유한 젤리라면 null 반환 (획득 실패)
        }

        Jelly jelly = Jelly.builder()
                .user(user)
                .type(type)
                .color(color)
                .isNew(true)
                .build();

        return jellyRepository.save(jelly);
    }

    private JellyColor getRandomColor() {
        JellyColor[] colors = {
                JellyColor.RED, JellyColor.ORANGE, JellyColor.YELLOW,
                JellyColor.GREEN, JellyColor.BLUE, JellyColor.INDIGO, JellyColor.PURPLE
        };
        return colors[random.nextInt(colors.length)];
    }

    @Transactional(readOnly = true)
    public List<JellyResponseDto> getMyJellies(Long userId) {
        List<Jelly> jellies = jellyRepository.findByUserId(userId);
        return jellies.stream()
                .map(JellyResponseDto::fromEntity)
                .toList();
    }
}
