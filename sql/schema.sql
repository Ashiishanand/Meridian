CREATE DATABASE IF NOT EXISTS meridian
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE meridian;

CREATE TABLE IF NOT EXISTS users (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(100) NOT NULL,
  email         VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  role          ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
  is_active     BOOLEAN NOT NULL DEFAULT TRUE,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_login    TIMESTAMP NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS goal_types (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  name            VARCHAR(60) NOT NULL UNIQUE,
  description     VARCHAR(255),
  tracking_metric ENUM('HOURS','SESSIONS','PERCENT') NOT NULL DEFAULT 'HOURS',
  is_active       BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS goals (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  user_id      INT NOT NULL,
  goal_type_id INT NOT NULL,
  name         VARCHAR(150) NOT NULL,
  target_hours DECIMAL(7,2) NOT NULL,
  deadline     DATE NOT NULL,
  status       ENUM('ACTIVE','COMPLETED','OVERDUE') NOT NULL DEFAULT 'ACTIVE',
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_goal_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_goal_type FOREIGN KEY (goal_type_id) REFERENCES goal_types(id),
  CONSTRAINT chk_target CHECK (target_hours > 0),
  INDEX idx_goal_user (user_id),
  INDEX idx_goal_deadline (deadline)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS time_entries (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  goal_id    INT NOT NULL,
  user_id    INT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time   DATETIME NOT NULL,
  notes      VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_te_goal FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE,
  CONSTRAINT fk_te_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT chk_time CHECK (end_time > start_time),
  INDEX idx_te_goal (goal_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS activity_log (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT NULL,
  action     VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_log_time (created_at)
) ENGINE=InnoDB;

INSERT IGNORE INTO goal_types (name, description, tracking_metric) VALUES
 ('Study',    'Courses, exams and skill building', 'HOURS'),
 ('Fitness',  'Workouts and health habits',        'SESSIONS'),
 ('Career',   'Projects, interviews, networking',  'HOURS'),
 ('Personal', 'Hobbies and life goals',            'HOURS');
