package com.example.runrankback.service;

import com.example.runrankback.dto.request.CourseRequestDto;
import com.example.runrankback.dto.response.CourseResponseDto;
import com.example.runrankback.entity.Course;
import com.example.runrankback.entity.User;
import com.example.runrankback.repository.CourseRepository;
import com.example.runrankback.repository.RunningRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final RunningRecordRepository runningRecordRepository;
    private final S3Service s3Service;

    public List<CourseResponseDto> getNearbyCourses(double lat, double lng, double radius) {
        return courseRepository.findNearbyCourses(lat, lng, radius)
                .stream()
                .map(CourseResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createCourse(CourseRequestDto requestDto, MultipartFile thumbnailFile, User user) {
        LineString path = createLineStrFromRoute(requestDto.getRoute());

        String uploadedUrl = null;
        if(thumbnailFile != null && !thumbnailFile.isEmpty()) {
            uploadedUrl = s3Service.uploadCourseThumbnail(thumbnailFile, user.getId());
        }

        Course course = Course.builder()
                .user(user)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .distance(requestDto.getDistance())
                .encodedPolyline(requestDto.getEncodedPolyline())
                .path(path)
                .route(requestDto.getRoute())
                .visibility(requestDto.getVisibility())
                .thumbnailUrl(uploadedUrl)
                .build();

        return courseRepository.save(course).getId();
    }

    private LineString createLineStrFromRoute(List<Map<String, Object>> route) {
        if(route == null || route.isEmpty()) {
            throw new IllegalArgumentException("코스 경로 데이터가 없습니다.");
        }

        try{
            Coordinate[] coordinates = route.stream()
                    .map(point -> new Coordinate(
                            Double.parseDouble(point.get("lng").toString()),
                            Double.parseDouble(point.get("lat").toString())
                    ))
                    .toArray(Coordinate[]::new);

            return geometryFactory.createLineString(coordinates);
        }
        catch(Exception e) {
            throw new IllegalArgumentException("올바르지 않은 경로 데이터 형식: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAllByOrderByCreatedAtDesc();

        return courses.stream()
                .map(CourseResponseDto::from)
                .collect(Collectors.toList());
    }

    public CourseResponseDto getCourseDetail(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("코스를 찾을 수 없습니다."));

        Integer myBestDuration = null;
        if(userId != null) {
            myBestDuration = runningRecordRepository.findBestDurationByCourseAndUser(courseId, userId)
                    .orElse(null);
        }

        return CourseResponseDto.from(course, myBestDuration);
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDto> getPopularCourses() {
        List<Course> courseList = courseRepository.findAllByOrderBySavedCountDesc();

        return courseList.stream()
                .map(CourseResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDto> getMyCourses(User user) {
        List<Course> myCourseList = courseRepository.findAllByUserOrderByCreatedAtDesc(user);

        return myCourseList.stream()
                .map(CourseResponseDto::from)
                .toList();
    }
}