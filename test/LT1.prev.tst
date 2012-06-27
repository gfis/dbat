wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/textarea"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>textarea</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>


	<!--Test of an input text area-->
	<!--Test eines Eingabe-Textbereichs-->

    <h2>Textarea Test</h2>
    <form method="get" action="servlet?spec=test/textarea"><input name="spec" type="hidden" value="test/textarea" />

        Word List: <textarea name="wordlist" cols="20" rows="5" init="alpha beta gamma
delta tau omega" value="alpha beta gamma
delta tau omega">alpha beta gamma
delta tau omega</textarea>    
        UPPER CASE: <textarea name="WORDLIST" cols="20" rows="5" init="epsilon my ny
rho zeta chi" value="epsilon my ny
rho zeta chi">epsilon my ny
rho zeta chi</textarea>    
        <input type="submit" value="Submit"></input>
    </form>

	<h4>Word List:  alpha beta gamma
delta tau omega</h4>
	<h4>UPPER CASE: EPSILON MY NY
RHO ZETA CHI</h4>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/textarea.xml" type="text/plain">test/textarea</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;spec=test%2Ftextarea&amp;WORDLIST=epsilon+my+ny%0Arho+zeta+chi&amp;lang=en&amp;wordlist=alpha+beta+gamma%0Adelta+tau+omega">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;spec=test%2Ftextarea&amp;WORDLIST=epsilon+my+ny%0Arho+zeta+chi&amp;lang=en&amp;wordlist=alpha+beta+gamma%0Adelta+tau+omega">more</a>

</body></html>
