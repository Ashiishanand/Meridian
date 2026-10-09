-- Meridian: Time Management and Goal Setting Tool
-- Run this script in MySQL Workbench before starting the application.
CREATE DATABASE IF NOT EXISTS meridian_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE meridian_db;

CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(190) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  password_salt VARCHAR(255) NOT NULL,
  role ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
  account_status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS goals (
  goal_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(150) NOT NULL,
  description TEXT,
  start_date DATE NOT NULL,
  target_date DATE NOT NULL,
  status ENUM('NOT_STARTED','IN_PROGRESS','COMPLETED','ON_HOLD') NOT NULL DEFAULT 'NOT_STARTED',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_goals_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  INDEX idx_goals_user_status (user_id, status),
  CONSTRAINT chk_goal_dates CHECK (target_date >= start_date)
);

CREATE TABLE IF NOT EXISTS tasks (
  task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  goal_id BIGINT NULL,
  title VARCHAR(150) NOT NULL,
  description TEXT,
  due_date DATE NULL,
  priority ENUM('LOW','MEDIUM','HIGH') NOT NULL DEFAULT 'MEDIUM',
  status ENUM('PENDING','IN_PROGRESS','COMPLETED') NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_tasks_goal FOREIGN KEY (goal_id) REFERENCES goals(goal_id) ON DELETE SET NULL,
  INDEX idx_tasks_user_status (user_id, status),
  INDEX idx_tasks_due_date (due_date)
);
