CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE users (
      id BIGSERIAL PRIMARY KEY,     -- PK: BIGSERIAL(자동 증가)
      email VARCHAR(255) NOT NULL,
      password VARCHAR(255) NOT NULL,
      user_name VARCHAR(255) NOT NULL,
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
    distance INTEGER NOT NULL,
    encoded_polyline TEXT NOT NULL,
    path GEOMETRY(LineString, 4326),
    start_point GEOMETRY(Point, 4326),
    start_lat DOUBLE PRECISION NOT NULL,
    start_lng DOUBLE PRECISION NOT NULL,
    visibility VARCHAR(20) NOT NULL,
    route JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_course_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_course_coords ON course (start_lat, start_lng);

CREATE TABLE running_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_record_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_record_course FOREIGN KEY (course_id)
        REFERENCES course(id) ON DELETE CASCADE,
    duration INTEGER NOT NULL,      -- 초 단위
    distance INTEGER NOT NULL,      -- 미터 단위
    run_date DATE NOT NULL,
    gps_route JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
