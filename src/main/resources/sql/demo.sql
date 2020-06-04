-- phpMyAdmin SQL Dump
-- version 5.0.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 数据库： `dataapi`
--
CREATE DATABASE IF NOT EXISTS `dataapi` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `dataapi`;

-- --------------------------------------------------------

--
-- 表的结构 `presto_sql`
--

DROP TABLE IF EXISTS `presto_sql`;
CREATE TABLE `presto_sql` (
  `id` bigint NOT NULL,
  `sql` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `push_task_his`
--

DROP TABLE IF EXISTS `push_task_his`;
CREATE TABLE `push_task_his` (
  `id` bigint NOT NULL COMMENT 'id',
  `task_id` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '任务ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `reckon_rule`
--

DROP TABLE IF EXISTS `reckon_rule`;
CREATE TABLE `reckon_rule` (
  `id` int NOT NULL COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '规则名称',
  `temple_code` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '模板code',
  `tabale_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'task表名称',
  `rule_type` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '类型：实时REALTIME，离线OFFLINE',
  `task_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '00' COMMENT '任务状态00未执行，01执行中，02执行完成，03执行失败',
  `rule` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '查询规则',
  `descs` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT '2018-01-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_del` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '01' COMMENT '是否删除：01否，02删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `temple_db`
--

DROP TABLE IF EXISTS `temple_db`;
CREATE TABLE `temple_db` (
  `source_code` int NOT NULL COMMENT '数据库模板code',
  `name` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '名称',
  `type` char(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'ES,HIVE,HBASE,KAFKA,ORACLE,MYSQL',
  `url` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '连接url',
  `zk` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'zookeper',
  `driver` varchar(800) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '驱动配置',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '密码',
  `descs` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建人',
  `is_del` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '01' COMMENT '是否删除：01否，02删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `temple_log`
--

DROP TABLE IF EXISTS `temple_log`;
CREATE TABLE `temple_log` (
  `id` bigint NOT NULL,
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '操作类型：ES ,HIVE,HBASE',
  `opt_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间',
  `opt_status` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '操作状态：01新增，02修改，03删除',
  `opt_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '操作人',
  `temple_code` bigint DEFAULT NULL COMMENT '模板code'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `temple_sql`
--

DROP TABLE IF EXISTS `temple_sql`;
CREATE TABLE `temple_sql` (
  `temple_code` bigint NOT NULL COMMENT '模板编号',
  `source_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'DB库模板',
  `type` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作类型：ES ,HIVE,HBASE',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板内容',
  `descr` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
  `creator` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_del` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '01' COMMENT '是否删除 01默认显示， 02软删除',
  `is_open` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '01' COMMENT '01私有，02公共'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- 转储表的索引
--

--
-- 表的索引 `presto_sql`
--
ALTER TABLE `presto_sql`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 表的索引 `push_task_his`
--
ALTER TABLE `push_task_his`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `task_idex` (`task_id`) USING BTREE;

--
-- 表的索引 `reckon_rule`
--
ALTER TABLE `reckon_rule`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 表的索引 `temple_db`
--
ALTER TABLE `temple_db`
  ADD PRIMARY KEY (`source_code`) USING BTREE,
  ADD KEY `db_type_index` (`type`) USING BTREE;

--
-- 表的索引 `temple_log`
--
ALTER TABLE `temple_log`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `log_temcode_index` (`temple_code`) USING BTREE,
  ADD KEY `log_type_index` (`type`) USING BTREE;

--
-- 表的索引 `temple_sql`
--
ALTER TABLE `temple_sql`
  ADD PRIMARY KEY (`temple_code`) USING BTREE,
  ADD KEY `code_index` (`temple_code`) USING BTREE,
  ADD KEY `type_index` (`type`) USING BTREE;

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `presto_sql`
--
ALTER TABLE `presto_sql`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `push_task_his`
--
ALTER TABLE `push_task_his`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id';

--
-- 使用表AUTO_INCREMENT `reckon_rule`
--
ALTER TABLE `reckon_rule`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT 'id';

--
-- 使用表AUTO_INCREMENT `temple_db`
--
ALTER TABLE `temple_db`
  MODIFY `source_code` int NOT NULL AUTO_INCREMENT COMMENT '数据库模板code';

--
-- 使用表AUTO_INCREMENT `temple_log`
--
ALTER TABLE `temple_log`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `temple_sql`
--
ALTER TABLE `temple_sql`
  MODIFY `temple_code` bigint NOT NULL AUTO_INCREMENT COMMENT '模板编号';
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
