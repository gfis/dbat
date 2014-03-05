<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>test/var02</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

    <!--Test of the var element with data types-->
    <!--Test des var-Elements mit Datentypen-->
            
    <h2><a href="servlet?spec=test.index">Test</a> - Test of a <var> element with data types</h2>
    <form method="get" action="servlet?spec=test/var02"><input name="spec" type="hidden" value="test/var02" />

        Name: <input name="name" maxsize="20" size="10" init="A" value="M"></input><br />
        Birthdate: <input name="birth" maxsize="20" size="10" init="1800-01-01" value="1800-01-01"></input><br />
        Decease: <input name="decease" maxsize="20" size="10" init="1800" value="1800"></input><br />
        Changed: <input name="changed" maxsize="20" size="10" init="1900-01-01 00:00:00" value="1900-01-01 00:00:00"></input><br />
        <input type="submit" value="Submit"></input>
    </form>

    <!-- SQL:
SELECT name
, family
, gender
, birth
, decease 
FROM relatives 
WHERE name    >=  ? 
            and birth   >=  ? 
            and decease >=  ? 
            and changed >=  ? 
ORDER BY 1;
:SQL -->
<table id="tab1"><!-- table_not_specified -->
<tr><th title="name">Name</th><th title="family">Family</th><th title="gender">Gender</th><th title="birth">Birthdate</th><th title="decease">Decease</th></tr>
<tr><td>Maria</td><td>Ritter</td><td align="center">F</td><td>1914-09-17</td><td>1999</td></tr>
<tr><td>Martha</td><td>Fischer</td><td align="center">F</td><td>1909-11-17</td><td>1999</td></tr>
<tr><td>Teherba</td><td>Ritter</td><td align="center">F</td><td>1886-02-04</td><td>1968</td></tr>
</table>

<br />Output on yyyy-mm-dd hh:mm:ss by <a href="index.html">Dbat</a> script <a target="_blank" href="spec/test/var02.xml" type="text/plain">test/var02</a>,
<a target="_blank" href="servlet?&amp;mode=xls&amp;name=M&amp;spec=test.var02&amp;lang=en&amp;birth=1800-01-01&amp;decease=1800&amp;changed=1900-01-01+00%3A00%3A00">Excel</a>,
<a href="servlet?&amp;view=more&amp;mode=html&amp;name=M&amp;spec=test.var02&amp;lang=en&amp;birth=1800-01-01&amp;decease=1800&amp;changed=1900-01-01+00%3A00%3A00">more</a>

</body></html>
