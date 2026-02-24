package com.example.runrankback.repository;

import com.example.runrankback.entity.Course;
import com.example.runrankback.entity.CourseBookmark;
import com.example.runrankback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Long> {
    Optional<CourseBookmark> findByUserAndCourse(User user, Course course);

    @Query("SELECT cb FROM CourseBookmark cb JOIN FETCH cb.course WHERE cb.user = :user")
    List<CourseBookmark> findAllByUserWithCourse(@Param("user") User user);
}
