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
FROM pivot;
:SQL -->
<table id="table1">
<tr><th title="sp1">Application</th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))"><a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c1&amp;sp3=S1">S1</a></th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))"><a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c2&amp;sp3=S2">S2</a></th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))"><a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c3&amp;sp3=S3">S3</a></th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))"><a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c4&amp;sp3=S4">S4</a></th><th title="concat(sp1, concat('=', concat(sp2, concat('=', sp3))))"><a href="servlet?spec=test/selec01&amp;alpha=&amp;beta=c5&amp;sp3=S5">S5</a></th></tr>
<tr><td><a href="servlet?spec=test/selec01&amp;name=A">A</a></td><td align="right"><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c1&amp;sp3=A1">A1</a></td><td align="right"><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c2&amp;sp3=A2">A2</a></td><td align="right"><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c3&amp;sp3=A3">A3</a></td><td align="right"><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c4&amp;sp3=A4">A4</a></td><td align="right"><a href="servlet?spec=test/selec01&amp;alpha=A&amp;beta=c5&amp;sp3=A5">A5</a></td></tr>
</table>


<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/pivot03.xml" type="text/plain">test/pivot03</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;fetch=10&amp;spec=test%2Fpivot03&amp;lang=en&amp;name=%25r">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;fetch=10&amp;spec=test%2Fpivot03&amp;lang=en&amp;name=%25r">more</a>

</body></html>
