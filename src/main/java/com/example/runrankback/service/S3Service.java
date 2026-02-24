package com.example.runrankback.service;

import com.example.runrankback.exception.CustomException;
import com.example.runrankback.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    // 허용되는 이미지 타입
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    // 최대 파일 크기 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 프로필 이미지 업로드
     */
    public String uploadProfileImage(MultipartFile file, Long userId) {
        // 파일 검증
        validateFile(file);

        // 파일명 생성 (profiles/userId/UUID.확장자)
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String fileName = "profiles/" + userId + "/" + UUID.randomUUID() + "." + extension;

        return uploadToS3(file, fileName);
    }

    // 코스 썸네일 업로드
    public String uploadCourseThumbnail(MultipartFile file, Long userId) {
        validateFile(file);

        String fileName = "courses/" + userId + "/" +
                UUID.randomUUID() + "." + getExtension(file.getOriginalFilename());

        return uploadToS3(file, fileName);
    }

    // S3 업로드
    private String uploadToS3(MultipartFile file, String fileName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String fileUrl = getFileUrl(fileName);

            return fileUrl;

        } catch (IOException e) {
            throw new CustomException((ErrorCode.FILE_UPLOAD_FAILED));
        }

    }

    //이미지 삭제
    public void deleteImage(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        // 외부 URL (카카오 프로필) 이면 삭제하지 않음
        if (fileUrl.contains("kakao") || !fileUrl.contains(bucket)) {
            return;
        }

        try {
            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3 파일 삭제 성공 - url: {}", fileUrl);

        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
        }
    }

    /**
     * 파일 검증
     */
    private void validateFile(MultipartFile file) {
        // 파일 존재 여부
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        // 파일 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * S3 파일 URL 생성
     */
    private String getFileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
    }

    /**
     * URL에서 S3 key 추출
     */
    private String extractKeyFromUrl(String fileUrl) {
        // https://bucket.s3.region.amazonaws.com/profiles/1/uuid.jpg
        // -> profiles/1/uuid.jpg
        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucket, region);
        return fileUrl.replace(prefix, "");
    }
}
