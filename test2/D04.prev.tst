-- SQL generated by Dbat on yyyy-mm-dd hh:mm:ss
-- MySQL 5.1.66-0ubuntu0.11.10.3 with MySQL-AB JDBC Driver mysql-connector-java-5.1.11 ( Revision: ${svn.Revision} )
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