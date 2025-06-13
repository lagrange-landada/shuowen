/*
 Navicat Premium Data Transfer

 Source Server         : MySQL_TEST_local
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : localhost:3306
 Source Schema         : shuowen

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 13/06/2025 14:28:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tmp2
-- ----------------------------
DROP TABLE IF EXISTS `tmp2`;
CREATE TABLE `tmp2`  (
  `word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `delete` int(1) NULL DEFAULT 0,
  UNIQUE INDEX `idx_word`(`word`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '临时表2：用来缓存part_base、voice_base的数据栈' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tmp2
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
