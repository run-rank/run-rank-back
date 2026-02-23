package com.example.runrankback.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JellyType {
    NORMAL("일반"),
    GLITTER("반짝이는"),
    RARE("레어");

    private final String description;
}
