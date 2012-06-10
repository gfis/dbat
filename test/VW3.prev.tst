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
<link rel="stylesheet" type="text/css" href="stylesheet.css" />
</head><body>

    
	<!--Gibt die DDL-Beschreibung einer Tabelle oder View aus-->
	<!--Shows the DDL Description of a Table or View-->

    <h2>DDL of Table/View(s) <em>vw1%</em></h2>
	<form method="get" action="servlet?spec=describe"><input name="spec" type="hidden" value="describe" />

		Name: <input name="table" maxsize="64" size="32" init="spec_ind" value="vw1"></input>%    
        <input type="submit" value="Show DDL"></input> 
	</form>
	
<br />Output on 2012-01-11T07:41:07.437 by <a href="index.jsp">Dbat</a> script <a href="/dbat/spec/describe.xml" type="text/plain">describe</a>
<a href="servlet?&amp;mode=xls&amp;spec=describe&amp;table=vw1">Excel</a>,
<a href="more.jsp?&amp;lang=en&amp;mode=html&amp;spec=describe&amp;table=vw1">more</a>

</body></html>
