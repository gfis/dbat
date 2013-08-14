<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>order02</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

	<!--Select from test table with ORDER-->
	<!--Abfrage der Test-Tabelle mit ORDER-->
	
    <h3>Select from test table c01 with ORDER</h3>
    <form method="get" action="servlet?spec=test/order02"><input name="spec" type="hidden" value="test/order02" />

        Name: <input name="name" maxsize="20" size="10" init="%r" value="%r"></input>
        <input type="submit" value="Submit"></input>
    </form>
    <!-- SQL:
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like '%r%' ORDER by birth limit 2;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="name">Name</th><th title="univ">University</th><th title="year">Year</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td>Fritz</td><td>Waldshut</td><td align="right">1995</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td>Martha</td><td>Freiburg</td><td align="right">1999</td><td>&amp;</td><td>1909-11-17</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/order02.xml" type="text/plain">test/order02</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Forder02&amp;lang=en&amp;name=%25r">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Forder02&amp;lang=en&amp;name=%25r">more</a>

</body></html>
