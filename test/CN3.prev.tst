<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>counter01</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

	<!--Counter Test-->
	<!--Test des Zeilenzählers-->
	
    <h3>Counter Test</h3>
    <form method="get" action="servlet?spec=test/counter01"><input name="spec" type="hidden" value="test/counter01" />

        Name: <input name="name" maxsize="20" size="10" init="%r" value="Ilse"></input>
        <input type="submit" value="Submit"></input>
    </form>

    <!-- SQL:
SELECT name 
FROM c01 
WHERE name like 'Ilse%';
:SQL -->
<div>
<table id="table1">
<tr><th title="name">Name</th></tr>
<tr><td>Ilse</td></tr>
<tr><td class="counter" colspan="1">1 row</td></tr>
</table>
</div>


    <!-- SQL:
SELECT name 
FROM c01 
WHERE name like 'Ilse%';
:SQL -->
<div>
<table id="table2">
<tr><th title="name">Name</th></tr>
<tr><td>Ilse</td></tr>
<tr><td class="counter" colspan="1">1 Person</td></tr>
</table>
</div>


    <!-- SQL:
SELECT name 
FROM c01 
WHERE name like 'Ilse%';
:SQL -->
<div>
<table id="table3">
<tr><th title="name">Name</th></tr>
<tr><td>Ilse</td></tr>
<tr><td class="counter" colspan="1">1 Person</td></tr>
</table>
</div>


    <!-- SQL:
SELECT name 
FROM c01 
WHERE name like 'Ilse%';
:SQL -->
<div>
<table id="table4">
<tr><th title="name">Name</th></tr>
<tr><td>Ilse</td></tr>
<tr><td class="counter" colspan="1">1 Person</td></tr>
</table>
</div>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/counter01.xml" type="text/plain">test/counter01</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;name=Ilse&amp;spec=test%2Fcounter01&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;name=Ilse&amp;spec=test%2Fcounter01&amp;lang=en">more</a>

</body></html>
