<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>multijs</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
<script src="spec/test/../sorttable.js" type="text/javascript"></script>
<script src="spec/test/../http_request.js" type="text/javascript"></script>
</head><body>

    <!--Test with 2 Javascripts-->
    <!--Test mit 2 Javascripts-->

    <h3>Select from test table c01 - with 2 Javascripts</h3>
    <form method="get" action="servlet?spec=test/multijs"><input name="spec" type="hidden" value="test/multijs" />

        Name: <input name="name" maxsize="20" size="10" init="%" value="%"></input>%    
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
WHERE name like '%%';
:SQL -->
<table id="table1" class="sortable">
<thead>
<tr><th title="Click =&gt; Sort">Year</th><th title="Click =&gt; Sort">Name</th><th title="Click =&gt; Sort">University</th><th title="Click =&gt; Sort">Gender</th><th title="Click =&gt; Sort">Birthdate</th></tr>
</thead>
<tr><td align="right">1999</td><td>Martha</td><td>Freiburg</td><td>&amp;</td><td>1909-11-17</td>
<script type="text/javascript">with(document.getElementById("tab1").rows[1]){className="red";}</script></tr>
<tr><td align="right">1992</td><td>Johannes</td><td>Schramberg</td><td>&lt;</td><td>1911-06-03</td>
<script type="text/javascript">with(document.getElementById("tab1").rows[2]){className="red";}</script></tr>
<tr><td align="right">1945</td><td>Eberhard</td><td>Groß-Gerau</td><td>&gt;</td><td>1912-11-17</td>
<script type="text/javascript">with(document.getElementById("tab1").rows[3]){className="blu";}</script></tr>
<tr><td align="right">1995</td><td>Fritz</td><td>Waldshut</td><td>&lt;</td><td>1907-08-08</td>
<script type="text/javascript">with(document.getElementById("tab1").rows[4]){className="red";}</script></tr>
<tr><td align="right">1999</td><td>Maria</td><td>Hermsdorf</td><td>#</td><td>1914-09-17</td>
<script type="text/javascript">with(document.getElementById("tab1").rows[5]){className="red";}</script></tr>
<tr><td align="right">1983</td><td>Ilse</td><td>Lübars</td><td>$</td><td>1909-02-09</td>
<script type="text/javascript">with(document.getElementById("tab1").rows[6]){className="blu";}</script></tr>
<tr><td align="right">1985</td><td>Dorothea</td><td>Lübars</td><td>$</td><td>1910-02-07</td>
<script type="text/javascript">with(document.getElementById("tab1").rows[7]){className="blu";}</script></tr>
<tr><td align="right">1984</td><td>Lucie</td><td>Lübars</td><td>'</td><td>1887-07-09</td>
<script type="text/javascript">with(document.getElementById("tab1").rows[8]){className="blu";}</script></tr>
<tfoot>
<tr><td class="counter" colspan="6">8 Persons</td></tr>
</tfoot>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/multijs.xml" type="text/plain">test/multijs</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Fmultijs&amp;lang=en&amp;name=%25">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fmultijs&amp;lang=en&amp;name=%25">more</a>

</body></html>
