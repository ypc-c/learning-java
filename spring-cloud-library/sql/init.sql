-- ============================================================
-- Campus Library Management System - Database Initialization
-- ============================================================

-- Database: library_user (user-service)
CREATE DATABASE IF NOT EXISTS `library_user`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `library_user`;

CREATE TABLE IF NOT EXISTS `t_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `username`    VARCHAR(50)  NOT NULL COMMENT 'login username',
  `password`    VARCHAR(255) NOT NULL COMMENT 'BCrypt-encoded password',
  `real_name`   VARCHAR(50)  DEFAULT NULL COMMENT 'real name',
  `role`        VARCHAR(20)  NOT NULL DEFAULT 'STUDENT' COMMENT 'STUDENT / ADMIN',
  `phone`       VARCHAR(20)  DEFAULT NULL,
  `email`       VARCHAR(100) DEFAULT NULL,
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '1=active, 0=disabled',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='user table';

-- Database: library_book (book-service)
CREATE DATABASE IF NOT EXISTS `library_book`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `library_book`;

CREATE TABLE IF NOT EXISTS `t_book` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT,
  `isbn`             VARCHAR(20)   DEFAULT NULL COMMENT 'ISBN number',
  `title`            VARCHAR(200)  NOT NULL COMMENT 'book title',
  `author`           VARCHAR(100)  DEFAULT NULL,
  `publisher`        VARCHAR(100)  DEFAULT NULL,
  `category`         VARCHAR(50)   DEFAULT NULL,
  `description`      TEXT          COMMENT 'book description',
  `cover_url`        VARCHAR(500)  DEFAULT NULL,
  `total_copies`     INT           NOT NULL DEFAULT 1,
  `available_copies` INT           NOT NULL DEFAULT 1,
  `status`           TINYINT       NOT NULL DEFAULT 1 COMMENT '1=available, 0=removed',
  `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='book table';

-- Database: library_borrow (borrow-service)
CREATE DATABASE IF NOT EXISTS `library_borrow`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `library_borrow`;

CREATE TABLE IF NOT EXISTS `t_borrow_record` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT       NOT NULL COMMENT 'user id from user-service',
  `book_id`     BIGINT       NOT NULL COMMENT 'book id from book-service',
  `borrow_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `due_time`    DATETIME     NOT NULL COMMENT 'expected return date',
  `return_time` DATETIME     DEFAULT NULL COMMENT 'actual return time',
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'BORROWED' COMMENT 'BORROWED / RETURNED / OVERDUE',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='borrow records table';

-- Database: library_notice (notice-service)
CREATE DATABASE IF NOT EXISTS `library_notice`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `library_notice`;

CREATE TABLE IF NOT EXISTS `t_notice` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT,
  `title`          VARCHAR(200) NOT NULL COMMENT 'notice title',
  `content`        TEXT         NOT NULL COMMENT 'notice content',
  `type`           VARCHAR(30)  NOT NULL DEFAULT 'ANNOUNCEMENT' COMMENT 'ANNOUNCEMENT / OVERDUE_REMINDER / SYSTEM',
  `target_user_id` BIGINT       DEFAULT NULL COMMENT 'NULL=all users, otherwise specific user',
  `is_read`        TINYINT      NOT NULL DEFAULT 0 COMMENT '0=unread, 1=read',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_target_user` (`target_user_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='notice table';

-- ============================================================
-- Seed Data
-- ============================================================

USE `library_user`;

-- Admin user (password: admin123, BCrypt encoded)
INSERT INTO `t_user` (`username`, `password`, `real_name`, `role`, `phone`, `email`) VALUES
('admin', '$2b$12$N9KmmUj.Uew0CXSp7c9ZUeMMyQRbgG/IQN3WnFeAfRCAh2rL.98Z.', 'System Admin', 'ADMIN', '13800000000', 'admin@library.com'),
('student1', '$2b$12$3BNRBo6ky44GO0ckM.80h.7GF/HC116FN4G5NZkD4JhXTDakNVh6O', 'Zhang San', 'STUDENT', '13800000001', 'zhangsan@library.com'),
('student2', '$2b$12$3BNRBo6ky44GO0ckM.80h.7GF/HC116FN4G5NZkD4JhXTDakNVh6O', 'Li Si', 'STUDENT', '13800000002', 'lisi@library.com');

USE `library_book`;

INSERT INTO `t_book` (`isbn`, `title`, `author`, `publisher`, `category`, `description`, `total_copies`, `available_copies`) VALUES
('978-7-111-58644-7', 'Deep in Java Virtual Machine', 'Zhou Zhiming', 'China Machine Press', 'Computer Science', 'In-depth analysis of JVM internals, memory management, and performance tuning', 5, 5),
('978-7-121-37945-7', 'Spring Cloud Microservices in Action', 'Zhai Yongchao', 'Publishing House of Electronics Industry', 'Computer Science', 'Comprehensive guide to Spring Cloud microservices architecture', 3, 3),
('978-7-302-53958-0', 'Data Structures and Algorithm Analysis', 'Mark Allen Weiss', 'Tsinghua University Press', 'Computer Science', 'Classic textbook on data structures and algorithms', 4, 4),
('978-7-111-61234-0', 'Clean Code', 'Robert C. Martin', 'China Machine Press', 'Software Engineering', 'A handbook of agile software craftsmanship', 3, 3),
('978-7-121-34567-8', 'Introduction to Artificial Intelligence', 'Stuart Russell', 'Publishing House of Electronics Industry', 'Artificial Intelligence', 'Comprehensive introduction to AI principles and practices', 2, 2);

USE `library_notice`;

INSERT INTO `t_notice` (`title`, `content`, `type`, `target_user_id`) VALUES
('Library Opening Hours Update', 'Starting from next week, the library will extend its opening hours to 10:00 PM on weekdays.', 'ANNOUNCEMENT', NULL),
('New Books Arrival', 'We have added 200 new books in the Computer Science category. Come check them out!', 'ANNOUNCEMENT', NULL),
('System Maintenance Notice', 'The library system will undergo maintenance this Saturday from 2:00 AM to 6:00 AM.', 'SYSTEM', NULL);
