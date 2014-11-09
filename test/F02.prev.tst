<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>parmform</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

	<!--Test of the format= attribute in &lt;parm&gt;-->
	<!--Test des format-Attributs in in &lt;parm&gt;-->

    <h3>Test of the format= attribute in &lt;parm&gt;</h3>
	
	&lt;parm name="VALUE" /&gt; HIER IST DER 1.WERT<br />
	&lt;parm name="value" /&gt; Hier ist der 1.Wert<br />
	&lt;parm name="value" format="lower" /&gt; hier ist der 1.wert<br />
	&lt;parm name="value" format="upper" /&gt; HIER IST DER 1.WERT<br />
	&lt;parm name="VALUE" format="lower" /&gt; hier ist der 1.wert<br />
	&lt;parm name="VALUE" format="upper" /&gt; HIER IST DER 1.WERT<br />
<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/parmform.xml" type="text/plain">test/parmform</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;VALUE=Hier+ist+der+1.Wert&amp;spec=test.parmform&amp;value=Hier+ist+der+1.Wert&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;VALUE=Hier+ist+der+1.Wert&amp;spec=test.parmform&amp;value=Hier+ist+der+1.Wert&amp;lang=en">more</a>

</body></html>
