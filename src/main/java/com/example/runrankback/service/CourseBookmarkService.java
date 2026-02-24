package com.example.runrankback.service;


import com.example.runrankback.dto.response.CourseResponseDto;
import com.example.runrankback.entity.Course;
import com.example.runrankback.entity.CourseBookmark;
import com.example.runrankback.entity.User;
import com.example.runrankback.repository.CourseBookmarkRepository;
import com.example.runrankback.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseBookmarkService {

    private final CourseBookmarkRepository bookmarkRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void addBookmark(User user, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 코스입니다."));

        Optional<CourseBookmark> existingBookmark = bookmarkRepository.findByUserAndCourse(user, course);

        if(existingBookmark.isPresent()) {
            throw new IllegalArgumentException("이미 북마크에 추가된 코스입니다");
        }

        CourseBookmark newBookmark = CourseBookmark.builder()
                .user(user)
                .course(course)
                .build();

        bookmarkRepository.save(newBookmark);

        courseRepository.increaseSavedCount(courseId);
    }

    @Transactional
    public void removeBookmark(User user, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 코스입니다."));

        CourseBookmark bookmark = bookmarkRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new EntityNotFoundException("북마크 내역을 찾을 수 없습니다."));

        bookmarkRepository.delete(bookmark);

        courseRepository.decreaseSavedCount(courseId);
    }

    public List<CourseResponseDto> getMyBookmarks(User user) {
        List<CourseBookmark> bookmarkList = bookmarkRepository.findAllByUserWithCourse(user);

        return bookmarkList.stream()
                .map(courseBookmark -> CourseResponseDto.from(courseBookmark.getCourse()))
                .toList();
    }
}
