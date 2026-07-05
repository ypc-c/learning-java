-- 学生成绩管理系统数据库
CREATE DATABASE IF NOT EXISTS student_score_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_score_system;

-- 用户表
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `role` VARCHAR(20) NOT NULL COMMENT '角色：ADMIN-超级管理员，TEACHER-教师，STUDENT-学生',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `student_no` VARCHAR(20) COMMENT '学号（学生专用）',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (`username`),
  INDEX idx_student_no (`student_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 课程表
CREATE TABLE `course` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
  `course_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '课程编号',
  `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
  `credit` DECIMAL(3,1) NOT NULL COMMENT '学分',
  `semester` VARCHAR(20) NOT NULL COMMENT '授课学期',
  `teacher_id` BIGINT COMMENT '授课教师ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_course_code (`course_code`),
  INDEX idx_course_name (`course_name`),
  INDEX idx_semester (`semester`),
  INDEX idx_teacher_id (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 选课表（学生-课程关联）
CREATE TABLE `course_student` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_course_student (`course_id`, `student_id`),
  INDEX idx_student_id (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课表';

-- 成绩表
CREATE TABLE `score` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '成绩ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `usual_score` DECIMAL(5,2) COMMENT '平时成绩',
  `midterm_score` DECIMAL(5,2) COMMENT '期中成绩',
  `final_score` DECIMAL(5,2) COMMENT '期末成绩',
  `total_score` DECIMAL(5,2) COMMENT '总成绩',
  `grade_level` VARCHAR(20) COMMENT '成绩等级：优秀、良好、及格、不及格',
  `gpa` DECIMAL(3,1) COMMENT '绩点',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_course_student (`course_id`, `student_id`),
  INDEX idx_student_id (`student_id`),
  INDEX idx_total_score (`total_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 插入初始数据
-- 超级管理员账号：admin/admin123
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `phone`, `email`) 
VALUES ('admin', 'admin123', '系统管理员', 'ADMIN', '13800000000', 'admin@example.com');

-- 教师账号：teacher1/123456, teacher2/123456
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `phone`, `email`) 
VALUES 
('teacher1', '123456', '张老师', 'TEACHER', '13800000001', 'teacher1@example.com'),
('teacher2', '123456', '李老师', 'TEACHER', '13800000002', 'teacher2@example.com');

-- 学生账号：学号作为用户名，初始密码123456
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `phone`, `email`, `student_no`) 
VALUES 
('2024001', '123456', '王小明', 'STUDENT', '13900000001', 'student1@example.com', '2024001'),
('2024002', '123456', '李小红', 'STUDENT', '13900000002', 'student2@example.com', '2024002'),
('2024003', '123456', '张小刚', 'STUDENT', '13900000003', 'student3@example.com', '2024003'),
('2024004', '123456', '刘小芳', 'STUDENT', '13900000004', 'student4@example.com', '2024004'),
('2024005', '123456', '陈小华', 'STUDENT', '13900000005', 'student5@example.com', '2024005');

-- 课程数据
INSERT INTO `course` (`course_code`, `course_name`, `credit`, `semester`, `teacher_id`) 
VALUES 
('CS101', 'Java程序设计', 4.0, '2024-1', 2),
('CS102', '数据结构与算法', 4.0, '2024-1', 2),
('CS201', '数据库原理', 3.0, '2024-2', 3),
('CS202', '操作系统', 3.5, '2024-2', 3),
('MATH101', '高等数学', 5.0, '2024-1', 2);

-- 选课数据
INSERT INTO `course_student` (`course_id`, `student_id`) 
VALUES 
(1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(2, 4), (2, 5), (2, 6), (2, 7), (2, 8),
(3, 4), (3, 5), (3, 6),
(4, 4), (4, 5),
(5, 4), (5, 5), (5, 6), (5, 7), (5, 8);
