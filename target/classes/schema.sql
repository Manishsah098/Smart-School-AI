-- ============================================================
-- SmartSchool AI - Complete MySQL 8+ Database Schema
-- Phase 2: Database Design & Migration SQL
-- ============================================================

CREATE DATABASE IF NOT EXISTS smartschool_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE smartschool_db;

-- ============================================================
-- CORE USER TABLES
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role        ENUM('ADMIN','TEACHER','STUDENT','PARENT','ACCOUNTANT') NOT NULL,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    failed_attempts INT NOT NULL DEFAULT 0,
    locked_until DATETIME NULL,
    last_login  DATETIME NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_role (role),
    INDEX idx_users_email (email)
);

-- ============================================================
-- ACADEMIC STRUCTURE TABLES
-- ============================================================

CREATE TABLE IF NOT EXISTS classes (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    grade_level INT NOT NULL,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_class_name (name)
);

CREATE TABLE IF NOT EXISTS sections (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id    BIGINT NOT NULL,
    name        VARCHAR(10) NOT NULL,
    capacity    INT NOT NULL DEFAULT 40,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_section_class FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    UNIQUE KEY uq_section_class (class_id, name)
);

CREATE TABLE IF NOT EXISTS subjects (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    credits     INT NOT NULL DEFAULT 1,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- PEOPLE TABLES
-- ============================================================

CREATE TABLE IF NOT EXISTS admins (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS teachers (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    qualification   VARCHAR(200),
    department      VARCHAR(100),
    experience_years INT NOT NULL DEFAULT 0,
    status          ENUM('ACTIVE','INACTIVE','ON_LEAVE') NOT NULL DEFAULT 'ACTIVE',
    join_date       DATE NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_teacher_status (status)
);

CREATE TABLE IF NOT EXISTS parents (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL,
    phone       VARCHAR(20) NOT NULL,
    address     TEXT,
    occupation  VARCHAR(100),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_parent_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS students (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    student_code    VARCHAR(20) NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    date_of_birth   DATE NOT NULL,
    gender          ENUM('MALE','FEMALE','OTHER') NOT NULL,
    email           VARCHAR(100),
    phone           VARCHAR(20),
    address         TEXT,
    admission_date  DATE NOT NULL,
    class_id        BIGINT,
    section_id      BIGINT,
    parent_id       BIGINT,
    status          ENUM('ACTIVE','INACTIVE','GRADUATED','EXPELLED','TRANSFERRED') NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_student_class   FOREIGN KEY (class_id)   REFERENCES classes(id)  ON DELETE SET NULL,
    CONSTRAINT fk_student_section FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE SET NULL,
    CONSTRAINT fk_student_parent  FOREIGN KEY (parent_id)  REFERENCES parents(id)  ON DELETE SET NULL,
    INDEX idx_student_status (status),
    INDEX idx_student_class (class_id),
    INDEX idx_student_code (student_code)
);

CREATE TABLE IF NOT EXISTS accountants (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accountant_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- TEACHER ASSIGNMENTS
-- ============================================================

CREATE TABLE IF NOT EXISTS teacher_subjects (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id  BIGINT NOT NULL,
    subject_id  BIGINT NOT NULL,
    class_id    BIGINT NOT NULL,
    section_id  BIGINT NOT NULL,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ts_teacher  FOREIGN KEY (teacher_id)  REFERENCES teachers(id)  ON DELETE CASCADE,
    CONSTRAINT fk_ts_subject  FOREIGN KEY (subject_id)  REFERENCES subjects(id)  ON DELETE CASCADE,
    CONSTRAINT fk_ts_class    FOREIGN KEY (class_id)    REFERENCES classes(id)   ON DELETE CASCADE,
    CONSTRAINT fk_ts_section  FOREIGN KEY (section_id)  REFERENCES sections(id)  ON DELETE CASCADE,
    UNIQUE KEY uq_teacher_assignment (teacher_id, subject_id, class_id, section_id)
);

-- ============================================================
-- TIMETABLE
-- ============================================================

CREATE TABLE IF NOT EXISTS timetable (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id    BIGINT NOT NULL,
    section_id  BIGINT NOT NULL,
    subject_id  BIGINT NOT NULL,
    teacher_id  BIGINT NOT NULL,
    day_of_week ENUM('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY') NOT NULL,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    room_number VARCHAR(20),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tt_class   FOREIGN KEY (class_id)   REFERENCES classes(id)   ON DELETE CASCADE,
    CONSTRAINT fk_tt_section FOREIGN KEY (section_id) REFERENCES sections(id)  ON DELETE CASCADE,
    CONSTRAINT fk_tt_subject FOREIGN KEY (subject_id) REFERENCES subjects(id)  ON DELETE CASCADE,
    CONSTRAINT fk_tt_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id)  ON DELETE CASCADE,
    INDEX idx_tt_day (day_of_week),
    INDEX idx_tt_teacher_day (teacher_id, day_of_week)
);

-- ============================================================
-- ATTENDANCE
-- ============================================================

CREATE TABLE IF NOT EXISTS attendance (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT NOT NULL,
    subject_id      BIGINT NOT NULL,
    class_id        BIGINT NOT NULL,
    section_id      BIGINT NOT NULL,
    teacher_id      BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    status          ENUM('PRESENT','ABSENT','LATE','EXCUSED') NOT NULL,
    remarks         VARCHAR(255),
    marked_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_att_student  FOREIGN KEY (student_id)  REFERENCES students(id)  ON DELETE CASCADE,
    CONSTRAINT fk_att_subject  FOREIGN KEY (subject_id)  REFERENCES subjects(id)  ON DELETE CASCADE,
    CONSTRAINT fk_att_class    FOREIGN KEY (class_id)    REFERENCES classes(id)   ON DELETE CASCADE,
    CONSTRAINT fk_att_section  FOREIGN KEY (section_id)  REFERENCES sections(id)  ON DELETE CASCADE,
    CONSTRAINT fk_att_teacher  FOREIGN KEY (teacher_id)  REFERENCES teachers(id)  ON DELETE CASCADE,
    UNIQUE KEY uq_attendance (student_id, subject_id, attendance_date),
    INDEX idx_att_student_date (student_id, attendance_date),
    INDEX idx_att_date (attendance_date)
);

-- ============================================================
-- EXAMINATIONS
-- ============================================================

CREATE TABLE IF NOT EXISTS exams (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    type            ENUM('UNIT_TEST','MID_TERM','FINAL','PRACTICAL') NOT NULL,
    class_id        BIGINT NOT NULL,
    section_id      BIGINT NOT NULL,
    subject_id      BIGINT NOT NULL,
    max_marks       DECIMAL(6,2) NOT NULL,
    exam_date       DATE NOT NULL,
    academic_year   VARCHAR(10) NOT NULL,
    created_by      BIGINT NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exam_class   FOREIGN KEY (class_id)    REFERENCES classes(id)   ON DELETE CASCADE,
    CONSTRAINT fk_exam_section FOREIGN KEY (section_id)  REFERENCES sections(id)  ON DELETE CASCADE,
    CONSTRAINT fk_exam_subject FOREIGN KEY (subject_id)  REFERENCES subjects(id)  ON DELETE CASCADE,
    CONSTRAINT fk_exam_creator FOREIGN KEY (created_by)  REFERENCES users(id),
    INDEX idx_exam_class_year (class_id, academic_year),
    INDEX idx_exam_date (exam_date)
);

CREATE TABLE IF NOT EXISTS marks (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id     BIGINT NOT NULL,
    student_id  BIGINT NOT NULL,
    marks_obtained DECIMAL(6,2) NOT NULL,
    percentage  DECIMAL(5,2) GENERATED ALWAYS AS (marks_obtained / (SELECT max_marks FROM exams WHERE id = exam_id) * 100) VIRTUAL,
    grade       VARCHAR(5),
    is_pass     BOOLEAN,
    remarks     VARCHAR(255),
    entered_by  BIGINT NOT NULL,
    entered_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_marks_exam    FOREIGN KEY (exam_id)    REFERENCES exams(id)     ON DELETE CASCADE,
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id) REFERENCES students(id)  ON DELETE CASCADE,
    CONSTRAINT fk_marks_entry   FOREIGN KEY (entered_by) REFERENCES users(id),
    UNIQUE KEY uq_student_exam (student_id, exam_id),
    INDEX idx_marks_student (student_id),
    INDEX idx_marks_exam (exam_id)
);

-- ============================================================
-- FEE MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS fee_structures (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id        BIGINT NOT NULL,
    academic_year   VARCHAR(10) NOT NULL,
    category        ENUM('TUITION','LIBRARY','LABORATORY','TRANSPORT','EXAMINATION','SPORTS','OTHER') NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    due_date        DATE NOT NULL,
    description     VARCHAR(255),
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fee_class FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    INDEX idx_fee_class_year (class_id, academic_year)
);

CREATE TABLE IF NOT EXISTS fees (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT NOT NULL,
    fee_structure_id BIGINT NOT NULL,
    total_amount    DECIMAL(10,2) NOT NULL,
    paid_amount     DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    remaining_amount DECIMAL(10,2) GENERATED ALWAYS AS (total_amount - paid_amount) STORED,
    due_date        DATE NOT NULL,
    status          ENUM('PAID','PARTIALLY_PAID','PENDING','OVERDUE') NOT NULL DEFAULT 'PENDING',
    academic_year   VARCHAR(10) NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_fee_student   FOREIGN KEY (student_id)        REFERENCES students(id)       ON DELETE CASCADE,
    CONSTRAINT fk_fee_structure FOREIGN KEY (fee_structure_id)  REFERENCES fee_structures(id) ON DELETE CASCADE,
    INDEX idx_fee_student (student_id),
    INDEX idx_fee_status (status)
);

CREATE TABLE IF NOT EXISTS payments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    fee_id          BIGINT NOT NULL,
    student_id      BIGINT NOT NULL,
    amount_paid     DECIMAL(10,2) NOT NULL,
    payment_date    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_mode    ENUM('CASH','CHEQUE','ONLINE','CARD') NOT NULL DEFAULT 'CASH',
    receipt_number  VARCHAR(50) NOT NULL UNIQUE,
    transaction_ref VARCHAR(100),
    collected_by    BIGINT NOT NULL,
    remarks         VARCHAR(255),
    CONSTRAINT fk_payment_fee     FOREIGN KEY (fee_id)       REFERENCES fees(id)      ON DELETE CASCADE,
    CONSTRAINT fk_payment_student FOREIGN KEY (student_id)   REFERENCES students(id)  ON DELETE CASCADE,
    CONSTRAINT fk_payment_by      FOREIGN KEY (collected_by) REFERENCES users(id),
    INDEX idx_payment_student (student_id),
    INDEX idx_payment_date (payment_date)
);

-- ============================================================
-- NOTICES
-- ============================================================

CREATE TABLE IF NOT EXISTS notices (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    content         TEXT NOT NULL,
    target_role     ENUM('ALL','ADMIN','TEACHER','STUDENT','PARENT','ACCOUNTANT') NOT NULL DEFAULT 'ALL',
    class_id        BIGINT NULL,
    published_by    BIGINT NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    published_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      DATETIME NULL,
    CONSTRAINT fk_notice_publisher FOREIGN KEY (published_by) REFERENCES users(id),
    CONSTRAINT fk_notice_class     FOREIGN KEY (class_id)     REFERENCES classes(id) ON DELETE SET NULL,
    INDEX idx_notice_role (target_role),
    INDEX idx_notice_active (is_active)
);

-- ============================================================
-- NOTIFICATIONS
-- ============================================================

CREATE TABLE IF NOT EXISTS notifications (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id    BIGINT NOT NULL,
    type            ENUM('ATTENDANCE_ALERT','FEE_REMINDER','EXAM_REMINDER','NOTICE','AI_RECOMMENDATION','SYSTEM') NOT NULL,
    title           VARCHAR(200) NOT NULL,
    message         TEXT NOT NULL,
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    read_at         DATETIME NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_recipient (recipient_id),
    INDEX idx_notif_read (recipient_id, is_read)
);

-- ============================================================
-- AI AGENT TABLES
-- ============================================================

CREATE TABLE IF NOT EXISTS agent_decisions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_name      VARCHAR(100) NOT NULL,
    student_id      BIGINT NULL,
    analysis_data   TEXT NOT NULL COMMENT 'JSON: input data analyzed',
    analysis_result TEXT NOT NULL COMMENT 'JSON: agent output',
    risk_level      ENUM('LOW','MEDIUM','HIGH','CRITICAL') NULL,
    risk_score      DECIMAL(5,2) NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ad_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL,
    INDEX idx_ad_student (student_id),
    INDEX idx_ad_agent (agent_name),
    INDEX idx_ad_risk (risk_level)
);

