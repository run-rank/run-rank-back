-- 사용자 프로필 관련 컬럼 추가
-- provider: 로그인 제공자 (local, kakao)
-- profile_image_url: 프로필 이미지 URL (S3 또는 카카오 프로필)

ALTER TABLE users
    ADD COLUMN provider VARCHAR(20) DEFAULT 'local',
    ADD COLUMN profile_image_url VARCHAR(500);

-- 기존 사용자들은 모두 local 사용자로 설정
UPDATE users SET provider = 'local' WHERE provider IS NULL;

-- 카카오 로그인 사용자는 비밀번호가 NULL일 수 있으므로 NOT NULL 제약 해제
ALTER TABLE users
    ALTER COLUMN password DROP NOT NULL;
