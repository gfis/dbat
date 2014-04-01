<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>parmlink</title>
<link rel="stylesheet" type="text/css" href="./web/spec/test/stylesheet.css" />
</head><body>

    <!--Test of an HTML Link with Parameter Substitution-->
    <!--Test eines HTML-Links mit Parameter-Ersetzung-->

    <h3>Select from test table c01 - with alignment</h3>
    <form method="get" action="servlet?spec=web/spec/test/parmlink.xml"><input name="spec" type="hidden" value="web/spec/test/parmlink.xml" />

        Name: <input name="name" maxsize="20" size="10" init="M" value="M"></input>%   
        Year: <input name="year" maxsize="20" size="10" init="1995" value="1995"></input>   
        <input type="submit" value="Submit"></input>
    </form>

    
    <h3><a href="servlet?spec=test.align01&amp;name=M&amp;year=1995">1995</a>
    </h3>
<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="./web/spec/test/parmlink.xml" type="text/plain">web/spec/test/parmlink.xml</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;lang=en&amp;name=M&amp;year=1995">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;lang=en&amp;name=M&amp;year=1995">more</a>

</body></html>
