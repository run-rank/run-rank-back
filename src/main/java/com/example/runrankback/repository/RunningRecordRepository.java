package com.example.runrankback.repository;

import com.example.runrankback.entity.RunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {
    List<RunningRecord> findByUserIdOrderByRunDateDesc(Long userId);
}
