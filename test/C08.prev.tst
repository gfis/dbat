<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>color08</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

	<!--Select from test table - with colors on rows-->
	<!--Abfrage der Test-Tabelle mit farbigen Zeilen-->

    <h3>Select from test table c01 - with colors on rows</h3>
    <form method="get" action="servlet?spec=test/color08"><input name="spec" type="hidden" value="test/color08" />

        Name: <input name="name" maxsize="20" size="10" init="%r" value="%r"></input>%    
        <input type="submit" value="Submit"></input>
    </form>

    <!-- SQL:
SELECT year
, name
, univ
, gender
, birth
, case when year <= 1990 then 'blu' else 'red' end 
FROM c01 
WHERE name like '%r%';
:SQL -->
<div>
<table id="table1">
<tr><th title="year">Year</th><th title="name">Name</th><th title="univ">University</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td align="right">1999</td><td>Martha</td><td>Freiburg</td><td>&amp;</td><td>1909-11-17</td>
<script type="text/javascript">with(document.getElementById("table1").rows[1]){className="red";}</script></tr>
<tr><td align="right">1945</td><td>Eberhard</td><td>Groß-Gerau</td><td>&gt;</td><td>1912-11-17</td>
<script type="text/javascript">with(document.getElementById("table1").rows[2]){className="blu";}</script></tr>
<tr><td align="right">1995</td><td>Fritz</td><td>Waldshut</td><td>&lt;</td><td>1907-08-08</td>
<script type="text/javascript">with(document.getElementById("table1").rows[3]){className="red";}</script></tr>
<tr><td align="right">1999</td><td>Maria</td><td>Hermsdorf</td><td>#</td><td>1914-09-17</td>
<script type="text/javascript">with(document.getElementById("table1").rows[4]){className="red";}</script></tr>
<tr><td align="right">1985</td><td>Dorothea</td><td>Lübars</td><td>$</td><td>1910-02-07</td>
<script type="text/javascript">with(document.getElementById("table1").rows[5]){className="blu";}</script></tr>
<tr><td class="counter" colspan="6">5 Persons</td></tr>
</table>
</div>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/color08.xml" type="text/plain">test/color08</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;spec=test%2Fcolor08&amp;lang=en&amp;name=%25r">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fcolor08&amp;lang=en&amp;name=%25r">more</a>

</body></html>
