package com.example.runrankback.controller;

import com.example.runrankback.dto.request.CourseRequestDto;
import com.example.runrankback.dto.response.CourseResponseDto;
import com.example.runrankback.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @Operation(summary = "주변 러닝 코스 조회", description = "출발지 좌표 기준으로 주변 코스를 조회힙니다.")
    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getCourses(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "1.0") Double range    // 반경 1km를 기본값으로 설정
    ) {
        List<CourseResponseDto> courses = courseService.getCoursesNearby(lat, lng, range);

        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "코스 단일 조회", description = "코스 ID로 특정 코스의 상세 정보를 조회합니다.")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDto> getCourseDetail(@PathVariable Long courseId) {
        CourseResponseDto response = courseService.getCourseDetail(courseId);

        return ResponseEntity.ok(response);
    }
}