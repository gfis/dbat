java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m html -f ../web/spec/test/column02.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>column02</title>
<link rel="stylesheet" type="text/css" href="./../web/spec/test/stylesheet.css" />
</head><body>

	<!--Select from test table - same as <em>selec02</em>-->
	<!--Abfrage der Test-Tabelle - wie <em>selec02</em>-->

    <h3>Select from test table <em>c01</em> - same as <em>selec02, with <column> elements</em></h3>
    <form method="get" action="servlet?spec=../web/spec/test/column02.xml"><input name="spec" type="hidden" value="../web/spec/test/column02.xml" />

        Name: <input name="name" maxsize="20" size="10" init="%" value="%"></input>
        <input type="submit" value="Submit"></input>
    </form>
    
    <!-- SQL:
SELECT concat(name, concat('*', cast(year as char)))
, concat(cast(year as char), concat('=', name))
, concat(cast(year as char), concat('=e.', cast(year as char)))
, concat(name, concat('=f.', name))
, univ
, gender
, birth 
FROM c01 
WHERE name like '%'
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="concat(name, concat('*', cast(year as char)))">Year</th><th title="concat(cast(year as char), concat('=', name))">Name</th><th title="concat(cast(year as char), concat('=e.', cast(year as char)))">Year</th><th title="concat(name, concat('=f.', name))">Name</th><th title="univ">University</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Martha&amp;year=1999">1999</a></td><td><a href="servlet?spec=test/selec01&amp;year=1999&amp;name=Martha">Martha</a></td><td><a href="servlet?spec=test/selec01&amp;year=1999&amp;name=e.1999">e.1999</a></td><td><a href="servlet?spec=test/selec01&amp;name=Martha&amp;year=f.Martha">f.Martha</a></td><td>Freiburg</td><td align="center">&amp;</td><td>1909-11-17</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Johannes&amp;year=1992">1992</a></td><td><a href="servlet?spec=test/selec01&amp;year=1992&amp;name=Johannes">Johannes</a></td><td><a href="servlet?spec=test/selec01&amp;year=1992&amp;name=e.1992">e.1992</a></td><td><a href="servlet?spec=test/selec01&amp;name=Johannes&amp;year=f.Johannes">f.Johannes</a></td><td>Schramberg</td><td align="center">&lt;</td><td>1911-06-03</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Eberhard&amp;year=1945">1945</a></td><td><a href="servlet?spec=test/selec01&amp;year=1945&amp;name=Eberhard">Eberhard</a></td><td><a href="servlet?spec=test/selec01&amp;year=1945&amp;name=e.1945">e.1945</a></td><td><a href="servlet?spec=test/selec01&amp;name=Eberhard&amp;year=f.Eberhard">f.Eberhard</a></td><td>Groß-Gerau</td><td align="center">&gt;</td><td>1912-11-17</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Fritz&amp;year=1995">1995</a></td><td><a href="servlet?spec=test/selec01&amp;year=1995&amp;name=Fritz">Fritz</a></td><td><a href="servlet?spec=test/selec01&amp;year=1995&amp;name=e.1995">e.1995</a></td><td><a href="servlet?spec=test/selec01&amp;name=Fritz&amp;year=f.Fritz">f.Fritz</a></td><td>Waldshut</td><td align="center">&lt;</td><td>1907-08-08</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Maria&amp;year=1999">1999</a></td><td><a href="servlet?spec=test/selec01&amp;year=1999&amp;name=Maria">Maria</a></td><td><a href="servlet?spec=test/selec01&amp;year=1999&amp;name=e.1999">e.1999</a></td><td><a href="servlet?spec=test/selec01&amp;name=Maria&amp;year=f.Maria">f.Maria</a></td><td>Hermsdorf</td><td align="center">#</td><td>1914-09-17</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Ilse&amp;year=1983">1983</a></td><td><a href="servlet?spec=test/selec01&amp;year=1983&amp;name=Ilse">Ilse</a></td><td><a href="servlet?spec=test/selec01&amp;year=1983&amp;name=e.1983">e.1983</a></td><td><a href="servlet?spec=test/selec01&amp;name=Ilse&amp;year=f.Ilse">f.Ilse</a></td><td>Lübars</td><td align="center">$</td><td>1909-02-09</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Dorothea&amp;year=1985">1985</a></td><td><a href="servlet?spec=test/selec01&amp;year=1985&amp;name=Dorothea">Dorothea</a></td><td><a href="servlet?spec=test/selec01&amp;year=1985&amp;name=e.1985">e.1985</a></td><td><a href="servlet?spec=test/selec01&amp;name=Dorothea&amp;year=f.Dorothea">f.Dorothea</a></td><td>Lübars</td><td align="center">$</td><td>1910-02-07</td></tr>
<tr><td align="right"><a href="servlet?spec=test/selec01&amp;name=Lucie&amp;year=1984">1984</a></td><td><a href="servlet?spec=test/selec01&amp;year=1984&amp;name=Lucie">Lucie</a></td><td><a href="servlet?spec=test/selec01&amp;year=1984&amp;name=e.1984">e.1984</a></td><td><a href="servlet?spec=test/selec01&amp;name=Lucie&amp;year=f.Lucie">f.Lucie</a></td><td>Lübars</td><td align="center">'</td><td>1887-07-09</td></tr>
<tr><td class="counter" colspan="7">8 persons</td></tr>
</table>

<br />Output on 2012-06-01 22:56:39.921 by <a href="index.html">Dbat</a> script <a target="_blank" href="./../web/spec/test/column02.xml.xml" type="text/plain">../web/spec/test/column02.xml</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;name=%25&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;name=%25&amp;lang=en">more</a>

</body></html>
