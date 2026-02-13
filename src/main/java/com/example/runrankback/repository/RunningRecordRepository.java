package com.example.runrankback.repository;

import com.example.runrankback.entity.RunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {
    List<RunningRecord> findByUserIdOrderByRunDateDesc(Long userId);

    @Query(value = "SELECT r.user_id, u.user_name, MIN(r.duration) as min_duration " +
            "FROM running_record r " +
            "JOIN users u ON r.user_id = u.id " +
            "WHERE r.course_id = :courseId " +
            "GROUP BY r.user_id, u.user_name " +
            "ORDER BY min_duration ASC", nativeQuery = true)
    List<Object[]> findBestRecordsByCourse(@Param("courseId") Long courseId);
}
