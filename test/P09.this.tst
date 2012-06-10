wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/pivot03&fetch=10"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>pivot03</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>


	<!--Pivot matrix output with linked values-->
	<!--Ausgabe einer Pivot-Matrix mit Verweisen auf den Werten-->

    <h3>Pivot matrix test - with linked values</h3>
    <form method="get" action="servlet?spec=test/pivot03"><input name="spec" type="hidden" value="test/pivot03" />

        Name: <input name="name" maxsize="20" size="10" init="%r" value="%r"></input>
        <input type="submit" value="Submit"></input>
    </form>
    
    <!-- SQL:
SELECT sp1
, sp2
, concat(sp1, concat('=', concat(sp2, concat('=', sp3)))) 
FROM pivot
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="sp1">Anwendung</th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))">S1</th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))">S2</th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))">S3</th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))">S4</th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))">S5</th></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name=A">A</a></td><td align="right"><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c1&amp;sp3=A1">A1</a></td><td align="right"><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c2&amp;sp3=A2">A2</a></td><td align="right"><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c3&amp;sp3=A3">A3</a></td><td align="right"><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c4&amp;sp3=A4">A4</a></td><td align="right"><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c5&amp;sp3=A5">A5</a></td></tr>
</table>


<br />Output on 2012-06-01 22:56:44.763 by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/pivot03.xml" type="text/plain">test/pivot03</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Fpivot03&amp;fetch=10&amp;name=%25r&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fpivot03&amp;fetch=10&amp;name=%25r&amp;lang=en">more</a>

</body></html>
