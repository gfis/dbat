java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -g sp1 -m html -f ../web/spec/test/visible05.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>visible05</title>
<link rel="stylesheet" type="text/css" href="./../web/spec/test/stylesheet.css" />
</head><body>

	<!--Select from test table - suppress subsequent column values in groups-->
	<!--Abfrage der Test-Tabelle mit Unterdrückung von Werten in einer Gruppe-->

    <h3>Select from test table c01 - test of "visible" feature</h3>
   
    <!-- SQL:
SELECT univ
, gender
, birth 
FROM c01
        	where univ like 'L%';
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="univ">University</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td class="visible">Lübars</td><td>$</td><td>1909-02-09</td></tr>
<tr><td class="invisible">Lübars</td><td>$</td><td>1910-02-07</td></tr>
<tr><td class="invisible">Lübars</td><td>'</td><td>1887-07-09</td></tr>
</table>

<br />Output on 2012-06-15 08:03:30.902 by <a href="index.html">Dbat</a> script <a target="_blank" href="./../web/spec/test/visible05.xml.xml" type="text/plain">../web/spec/test/visible05.xml</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;lang=en">more</a>

</body></html>
