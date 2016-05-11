<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>selec01</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    <!--Select from test table-->
    <!--Abfrage der Test-Tabelle-->
    
    <h3>Select from test table c01</h3>
    <form method="get" action="servlet?spec=test/selec01"><input name="spec" type="hidden" value="test/selec01" />

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
WHERE name like '%r%';
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="name">Name</th><th title="univ">University</th><th title="year">Year</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td>Martha</td><td>Freiburg</td><td align="right">1999</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td>Eberhard</td><td>Groß-Gerau</td><td align="right">1945</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>Waldshut</td><td align="right">1995</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td>Maria</td><td>Hermsdorf</td><td align="right">1999</td><td>#</td><td>1914-09-17</td></tr>
<tr><td>Dorothea</td><td>Lübars</td><td align="right">1985</td><td>$</td><td>1910-02-07</td></tr>
<tr><td class="counter" colspan="5">5 Persons</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/selec01.xml" type="text/plain">test/selec01</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Fselec01&amp;lang=en&amp;name=%25r">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fselec01&amp;lang=en&amp;name=%25r">more</a>

</body></html>
