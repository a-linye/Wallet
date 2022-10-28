/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50720
 Source Host           : localhost:3306
 Source Schema         : wallet

 Target Server Type    : MySQL
 Target Server Version : 50720
 File Encoding         : 65001

 Date: 28/10/2022 13:26:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for transaction
-- ----------------------------
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID，自增',
  `tx_hash` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易哈希',
  `nonce` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账户交易次数',
  `blockHash` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区块哈希',
  `blockNumber` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区块号',
  `transactionIndex` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易索引',
  `tx_from` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易发起者',
  `tx_to` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易接收者',
  `tx_value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '转账金额',
  `gasPrice` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手续费价格',
  `gas` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户提交的手续费',
  `input` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易的附加数据',
  `creates` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'creates',
  `publicKey` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公钥',
  `raw` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'raw',
  `r` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易签名的值',
  `s` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易签名的值',
  `v` bigint(100) NULL DEFAULT NULL COMMENT '交易签名的值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '交易记录表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of transaction
-- ----------------------------
INSERT INTO `transaction` VALUES (3, '0xf27177f100e677ae305ac88b4aa083d8cbb29e6d4616faa366fe1b04be21d963', '44', '0x0ae4674e4fa4ddd15820898139040e7e13d6e729c01c5e6584793d4685eacd19', '1466701', '0', '0x21197647b87f385c7284de7e31d1f2bba39a5bab', '0x21197647b87f385c7284de7e31d1f2bba39a5bcc', '10000000000000000000', '1', '210000', '0x', NULL, NULL, NULL, '0x3ccecae4bb4732eb10734c5a0c66be4edd206d26a28007521a45f7dcd02e306e', '0x17a2cd462b54d1b0827b8b3b07b7d70eee606e6ec83576d572eafaf9b0e87274', 28);
INSERT INTO `transaction` VALUES (4, '0xe95b3ba7bdd2a75f80651408463b2ca38c79d56b0e9e6a40d871cb3a56c4eacf', '45', '0x79d1a69e2efe09ae360eee61e8d570316f60da2cdbc6edc0ec0aa10cc311c20b', '1466863', '0', '0x21197647b87f385c7284de7e31d1f2bba39a5bab', '0x21197647b87f385c7284de7e31d1f2bba39a5bcc', '10000000000000000000', '1', '210000', '0x', NULL, NULL, NULL, '0xe90b8215608f42869fab67d119058830af3907385a6954a6d345c39865d826d0', '0x63076f36f4c91e3f1867133669df514bc5e2f849e882eae8a74d0e9184854d70', 28);
INSERT INTO `transaction` VALUES (5, '0xe2ffb400e012a0157c5279c327024693e6176932ddef2a4b27a718f0d809cefd', '46', '0xffccfcd03209bf6b7cd698faa5f41b8e33ca88c372428369b82649d8da7751f8', '1467689', '0', '0x21197647b87f385c7284de7e31d1f2bba39a5bab', '0x21197647b87f385c7284de7e31d1f2bba39a5bcc', '10000000000000000000', '1', '210000', '0x', NULL, NULL, NULL, '0x8d1d30148e1c77cdbe41bd245def03ce00c21fb7971ea253d4555a4b9d8f4f94', '0x3cf2aca66f081b8f17364abd5ce69460b5f2dcafcf17eb289a2cfa16dfc17d7', 28);

SET FOREIGN_KEY_CHECKS = 1;