CREATE TABLE IF NOT EXISTS agent_recommendations (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    decision_id     BIGINT NOT NULL,
    agent_name      VARCHAR(100) NOT NULL,
    title           VARCHAR(200) NOT NULL,
    recommendation  TEXT NOT NULL,
    priority        ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL DEFAULT 'MEDIUM',
    confidence      DECIMAL(5,2) NULL COMMENT '0-100 percent',
    status          ENUM('PENDING_APPROVAL','APPROVED','REJECTED','EXECUTED') NOT NULL DEFAULT 'PENDING_APPROVAL',
    reviewed_by     BIGINT NULL,
    reviewed_at     DATETIME NULL,
    rejection_reason VARCHAR(500) NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ar_decision   FOREIGN KEY (decision_id)  REFERENCES agent_decisions(id) ON DELETE CASCADE,
    CONSTRAINT fk_ar_reviewer   FOREIGN KEY (reviewed_by)  REFERENCES users(id),
    INDEX idx_ar_status (status),
    INDEX idx_ar_agent (agent_name),
    INDEX idx_ar_priority (priority)
);

CREATE TABLE IF NOT EXISTS student_risk_scores (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id  BIGINT NOT NULL,
    risk_level  ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL,
    risk_score  DECIMAL(5,2) NOT NULL,
    att_score   DECIMAL(5,2) NULL,
    marks_score DECIMAL(5,2) NULL,
    trend_score DECIMAL(5,2) NULL,
    risk_reason TEXT,
    assessed_by VARCHAR(100) NOT NULL DEFAULT 'StudentSuccessAgent',
    assessed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_risk_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_risk_student (student_id),
    INDEX idx_risk_level (risk_level),
    INDEX idx_risk_date (assessed_at)
);

