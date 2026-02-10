package com.example.runrankback.service;

import com.example.runrankback.dto.request.RecordRequestDto;
import com.example.runrankback.entity.Course;
import com.example.runrankback.entity.RunningRecord;
import com.example.runrankback.entity.User;
import com.example.runrankback.repository.CourseRepository;
import com.example.runrankback.repository.RunningRecordRepository;
import com.example.runrankback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RunningRecordService {

    private final RunningRecordRepository recordRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public Long createRecord(Long userId, RecordRequestDto requestDto) {
        Course course = courseRepository.findById(requestDto.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코스입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        RunningRecord record = RunningRecord.builder()
                .user(user)
                .course(course)
                .duration(requestDto.getDuration())
                .distance(requestDto.getDistance())
                .runDate(requestDto.getRunDate())
                .gpsRoute(requestDto.getGpsRoute())
                .build();

        return recordRepository.save(record).getId();
    }
}
