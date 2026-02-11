package com.example.runrankback.controller;

import com.example.runrankback.dto.request.RecordRequestDto;
import com.example.runrankback.security.CustomUserDetails;
import com.example.runrankback.service.RunningRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "Running Record", description = "러닝 기록 관련 API")
public class RunningRecordController {

    private final RunningRecordService recordService;

    @Operation(summary = "러닝 기록 저장", description = "완료한 러닝 기록을 저장합니다.")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RecordRequestDto requestDto
    ) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "로그인이 필요합니다."));
        }

        Long userId = userDetails.getUserId();
        Long recordId = recordService.createRecord(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap("recordId", recordId));
    }
}
