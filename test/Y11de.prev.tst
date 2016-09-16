<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>crud01</title>
<link rel="stylesheet" type="text/css" href="spec/test/stylesheet.css" />
</head><body>

	<!--Show entries for editing-->
	<!--Anzeige der editierbaren Einträge-->
	
    <h3>Specification with XML Syntax Error</h3>
    <form method="get" action="servlet?spec=test/badxml"><input name="spec" type="hidden" value="test/badxml" />

        Name: <input name="name" maxsize="20" size="10" init="%" value="%"></input>
        <input type="submit" value="Show"></input>  
        <a href="servlet?spec=test.ins1">Neuer Eintrag</a>
    </form>
    <h3 class="error">XML SAX parsing error: Der Content von Elementen muss aus ordnungsgemäß formatierten Zeichendaten oder Markups bestehen. in Dbat specification, line 29, column 10, cause: null</h3></body></html>
