<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>test/var01</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

	<!--Test of the var element-->
	<!--Test des var-Elements-->
			
	<h2><a href="servlet?spec=test.index">Test</a> - Test of a <var> element</h2>
    <form method="get" action="servlet?spec=test/var01"><input name="spec" type="hidden" value="test/var01" />

        Name: <input name="name" maxsize="20" size="10" init="%" value="%"></input>
        <input type="submit" value="Submit"></input>
    </form>

	<!-- SQL:
SELECT name
, family
, birth 
FROM relatives 
WHERE name like  ? 
ORDER BY 1;
:SQL -->
<table id="table1">
<tr><th title="name">Name</th><th title="family">Family</th><th title="birth">Birthdate</th></tr>
<tr><td>Dorothea</td><td>Fischer</td><td>1910-02-07</td></tr>
<tr><td>Eberhard</td><td>Fischer</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>Fischer</td><td>1907-08-08</td></tr>
<tr><td>Ilse</td><td>Ritter</td><td>1909-02-09</td></tr>
<tr><td>Johannes</td><td>Fischer</td><td>1911-06-03</td></tr>
<tr><td>Lucie</td><td>Ritter</td><td>1887-07-09</td></tr>
<tr><td>Maria</td><td>Ritter</td><td>1914-09-17</td></tr>
<tr><td>Martha</td><td>Fischer</td><td>1909-11-17</td></tr>
<tr><td>Teherba</td><td>Ritter</td><td>1886-02-04</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/var01.xml" type="text/plain">test/var01</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;name=%25&amp;spec=test.var01&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;name=%25&amp;spec=test.var01&amp;lang=en">more</a>

</body></html>
