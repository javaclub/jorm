/*
SQLyog Enterprise - MySQL GUI
MySQL - 5.1.45-community 
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

create table `seqence` (
	`seq_name` varchar (150),
	`cur_value` double ,
	`gmt_modified` datetime 
); 
insert into `seqence` (`seq_name`, `cur_value`, `gmt_modified`) values('mysql_incr_seq','31101','0000-00-00 00:00:00');
insert into `seqence` (`seq_name`, `cur_value`, `gmt_modified`) values('user_sequence','1158001','2017-03-17 00:44:53');
