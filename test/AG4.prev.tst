<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>aggr01</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

	<!--Column Aggregation with linked values-->
	<!--Aggregierte Spalte mit Verweisen auf den Werten-->

    <h3>Column aggregation test - with linked values</h3>
    
    <!-- SQL:
SELECT sp1
, concat(sp1, concat('=', concat(sp2, concat('=', sp3)))) 
FROM pivot;
:SQL -->
<table id="table1">
<tr><th title="sp1">Application</th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))">Aggr. Column</th></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name="></a></td><td><a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c1&amp;gamma=S1">S1</a>, <a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c2&amp;gamma=S2">S2</a>, <a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c3&amp;gamma=S3">S3</a>, <a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c4&amp;gamma=S4">S4</a>, <a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c5&amp;gamma=S5">S5</a></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name=A">A</a></td><td><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c1&amp;gamma=A1">A1</a>, <a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c2&amp;gamma=A2">A2</a>, <a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c3&amp;gamma=A3">A3</a>, <a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c4&amp;gamma=A4">A4</a>, <a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c5&amp;gamma=A5">A5</a></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name=B">B</a></td><td><a href="servlet?spec=test/selec01&amp;alpha=B&amp;beta=c1&amp;gamma=B1">B1</a>, <a href="servlet?spec=test/selec01&amp;alpha=B&amp;beta=c2&amp;gamma=B2">B2</a>, <a href="servlet?spec=test/selec01&amp;alpha=B&amp;beta=c3&amp;gamma=B3">B3</a>, <a href="servlet?spec=test/selec01&amp;alpha=B&amp;beta=c4&amp;gamma=B4">B4</a>, <a href="servlet?spec=test/selec01&amp;alpha=B&amp;beta=c5&amp;gamma=B5">B5</a></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name=C">C</a></td><td><a href="servlet?spec=test/selec01&amp;alpha=C&amp;beta=c1&amp;gamma=C1">C1</a>, <a href="servlet?spec=test/selec01&amp;alpha=C&amp;beta=c2&amp;gamma=C2">C2</a>, <a href="servlet?spec=test/selec01&amp;alpha=C&amp;beta=c3&amp;gamma=C3">C3</a>, <a href="servlet?spec=test/selec01&amp;alpha=C&amp;beta=c4&amp;gamma=C4">C4</a>, <a href="servlet?spec=test/selec01&amp;alpha=C&amp;beta=c5&amp;gamma=C5">C5</a></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name=D">D</a></td><td><a href="servlet?spec=test/selec01&amp;alpha=D&amp;beta=c1&amp;gamma=D1">D1</a>, <a href="servlet?spec=test/selec01&amp;alpha=D&amp;beta=c2&amp;gamma=D2">D2</a>, <a href="servlet?spec=test/selec01&amp;alpha=D&amp;beta=c3&amp;gamma=D3">D3</a>, <a href="servlet?spec=test/selec01&amp;alpha=D&amp;beta=c4&amp;gamma=D4">D4</a>, <a href="servlet?spec=test/selec01&amp;alpha=D&amp;beta=c5&amp;gamma=D5">D5</a></td></tr>
</table>


<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/aggr01.xml" type="text/plain">test/aggr01</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;spec=test.aggr01&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test.aggr01&amp;lang=en">more</a>

</body></html>
