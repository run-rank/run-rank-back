package com.example.runrankback.service;

import com.example.runrankback.dto.request.RecordRequestDto;
import com.example.runrankback.dto.response.JellyResponseDto;
import com.example.runrankback.dto.response.RecordCreateResponseDto;
import com.example.runrankback.dto.response.RecordResponseDto;
import com.example.runrankback.entity.Course;
import com.example.runrankback.entity.Jelly;
import com.example.runrankback.entity.RunningRecord;
import com.example.runrankback.entity.User;
import com.example.runrankback.repository.CourseRepository;
import com.example.runrankback.repository.RunningRecordRepository;
import com.example.runrankback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RunningRecordService {

    private final RunningRecordRepository recordRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final JellyService jellyService;

    public RecordCreateResponseDto createRecord(Long userId, RecordRequestDto requestDto) {
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

        Long recordId = recordRepository.save(record).getId();
        Jelly earnedJelly = null;

        // 젤리 지급 로직
        // 코스 러닝, 거리 1km 이상(디버깅으로 인해 값은 임시로 100), duration 0초 초과, GPS 데이터 유효
        if (requestDto.getDistance() >= 1000 && requestDto.getDuration() > 0 && requestDto.getGpsRoute() != null && !requestDto.getGpsRoute().isEmpty()) {
            earnedJelly = jellyService.createRandomJelly(user);
        }

        return RecordCreateResponseDto.builder()
                .recordId(recordId)
                .earnedJelly(earnedJelly != null ? JellyResponseDto.fromEntity(earnedJelly) : null)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RecordResponseDto> getMyRecords(Long userId) {
        List<RunningRecord> records = recordRepository.findByUserIdOrderByRunDateDesc(userId);

        return records.stream()
                .map(RecordResponseDto::fromEntity)
                .toList();
    }
}
