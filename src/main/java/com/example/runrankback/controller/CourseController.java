package com.example.runrankback.controller;

import com.example.runrankback.dto.request.CourseRequestDto;
import com.example.runrankback.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Course API", description = "러닝 코스 관리 API")
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "러닝 코스 생성", description = "새로운 러닝 코스를 등록합니다. JWT 인증이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "코스 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패: JWT 토큰 오류")
    })
    @PostMapping
    public ResponseEntity<Map<String, Long>> createCourse(@RequestBody CourseRequestDto dto) {
        Long courseId = courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("courseId", courseId));
    }
}