SQL:
DROP   PROCEDURE if exists x07;
:SQL
SQL:
CREATE PROCEDURE x07 ( IN  iname0 VARCHAR(16) , OUT odec1  DECIMAL(18) , OUT odec2  DECIMAL(18,3) , OUT odate3 DATE , OUT otime4 TIME , OUT ots5   TIMESTAMP ) BEGIN SELECT dec1, dec2, date3, time4, ts5 INTO   odec1, odec2, odate3, otime4, ots5 FROM de1 WHERE name0 = iname0; END;
:SQL
 executed 2 SQL statements affecting 0 rows in ... ms
