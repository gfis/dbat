wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/grouping"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>grouping</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>


	<!--Test multiple header lines for control exchanges-->
	<!--Test der wiederholten Überschriften beim Gruppenwechsel-->

    <h3>Grouping Test</h3>
    <!-- SQL:
SELECT sp1
, sp2
, sp3 
FROM pivot 
ORDER BY 1,2,3;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="sp1">Column 1</th><th title="sp2">Column 2</th><th title="sp3">Column 3</th></tr>
<tr><td></td><td>c1</td><td>S1</td></tr>
<tr><td></td><td>c2</td><td>S2</td></tr>
<tr><td></td><td>c3</td><td>S3</td></tr>
<tr><td></td><td>c4</td><td>S4</td></tr>
<tr><td></td><td>c5</td><td>S5</td></tr>
<tr><th title="sp1">Column 1</th><th title="sp2">Column 2</th><th title="sp3">Column 3</th></tr>
<tr><td>A</td><td>c1</td><td>A1</td></tr>
<tr><td>A</td><td>c2</td><td>A2</td></tr>
<tr><td>A</td><td>c3</td><td>A3</td></tr>
<tr><td>A</td><td>c4</td><td>A4</td></tr>
<tr><td>A</td><td>c5</td><td>A5</td></tr>
<tr><th title="sp1">Column 1</th><th title="sp2">Column 2</th><th title="sp3">Column 3</th></tr>
<tr><td>B</td><td>c1</td><td>B1</td></tr>
<tr><td>B</td><td>c2</td><td>B2</td></tr>
<tr><td>B</td><td>c3</td><td>B3</td></tr>
<tr><td>B</td><td>c4</td><td>B4</td></tr>
<tr><td>B</td><td>c5</td><td>B5</td></tr>
<tr><th title="sp1">Column 1</th><th title="sp2">Column 2</th><th title="sp3">Column 3</th></tr>
<tr><td>C</td><td>c1</td><td>C1</td></tr>
<tr><td>C</td><td>c2</td><td>C2</td></tr>
<tr><td>C</td><td>c3</td><td>C3</td></tr>
<tr><td>C</td><td>c4</td><td>C4</td></tr>
<tr><td>C</td><td>c5</td><td>C5</td></tr>
<tr><th title="sp1">Column 1</th><th title="sp2">Column 2</th><th title="sp3">Column 3</th></tr>
<tr><td>D</td><td>c1</td><td>D1</td></tr>
<tr><td>D</td><td>c2</td><td>D2</td></tr>
<tr><td>D</td><td>c3</td><td>D3</td></tr>
<tr><td>D</td><td>c4</td><td>D4</td></tr>
<tr><td>D</td><td>c5</td><td>D5</td></tr>
<tr><td class="counter" colspan="3">25 rows</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/grouping.xml" type="text/plain">test/grouping</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Fgrouping&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Fgrouping&amp;lang=en">more</a>

</body></html>
