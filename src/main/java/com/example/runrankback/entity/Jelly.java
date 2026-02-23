package com.example.runrankback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jelly extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private JellyType type; // NORMAL, GLITTER, RARE

    @Enumerated(EnumType.STRING)
    private JellyColor color; // RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, PURPLE, RAINBOW(Rare)

}
