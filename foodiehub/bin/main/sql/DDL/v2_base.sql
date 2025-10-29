-- ===========================================
-- 0. 데이터베이스 생성
-- ===========================================
CREATE DATABASE IF NOT EXISTS foodiehub 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE foodiehub;

-- ===========================================
-- 1. User Table (사용자 정보)
-- ===========================================
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `birth_date` DATE DEFAULT NULL,
  `gender` ENUM('M','F','OTHER') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` VARCHAR(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `profile_image_url` VARCHAR(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `provider` VARCHAR(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` ENUM('ROLE_USER','ROLE_OWNER','ROLE_ADMIN') COLLATE utf8mb4_unicode_ci DEFAULT 'ROLE_USER',
  `is_deleted` CHAR(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================================
-- 2. Restaurant Table (맛집 정보)
-- ===========================================
CREATE TABLE `restaurant` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `owner_id` BIGINT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `address` VARCHAR(255) DEFAULT NULL,
  `region` VARCHAR(100) DEFAULT NULL,
  `category` VARCHAR(100) DEFAULT NULL,
  `latitude` DOUBLE DEFAULT NULL,
  `longitude` DOUBLE DEFAULT NULL,
  `main_image_url` VARCHAR(500) DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_restaurant_owner`
      FOREIGN KEY (`owner_id`)
      REFERENCES `user`(`id`)
      ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ===========================================
-- 3. Review Table (리뷰 + 댓글 통합)
-- ===========================================
CREATE TABLE `review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `restaurant_id` BIGINT NOT NULL,
  `user_id` BIGINT DEFAULT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `content` TEXT COLLATE utf8mb4_unicode_ci NOT NULL,
  `rating` INT DEFAULT NULL,
  `is_reply` TINYINT(1) DEFAULT '0',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_review_restaurant` (`restaurant_id`),
  KEY `fk_review_user` (`user_id`),
  KEY `fk_review_parent` (`parent_id`),
  CONSTRAINT `fk_review_parent` FOREIGN KEY (`parent_id`) REFERENCES `review` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
  CONSTRAINT `review_chk_1` CHECK ((`rating` BETWEEN 1 AND 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================================
-- 4. Board Table (공지/문의/건의/일반)
-- ===========================================
CREATE TABLE board (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  parent_id BIGINT DEFAULT NULL,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  category ENUM(
      'NOTICE',
      'NOTICE_GENERAL',
      'NOTICE_QUESTION',
      'NOTICE_SUGGESTION',
      'GENERAL',
      'QUESTION',
      'SUGGESTION'
  ) DEFAULT 'GENERAL',
  is_private TINYINT(1) DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY fk_board_user (user_id),
  KEY fk_board_parent (parent_id),
  CONSTRAINT fk_board_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
  CONSTRAINT fk_board_parent FOREIGN KEY (parent_id) REFERENCES board (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===========================================
-- 5. Image Table (리뷰 이미지)
-- ===========================================
CREATE TABLE `image` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `review_id` BIGINT NOT NULL,
  `file_name` VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_url` VARCHAR(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_type` VARCHAR(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_image_review` (`review_id`),
  CONSTRAINT `fk_image_review` FOREIGN KEY (`review_id`) REFERENCES `review` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================================
-- 6. AI Review Summary Table (AI 요약)
-- ===========================================
CREATE TABLE `ai_review_summary` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `restaurant_id` BIGINT NOT NULL,
  `summary_text` TEXT COLLATE utf8mb4_unicode_ci,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `restaurant_id` (`restaurant_id`),
  CONSTRAINT `fk_ai_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
