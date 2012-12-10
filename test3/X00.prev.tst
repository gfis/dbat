SQL:
DROP   PROCEDURE if exists x00;
:SQL
SQL:
CREATE PROCEDURE x00 (IN part varchar(20), OUT num int, OUT last varchar(20)) BEGIN SELECT count(*), max(name) INTO num, last FROM c01 WHERE name LIKE concat('%', concat(part, '%')); END;
:SQL
 executed 2 SQL statements affecting 0 rows in ... ms
