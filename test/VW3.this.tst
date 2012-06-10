wget -q -O - "http://localhost:8080/dbat/servlet?spec=describe&table=vw1"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>DDL einer Tabelle</title>
<link rel="stylesheet" type="text/css" href="spec/stylesheet.css" />
</head><body>

    
	<!--Gibt die DDL-Beschreibung einer Tabelle oder View aus-->
	<!--Shows the DDL Description of a Table or View-->

    <h2>DDL of Table/View(s) <em>vw1%</em></h2>
	<form method="get" action="servlet?spec=describe"><input name="spec" type="hidden" value="describe" />

		Name: <input name="table" maxsize="64" size="32" init="spec_ind" value="vw1"></input>%    
        <input type="submit" value="Show DDL"></input> 
	</form>
	
<table><tr><td><pre>-- MySQL 5.1.58-1ubuntu1 with MySQL-AB JDBC Driver mysql-connector-java-5.1.11 ( Revision: ${svn.Revision} )
DROP   VIEW vw1;
CREATE VIEW vw1
	( name	VARCHAR(16) NOT NULL -- Name of the Relative
	, birth	DATE
	);
COMMIT;
</pre></td></tr></table>

<br />Output on 2012-02-16T09:26:02.553 by <a href="index.html">Dbat</a> script <a href="spec/describe.xml" type="text/plain">describe</a>,
<a href="servlet?&amp;mode=xls&amp;spec=describe&amp;table=vw1&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=describe&amp;table=vw1&amp;lang=en">more</a>

</body></html>
