package com.example.runrankback.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JellyColor {
    RED("빨간색"),
    ORANGE("주황색"),
    YELLOW("노란색"),
    GREEN("초록색"),
    BLUE("파란색"),
    INDIGO("남색"),
    PURPLE("보라색"),
    RAINBOW("무지개색");

    private final String description;
}
