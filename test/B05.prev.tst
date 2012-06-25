java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -d b04
-- SQL generated by Dbat at 2012-06-25 21:32:12
-- MySQL 5.1.62-0ubuntu0.11.10.1 with MySQL-AB JDBC Driver mysql-connector-java-5.1.11 ( Revision: ${svn.Revision} )
DROP   TABLE b04;
CREATE TABLE b04
	( name	VARCHAR(16) NOT NULL -- key for the LOB
	, len	INT -- size of the LOB content
	, content	TEXT -- character large object (CLOB)
	);
COMMIT;
