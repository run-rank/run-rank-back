CREATE EXTENSION IF NOT EXISTS postgis;

CREATE OR REPLACE FUNCTION update_modified_column()     -- 데이터가 수정될 때마다 수정된 시간 업데이트 하도록 트리거 함수 정의
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,     -- PK: BIGSERIAL(자동 증가)
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    user_name VARCHAR(255) NOT NULL,
    provider VARCHAR(20) DEFAULT 'local',   -- local, kakao
    profile_image_url VARCHAR(500),         -- 프로필 이미지 URL (S3 또는 카카오 프로필)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,   -- FK: SEREAL 타입 사용 X
    refresh_token VARCHAR(255) NOT NULL,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE -- CASCADE로 고립 데이터 방지
);

CREATE TABLE course (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    distance INTEGER NOT NULL,      -- 미터 단위
    encoded_polyline TEXT NOT NULL,
    path GEOMETRY(LineString, 4326),
    visibility VARCHAR(20) NOT NULL,
    route JSONB,
    saved_count BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_course_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE running_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    duration INTEGER NOT NULL,      -- 초 단위
    distance INTEGER NOT NULL,      -- 미터 단위
    run_date DATE NOT NULL,
    gps_route JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_record_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_record_course FOREIGN KEY (course_id)
        REFERENCES course(id) ON DELETE CASCADE
);

CREATE INDEX idx_course_path_gist ON course USING GIST (path);
CREATE INDEX idx_record_course_duration ON running_record (course_id, duration);
CREATE INDEX idx_record_user_date ON running_record (user_id, run_date DESC);

CREATE TRIGGER update_course_modtime
    BEFORE UPDATE ON course
    FOR EACH ROW EXECUTE PROCEDURE update_modified_column();