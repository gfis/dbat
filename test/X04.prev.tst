java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m html -p part=i -f ../web/spec/test/callproc.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>callproc</title>
<link rel="stylesheet" type="text/css" href="./../web/spec/test/stylesheet.css" />
</head><body>


	<!--Activation of a Stored Procedure-->
	<!--Aufruf einer Stored Procedure-->

    <h2>Call of Stored Procedure <em>x00</em></h2>
    <form method="get" action="servlet?spec=../web/spec/test/callproc.xml"><input name="spec" type="hidden" value="../web/spec/test/callproc.xml" />

        Part: <input name="part" init="" value="i"></input>
        <input type="submit" value="Submit"></input>
    </form>

    <table id="tab1"><!-- x00 -->
<tr><th>Name</th><th>Count</th><th>Last</th></tr>
<tr><td>i</td><td>3</td><td>Maria</td></tr>
</table>

<br />Output on 2012-06-15 06:56:59.497 by <a href="index.html">Dbat</a> script <a target="_blank" href="./../web/spec/test/callproc.xml.xml" type="text/plain">../web/spec/test/callproc.xml</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;part=i&amp;update_count=0&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;part=i&amp;update_count=0&amp;lang=en">more</a>

</body></html>
