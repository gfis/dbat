SQL:
DROP   PROCEDURE if exists x01;
:SQL
SQL:
CREATE PROCEDURE x01 (IN x CHAR(1)) BEGIN SELECT * FROM c01 WHERE name like '%r%' ; SELECT name, birth FROM c01 ; END;
:SQL
 executed 2 SQL statements affecting 0 rows in ... ms
