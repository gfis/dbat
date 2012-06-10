java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m html -f ../web/spec/test/badxml.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" [
]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<title>crud01</title>
<link rel="stylesheet" type="text/css" href="./../web/spec/test/stylesheet.css" />
</head><body>
2012-06-01 22:56:13,315 0    [main] ERROR org.teherba.xtrans.BaseTransformer  - BaseTransformer: The content of elements must consist of well-formed character data or markup.
 
	<!--Show entries for editing-->
	<!--Anzeige der editierbaren Einträge-->
	
    <h3>Specification with XML Syntax Error</h3>
    <form method="get" action="servlet?spec=../web/spec/test/badxml.xml"><input name="spec" type="hidden" value="../web/spec/test/badxml.xml" />

        Name: <input name="name" maxsize="20" size="10" init="%" value="%"></input>
        <input type="submit" value="Show"></input>  
        <a href="servlet?spec=test.ins1">Neuer Eintrag</a>
    </form>
    <h3 class="error">XML SAX parsing error: The content of elements must consist of well-formed character data or markup. in Dbat specification, line 29, column 10, cause: null</h3></body></html>
2012-06-01 22:56:13,318 3    [main] ERROR org.teherba.dbat.Dbat  - The content of elements must consist of well-formed character data or markup.
 org.xml.sax.SAXParseException; lineNumber: 29; columnNumber: 10; The content of elements must consist of well-formed character data or markup.
	at com.sun.org.apache.xerces.internal.parsers.AbstractSAXParser.parse(AbstractSAXParser.java:1234)
	at com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl$JAXPSAXParser.parse(SAXParserImpl.java:525)
	at javax.xml.parsers.SAXParser.parse(SAXParser.java:392)
	at org.teherba.dbat.Dbat.parseXML(Dbat.java:228)
	at org.teherba.dbat.Dbat.processXMLFile(Dbat.java:271)
	at org.teherba.dbat.Dbat.process(Dbat.java:606)
	at org.teherba.dbat.Dbat.processArguments(Dbat.java:694)
	at org.teherba.dbat.Dbat.main(Dbat.java:714)
2012-06-01 22:56:13,319 4    [main] ERROR org.teherba.dbat.Dbat  - The content of elements must consist of well-formed character data or markup.
 org.xml.sax.SAXParseException; lineNumber: 29; columnNumber: 10; The content of elements must consist of well-formed character data or markup.
	at com.sun.org.apache.xerces.internal.parsers.AbstractSAXParser.parse(AbstractSAXParser.java:1234)
	at com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl$JAXPSAXParser.parse(SAXParserImpl.java:525)
	at javax.xml.parsers.SAXParser.parse(SAXParser.java:392)
	at org.teherba.dbat.Dbat.parseXML(Dbat.java:228)
	at org.teherba.dbat.Dbat.processXMLFile(Dbat.java:271)
	at org.teherba.dbat.Dbat.process(Dbat.java:606)
	at org.teherba.dbat.Dbat.processArguments(Dbat.java:694)
	at org.teherba.dbat.Dbat.main(Dbat.java:714)
