<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>includer</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    <!--tests the inclusion of system entities-->
    <!--testet die Einbindung von System Entities-->

    <h2>Inclusion of System Entities</h2>
    <form method="get" action="servlet?spec=test/includer"><input name="spec" type="hidden" value="test/includer" />

        Name: <input name="name" init="" value=""></input>
        <input type="submit" value="Submit"></input>
    </form>

    <h3>Inclusion of system entity with "http:" schema and absolute path: <em>rem01</em></h3>
    


	<h4>Include file <em>include01.xml</em></h4>
    <!-- SQL:
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like '%';
:SQL -->
<table id="table1">
<tr><th title="name">Name</th><th title="univ">University</th><th title="year">Year</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td>Martha</td><td>Freiburg</td><td align="right">1999</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td>Johannes</td><td>Schramberg</td><td align="right">1992</td><td>&lt;</td><td>1911-06-03</td></tr>
<tr><td>Eberhard</td><td>Groß-Gerau</td><td align="right">1945</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>Waldshut</td><td align="right">1995</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td>Maria</td><td>Hermsdorf</td><td align="right">1999</td><td>#</td><td>1914-09-17</td></tr>
<tr><td>Ilse</td><td>Lübars</td><td align="right">1983</td><td>$</td><td>1909-02-09</td></tr>
<tr><td>Dorothea</td><td>Lübars</td><td align="right">1985</td><td>$</td><td>1910-02-07</td></tr>
<tr><td>Lucie</td><td>Lübars</td><td align="right">1984</td><td>'</td><td>1887-07-09</td></tr>
<tr><td class="counter" colspan="5">8 Persons</td></tr>
</table>

    <p>
    	Now the nested include reference
    </p>
    



    <h4>Nested include file <em>include12.xml</em></h4>
    <!-- SQL:
SELECT name
, birth 
FROM c01 
WHERE name like '%';
:SQL -->
<table id="table2">
<tr><th title="name">Name</th><th title="birth">Birthdate</th></tr>
<tr><td>Martha</td><td>1909-11-17</td></tr>
<tr><td>Johannes</td><td>1911-06-03</td></tr>
<tr><td>Eberhard</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>1907-08-08</td></tr>
<tr><td>Maria</td><td>1914-09-17</td></tr>
<tr><td>Ilse</td><td>1909-02-09</td></tr>
<tr><td>Dorothea</td><td>1910-02-07</td></tr>
<tr><td>Lucie</td><td>1887-07-09</td></tr>
<tr><td class="counter" colspan="2">8 Persons</td></tr>
</table>





    <h3>Inclusion of system entity with "file:" schema and absolute path: <em>loc01</em></h3>
    


	<h4>Include file <em>include01.xml</em></h4>
    <!-- SQL:
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like '%';
:SQL -->
<table id="table3">
<tr><th title="name">Name</th><th title="univ">University</th><th title="year">Year</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td>Martha</td><td>Freiburg</td><td align="right">1999</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td>Johannes</td><td>Schramberg</td><td align="right">1992</td><td>&lt;</td><td>1911-06-03</td></tr>
<tr><td>Eberhard</td><td>Groß-Gerau</td><td align="right">1945</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>Waldshut</td><td align="right">1995</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td>Maria</td><td>Hermsdorf</td><td align="right">1999</td><td>#</td><td>1914-09-17</td></tr>
<tr><td>Ilse</td><td>Lübars</td><td align="right">1983</td><td>$</td><td>1909-02-09</td></tr>
<tr><td>Dorothea</td><td>Lübars</td><td align="right">1985</td><td>$</td><td>1910-02-07</td></tr>
<tr><td>Lucie</td><td>Lübars</td><td align="right">1984</td><td>'</td><td>1887-07-09</td></tr>
<tr><td class="counter" colspan="5">8 Persons</td></tr>
</table>

    <p>
    	Now the nested include reference
    </p>
    



    <h4>Nested include file <em>include12.xml</em></h4>
    <!-- SQL:
SELECT name
, birth 
FROM c01 
WHERE name like '%';
:SQL -->
<table id="table4">
<tr><th title="name">Name</th><th title="birth">Birthdate</th></tr>
<tr><td>Martha</td><td>1909-11-17</td></tr>
<tr><td>Johannes</td><td>1911-06-03</td></tr>
<tr><td>Eberhard</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>1907-08-08</td></tr>
<tr><td>Maria</td><td>1914-09-17</td></tr>
<tr><td>Ilse</td><td>1909-02-09</td></tr>
<tr><td>Dorothea</td><td>1910-02-07</td></tr>
<tr><td>Lucie</td><td>1887-07-09</td></tr>
<tr><td class="counter" colspan="2">8 Persons</td></tr>
</table>





    <h3>Inclusion of system entity with relative path: <em>rel01</em></h3>
    


	<h4>Include file <em>include01.xml</em></h4>
    <!-- SQL:
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like '%';
:SQL -->
<table id="table5">
<tr><th title="name">Name</th><th title="univ">University</th><th title="year">Year</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td>Martha</td><td>Freiburg</td><td align="right">1999</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td>Johannes</td><td>Schramberg</td><td align="right">1992</td><td>&lt;</td><td>1911-06-03</td></tr>
<tr><td>Eberhard</td><td>Groß-Gerau</td><td align="right">1945</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>Waldshut</td><td align="right">1995</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td>Maria</td><td>Hermsdorf</td><td align="right">1999</td><td>#</td><td>1914-09-17</td></tr>
<tr><td>Ilse</td><td>Lübars</td><td align="right">1983</td><td>$</td><td>1909-02-09</td></tr>
<tr><td>Dorothea</td><td>Lübars</td><td align="right">1985</td><td>$</td><td>1910-02-07</td></tr>
<tr><td>Lucie</td><td>Lübars</td><td align="right">1984</td><td>'</td><td>1887-07-09</td></tr>
<tr><td class="counter" colspan="5">8 Persons</td></tr>
</table>

    <p>
    	Now the nested include reference
    </p>
    



    <h4>Nested include file <em>include12.xml</em></h4>
    <!-- SQL:
SELECT name
, birth 
FROM c01 
WHERE name like '%';
:SQL -->
<table id="table6">
<tr><th title="name">Name</th><th title="birth">Birthdate</th></tr>
<tr><td>Martha</td><td>1909-11-17</td></tr>
<tr><td>Johannes</td><td>1911-06-03</td></tr>
<tr><td>Eberhard</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>1907-08-08</td></tr>
<tr><td>Maria</td><td>1914-09-17</td></tr>
<tr><td>Ilse</td><td>1909-02-09</td></tr>
<tr><td>Dorothea</td><td>1910-02-07</td></tr>
<tr><td>Lucie</td><td>1887-07-09</td></tr>
<tr><td class="counter" colspan="2">8 Persons</td></tr>
</table>





<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/includer.xml" type="text/plain">test/includer</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Fincluder&amp;lang=en&amp;name=">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fincluder&amp;lang=en&amp;name=">more</a>

</body></html>
