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
    user_id BIGINT NOT NULL,                    -- FK
    title VARCHAR(100) NOT NULL,
    path GEOMETRY(LineString, 4326) NOT NULL,   -- GPS 경로 Data
    distance DOUBLE PRECISION NOT NULL,         -- meter
    duration INTEGER NOT NULL,                  -- seconds
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_course_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);