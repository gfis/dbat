<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>switchconn</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    <!--Switch to another database connection-->
    
    <h3>Select from connection <em>worddb</em></h3>

    <!-- SQL:
SELECT count(*) 
FROM c01;
:SQL -->
<table id="table1">
<tr><th title="count(*)">Number of Rows</th></tr>
<tr><td align="right">8</td></tr>
</table>


    <h3>Switch to connection <em>mysql</em></h3>
    </connect>
    <!-- SQL:
SELECT count(*) 
FROM c01;
:SQL -->
<table id="table2">
<tr><th title="count(*)">Number of Rows</th></tr>
<tr><td align="right">8</td></tr>
</table>


    <h3>Switch to connection <em>wiki</em></h3>
    <h3 class="error">Specification parsing error: org.apache.tomcat.dbcp.dbcp.SQLNestedException: Cannot create PoolableConnectionFactory (Access denied for user 'wikiuser'@'localhost' (using password: YES))</h3><pre class="error">connect</pre>
