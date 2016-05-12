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
<tr><td><a href="servlet?spec=test/selec01&amp;name="></a></td><td><a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c1&amp;gamma=S1">S1</a>, <a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c2&amp;gamma=S2">S2</a>, <a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c3&amp;gamma=S3">S3</a></td></tr>
</table>


<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/aggr01.xml" type="text/plain">test/aggr01</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;fetch=3&amp;spec=test.aggr01&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;fetch=3&amp;spec=test.aggr01&amp;lang=en">more</a>

</body></html>
