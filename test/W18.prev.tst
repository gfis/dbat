<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>selec07</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    
    <!--Select from test table - with target="_blank" attribute-->
    <!--Abfrage der Test-Tabelle mit Attribut target="_blank"-->

    <h3>Select from test table <em>c01</em> - with target="_blank" attribute</h3>
    <form method="get" action="servlet?spec=test/selec07"><input name="spec" type="hidden" value="test/selec07" />

        Name: <input name="name" maxsize="20" size="10" init="%" value="%"></input>
        <input type="submit" value="Submit"></input>
    </form>
    
    <!-- SQL:
SELECT concat(name, concat('*', cast(year as char)))
, concat(cast(year as char), concat('=', name))
, univ
, gender
, birth 
FROM c01 
WHERE name like '%';
:SQL -->
<div>
<table id="table1">
<tr><th title="concat(name, concat('*', cast(year as char)))">Year</th><th title="concat(cast(year as char), concat('=', name))">Name</th><th title="univ">University</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Martha&amp;year=1999">1999</a></td><td><a href="servlet?spec=test/selec01&amp;year=1999&amp;name=Martha" target="_blank">Martha</a></td><td>Freiburg</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Johannes&amp;year=1992">1992</a></td><td><a href="servlet?spec=test/selec01&amp;year=1992&amp;name=Johannes" target="_blank">Johannes</a></td><td>Schramberg</td><td>&lt;</td><td>1911-06-03</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Eberhard&amp;year=1945">1945</a></td><td><a href="servlet?spec=test/selec01&amp;year=1945&amp;name=Eberhard" target="_blank">Eberhard</a></td><td>Groß-Gerau</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Fritz&amp;year=1995">1995</a></td><td><a href="servlet?spec=test/selec01&amp;year=1995&amp;name=Fritz" target="_blank">Fritz</a></td><td>Waldshut</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Maria&amp;year=1999">1999</a></td><td><a href="servlet?spec=test/selec01&amp;year=1999&amp;name=Maria" target="_blank">Maria</a></td><td>Hermsdorf</td><td>#</td><td>1914-09-17</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Ilse&amp;year=1983">1983</a></td><td><a href="servlet?spec=test/selec01&amp;year=1983&amp;name=Ilse" target="_blank">Ilse</a></td><td>Lübars</td><td>$</td><td>1909-02-09</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Dorothea&amp;year=1985">1985</a></td><td><a href="servlet?spec=test/selec01&amp;year=1985&amp;name=Dorothea" target="_blank">Dorothea</a></td><td>Lübars</td><td>$</td><td>1910-02-07</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Lucie&amp;year=1984">1984</a></td><td><a href="servlet?spec=test/selec01&amp;year=1984&amp;name=Lucie" target="_blank">Lucie</a></td><td>Lübars</td><td>'</td><td>1887-07-09</td></tr>
<tr><td class="counter" colspan="5">8 persons</td></tr>
</table>
</div>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/selec07.xml" type="text/plain">test/selec07</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;spec=test%2Fselec07&amp;lang=en&amp;name=%25">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fselec07&amp;lang=en&amp;name=%25">more</a>

</body></html>
