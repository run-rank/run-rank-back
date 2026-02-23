package com.example.runrankback.controller;

import com.example.runrankback.dto.response.JellyResponseDto;
import com.example.runrankback.security.CustomUserDetails;
import com.example.runrankback.service.JellyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jellies")
@RequiredArgsConstructor
@Tag(name = "Jelly", description = "젤리(업적) 관련 API")
public class JellyController {

    private final JellyService jellyService;

    @Operation(summary = "내 젤리 조회", description = "보유한 젤리 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<JellyResponseDto>> getMyJellies(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(jellyService.getMyJellies(userDetails.getUserId()));
    }
}
