
-- 创建DBA用户
create user root identified by root;
grant dba to root;

CREATE DATABASE test USER root IDENTIFIED BY root;

create table T_USER
(
  ID     NUMBER not null,
  NAME   VARCHAR2(50),
  SEX    VARCHAR2(10),
  AGE    NUMBER,
  CAREER VARCHAR2(100)
);
alter table T_USER add constraint PK_ID primary key (ID);


-- 定义两个包（package）
CREATE OR REPLACE PACKAGE package_one AS
TYPE cursor_one IS REF CURSOR;
end package_one;

CREATE OR REPLACE PACKAGE package_two AS
TYPE cursor_two IS REF CURSOR;
end package_two;

-- 定义存储过程
CREATE OR REPLACE PROCEDURE pro_query_users
(
--参数IN表示输入参数，OUT表示输入参数，类型可以使用任意Oracle中的合法类型。
 in_id IN NUMBER,
 out_cursor_one OUT package_one.cursor_one,
 out_cursor_two OUT package_two.cursor_two     
)
AS
--定义变量
 vs_id_value   NUMBER;  --变量
  
BEGIN
 --用输入参数给变量赋初值。
 vs_id_value:= in_id;

 OPEN out_cursor_one FOR SELECT * FROM t_user WHERE id > vs_id_value;
  
 OPEN out_cursor_two FOR SELECT * FROM t_user WHERE name LIKE '%a%';
 
 --错误处理部分。OTHERS表示除了声明外的任意错误。SQLERRM是系统内置变量保存了当前错误的详细信息。
Exception
   WHEN OTHERS Then
   ROLLBACK;
   Return;
End pro_query_users;