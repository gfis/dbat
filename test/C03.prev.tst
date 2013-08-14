<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>color04</title>
<link rel="stylesheet" type="text/css" href="./web/spec/test/stylesheet.css" />
</head><body>

	<!--Select from test table - with colors and linked values-->
	<!--Abfrage der Test-Tabelle mit farbigem Hintergrund und Verweisen auf den Werten-->

    <h3>Select from test table c01 - with colors and linked values</h3>
    <form method="get" action="servlet?spec=web/spec/test/color04.xml"><input name="spec" type="hidden" value="web/spec/test/color04.xml" />

        Name: <input name="name" maxsize="20" size="10" init="%r" value="%r"></input>
        <input type="submit" value="Submit"></input>
    </form>
    
    <!-- SQL:
SELECT case when year < 1980 then 'gry' else 'wht' end
, concat(name, concat('=', cast(year as char)))
, case when name like '%r%' then 'blu' else 'wht' end
, concat(cast(year as char), concat('=', name))
, case when univ like 'L%' then 'red' else 'lye' end
, univ
, gender
, birth 
FROM c01;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="Year of Decease">Year</th><th title="Family Name">Name</th><th title="univ">University</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td align="right" class="wht"><a href="servlet?spec=test/selec01&amp;name=Martha&amp;year=1999">1999</a></td><td class="blu"><a href="servlet?spec=test/selec01&amp;year=1999&amp;name=Martha">Martha</a></td><td class="lye">Freiburg</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td align="right" class="wht"><a href="servlet?spec=test/selec01&amp;name=Johannes&amp;year=1992">1992</a></td><td class="wht"><a href="servlet?spec=test/selec01&amp;year=1992&amp;name=Johannes">Johannes</a></td><td class="lye">Schramberg</td><td>&lt;</td><td>1911-06-03</td></tr>
<tr><td align="right" class="gry"><a href="servlet?spec=test/selec01&amp;name=Eberhard&amp;year=1945">1945</a></td><td class="blu"><a href="servlet?spec=test/selec01&amp;year=1945&amp;name=Eberhard">Eberhard</a></td><td class="lye">Groß-Gerau</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td align="right" class="wht"><a href="servlet?spec=test/selec01&amp;name=Fritz&amp;year=1995">1995</a></td><td class="blu"><a href="servlet?spec=test/selec01&amp;year=1995&amp;name=Fritz">Fritz</a></td><td class="lye">Waldshut</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td align="right" class="wht"><a href="servlet?spec=test/selec01&amp;name=Maria&amp;year=1999">1999</a></td><td class="blu"><a href="servlet?spec=test/selec01&amp;year=1999&amp;name=Maria">Maria</a></td><td class="lye">Hermsdorf</td><td>#</td><td>1914-09-17</td></tr>
<tr><td align="right" class="wht"><a href="servlet?spec=test/selec01&amp;name=Ilse&amp;year=1983">1983</a></td><td class="wht"><a href="servlet?spec=test/selec01&amp;year=1983&amp;name=Ilse">Ilse</a></td><td class="red">Lübars</td><td>$</td><td>1909-02-09</td></tr>
<tr><td align="right" class="wht"><a href="servlet?spec=test/selec01&amp;name=Dorothea&amp;year=1985">1985</a></td><td class="blu"><a href="servlet?spec=test/selec01&amp;year=1985&amp;name=Dorothea">Dorothea</a></td><td class="red">Lübars</td><td>$</td><td>1910-02-07</td></tr>
<tr><td align="right" class="wht"><a href="servlet?spec=test/selec01&amp;name=Lucie&amp;year=1984">1984</a></td><td class="wht"><a href="servlet?spec=test/selec01&amp;year=1984&amp;name=Lucie">Lucie</a></td><td class="red">Lübars</td><td>'</td><td>1887-07-09</td></tr>
<tr><td class="counter" colspan="8">8 persons</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="./web/spec/test/color04.xml" type="text/plain">web/spec/test/color04.xml</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;lang=en&amp;name=%25r">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;lang=en&amp;name=%25r">more</a>

</body></html>
