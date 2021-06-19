<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>visible05</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
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
<div>
<table id="table1">
<tr><th title="univ">University</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td class="visible">Lübars</td><td>$</td><td>1909-02-09</td></tr>
<tr><td class="invisible"></td><td>$</td><td>1910-02-07</td></tr>
<tr><td class="invisible"></td><td>'</td><td>1887-07-09</td></tr>
</table>
</div>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/visible05.xml" type="text/plain">test/visible05</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;spec=test%2Fvisible05&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fvisible05&amp;lang=en">more</a>

</body></html>
