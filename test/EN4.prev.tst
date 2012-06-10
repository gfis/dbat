wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/enum02"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>enum02</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>


	<!--Fetch Enumerations and build dynamic listbox-->
	<!--Dyn. Listbox mit Parametern einer Enumeration füllen-->
	
    <h3>Fetch Enumerations and build a dynamic listbox 
    	with default <em>N</em>
    </h3>

    <!-- SQL:
SELECT DISTINCT code
, display 
FROM en1 
WHERE lang = 'eng' 
ORDER BY 1
:SQL -->


	<form action="servlet?spec=test/enum02" method="post"><input name="spec" type="hidden" value="test/enum02" />

		<table>
		<tr><td valign="top">Gender: <select name="gender" size="3">
<option value="F">female</option>
<option value="M">male</option>
<option value="N" selected="yes">neuter</option>
</select>
</td></tr>
		<tr><td valign="top">Gender: <select name="gender" size="4">
<option value="">(any)</option>
<option value="F">female</option>
<option value="M">male</option>
<option value="N" selected="yes">neuter</option>
</select>
</td></tr>
		</table>
	</form>
<br />Output on 2012-05-07 18:38:32.607 by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/enum02.xml" type="text/plain">test/enum02</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Fenum02&amp;gender=N&amp;display=female&amp;display=male&amp;display=neuter&amp;code=F&amp;code=M&amp;code=N&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fenum02&amp;gender=N&amp;display=female&amp;display=male&amp;display=neuter&amp;code=F&amp;code=M&amp;code=N&amp;lang=en">more</a>

</body></html>
