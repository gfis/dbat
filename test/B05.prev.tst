-- SQL generated by Dbat on yyyy-mm-dd hh:mm:ss
-- MySQL 5.1.66-0ubuntu0.11.10.3 with MySQL-AB JDBC Driver mysql-connector-java-5.1.11 ( Revision: ${svn.Revision} )
DROP   TABLE b04;
CREATE TABLE b04
	( name	VARCHAR(16) NOT NULL -- key for the LOB
	, len	INT -- size of the LOB content
	, content	TEXT -- character large object (CLOB)
	);
COMMIT;
