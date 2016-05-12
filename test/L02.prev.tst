<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>listbox</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>


	<!--List Box and List Parameter Test-->
	<!--Test von Auswahl- und Parameterlisten-->

    <h2>List Box and List Parameter Test</h2>
    <form method="get" action="servlet?spec=test/listbox"><input name="spec" type="hidden" value="test/listbox" />

    	Initial: <select name="namebox" multiple="yes" init="M L" size="7">
    		<option value="M">M</option>
    		<option value="J" selected="yes">J</option>
    		<option value="E" selected="yes">E</option>
    		<option value="F">F</option>
    		<option value="L">L</option>
    		<option value="I">I</option>
    		<option value="D">D</option>
    	</select>   
    	Int List: <input name="birthint" init="1887 1914" value="1910"></input>   
    	Char List: <input name="birthchar" init="1910 1911" value="1914"></input>   
        <input type="submit" value="Submit"></input>
    </form>
    
	<h3>Test with multiple selected initials</h3>
    <!-- SQL:
SELECT name
, univ
, birth 
FROM c01 
WHERE substr(name, 1, 1) in ('J', 'E') 
ORDER BY 3;
:SQL -->
<table id="table1">
<tr><th title="name">Name</th><th title="univ">University</th><th title="birth">Birthdate</th></tr>
<tr><td>Johannes</td><td>Schramberg</td><td>1911-06-03</td></tr>
<tr><td>Eberhard</td><td>Groß-Gerau</td><td>1912-11-17</td></tr>
<tr><td class="counter" colspan="3">2 Persons</td></tr>
</table>


	<h3>Test with list of int years</h3>
    <!-- SQL:
SELECT name
, univ
, birth 
FROM c01 
WHERE year(birth) in (1910) 
ORDER BY 3;
:SQL -->
<table id="table2">
<tr><th title="name">Name</th><th title="univ">University</th><th title="birth">Birthdate</th></tr>
<tr><td>Dorothea</td><td>Lübars</td><td>1910-02-07</td></tr>
<tr><td class="counter" colspan="3">1 Person</td></tr>
</table>


	<h3>Test with list of char years</h3>
    <!-- SQL:
SELECT name
, univ
, birth 
FROM c01 
WHERE cast(year(birth) as char) in ('1914') 
ORDER BY 3;
:SQL -->
<table id="table3">
<tr><th title="name">Name</th><th title="univ">University</th><th title="birth">Birthdate</th></tr>
<tr><td>Maria</td><td>Hermsdorf</td><td>1914-09-17</td></tr>
<tr><td class="counter" colspan="3">1 Person</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/listbox.xml" type="text/plain">test/listbox</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;birthchar=1914&amp;birthint=1910&amp;namebox=J&amp;namebox=E&amp;spec=test%2Flistbox&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;birthchar=1914&amp;birthint=1910&amp;namebox=J&amp;namebox=E&amp;spec=test%2Flistbox&amp;lang=en">more</a>

</body></html>
