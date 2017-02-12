SQL:
DROP   TABLE IF EXISTS c11;
:SQL
SQL:
CREATE TABLE c11 ( name   VARCHAR(16) NOT NULL , univ   VARCHAR(16) , year   INT , gender CHAR(1) , birth  DATE , PRIMARY KEY (name) ) ENGINE=MyISAM CHARACTER SET utf8 COLLATE utf8_bin;
:SQL
SQL:
SELECT COUNT(*) FROM c11;
:SQL
COUNT(*)
0
 executed 3 SQL statements affecting 0 rows in ... ms
