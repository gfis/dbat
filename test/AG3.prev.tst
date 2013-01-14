<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>aggr01</title>
<link rel="stylesheet" type="text/css" href="./web/spec/test/stylesheet.css" />
</head><body>

	<!--Column Aggregation with linked values-->
	<!--Aggregierte Spalte mit Verweisen auf den Werten-->

    <h3>Column Aggregation test - with linked values</h3>
    
    <!-- SQL:
SELECT sp1
, concat(sp1, concat('=', concat(sp2, concat('=', sp3)))) 
FROM pivot;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="sp1">Application</th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))">sp2</th></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name="></a></td><td><a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c5&amp;gamma=S5">S1, S2, S3, S4, S5</a></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name=A">A</a></td><td><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c5&amp;gamma=A5">A1, A2, A3, A4, A5</a></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name=B">B</a></td><td><a href="servlet?spec=test/selec01&amp;alpha=B&amp;beta=c5&amp;gamma=B5">B1, B2, B3, B4, B5</a></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name=C">C</a></td><td><a href="servlet?spec=test/selec01&amp;alpha=C&amp;beta=c5&amp;gamma=C5">C1, C2, C3, C4, C5</a></td></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name=D">D</a></td><td><a href="servlet?spec=test/selec01&amp;alpha=D&amp;beta=c5&amp;gamma=D5">D1, D2, D3, D4, D5</a></td></tr>
</table>


<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="./web/spec/test/aggr01.xml" type="text/plain">web/spec/test/aggr01.xml</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;lang=en">more</a>

</body></html>
