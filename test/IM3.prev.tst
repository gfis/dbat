<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>selec06</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
<script src="spec/test/../javascript.js" type="text/javascript"></script>
</head><body>

    <!--Select from test table - sometimes with Javascript-->
    <!--Abfrage der Test-Tabelle, manchmal mit Javascript-->

    <h3>Select from test table <em>c01</em> - with Javascript</h3>
    <form method="get" action="servlet?spec=test/selec06"><input name="spec" type="hidden" value="test/selec06" />

        Name: <input name="name" maxsize="20" size="10" init="%" value="%"></input>
        <input type="submit" value="Submit"></input>
    </form>
    
    <!-- SQL:
SELECT concat(cast(year as char), concat('=', name))
, univ
, 'mailtoLink,' || (case 
            when year >= 1990 then name || '@' || univ || '.de'
            else '' end) 
FROM c01 
WHERE name like '%';
:SQL -->
<table id="table1">
<tr><th title="concat(cast(year as char), concat('=', name))">Name</th><th title="univ">University</th><th title="'mailtoLink,' || (case 
            when year >= 1990 then name || '@' || univ || '.de'
            else '' end)">EMail</th></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;year=1999&amp;name=Martha">Martha</a></td><td>Freiburg</td><td><script type="text/javascript">mailtoLink("Martha@Freiburg.de");</script></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;year=1992&amp;name=Johannes">Johannes</a></td><td>Schramberg</td><td><script type="text/javascript">mailtoLink("Johannes@Schramberg.de");</script></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;year=1945&amp;name=Eberhard">Eberhard</a></td><td>Groß-Gerau</td><td></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;year=1995&amp;name=Fritz">Fritz</a></td><td>Waldshut</td><td><script type="text/javascript">mailtoLink("Fritz@Waldshut.de");</script></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;year=1999&amp;name=Maria">Maria</a></td><td>Hermsdorf</td><td><script type="text/javascript">mailtoLink("Maria@Hermsdorf.de");</script></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;year=1983&amp;name=Ilse">Ilse</a></td><td>Lübars</td><td></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;year=1985&amp;name=Dorothea">Dorothea</a></td><td>Lübars</td><td></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;year=1984&amp;name=Lucie">Lucie</a></td><td>Lübars</td><td></td></tr>
<tr><td class="counter" colspan="3">8 persons</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/selec06.xml" type="text/plain">test/selec06</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test.selec06&amp;lang=en&amp;name=%25">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test.selec06&amp;lang=en&amp;name=%25">more</a>

</body></html>
