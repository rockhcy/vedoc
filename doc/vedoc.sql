/*
SQLyog Ultimate v11.22 (64 bit)
MySQL - 5.7.17-log : Database - vedoc
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`vedoc` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `vedoc`;

/*Table structure for table `doc_lock` */

DROP TABLE IF EXISTS `doc_lock`;

CREATE TABLE `doc_lock` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文件锁表',
  `type` tinyint(4) DEFAULT NULL COMMENT '1-文件锁，2-目录锁',
  `path` varchar(600) DEFAULT NULL COMMENT '文件或者目录的物理路径',
  `state` tinyint(4) DEFAULT NULL COMMENT '锁状态，0-无锁，1-锁文件，2-锁目录',
  `locker` varchar(100) DEFAULT NULL COMMENT '加锁人',
  `lock_time` bigint(20) DEFAULT NULL COMMENT '锁定时间，当操过该时间后自动解锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

/*Data for the table `doc_lock` */

/*Table structure for table `doc_share` */

DROP TABLE IF EXISTS `doc_share`;

CREATE TABLE `doc_share` (
  `share_id` bigint(20) NOT NULL COMMENT '雪花主键',
  `share_name` varchar(60) DEFAULT NULL,
  `repo_id` int(11) DEFAULT NULL,
  `relative_path` varchar(500) DEFAULT NULL COMMENT '基于仓库的相对路径',
  `share_auth` varchar(200) DEFAULT NULL COMMENT '分享权限',
  `share_pwd` varchar(60) DEFAULT NULL COMMENT '分享密码',
  `user_id` int(11) DEFAULT NULL COMMENT '分享用户id',
  `expire_time` datetime DEFAULT NULL COMMENT '分享有效时间',
  PRIMARY KEY (`share_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `doc_share` */

/*Table structure for table `file_fingerprint` */

DROP TABLE IF EXISTS `file_fingerprint`;

CREATE TABLE `file_fingerprint` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '只记录10M以上的大文件',
  `file_path` varchar(600) DEFAULT NULL COMMENT '文件路径',
  `hash` varchar(100) DEFAULT NULL COMMENT '文件hash',
  `is_exist` tinyint(1) DEFAULT NULL COMMENT '文件是否还存在',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `file_fingerprint` */

/*Table structure for table `mapping_group_user` */

DROP TABLE IF EXISTS `mapping_group_user`;

CREATE TABLE `mapping_group_user` (
  `group_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `level` tinyint(4) DEFAULT NULL COMMENT '1-用户级别，2-管理员级别'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `mapping_group_user` */

/*Table structure for table `mapping_repo_user` */

DROP TABLE IF EXISTS `mapping_repo_user`;

CREATE TABLE `mapping_repo_user` (
  `repo_id` int(11) DEFAULT NULL COMMENT '仓库id',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `access` tinyint(1) DEFAULT NULL COMMENT '权限，是否允许访问文件',
  `add` tinyint(1) DEFAULT NULL COMMENT '权限，是否允许增加文件',
  `del` tinyint(1) DEFAULT NULL COMMENT '权限，是否允许删除文件',
  `mod` tinyint(1) DEFAULT NULL COMMENT '权限，是否允许修改文件',
  `mapping_name` varchar(60) DEFAULT NULL COMMENT '映射名称，允许用户自己定义映射名称，当仓库被删除时提示主仓库已经被删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `mapping_repo_user` */

insert  into `mapping_repo_user`(`repo_id`,`user_id`,`access`,`add`,`del`,`mod`,`mapping_name`) values (7,1,1,1,1,1,'1');

/*Table structure for table `mapping_role_api` */

DROP TABLE IF EXISTS `mapping_role_api`;

CREATE TABLE `mapping_role_api` (
  `role_id` int(11) DEFAULT NULL,
  `api_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `mapping_role_api` */

/*Table structure for table `mapping_user_api` */

DROP TABLE IF EXISTS `mapping_user_api`;

CREATE TABLE `mapping_user_api` (
  `user_id` int(11) DEFAULT NULL,
  `api_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `mapping_user_api` */

/*Table structure for table `repos` */

DROP TABLE IF EXISTS `repos`;

CREATE TABLE `repos` (
  `repo_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '仓库表',
  `repo_name` varchar(60) NOT NULL,
  `repo_type` tinyint(4) NOT NULL COMMENT '1-普通仓库，2-版本仓库，3-协作仓库',
  `repo_path` varchar(500) DEFAULT NULL COMMENT '仓库在服务器上的物理路径',
  `repo_des` varchar(500) DEFAULT NULL COMMENT '仓库简介',
  `repo_pwd` varchar(500) DEFAULT NULL COMMENT '仓库访问密码',
  `creater_id` int(11) DEFAULT NULL COMMENT '创建人id',
  `creater` varchar(60) DEFAULT NULL COMMENT '仓库创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remote_addr` varchar(500) DEFAULT NULL COMMENT '远程仓库的地址，仅type=3时有效',
  `remote_uname` varchar(500) DEFAULT NULL COMMENT '远程仓库的用户名，仅type=3时有效',
  `remote_pwd` varchar(500) DEFAULT NULL COMMENT '远程仓库的密码，仅type=3时有效',
  PRIMARY KEY (`repo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

/*Data for the table `repos` */

insert  into `repos`(`repo_id`,`repo_name`,`repo_type`,`repo_path`,`repo_des`,`repo_pwd`,`creater_id`,`creater`,`create_time`,`remote_addr`,`remote_uname`,`remote_pwd`) values (1,'r1',2,'D:/versionTest/Repository/1/','测试仓库1','123',1,'root','2020-09-01 14:43:45',NULL,NULL,NULL),(7,'r1',2,'D:/versionTest/Repository/7/','测试仓库1','123',2,'root','2020-09-01 19:49:01',NULL,NULL,NULL),(8,'r1',2,'D:/versionTest/Repository/8/','测试仓库1','123',1,'root','2020-09-01 19:56:49',NULL,NULL,NULL),(9,'r1',1,'D:/versionTest/Repository/9/','测试普通仓库1','',1,'root','2020-09-03 20:18:26',NULL,NULL,NULL),(10,'协作仓库',3,'D:/versionTest/Repository/10/','测试普通仓库1','',1,'root','2020-09-03 21:29:31',NULL,NULL,NULL),(11,'测试仓库1',3,'D:/versionTest/Repository/11/','测试创建仓库','123',1,'root','2020-09-04 10:25:11',NULL,NULL,NULL),(12,'仓库2',3,'D:/versionTest/Repository/12/','按城','',1,'root','2020-09-04 10:36:38',NULL,NULL,NULL);

/*Table structure for table `repos_share` */

DROP TABLE IF EXISTS `repos_share`;

CREATE TABLE `repos_share` (
  `share_id` bigint(20) NOT NULL COMMENT '仓库分享表',
  `repo_id` int(11) DEFAULT NULL COMMENT '仓库id',
  `repo_pwd` varchar(60) DEFAULT NULL COMMENT '仓库密码',
  PRIMARY KEY (`share_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `repos_share` */

/*Table structure for table `sys_api` */

DROP TABLE IF EXISTS `sys_api`;

CREATE TABLE `sys_api` (
  `api_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'api表',
  `url` varchar(200) NOT NULL COMMENT '请求地址',
  `method` varchar(10) NOT NULL COMMENT '请求类型',
  `des` varchar(200) NOT NULL COMMENT '描述',
  PRIMARY KEY (`api_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_api` */

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色表',
  `role` varchar(20) NOT NULL COMMENT '角色名称',
  `role_des` varchar(200) NOT NULL COMMENT '角色描述',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_role` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户表',
  `username` varchar(60) NOT NULL COMMENT '用户名',
  `password` varchar(60) NOT NULL COMMENT '密码',
  `alias` varchar(60) NOT NULL COMMENT '昵称',
  `tel` varchar(60) NOT NULL COMMENT '电话',
  `email` varchar(60) NOT NULL COMMENT '邮箱',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `last_login_ip` varchar(60) DEFAULT NULL COMMENT '最后登陆ip',
  `creater` varchar(60) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `role_id` int(11) DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`user_id`,`username`,`password`,`alias`,`tel`,`email`,`last_login_time`,`last_login_ip`,`creater`,`create_time`,`role_id`) values (1,'root','3.14','root','','','2020-08-24 11:54:40','192.168.10.143','hcy','2020-08-24 11:54:51',1),(2,'hcy','3.14','何重洋','10086','3@q.com',NULL,NULL,'root','2020-09-01 14:17:41',1);

/*Table structure for table `user_group` */

DROP TABLE IF EXISTS `user_group`;

CREATE TABLE `user_group` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户组表',
  `group_name` varchar(60) DEFAULT NULL COMMENT '组名',
  `group_des` varchar(200) DEFAULT NULL COMMENT '组描述',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user_group` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
