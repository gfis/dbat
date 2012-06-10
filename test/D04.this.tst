java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m sql -d c01
-- SQL generated by Dbat at 2012-06-01T22:56:19
-- MySQL 5.1.62-0ubuntu0.11.10.1 with MySQL-AB JDBC Driver mysql-connector-java-5.1.11 ( Revision: ${svn.Revision} )
DROP   TABLE c01;
CREATE TABLE c01
	( name	VARCHAR(16) NOT NULL -- Name of the Relative
	, univ	VARCHAR(16) -- Town
	, year	INT -- Decease Year
	, gender	CHAR(1) -- some escaped character
	, birth	DATE
	, CONSTRAINT PK29 PRIMARY KEY (name)
	);
COMMIT;
-- finished at 2012-06-01T22:56:19
