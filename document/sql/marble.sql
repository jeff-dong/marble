/*
Navicat MySQL Data Transfer

Source Server         : localhost_3307
Source Server Version : 50611
Source Host           : localhost:3307
Source Database       : marble

Target Server Type    : MYSQL
Target Server Version : 50611
File Encoding         : 65001

Date: 2016-02-01 15:54:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for marble_app
-- ----------------------------
DROP TABLE IF EXISTS `marble_app`;
CREATE TABLE `marble_app` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长ID',
  `code` varchar(20) NOT NULL DEFAULT '' COMMENT '应用code。APPID',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '应用名称',
  `description` varchar(100) DEFAULT NULL COMMENT '应用描述',
  `owner` varchar(20) NOT NULL DEFAULT '' COMMENT '应用所有者. 使用员工号',
  `status` smallint(6) NOT NULL DEFAULT '0' COMMENT '应用状态。0:不可用；1:可用',
  `marbleVersion` varchar(10) DEFAULT '' COMMENT '使用的marble的版本号',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_code` (`code`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='应用信息表';

-- ----------------------------
-- Table structure for marble_app_sched
-- ----------------------------
DROP TABLE IF EXISTS `marble_app_sched`;
CREATE TABLE `marble_app_sched` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长主键',
  `app_code` varchar(20) NOT NULL DEFAULT '' COMMENT '应用ID',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '计划任务scheduler名称',
  `description` varchar(100) DEFAULT NULL COMMENT '计划任务scheduler描述',
  `status` smallint(6) NOT NULL DEFAULT '0' COMMENT '应用状态。0:不可用；1:可用',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_app_sched` (`app_code`,`name`) USING BTREE,
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='计划任务表';

-- ----------------------------
-- Table structure for marble_app_server
-- ----------------------------
DROP TABLE IF EXISTS `marble_app_server`;
CREATE TABLE `marble_app_server` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长主键',
  `app_code` varchar(20) NOT NULL DEFAULT '' COMMENT '应用code。APPID',
  `server_group` varchar(50) NOT NULL DEFAULT '' COMMENT '服务器所属的组',
  `server_name` varchar(50) NOT NULL DEFAULT '' COMMENT '服务器名称',
  `server_ip` varchar(20) NOT NULL DEFAULT '' COMMENT '服务器IP地址',
  `server_description` varchar(100) DEFAULT NULL COMMENT '服务器描述',
  `server_status` smallint(6) NOT NULL DEFAULT '0' COMMENT '0: 不可用；1：可用',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_app_server_name` (`app_code`,`server_group`,`server_name`) USING BTREE,
  UNIQUE KEY `IDX_U_app_server_ip` (`app_code`,`server_group`,`server_ip`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE,
  KEY `IDX_S_all` (`app_code`,`server_group`,`server_name`,`server_ip`,`server_status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT=' 应用服务器对应表';

-- ----------------------------
-- Table structure for marble_configure
-- ----------------------------
DROP TABLE IF EXISTS `marble_configure`;
CREATE TABLE `marble_configure` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group` varchar(45) NOT NULL DEFAULT '' COMMENT '配置组名称',
  `key` varchar(45) NOT NULL DEFAULT '' COMMENT '配置项Key',
  `value` varchar(1000) NOT NULL DEFAULT '' COMMENT '值内容',
  `description` varchar(300) DEFAULT NULL COMMENT '描述',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_GroupKey` (`group`,`key`) USING BTREE,
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='marble配置表';

-- ----------------------------
-- Table structure for marble_log_job
-- ----------------------------
DROP TABLE IF EXISTS `marble_log_job`;
CREATE TABLE `marble_log_job` (
  `id` bigint(50) NOT NULL AUTO_INCREMENT COMMENT '自增长主键',
  `app_code` varchar(255) DEFAULT NULL COMMENT '应用code',
  `sched_name` varchar(50) NOT NULL DEFAULT '' COMMENT '计划任务scheduler名称',
  `job_name` varchar(50) NOT NULL DEFAULT '' COMMENT 'job名称',
  `job_cron_express` varchar(100) NOT NULL DEFAULT '' COMMENT 'cron表达式',
  `server_info` varchar(100) DEFAULT '' COMMENT '服务器信息。ip:port',
  `result_code` int(6) NOT NULL DEFAULT '10' COMMENT '执行结果代码。10：成功；20：失败',
  `result_msg` varchar(500) DEFAULT '' COMMENT '执行结果描述。',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='Job执行历史记录';

-- ----------------------------
-- Table structure for marble_qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_blob_triggers`;
CREATE TABLE `marble_qrtz_blob_triggers` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长主键',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT 'scheduler 名称',
  `TRIGGER_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT 'Trigger名称',
  `TRIGGER_GROUP` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger组名',
  `BLOB_DATA` varchar(2000) DEFAULT '' COMMENT 'blob类型的数据',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_TRIG` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT=' Trigger 作为 Blob 类型存储。用于 Quartz 用户用 JDBC 创建他们自己定制的 Trigger 类型，JobStore 并不知道如何存储实例的时候';

-- ----------------------------
-- Table structure for marble_qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_calendars`;
CREATE TABLE `marble_qrtz_calendars` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `CALENDAR_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT 'canendar名称',
  `CALENDAR` varchar(2000) NOT NULL DEFAULT '' COMMENT 'calendar数据',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_CAL` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='以 Blob 类型存储 Quartz 的 Calendar 信息 ';

-- ----------------------------
-- Table structure for marble_qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_cron_triggers`;
CREATE TABLE `marble_qrtz_cron_triggers` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `TRIGGER_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger名称',
  `TRIGGER_GROUP` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger 组',
  `CRON_EXPRESSION` varchar(120) NOT NULL DEFAULT '' COMMENT 'cron表达式',
  `TIME_ZONE_ID` varchar(80) DEFAULT '' COMMENT '时区ID',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='存储 Cron Trigger，包括 Cron 表达式和时区信息';

-- ----------------------------
-- Table structure for marble_qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_fired_triggers`;
CREATE TABLE `marble_qrtz_fired_triggers` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `ENTRY_ID` varchar(95) NOT NULL DEFAULT '' COMMENT 'entry ID',
  `TRIGGER_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger名称',
  `TRIGGER_GROUP` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger 组',
  `INSTANCE_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT '实例名',
  `FIRED_TIME` bigint(13) NOT NULL DEFAULT '0' COMMENT '触发时间',
  `SCHED_TIME` bigint(13) NOT NULL DEFAULT '0' COMMENT '计划任务时间',
  `PRIORITY` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `STATE` varchar(16) NOT NULL DEFAULT '' COMMENT '状态',
  `JOB_NAME` varchar(200) DEFAULT '' COMMENT 'job名称',
  `JOB_GROUP` varchar(200) DEFAULT '' COMMENT 'job组',
  `IS_NONCONCURRENT` varchar(1) DEFAULT '' COMMENT '是否非并发',
  `REQUESTS_RECOVERY` varchar(1) DEFAULT '' COMMENT '是否故障转移支持',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_S_ENTRY` (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_MARBLE_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_MARBLE_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_MARBLE_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_MARBLE_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_MARBLE_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_MARBLE_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COMMENT='存储与已触发的 Trigger 相关的状态信息，以及相联 Job 的执行信息';

-- ----------------------------
-- Table structure for marble_qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_job_details`;
CREATE TABLE `marble_qrtz_job_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `JOB_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT 'job名称',
  `JOB_GROUP` varchar(200) NOT NULL DEFAULT '' COMMENT 'job组',
  `DESCRIPTION` varchar(250) DEFAULT '' COMMENT '描述信息',
  `JOB_CLASS_NAME` varchar(250) NOT NULL DEFAULT '' COMMENT '执行job的class name',
  `IS_DURABLE` varchar(1) NOT NULL DEFAULT '' COMMENT '是否持久化',
  `IS_NONCONCURRENT` varchar(1) NOT NULL DEFAULT '' COMMENT '是否非并发',
  `IS_UPDATE_DATA` varchar(1) NOT NULL DEFAULT '' COMMENT '是否更新数据',
  `REQUESTS_RECOVERY` varchar(1) NOT NULL DEFAULT '' COMMENT '是否支持故障转移',
  `JOB_DATA` varchar(2000) DEFAULT NULL COMMENT '存储job数据',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_SCHED_JOB` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  UNIQUE KEY `IDX_MARBLE_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`,`JOB_NAME`) USING BTREE,
  KEY `IDX_MARBLE_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='存储每一个已配置的 Job 的详细信息';

-- ----------------------------
-- Table structure for marble_qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_locks`;
CREATE TABLE `marble_qrtz_locks` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `LOCK_NAME` varchar(40) NOT NULL DEFAULT '' COMMENT '锁名称',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_LOCK` (`SCHED_NAME`,`LOCK_NAME`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='存储程序的悲观锁的信息';

-- ----------------------------
-- Table structure for marble_qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_paused_trigger_grps`;
CREATE TABLE `marble_qrtz_paused_trigger_grps` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `TRIGGER_GROUP` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger组',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_P_TRIG` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='存储已暂停的 Trigger 组的信息';

-- ----------------------------
-- Table structure for marble_qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_scheduler_state`;
CREATE TABLE `marble_qrtz_scheduler_state` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `INSTANCE_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT '实例名称',
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  `CHECKIN_INTERVAL` bigint(13) NOT NULL DEFAULT '0' COMMENT '更新间隔',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_SCHED_S` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT=' 存储少量的有关 Scheduler 的状态信息，和别的 Scheduler 实例(假如是用于一个集群中) ';

-- ----------------------------
-- Table structure for marble_qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_simple_triggers`;
CREATE TABLE `marble_qrtz_simple_triggers` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `TRIGGER_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger名称',
  `TRIGGER_GROUP` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger组',
  `REPEAT_COUNT` bigint(7) NOT NULL DEFAULT '3' COMMENT '重试次数',
  `REPEAT_INTERVAL` bigint(12) NOT NULL DEFAULT '1' COMMENT '重试间隔',
  `TIMES_TRIGGERED` bigint(10) NOT NULL DEFAULT '0' COMMENT '触发次数',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_TRIGGER` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='存储简单的 Trigger，包括重复次数，间隔，以及已触的次数';

-- ----------------------------
-- Table structure for marble_qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_simprop_triggers`;
CREATE TABLE `marble_qrtz_simprop_triggers` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `TRIGGER_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger名称',
  `TRIGGER_GROUP` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger组',
  `STR_PROP_1` varchar(512) DEFAULT '' COMMENT '属性1',
  `STR_PROP_2` varchar(512) DEFAULT '' COMMENT '属性2',
  `STR_PROP_3` varchar(512) DEFAULT '' COMMENT '属性3',
  `INT_PROP_1` int(11) DEFAULT NULL COMMENT 'int属性1',
  `INT_PROP_2` int(11) DEFAULT NULL COMMENT 'int属性2',
  `LONG_PROP_1` bigint(20) DEFAULT NULL COMMENT 'long属性1',
  `LONG_PROP_2` bigint(20) DEFAULT NULL COMMENT 'long属性2',
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL COMMENT 'dec属性1',
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL COMMENT 'dec属性2',
  `BOOL_PROP_1` varchar(1) DEFAULT NULL COMMENT 'bool属性1',
  `BOOL_PROP_2` varchar(1) DEFAULT NULL COMMENT 'bool属性2',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_TRIGGER` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='简单的属性触发器';

-- ----------------------------
-- Table structure for marble_qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `marble_qrtz_triggers`;
CREATE TABLE `marble_qrtz_triggers` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SCHED_NAME` varchar(120) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `TRIGGER_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger名称',
  `TRIGGER_GROUP` varchar(200) NOT NULL DEFAULT '' COMMENT 'trigger组',
  `JOB_NAME` varchar(200) NOT NULL DEFAULT '' COMMENT 'job名称',
  `JOB_GROUP` varchar(200) NOT NULL DEFAULT '' COMMENT 'job组',
  `DESCRIPTION` varchar(250) DEFAULT '' COMMENT '描述信息',
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL COMMENT '下次出发时间',
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL COMMENT '上次触发事件',
  `PRIORITY` int(11) DEFAULT NULL COMMENT '优先级',
  `TRIGGER_STATE` varchar(16) NOT NULL DEFAULT '' COMMENT '触发器状态',
  `TRIGGER_TYPE` varchar(8) NOT NULL DEFAULT '' COMMENT '触发器类型',
  `START_TIME` bigint(13) NOT NULL DEFAULT '0' COMMENT '开始时间',
  `END_TIME` bigint(13) DEFAULT NULL COMMENT '结束时间',
  `CALENDAR_NAME` varchar(200) DEFAULT '' COMMENT 'calendar名称',
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL COMMENT 'misfire策略',
  `JOB_DATA` varchar(2000) DEFAULT NULL COMMENT 'job数据',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_U_TRIGGER` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_MARBLE_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_MARBLE_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_MARBLE_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_MARBLE_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_MARBLE_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_MARBLE_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_MARBLE_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_MARBLE_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_MARBLE_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_MARBLE_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_MARBLE_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_MARBLE_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='存储已配置的Trigger 的信息 ';

-- ----------------------------
-- Table structure for marble_sched_job
-- ----------------------------
DROP TABLE IF EXISTS `marble_sched_job`;
CREATE TABLE `marble_sched_job` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '自增长主键',
  `app_code` varchar(20) NOT NULL DEFAULT '' COMMENT '应用code。APPID',
  `sched_name` varchar(50) NOT NULL DEFAULT '' COMMENT '计划任务scheduler名称',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT 'job名称',
  `description` varchar(100) DEFAULT '' COMMENT '触发器描述',
  `status` smallint(6) NOT NULL DEFAULT '0' COMMENT '触发器状态',
  `cron_express` varchar(100) NOT NULL DEFAULT '' COMMENT 'cron表达式',
  `param` varchar(500) DEFAULT '' COMMENT 'job执行的参数',
  `next_fire_time` datetime DEFAULT NULL COMMENT '下次触发时间',
  `prev_fire_time` datetime DEFAULT NULL COMMENT '上次触发时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `misfire_strategy` int(6) DEFAULT NULL COMMENT '执行错过策略',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='计划任务-触发器表';

-- ----------------------------
-- Table structure for marble_server_sched
-- ----------------------------
DROP TABLE IF EXISTS `marble_server_sched`;
CREATE TABLE `marble_server_sched` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '自增长主键',
  `app_code` varchar(20) NOT NULL DEFAULT '' COMMENT '应用code。APPID',
  `server_ip` varchar(20) NOT NULL DEFAULT '' COMMENT '服务器IP',
  `server_port` int(10) NOT NULL DEFAULT '9091' COMMENT '服务器打开的socket端口号',
  `sched_name` varchar(50) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dataChange_lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_server_sched` (`app_code`,`server_ip`,`server_port`,`sched_name`),
  KEY `IDX_lastTime` (`dataChange_lastTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='服务器与计划任务关系表';
