java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -d b04
-- SQL generated by Dbat at 2011-11-16T22:30:25
-- MySQL 5.1.54-1ubuntu4 with MySQL-AB JDBC Driver mysql-connector-java-5.1.11 ( Revision: ${svn.Revision} )
DROP   TABLE b04;
CREATE TABLE b04
	( name	VARCHAR(16) NOT NULL -- key for the LOB
	, len	INT -- size of the LOB content
	, content	TEXT -- character large object (CLOB)
	);
COMMIT;
-- finished at 2011-11-16T22:30:25
