<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>selvalid</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    <!--Select with input field validation-->
    <!--Abfrage mit Eingabefeld-Validierung-->
    
    <h3>Select with input field validation</h3>
    <form method="get" action="servlet?spec=test/selvalid"><input name="spec" type="hidden" value="test/selvalid" />

        Start: <input name="start" maxsize="4" size="4" init="1900" valid="\d{4}" value="1900" title="Field validation with pattern &quot;\d{4}&quot;"></input>  
        End: <input name="end" maxsize="4" size="4" init="2000" valid="\d{4}" value="2000" title="Field validation with pattern &quot;\d{4}&quot;"></input>  
        <input type="submit" value="Submit"></input>
    </form>
    <!-- SQL:
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE year BETWEEN 1900 AND 2000 
ORDER BY 1,3;
:SQL -->
<div>
<table id="table1">
<tr><th title="name">Name</th><th title="univ">University</th><th title="year">Year</th><th title="gender">Gender</th><th title="birth">Birthdate</th></tr>
<tr><td>Dorothea</td><td>Lübars</td><td align="right">1985</td><td>$</td><td>1910-02-07</td></tr>
<tr><td>Eberhard</td><td>Groß-Gerau</td><td align="right">1945</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>Waldshut</td><td align="right">1995</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td>Ilse</td><td>Lübars</td><td align="right">1983</td><td>$</td><td>1909-02-09</td></tr>
<tr><td>Johannes</td><td>Schramberg</td><td align="right">1992</td><td>&lt;</td><td>1911-06-03</td></tr>
<tr><td>Lucie</td><td>Lübars</td><td align="right">1984</td><td>'</td><td>1887-07-09</td></tr>
<tr><td>Maria</td><td>Hermsdorf</td><td align="right">1999</td><td>#</td><td>1914-09-17</td></tr>
<tr><td>Martha</td><td>Freiburg</td><td align="right">1999</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td class="counter" colspan="5">8 Persons</td></tr>
</table>
</div>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/selvalid.xml" type="text/plain">test/selvalid</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;spec=test.selvalid&amp;lang=en&amp;start=1900&amp;end=2000">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test.selvalid&amp;lang=en&amp;start=1900&amp;end=2000">more</a>

</body></html>
