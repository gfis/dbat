<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>highlight</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>


    <!--Spalte mit Hervorhebung eines Teilworts
    -->
    <!--Column with Highlighting of a Keyword
    -->

    <h2><a href="index.html">Dbat</a> - Test Specifications</h2>
    <h3>Select with highlighting in column <em>University</em></h3>
    <form method="get" action="servlet?spec=test/highlight"><input name="spec" type="hidden" value="test/highlight" />

        Keyword: <input name="keyword" maxsize="72" size="20" init="er" value="ajax"></input>  
        <input type="submit" value="Search"></input>
    </form>

    <!-- SQL:
SELECT name
, REPLACE(UPPER(univ),UPPER('ajax'), '<span style="background: lightsalmon;">' || UPPER('ajax') || '</span>')
, year
, gender
, birth 
FROM c01 
ORDER BY name;
:SQL -->
<table id="table1">
<tr><th title="name">Name</th><th title="REPLACE(UPPER(univ),UPPER('ajax'), '<span style=&quot;background: lightsalmon;&quot;>' || UPPER('ajax') || '</span>')">University</th><th title="year">Year</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td>Dorothea</td><td>LÜBARS</td><td align="right">1985</td><td>$</td><td>1910-02-07</td></tr>
<tr><td>Eberhard</td><td>GROß-GERAU</td><td align="right">1945</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>WALDSHUT</td><td align="right">1995</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td>Ilse</td><td>LÜBARS</td><td align="right">1983</td><td>$</td><td>1909-02-09</td></tr>
<tr><td>Johannes</td><td>SCHRAMBERG</td><td align="right">1992</td><td>&lt;</td><td>1911-06-03</td></tr>
<tr><td>Lucie</td><td>LÜBARS</td><td align="right">1984</td><td>'</td><td>1887-07-09</td></tr>
<tr><td>Maria</td><td>HERMSDORF</td><td align="right">1999</td><td>#</td><td>1914-09-17</td></tr>
<tr><td>Martha</td><td>FREIBURG</td><td align="right">1999</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td class="counter" colspan="5">8 Persons</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/highlight.xml" type="text/plain">test/highlight</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;keyword=ajax&amp;spec=test%2Fhighlight&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;keyword=ajax&amp;spec=test%2Fhighlight&amp;lang=en">more</a>

</body></html>
