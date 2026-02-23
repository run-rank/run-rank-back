package com.example.runrankback.controller;

import com.example.runrankback.dto.request.RecordRequestDto;
import com.example.runrankback.dto.response.RecordCreateResponseDto;
import com.example.runrankback.dto.response.RecordResponseDto;
import com.example.runrankback.security.CustomUserDetails;
import com.example.runrankback.service.RunningRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "Running Record", description = "러닝 기록 관련 API")
public class RunningRecordController {

    private final RunningRecordService recordService;

    @Operation(summary = "러닝 기록 저장", description = "완료한 러닝 기록을 저장합니다.")
    @PostMapping
    public ResponseEntity<RecordCreateResponseDto> createRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RecordRequestDto requestDto
    ) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = userDetails.getUserId();
        RecordCreateResponseDto response = recordService.createRecord(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "러닝 기록 조회", description = "로그인한 사용자의 러닝 기록 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<RecordResponseDto>> getMyRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(recordService.getMyRecords(userDetails.getUserId()));
    }
}
