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

 Date: 04/01/2026 21:26:39
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
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attack_logs
-- ----------------------------
INSERT INTO `attack_logs` VALUES (1, '0:0:0:0:0:0:0:1', '/api/data', 'POST', 'SQL注入(Body内容)', '{\"name\": \"hacker\", \"desc\": \"\' UNION SELECT * FROM users --\"}', '2026-01-04 18:30:43');
INSERT INTO `attack_logs` VALUES (2, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'XSS攻击(URL参数)', 'javascript:alert(1)', '2026-01-04 19:26:05');
INSERT INTO `attack_logs` VALUES (3, '0:0:0:0:0:0:0:1', '/api/data', 'POST', 'XSS攻击(Body内容)', '{\"comment\": \"Test <img src=x onerror=alert(1)>\"}', '2026-01-04 19:26:12');
INSERT INTO `attack_logs` VALUES (4, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'XSS攻击(URL参数)', 'javascript:alert(1)', '2026-01-04 19:47:30');
INSERT INTO `attack_logs` VALUES (5, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:26');
INSERT INTO `attack_logs` VALUES (6, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:26');
INSERT INTO `attack_logs` VALUES (7, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:26');
INSERT INTO `attack_logs` VALUES (8, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:26');
INSERT INTO `attack_logs` VALUES (9, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:26');
INSERT INTO `attack_logs` VALUES (10, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:26');
INSERT INTO `attack_logs` VALUES (11, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:26');
INSERT INTO `attack_logs` VALUES (12, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:26');
INSERT INTO `attack_logs` VALUES (13, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:26');
INSERT INTO `attack_logs` VALUES (14, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:26');
INSERT INTO `attack_logs` VALUES (15, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问(>10次/秒)，触发临时封禁策略', '2026-01-04 20:29:35');
INSERT INTO `attack_logs` VALUES (16, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'SQL注入(URL参数)', 'admin\' OR 1=1 --', '2026-01-04 20:40:04');
INSERT INTO `attack_logs` VALUES (17, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:18');
INSERT INTO `attack_logs` VALUES (18, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:18');
INSERT INTO `attack_logs` VALUES (19, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:18');
INSERT INTO `attack_logs` VALUES (20, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:18');
INSERT INTO `attack_logs` VALUES (21, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:18');
INSERT INTO `attack_logs` VALUES (22, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:18');
INSERT INTO `attack_logs` VALUES (23, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:18');
INSERT INTO `attack_logs` VALUES (24, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:18');
INSERT INTO `attack_logs` VALUES (25, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:18');
INSERT INTO `attack_logs` VALUES (26, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:18');
INSERT INTO `attack_logs` VALUES (27, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:19');
INSERT INTO `attack_logs` VALUES (28, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:22');
INSERT INTO `attack_logs` VALUES (29, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:22');
INSERT INTO `attack_logs` VALUES (30, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:23');
INSERT INTO `attack_logs` VALUES (31, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:24');
INSERT INTO `attack_logs` VALUES (32, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:24');
INSERT INTO `attack_logs` VALUES (33, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:24');
INSERT INTO `attack_logs` VALUES (34, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:25');
INSERT INTO `attack_logs` VALUES (35, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:25');
INSERT INTO `attack_logs` VALUES (36, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:25');
INSERT INTO `attack_logs` VALUES (37, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:25');
INSERT INTO `attack_logs` VALUES (38, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:27');
INSERT INTO `attack_logs` VALUES (39, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:28');
INSERT INTO `attack_logs` VALUES (40, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:28');
INSERT INTO `attack_logs` VALUES (41, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:28');
INSERT INTO `attack_logs` VALUES (42, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:28');
INSERT INTO `attack_logs` VALUES (43, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:03:28');
INSERT INTO `attack_logs` VALUES (44, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'SQL注入(URL参数)', 'admin\' OR 1=1 --', '2026-01-04 21:10:02');
INSERT INTO `attack_logs` VALUES (45, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'SQL注入(URL参数)', 'admin\' OR 1=1 --', '2026-01-04 21:10:20');
INSERT INTO `attack_logs` VALUES (46, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'XSS攻击(URL参数)', '<script>alert(\'xss\')</script>', '2026-01-04 21:23:58');
INSERT INTO `attack_logs` VALUES (47, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'XSS攻击(URL参数)', 'javascript:alert(1)', '2026-01-04 21:23:58');
INSERT INTO `attack_logs` VALUES (48, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'SQL注入(URL参数)', 'admin\' OR 1=1 --', '2026-01-04 21:24:06');
INSERT INTO `attack_logs` VALUES (49, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'SQL注入(URL参数)', '\' UNION SELECT 1,user(),3 --', '2026-01-04 21:24:06');
INSERT INTO `attack_logs` VALUES (50, '0:0:0:0:0:0:0:1', '/api/data', 'POST', 'SQL注入(Body内容)', '{\"data\": \"\'; DROP TABLE users; --\"}', '2026-01-04 21:24:06');
INSERT INTO `attack_logs` VALUES (51, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (52, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (53, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (54, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (55, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (56, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (57, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (58, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (59, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (60, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (61, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (62, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (63, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (64, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (65, '0:0:0:0:0:0:0:1', '/api/user', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:04');
INSERT INTO `attack_logs` VALUES (66, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:06');
INSERT INTO `attack_logs` VALUES (67, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:08');
INSERT INTO `attack_logs` VALUES (68, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:09');
INSERT INTO `attack_logs` VALUES (69, '0:0:0:0:0:0:0:1', '/api/logs', 'GET', 'DDoS攻击', '检测到高频访问，触发临时封禁策略', '2026-01-04 21:25:12');

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
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of security_rules
-- ----------------------------
INSERT INTO `security_rules` VALUES (1, 'SQL注入基础规则', '(?i)(.*)(\\b)+(OR|SELECT|INSERT|DELETE|UPDATE|DROP|UNION)(\\b)+(.*)', 'SQL', 1);
INSERT INTO `security_rules` VALUES (2, 'XSS-Script标签', '(?i)<script[\\s\\S]*?>[\\s\\S]*?<\\/script>', 'XSS', 1);
INSERT INTO `security_rules` VALUES (3, 'XSS-伪协议', '(?i)(javascript|vbscript|jscript|livescript):', 'XSS', 1);
INSERT INTO `security_rules` VALUES (4, 'XSS-常见事件处理器', '(?i)\\b(onload|onerror|onclick|onmouseover|onfocus|onblur|onchange|onsubmit)\\b\\s*=', 'XSS', 1);
INSERT INTO `security_rules` VALUES (5, 'XSS-Iframe注入', '(?i)<(iframe|frame|object|embed|applet)\\b.*?>', 'XSS', 1);
INSERT INTO `security_rules` VALUES (6, 'XSS-CSS表达式', '(?i)expression\\s*\\(', 'XSS', 1);
INSERT INTO `security_rules` VALUES (7, 'XSS-危险标签', '(?i)<(link|style|base)\\b.*?>', 'XSS', 1);
INSERT INTO `security_rules` VALUES (8, 'SQL-基础关键字', '(?i)\\b(select|update|delete|insert|drop|truncate|union|create|alter)\\b', 'SQL', 1);
INSERT INTO `security_rules` VALUES (9, 'SQL-逻辑注入', '(?i)\\b(and|or)\\b\\s+[\\w\\d]+', 'SQL', 1);
INSERT INTO `security_rules` VALUES (10, 'SQL-注释符号', '(--|#|\\/\\*)', 'SQL', 1);
INSERT INTO `security_rules` VALUES (11, 'SQL-恒等式', '(?i)(1=1|1=2)', 'SQL', 1);
INSERT INTO `security_rules` VALUES (12, 'SQL-系统函数', '(?i)\\b(user\\(\\)|database\\(\\)|version\\(\\)|sleep\\(\\))', 'SQL', 1);
INSERT INTO `security_rules` VALUES (13, 'SQL-联合查询', '(?i)union\\s+(all\\s+)?select', 'SQL', 1);

SET FOREIGN_KEY_CHECKS = 1;
