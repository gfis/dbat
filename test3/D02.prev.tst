<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml" />
<meta name="robots" content="noindex, nofollow" />
<title>dbat</title>
<link rel="stylesheet" type="text/css" href="stylesheet.css" />
</head><body>

<table><tr><td><pre>-- MySQL 5.1.62-0ubuntu0.11.10.1 with MySQL-AB JDBC Driver mysql-connector-java-5.1.11 ( Revision: ${svn.Revision} )
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
</pre></td></tr></table>
</body></html>
