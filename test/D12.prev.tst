<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>DDL of a Table/View</title>
<link rel="stylesheet" type="text/css" href="spec/stylesheet.css" />
</head><body>

    
    <!--Gibt die DDL-Beschreibung einer Tabelle oder View aus-->
    <!--Shows the DDL Description of a Table or View-->

    <h3><a href="index.html">Dbat</a> - DDL of Table/View(s) <em>%</em></h3>
    <p></p>
    
    <form method="get" action="servlet?spec=describe"><input name="spec" type="hidden" value="describe" />

        Name: <input name="table" maxsize="64" size="32" init="spec_ind" value="spec_ind"></input>% <br />
        <input type="submit" value="Show DDL"></input> 
          -> <a href="servlet?spec=describe&amp;mode=spec&amp;table=spec_ind&amp;dummy=Dbat+Spec.">Dbat Spec.</a>
          -> <a href="servlet?spec=describe&amp;mode=dbiv&amp;table=spec_ind&amp;dummy=Dbiv+Spec.">Dbiv Spec.</a>
    </form>
    
    
<table><tr><td><pre>-- MySQL with MySQL-AB JDBC Driver
DROP   TABLE spec_index;
CREATE TABLE spec_index
	( subdir	VARCHAR(16) NOT NULL
	, name	VARCHAR(32) NOT NULL
	, lang	VARCHAR(8) NOT NULL
	, title	VARCHAR(64)
	, comment	VARCHAR(512)
	, params	VARCHAR(1024)
	, CONSTRAINT PK29 PRIMARY KEY (subdir, lang, name)
	);
COMMIT;
</pre></td></tr></table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/describe.xml" type="text/plain">describe</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=describe&amp;lang=en&amp;table=spec_ind">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=describe&amp;lang=en&amp;table=spec_ind">more</a>

</body></html>
