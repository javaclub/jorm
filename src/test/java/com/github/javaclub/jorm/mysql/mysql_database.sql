

DROP TABLE IF EXISTS `t_book_category`;

CREATE TABLE `t_book_category` (
  `id` int(11) NOT NULL,
  `category_name` varchar(50) DEFAULT NULL,
  `r_order` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `t_books`;

CREATE TABLE `t_books` (
  `book_id` char(32) NOT NULL,
  `book_name` varchar(100) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `isbn_no` varchar(50) DEFAULT NULL,
  `pub_time` date DEFAULT NULL,
  `book_author` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `t_incre`;

CREATE TABLE `t_incre` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6177 DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `t_multi_key`;

CREATE TABLE `t_multi_key` (
  `pk_id` bigint(20) NOT NULL,
  `pk_name` varchar(50) NOT NULL,
  `note` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `pk_id` (`pk_id`),
  UNIQUE KEY `uniq_name` (`pk_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `t_multi_key_sub`;

CREATE TABLE `t_multi_key_sub` (
  `id` int(11) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `demo` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `t_user`;

CREATE TABLE `t_user` (
  `id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `sex` char(4) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `career` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `t_user2`;

CREATE TABLE `t_user2` (
  `id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `t_user3`;

CREATE TABLE `t_user3` (
  `id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `t_user_bean`;

CREATE TABLE `t_user_bean` (
  `user_id` char(32) NOT NULL,
  `user_name` varchar(50) NOT NULL,
  `sex` char(4) DEFAULT NULL,
  `u_age` int(11) DEFAULT NULL,
  `u_zhiye` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `t_user_entity`;

CREATE TABLE `t_user_entity` (
  `id` int(11) NOT NULL,
  `user_name` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `isAdult` tinyint(4) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `createUser` varchar(50) DEFAULT NULL,
  `updateUser` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `t_m_computer`;

CREATE TABLE `t_m_computer` (
  `id` bigint(20) NOT NULL,
  `thread` char(32) DEFAULT NULL,
  `product_no` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `t_m_item`;

CREATE TABLE `t_m_item` (
  `item_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pc_id` bigint(20) DEFAULT NULL,
  `thread` char(32) DEFAULT NULL,
  `pc_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `t_m_threads`;

CREATE TABLE `t_m_threads` (
  `thread_id` char(32) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`thread_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_id_auto` */

DROP TABLE IF EXISTS `t_id_auto`;

CREATE TABLE `t_id_auto` (
  `id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_id_guid` */

DROP TABLE IF EXISTS `t_id_guid`;

CREATE TABLE `t_id_guid` (
  `id` char(36) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_id_increment` */

DROP TABLE IF EXISTS `t_id_increment`;

CREATE TABLE `t_id_increment` (
  `id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_id_increment_mysql_auto` */

DROP TABLE IF EXISTS `t_id_increment_mysql_auto`;

CREATE TABLE `t_id_increment_mysql_auto` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12310 DEFAULT CHARSET=utf8;

/*Table structure for table `t_id_uuid` */

DROP TABLE IF EXISTS `t_id_uuid`;

CREATE TABLE `t_id_uuid` (
  `id` char(32) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_one_to_one_card`;

CREATE TABLE `t_one_to_one_card` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `identity_number` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into `t_one_to_one_card` (`id`, `identity_number`) values('1','421121198508312016');
insert into `t_one_to_one_card` (`id`, `identity_number`) values('2','421121198508312017');
insert into `t_one_to_one_card` (`id`, `identity_number`) values('3','421121198603252016');
insert into `t_one_to_one_card` (`id`, `identity_number`) values('5','321121198605282016');
insert into `t_one_to_one_card` (`id`, `identity_number`) values('6','621121198605282018');
insert into `t_one_to_one_card` (`id`, `identity_number`) values('8','121121198605282016');

/*Table structure for table `t_one_to_one_b` */

DROP TABLE IF EXISTS `t_one_to_one_person`;

CREATE TABLE `t_one_to_one_person` (
  `identity_number` varchar(32) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `job` varchar(50) DEFAULT NULL,
  `sex` tinyint(4) DEFAULT NULL,
  `telephone` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`identity_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into `t_one_to_one_person` (`identity_number`, `name`, `job`, `sex`, `telephone`) values('321121198605282016','KKK','工程师','1','13545241652');
insert into `t_one_to_one_person` (`identity_number`, `name`, `job`, `sex`, `telephone`) values('421121198508312016','Kate','teacher','0','13435682512');
insert into `t_one_to_one_person` (`identity_number`, `name`, `job`, `sex`, `telephone`) values('421121198508312017','Greald.Chen','engineer','1','15968870949');

DROP TABLE IF EXISTS `t_onetomany_class`;

CREATE TABLE `t_onetomany_class` (        
   `id` int(11) NOT NULL,                  
   `class_name` varchar(50) DEFAULT NULL,  
   PRIMARY KEY (`id`)                      
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_onetomany_student`;

CREATE TABLE `t_onetomany_student` (                        
   `id` bigint(20) NOT NULL AUTO_INCREMENT,                  
   `name` varchar(50) DEFAULT NULL,                          
   `birthday` date DEFAULT NULL,                             
   `class_id` int(11) NOT NULL DEFAULT '0',                  
   PRIMARY KEY (`id`)                                        
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8; 

DROP TABLE IF EXISTS `t_mtm_course`;
CREATE TABLE `t_mtm_course` (                             
                `id` int(11) NOT NULL AUTO_INCREMENT,                   
                `name` varchar(50) DEFAULT NULL,                        
                PRIMARY KEY (`id`)                                      
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `t_mtm_student`;
CREATE TABLE `t_mtm_student` (        
                 `id` bigint(20) NOT NULL,           
                 `name` varchar(50) DEFAULT NULL,    
                 `age` int(11) DEFAULT NULL,         
                 PRIMARY KEY (`id`)                  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_mtm_book_type`;
CREATE TABLE `t_mtm_book_type` (           
                   `id` bigint(20) NOT NULL,                
                   `type_name` varchar(50) DEFAULT NULL,    
                   `release_version` int(11) DEFAULT NULL,  
                   PRIMARY KEY (`id`)                       
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- PROCEDURE --

DELIMITER $$

DROP PROCEDURE IF EXISTS `p_t_user_query`$$
CREATE PROCEDURE `p_t_user_query`()
begin 
       select * from t_user limit 100;
end$$

DELIMITER ;

-------------------------------------------------------------

DELIMITER $$

DROP PROCEDURE IF EXISTS `p_t_incre_insert`$$

CREATE PROCEDURE `p_t_incre_insert`()
begin 
	insert into t_incre(name) values('aaa');
	insert into t_incre(name) values('bbb');
	insert into t_incre(name) values('ccc');
	insert into t_incre(name) values('ddd');
	insert into t_incre(name) values('eee');
end$$

DELIMITER ;

--------------------------------------------------------------

DELIMITER $$

DROP PROCEDURE IF EXISTS `loop_proc`$$

CREATE PROCEDURE loop_proc(IN in_count INT)
begin 
	DECLARE count INT default 0;
	increment: LOOP
	 SET count = count + 1;
         IF count < 20 THEN ITERATE increment; END IF;
         IF count > in_count THEN LEAVE increment;
         END IF;
         END LOOP increment;
    
         SELECT count;
end$$

DELIMITER ;

---------------------------------------------------------------

DROP FUNCTION IF EXISTS `hello_proc`;

CREATE FUNCTION hello_proc(s CHAR(20)) RETURNS CHAR(50) RETURN CONCAT('Hello, ',s,'!');