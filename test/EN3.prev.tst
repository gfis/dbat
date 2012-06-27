wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/enum01"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>enum01</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>


	<!--Fetch Enumerations into Parameters-->
	<!--Parameter aus einer Enumeration füllen-->
	
    <h3>Fetch Enumerations into Parameters <em>code = initcode,
    	display = initdisplay</em>
    </h3>

    <!-- SQL:
SELECT seq
, code
, display 
FROM en1 
WHERE lang = 'eng' 
ORDER BY 1;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="seq">seq</th><th title="code">code</th><th title="display">display</th></tr>
<tr><td>1</td><td>M</td><td>male</td></tr>
<tr><td>2</td><td>F</td><td>female</td></tr>
<tr><td>3</td><td>N</td><td>neuter</td></tr>
</table>


    <!-- SQL:
SELECT seq
, code
, display 
FROM en1 
WHERE lang = 'eng' 
ORDER BY 1;
:SQL -->


	<h3>Parameter Table</h3>
	<table>
		<tr>
			<td>M</td>
			<td>male</td>
		</tr>
		<tr>
			<td>F</td>
			<td>female</td>
		</tr>
		<tr>
			<td>N</td>
			<td>neuter</td>
		</tr>
	</table>
	Continue with <a href="servlet?spec=enum02">enum02</a>
<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/enum01.xml" type="text/plain">test/enum01</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Fenum01&amp;seq=1&amp;seq=2&amp;seq=3&amp;display=male&amp;display=female&amp;display=neuter&amp;code=M&amp;code=F&amp;code=N&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fenum01&amp;seq=1&amp;seq=2&amp;seq=3&amp;display=male&amp;display=female&amp;display=neuter&amp;code=M&amp;code=F&amp;code=N&amp;lang=en">more</a>

</body></html>
