<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>test/uml01</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    <!--Test of URL encoding with accented letters-->
    <!--Test der URL-Codierung von Umlauten-->
            
    <h2><a href="servlet?spec=test.index">Test</a> - Test of URL encoding</h2>
    <form method="post" action="servlet?spec=test/uml01"><input name="spec" type="hidden" value="test/uml01" />

        Name: <input name="name" maxsize="20" size="10" init="%" value="%"></input><br />
        <input type="submit" value="Submit"></input>
    </form>

    <h3>Search for <em>%</em></h3>
    <!-- SQL:
SELECT name
, family
, gender
, birth
, decease 
FROM relatives 
WHERE name    like '%' 
ORDER BY name;
:SQL -->
<table id="table1">
<tr><th title="name">Name</th><th title="family">Family</th><th title="gender">Gender</th><th title="birth">Birthdate</th><th title="decease">Decease</th></tr>
<tr><td><a href="servlet?spec=test/uml01&amp;name=C%C3%A4cilie">Cäcilie</a></td><td>Fischer</td><td align="center">F</td><td>1919-11-17</td><td>1999</td></tr>
<tr><td><a href="servlet?spec=test/uml01&amp;name=Dorothea">Dorothea</a></td><td>Fischer</td><td align="center">F</td><td>1910-02-07</td><td>1985</td></tr>
<tr><td><a href="servlet?spec=test/uml01&amp;name=Eberhard">Eberhard</a></td><td>Fischer</td><td align="center">M</td><td>1912-11-17</td><td>1945</td></tr>
<tr><td><a href="servlet?spec=test/uml01&amp;name=Fritz">Fritz</a></td><td>Fischer</td><td align="center">M</td><td>1907-08-08</td><td>1995</td></tr>
<tr><td><a href="servlet?spec=test/uml01&amp;name=Ilse">Ilse</a></td><td>Ritter</td><td align="center">F</td><td>1909-02-09</td><td>1983</td></tr>
<tr><td><a href="servlet?spec=test/uml01&amp;name=Johannes">Johannes</a></td><td>Fischer</td><td align="center">M</td><td>1911-06-03</td><td>1992</td></tr>
<tr><td><a href="servlet?spec=test/uml01&amp;name=Lucie">Lucie</a></td><td>Ritter</td><td align="center">F</td><td>1887-07-09</td><td>1984</td></tr>
<tr><td><a href="servlet?spec=test/uml01&amp;name=Maria">Maria</a></td><td>Ritter</td><td align="center">F</td><td>1914-09-17</td><td>1999</td></tr>
<tr><td><a href="servlet?spec=test/uml01&amp;name=Martha">Martha</a></td><td>Fischer</td><td align="center">F</td><td>1909-11-17</td><td>1999</td></tr>
<tr><td><a href="servlet?spec=test/uml01&amp;name=Teherba">Teherba</a></td><td>Ritter</td><td align="center">F</td><td>1886-02-04</td><td>1968</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/uml01.xml" type="text/plain">test/uml01</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;search_name=C%C3%A4cilie&amp;spec=test.uml01&amp;lang=en&amp;name=%25">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;search_name=C%C3%A4cilie&amp;spec=test.uml01&amp;lang=en&amp;name=%25">more</a>

</body></html>
