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

 Date: 13/06/2025 14:28:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tmp1
-- ----------------------------
DROP TABLE IF EXISTS `tmp1`;
CREATE TABLE `tmp1`  (
  `id` int(11) NOT NULL,
  `word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_word`(`word`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '临时表1：用来缓存part_base、voice_base的数据栈' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tmp1
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