CREATE TABLE IF NOT EXISTS agent_tasks (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_type   VARCHAR(100) NOT NULL,
    trigger_event VARCHAR(100) NOT NULL,
    payload     TEXT NOT NULL COMMENT 'JSON payload',
    status      ENUM('QUEUED','RUNNING','COMPLETED','FAILED') NOT NULL DEFAULT 'QUEUED',
    result      TEXT NULL,
    error       TEXT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at  DATETIME NULL,
    completed_at DATETIME NULL,
    INDEX idx_task_status (status),
    INDEX idx_task_type (task_type)
);

-- ============================================================
-- AUDIT LOGGING
-- ============================================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NULL,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id   BIGINT NULL,
    old_value   TEXT NULL COMMENT 'JSON',
    new_value   TEXT NULL COMMENT 'JSON',
    ip_address  VARCHAR(50),
    description TEXT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_entity (entity_type, entity_id),
    INDEX idx_audit_date (created_at)
);

-- ============================================================
-- SEED DATA - ROLES & DEFAULT ADMIN
-- ============================================================

-- Default admin user (password: Admin@123 - BCrypt hash)
INSERT IGNORE INTO users (username, email, password_hash, role)
VALUES ('admin', 'admin@smartschool.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewYpfQN.h0Z5vKzG', 'ADMIN');

