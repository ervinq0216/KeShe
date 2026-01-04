/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : waf_db

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 04/01/2026 20:18:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attack_logs
-- ----------------------------
DROP TABLE IF EXISTS `attack_logs`;
CREATE TABLE `attack_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '攻击者IP',
  `request_uri` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求路径',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法(GET/POST)',
  `attack_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '攻击类型(SQL注入/XSS/DDoS)',
  `payload` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '具体的攻击参数内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attack_logs
-- ----------------------------
INSERT INTO `attack_logs` VALUES (1, '0:0:0:0:0:0:0:1', '/api/data', 'POST', 'SQL注入(Body内容)', '{\"name\": \"hacker\", \"desc\": \"\' UNION SELECT * FROM users --\"}', '2026-01-04 18:30:43');
INSERT INTO `attack_logs` VALUES (2, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'XSS攻击(URL参数)', 'javascript:alert(1)', '2026-01-04 19:26:05');
INSERT INTO `attack_logs` VALUES (3, '0:0:0:0:0:0:0:1', '/api/data', 'POST', 'XSS攻击(Body内容)', '{\"comment\": \"Test <img src=x onerror=alert(1)>\"}', '2026-01-04 19:26:12');
INSERT INTO `attack_logs` VALUES (4, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'XSS攻击(URL参数)', 'javascript:alert(1)', '2026-01-04 19:47:30');

-- ----------------------------
-- Table structure for security_rules
-- ----------------------------
DROP TABLE IF EXISTS `security_rules`;
CREATE TABLE `security_rules`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规则名称',
  `regex_pattern` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '正则表达式',
  `rule_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规则类型(SQL/XSS)',
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of security_rules
-- ----------------------------
INSERT INTO `security_rules` VALUES (1, 'SQL注入基础规则', '(?i)(.*)(\\b)+(OR|SELECT|INSERT|DELETE|UPDATE|DROP|UNION)(\\b)+(.*)', 'SQL', 1);

SET FOREIGN_KEY_CHECKS = 1;
