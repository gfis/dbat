java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m html -f ../web/spec/test/selnull.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>selnull</title>
<link rel="stylesheet" type="text/css" href="./../web/spec/test/stylesheet.css" />
</head><body>

	<!--Select with NULL values-->
    <h3>Select with NULL values</h3>
    <!-- SQL:
SELECT c.univ
, e.display
, e.lang 
FROM c01 c 
        	left join en1 e on substr(c.name, 1, 1) = e.code;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="c.univ">Town</th><th title="e.display">Gender</th><th title="e.lang">Language</th></tr>
<tr><td>Freiburg</td><td>male</td><td>eng</td></tr>
<tr><td>Schramberg</td><td>null</td><td>null</td></tr>
<tr><td>Groß-Gerau</td><td>null</td><td>null</td></tr>
<tr><td>Waldshut</td><td>female</td><td>eng</td></tr>
<tr><td>Hermsdorf</td><td>male</td><td>eng</td></tr>
<tr><td>Lübars</td><td>null</td><td>null</td></tr>
<tr><td>Lübars</td><td>null</td><td>null</td></tr>
<tr><td>Lübars</td><td>null</td><td>null</td></tr>
</table>


<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="./../web/spec/test/selnull.xml" type="text/plain">../web/spec/test/selnull.xml</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;lang=en">more</a>

</body></html>
