java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m probe -f ../web/spec/test/with_cte.xml
2011-12-05 07:54:36,857 0    [main] ERROR org.teherba.dbat.SQLAction  - You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'WITH cte as
		( select entry, enrel 
		  from words
		  where entry like 'backe%' at line 1
 