<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>parmform</title>
<link rel="stylesheet" type="text/css" href="./web/spec/test/stylesheet.css" />
</head><body>

    <!--Test of the format="..." attribute in &lt;parm&gt;-->
    <!--Test des format-Attributs in in &lt;parm&gt;-->

    <h3>Test of the format= attribute in &lt;parm&gt;</h3>

    &lt;parm name="VALUE" /&gt; THAT+IS+THE+1ST+VALUE<br />
    &lt;parm name="value" /&gt; That+is+the+2nd+value<br />
    &lt;parm name="value" format="lower" /&gt; that+is+the+2nd+value<br />
    &lt;parm name="value" format="upper" /&gt; THAT+IS+THE+2ND+VALUE<br />
    &lt;parm name="VALUE" format="lower" /&gt; that+is+the+1st+value<br />
    &lt;parm name="VALUE" format="upper" /&gt; THAT+IS+THE+1ST+VALUE<br />
    &lt;parm name="VALUE" format="none"  /&gt; That+is+the+1st+value<br />
<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="./web/spec/test/parmform.xml" type="text/plain">web/spec/test/parmform.xml</a>,
<a target="_blank" href="servlet?&amp;mode=xlsx&amp;VALUE=That%2Bis%2Bthe%2B1st%2Bvalue&amp;value=That%2Bis%2Bthe%2B2nd%2Bvalue&amp;lang=en">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;VALUE=That%2Bis%2Bthe%2B1st%2Bvalue&amp;value=That%2Bis%2Bthe%2B2nd%2Bvalue&amp;lang=en">more</a>

</body></html>
