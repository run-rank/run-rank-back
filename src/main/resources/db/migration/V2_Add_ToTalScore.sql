ALTER TABLE users ADD COLUMN total_distance BIGINT DEFAULT 0;
ALTER TABLE users ADD COLUMN total_score DOUBLE PRECISION DEFAULT 0.0;

UPDATE users u
SET
    total_distance = (
        SELECT COALESCE(SUM(r.distance), 0)
        FROM running_record r
        WHERE r.user_id = u.id
    ),

    total_score = (
        (SELECT COALESCE(SUM(r.distance), 0)
         FROM running_record r WHERE r.user_id = u.id) * 0.5 +
        (SELECT COUNT(*) FROM jelly j WHERE j.user_id = u.id) * 0.5
    );

ALTER TABLE users ALTER COLUMN total_distance SET NOT NULL;
ALTER TABLE users ALTER COLUMN total_score SET NOT NULL;

CREATE INDEX idx_user_total_score ON users(total_score DESC);