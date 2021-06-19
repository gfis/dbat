<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>minusconn</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    <!--Spec without Connection Id-->
    
    <h3>Spec without Connection Id</h3>
    Current connection id: <strong><em>mysql</em></strong>
    <form method="get" action="servlet?spec=test/minusconn"><input name="spec" type="hidden" value="test/minusconn" />

        Table Name: <input name="tabname" maxsize="20" size="10" init="c01" value="c01"></input>
        <input name="conn" type="hidden" value="mysql"></input>
        <input type="submit" value="Submit"></input>
    </form>

    <!-- SQL:
SELECT count(*) 
FROM c01;
:SQL -->
<div>
<table id="table1">
<tr><th title="count(*)">Number of Rows</th></tr>
<tr><td>8</td></tr>
</table>
</div>


<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/minusconn.xml" type="text/plain">test/minusconn</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;conn=mysql&amp;spec=test%2Fminusconn&amp;tabname=c01&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;conn=mysql&amp;spec=test%2Fminusconn&amp;tabname=c01&amp;lang=en">more</a>

</body></html>