INSERT IGNORE INTO admins (user_id, name, phone)
VALUES (1, 'System Administrator', '0000000000');

-- Sample classes
INSERT IGNORE INTO classes (name, grade_level) VALUES
('Class 1', 1), ('Class 2', 2), ('Class 3', 3), ('Class 4', 4),
('Class 5', 5), ('Class 6', 6), ('Class 7', 7), ('Class 8', 8),
('Class 9', 9), ('Class 10', 10), ('Class 11', 11), ('Class 12', 12);

-- Sample sections for each class
INSERT IGNORE INTO sections (class_id, name) VALUES
(1, 'A'), (1, 'B'), (2, 'A'), (2, 'B'),
(9, 'A'), (9, 'B'), (10, 'A'), (10, 'B'),
(11, 'A'), (11, 'B'), (12, 'A'), (12, 'B');

-- Sample subjects
INSERT IGNORE INTO subjects (name, code, credits) VALUES
('Mathematics',   'MATH101', 4),
('Physics',       'PHY101',  4),
('Chemistry',     'CHM101',  4),
('Biology',       'BIO101',  4),
('English',       'ENG101',  3),
('Hindi',         'HIN101',  3),
('History',       'HIS101',  3),
('Geography',     'GEO101',  3),
('Computer Science', 'CS101', 4),
('Physical Education', 'PE101', 2);
