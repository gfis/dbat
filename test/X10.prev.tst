wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/callx10&iname0=row2"
<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=ISO-8859-1" />
<meta name="robots" content="noindex, nofollow" />
<title>callx10</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>


	<!--Activation of a Stored Procedure; results into parameters-->
	<!--Aufruf einer Stored Procedure; Ergebnis im Parametersatz-->

    <h2>Call of Stored Procedure <em>x07</em></h2>
    <form method="get" action="servlet?spec=test/callx10"><input name="spec" type="hidden" value="test/callx10" />

        Name: <input name="iname0" init="row2" value="row2"></input>
        <input type="submit" value="Submit"></input>
    </form>

	<h3>Vertical Result</h3>
    <table id="tab1"><!-- x07 -->
<tr><th align="left" class="vert">Name</th><td class="vert">row2</td></tr><tr><th align="left" class="vert">Dec1</th><td class="vert">194706290530000000</td></tr><tr><th align="left" class="vert">Dec2</th><td class="vert">194706.290</td></tr><tr><th align="left" class="vert">Date3</th><td class="vert">1981-08-09</td></tr><tr><th align="left" class="vert">Time4</th><td class="vert">11:30:00</td></tr><tr><th align="left" class="vert">TS5</th><td class="vert">1981-08-09 11:30:00.000</td></tr>
</table>

    
    
    
	<h3>Parameter Table</h3>
	<table>
		<tr>
			<td><strong>Name</strong></td>
			<td>row2</td>
		</tr>
		<tr>
			<td><strong>Dec1</strong></td>
			<td>194706290530000000</td>
		</tr>
		<tr>
			<td><strong>Dec2</strong></td>
			<td>194706.290</td>
		</tr>
		<tr>
			<td><strong>Date3</strong></td>
			<td>1981-08-09</td>
		</tr>
		<tr>
			<td><strong>Time4</strong></td>
			<td>11:30:00</td>
		</tr>
		<tr>
			<td><strong>TS5</strong></td>
			<td>1981-08-09 11:30:00.000</td>
		</tr>
	</table>
    
<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/callx10.xml" type="text/plain">test/callx10</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Fcallx10&amp;odec2=194706.290&amp;odec1=194706290530000000&amp;ots5=1981-08-09+11%3A30%3A00.000&amp;otime4=11%3A30%3A00&amp;iname0=row2&amp;odate3=1981-08-09&amp;update_count=0&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fcallx10&amp;odec2=194706.290&amp;odec1=194706290530000000&amp;ots5=1981-08-09+11%3A30%3A00.000&amp;otime4=11%3A30%3A00&amp;iname0=row2&amp;odate3=1981-08-09&amp;update_count=0&amp;lang=en">more</a>

</body></html>
