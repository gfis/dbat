<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>color09</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    <!--Colors on columns and rows-->
    <!--Farben auf Spalten und Zeilen-->

    <h3>Select with styles/classes on columns and rows for Johannes</h3>

	<a name="block0"></a>
    <h4>Class on column</h4>
    <!-- SQL:
SELECT year
, case when year <= 1990 then 'blu' else 'red' end
, name
, univ
, gender
, birth 
FROM c01 
WHERE name like 'Johannes%';
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="year">Year</th><th title="name">Name</th><th title="univ">University</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td align="right">1992</td><td class="red">Johannes</td><td>Schramberg</td><td>&lt;</td><td>1911-06-03</td></tr>
</table>


	<a name="block1"></a>
    <h4>Class on rows (with Javascript)</h4>
    <!-- SQL:
SELECT year
, name
, univ
, gender
, birth
, case when year <= 1990 then 'blu' else 'red' end 
FROM c01 
WHERE name like 'Johannes%';
:SQL -->
<table id="tab2"><!-- table_not_specified -->
<tr><th title="year">Year</th><th title="name">Name</th><th title="univ">University</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td align="right">1992</td><td>Johannes</td><td>Schramberg</td><td>&lt;</td><td>1911-06-03</td>
<script type="text/javascript">with(document.getElementById("tab2").rows[1]){className="red";}</script></tr>
</table>


	<a name="block2"></a>
    <h4>Styles on column</h4>
    <!-- SQL:
SELECT year
, case when year <= 1990 then 'color:black;background-color:lightblue' else 'color:white;background-color:red' end
, name
, univ
, gender
, birth 
FROM c01 
WHERE name like 'Johannes%';
:SQL -->
<table id="tab3"><!-- table_not_specified -->
<tr><th title="year">Year</th><th title="name">Name</th><th title="univ">University</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td align="right">1992</td><td style="color:white;background-color:red">Johannes</td><td>Schramberg</td><td>&lt;</td><td>1911-06-03</td></tr>
</table>


	<a name="block3"></a>
    <h4>Styles on rows (with Javascript)</h4>
    <!-- SQL:
SELECT year
, name
, univ
, gender
, birth
, case when year <= 1990 then 'color:black;background-color:lightblue' else 'color:white;background-color:red' end 
FROM c01 
WHERE name like 'Johannes%';
:SQL -->
<table id="tab4"><!-- table_not_specified -->
<tr><th title="year">Year</th><th title="name">Name</th><th title="univ">University</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td align="right">1992</td><td>Johannes</td><td>Schramberg</td><td>&lt;</td><td>1911-06-03</td>
<script type="text/javascript">with(document.getElementById("tab4").rows[1].style){color="white";backgroundColor="red";}</script></tr>
</table>


<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/color09.xml" type="text/plain">test/color09</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;fragment=&amp;name=Johannes&amp;spec=test.color09&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;fragment=&amp;name=Johannes&amp;spec=test.color09&amp;lang=en">more</a>

</body></html>
