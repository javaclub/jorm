/*
SQLyog Enterprise - MySQL GUI
MySQL - 5.1.45-community 
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

CREATE TABLE `seqence` (                 
	   `seq_name` varchar(50) NOT NULL,       
	   `cur_value` bigint(20) DEFAULT NULL,   
	   `gmt_modified` datetime DEFAULT NULL,  
	   PRIMARY KEY (`seq_name`)               
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 
insert into `seqence` (`seq_name`, `cur_value`, `gmt_modified`) values('mysql_incr_seq','31101','0000-00-00 00:00:00');
insert into `seqence` (`seq_name`, `cur_value`, `gmt_modified`) values('user_sequence','1158001','2017-03-17 00:44:53');
