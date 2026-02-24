package com.example.runrankback.repository;

import com.example.runrankback.entity.RunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {
    List<RunningRecord> findByUserIdOrderByRunDateDesc(Long userId);

    @Query(value = "SELECT u.id as userId, u.user_name as nickname, " +
            "u.profile_image_url as profileImageUrl, MIN(r.duration) as bestDuration " +
            "FROM running_record r " +
            "JOIN users u ON r.user_id = u.id " +
            "WHERE r.course_id = :courseId " +
            "GROUP BY u.id, u.user_name, u.profile_image_url " +
            "ORDER BY bestDuration ASC", nativeQuery = true)
    List<RankingItem> findBestRecordsByCourse(@Param("courseId") Long courseId);

    @Query("SELECT MIN(r.duration) FROM RunningRecord r " +
            "WHERE r.course.id = :courseId AND r.user.id = :userId")
    Optional<Integer> findBestDurationByCourseAndUser(
            @Param("courseId") Long courseId, @Param("userId") Long userId);

    interface RankingItem {
        Long getUserId();
        String getNickname();
        String getProfileImageUrl();
        Integer getBestDuration();
    }
}
