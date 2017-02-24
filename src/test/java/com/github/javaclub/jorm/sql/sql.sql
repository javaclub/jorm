select * from (select * from t where t.id in(1,11,11) order by t.name desc) tb

select * from (select * from t where t.id in(1,11,111,112,113)) tb order by tb.name desc

select * from (select * from t_user where t_user.id in(1,11,12,13) order by t_user.name desc) tb

SELECT * FROM t_user WHERE t_user.id IN (SELECT id FROM t_user WHERE id <= 111 order by name desc) order by id desc; 

SELECT FIRST_NAME, LAST_NAME, COUNT(*)
    FROM AUTHOR
    JOIN BOOK ON AUTHOR.ID = BOOK.AUTHOR_ID
   WHERE LANGUAGE = 'DE'
     AND PUBLISHED > '2008-01-01'
GROUP BY FIRST_NAME, LAST_NAME
  HAVING COUNT(*) > 5
ORDER BY LAST_NAME ASC NULLS FIRST
   LIMIT 2 
  OFFSET 1
     FOR UPDATE
      OF FIRST_NAME, LAST_NAME;
      
      
SELECT FIRST_NAME, LAST_NAME, COUNT(*) FROM AUTHOR JOIN BOOK ON AUTHOR.ID = BOOK.AUTHOR_ID WHERE LANGUAGE = 'DE' AND PUBLISHED > '2008-01-01' GROUP BY FIRST_NAME, LAST_NAME HAVING COUNT(*) > 5 ORDER BY LAST_NAME ASC NULLS FIRST LIMIT 2 OFFSET 1 FOR UPDATE OF FIRST_NAME, LAST_NAME;
  
SELECT * 
  FROM t_author a 
  JOIN t_book b ON a.id = b.author_id 
 WHERE a.year_of_birth > 1920 
   AND a.first_name = 'Paulo'
 ORDER BY b.title
 