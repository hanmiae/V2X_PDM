-- V2X 예지보전 — DB 명세 (public 스키마)
-- 기존에 Hibernate 등으로 생성된 동일 이름 테이블이 있으면 제거 후 재생성 (개발 기동용)

DROP TABLE IF EXISTS risk_analysis_result CASCADE;
DROP TABLE IF EXISTS signal_controller_log CASCADE;
DROP TABLE IF EXISTS v2x_communication_log CASCADE;
DROP TABLE IF EXISTS admin_user CASCADE;
DROP TABLE IF EXISTS intersection_info CASCADE;

CREATE TABLE intersection_info (
    intersection_id   VARCHAR(20) PRIMARY KEY,
    intersection_name VARCHAR(100) NOT NULL,
    latitude          NUMERIC(10, 7),
    longitude         NUMERIC(10, 7),
    created_at        TIMESTAMP NOT NULL
);

CREATE TABLE admin_user (
    employee_no   VARCHAR(30) PRIMARY KEY,
    login_id      VARCHAR(50) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    admin_name    VARCHAR(100) NOT NULL,
    phone_number  VARCHAR(20) NOT NULL
);

CREATE TABLE signal_controller_log (
    controller_log_id     BIGSERIAL PRIMARY KEY,
    intersection_id       VARCHAR(20) NOT NULL REFERENCES intersection_info (intersection_id),
    controller_device_id  VARCHAR(50) NOT NULL,
    controller_log_time   TIMESTAMP NOT NULL,
    response_time_ms      NUMERIC(8, 2),
    error_count           INTEGER NOT NULL,
    reboot_count          INTEGER NOT NULL,
    cpu_temp              NUMERIC(6, 2),
    uptime_min            INTEGER,
    created_at            TIMESTAMP NOT NULL
);

CREATE TABLE v2x_communication_log (
    communication_log_id      BIGSERIAL PRIMARY KEY,
    intersection_id             VARCHAR(20) NOT NULL REFERENCES intersection_info (intersection_id),
    v2x_device_id               VARCHAR(50) NOT NULL,
    v2x_log_time                TIMESTAMP NOT NULL,
    spat_send_count             INTEGER NOT NULL,
    spat_fail_count             INTEGER NOT NULL,
    avg_latency_ms              NUMERIC(8, 2),
    comm_fail_count             INTEGER NOT NULL,
    connected_vehicle_count     INTEGER NOT NULL,
    created_at                  TIMESTAMP NOT NULL
);

CREATE TABLE risk_analysis_result (
    risk_result_id         BIGSERIAL PRIMARY KEY,
    intersection_id        VARCHAR(20) NOT NULL REFERENCES intersection_info (intersection_id),
    controller_log_id      BIGINT NOT NULL REFERENCES signal_controller_log (controller_log_id),
    communication_log_id   BIGINT NOT NULL REFERENCES v2x_communication_log (communication_log_id),
    analysis_time          TIMESTAMP NOT NULL,
    controller_risk_score  NUMERIC(6, 2),
    v2x_risk_score         NUMERIC(6, 2),
    total_risk_score       NUMERIC(6, 2) NOT NULL,
    risk_level             VARCHAR(20) NOT NULL,
    analysis_comment       TEXT,
    created_at             TIMESTAMP NOT NULL
);
