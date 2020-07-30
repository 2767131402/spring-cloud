/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : localhost:3306
 Source Schema         : zuul

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 30/07/2020 15:18:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gateway_zuul_route
-- ----------------------------
DROP TABLE IF EXISTS `gateway_zuul_route`;
CREATE TABLE `gateway_zuul_route`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `prefix` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `resource_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `service_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `retryable` tinyint(1) NULL DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  `strip_prefix` int(11) NULL DEFAULT NULL,
  `api_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gateway_zuul_route
-- ----------------------------
INSERT INTO `gateway_zuul_route` VALUES ('1', '/api', NULL, '/c', '/test', 'hello-spring-cloud-service', NULL, 0, 1, 1, NULL);
INSERT INTO `gateway_zuul_route` VALUES ('2', '/api', NULL, '/b', '/hi', 'hello-spring-cloud-web-admin-feign', NULL, 0, 1, 1, NULL);
INSERT INTO `gateway_zuul_route` VALUES ('3', '/api', NULL, '/a', '/hi', 'hello-spring-cloud-web-admin-ribbon', NULL, 0, 1, 1, NULL);

SET FOREIGN_KEY_CHECKS = 1;
