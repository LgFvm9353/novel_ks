/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : novel_platform

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 01/01/2026 16:10:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for authors
-- ----------------------------
DROP TABLE IF EXISTS `authors`;
CREATE TABLE `authors`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '新手',
  `is_transcript` tinyint(1) NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_author_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_author_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chapters
-- ----------------------------
DROP TABLE IF EXISTS `chapters`;
CREATE TABLE `chapters`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `novel_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `chapter_number` int NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `word_count` int NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_novel_chapter`(`novel_id` ASC, `chapter_number` ASC) USING BTREE,
  CONSTRAINT `fk_chapter_novel` FOREIGN KEY (`novel_id`) REFERENCES `novels` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `novel_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `management_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price_per_thousand_words` decimal(10, 2) NULL DEFAULT NULL,
  `ranking` int NULL DEFAULT NULL,
  `novel_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status_note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_comment_novel`(`novel_id` ASC) USING BTREE,
  INDEX `fk_comment_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_novel` FOREIGN KEY (`novel_id`) REFERENCES `novels` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for novels
-- ----------------------------
DROP TABLE IF EXISTS `novels`;
CREATE TABLE `novels`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `author_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '连载中',
  `total_chapters` int NULL DEFAULT 0,
  `total_pages` int NULL DEFAULT 0,
  `vote_count` int NULL DEFAULT 0,
  `chapters_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_novel_author`(`author_id` ASC) USING BTREE,
  INDEX `fk_novel_category`(`category_id` ASC) USING BTREE,
  CONSTRAINT `fk_novel_author` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_novel_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for reading_history
-- ----------------------------
DROP TABLE IF EXISTS `reading_history`;
CREATE TABLE `reading_history`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `novel_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `chapter_number` int NOT NULL,
  `chapter_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `last_read_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_history_user_novel`(`user_id` ASC, `novel_id` ASC) USING BTREE,
  INDEX `fk_history_novel`(`novel_id` ASC) USING BTREE,
  CONSTRAINT `fk_history_novel` FOREIGN KEY (`novel_id`) REFERENCES `novels` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_history_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'reader',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for votes
-- ----------------------------
DROP TABLE IF EXISTS `votes`;
CREATE TABLE `votes`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `novel_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_vote_user_novel`(`user_id` ASC, `novel_id` ASC) USING BTREE,
  INDEX `fk_vote_novel`(`novel_id` ASC) USING BTREE,
  CONSTRAINT `fk_vote_novel` FOREIGN KEY (`novel_id`) REFERENCES `novels` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_vote_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
