<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>order01</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

	<!--Select from test table with ORDER-->
	<!--Suche in der Test-Tabelle mit ORDER-->
	
    <h3>Select <em>Mar%</em> from test table c01 with ORDER</h3>
    <form method="get" action="servlet?spec=test/order01"><input name="spec" type="hidden" value="test/order01" />

        Name: <input name="name" maxsize="20" size="10" init="%r" value="Mar"></input>%     
        <input type="submit" value="Submit"></input>
    </form>
    
    <!-- SQL:
SELECT concat(concat(concat(          name, '  '), '='), name)
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like 'Mar%' 
ORDER BY 1;
:SQL -->
<table id="table1">
<tr><th title="concat(concat(concat(          name, '  '), '='), name)">Name</th><th title="univ">University</th><th title="year">Year</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td><a href="servlet?spec=test.order02&amp;name=Maria&amp;name=Maria">Maria</a></td><td>Hermsdorf</td><td align="right">1999</td><td>#</td><td>1914-09-17</td></tr>
<tr><td><a href="servlet?spec=test.order02&amp;name=Martha&amp;name=Martha">Martha</a></td><td>Freiburg</td><td align="right">1999</td><td>&amp;</td><td>1909-11-17</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/order01.xml" type="text/plain">test/order01</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Forder01&amp;lang=en&amp;name=Mar">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Forder01&amp;lang=en&amp;name=Mar">more</a>

</body></html>
