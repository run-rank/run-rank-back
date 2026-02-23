package com.example.runrankback.controller;

import com.example.runrankback.dto.request.CourseRequestDto;
import com.example.runrankback.dto.response.CourseResponseDto;
import com.example.runrankback.security.CustomUserDetails;
import com.example.runrankback.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Course API", description = "러닝 코스 관리 API")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "코스 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "코스 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패: JWT 토큰 오류")
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createCourse(
            @RequestPart("course") CourseRequestDto requestDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @AuthenticationPrincipal CustomUserDetails userDetails

    ) {
        Long courseId = courseService.createCourse(requestDto, thumbnail, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(courseId);
    }

    @Operation(summary = "내 주변 코스 검색", description = "현재 위치와 반경을 기준으로 인근 코스 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 파라미터 (위경도 형식 오류)")
    })
    @GetMapping("/nearby")
    public ResponseEntity<List<CourseResponseDto>> getNearbyCourses(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1000") Double range    // 반경 1km를 기본값으로 설정
    ) {
        List<CourseResponseDto> courses = courseService.getNearbyCourses(lat, lng, range);

        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "코스 상세 조회", description = "코스 ID로 코스 상세 정보를 조회합니다.")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDto> getCourseDetail(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = (userDetails != null) ? userDetails.getUser().getId() : null;
        CourseResponseDto response = courseService.getCourseDetail(courseId, userId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 코스 조회", description = "생성된 모든 코스를 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
        List<CourseResponseDto> responses = courseService.getAllCourses();
        return ResponseEntity.ok(responses);
    }
}