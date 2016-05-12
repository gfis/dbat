<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<link rel="stylesheet" type="text/css" href="stylesheet.css" />
<title>Database Administration Tool</title>
<style>
td,th
{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;padding-right:10px;border:none;}
</style>
<script src="script.js" type="text/javascript">
</script>
</head>
<body>
<!--
enc="ISO-8859-1", mode="html", lang="de" 
-->
<h3><a href="index.html">Dbat</a>-Konsole </h3>
<form action="servlet" method="get">
<input type = "hidden" name="view" value="con2" />
<table cellpadding="8">
<tr><td class="bold">Codierung</td>
<td class="bold">Sprache</td>
<td class="bold">
Ausgabeformat</td>
<td class="bold">Verbindung</td>
</tr>
<tr valign="top">
<td>
<select name="enc" size="2">
<option value="ISO-8859-1"  selected="1">ISO-8859-1</option>
<option value="UTF-8">UTF-8</option>
</select>
<p /><span class="bold">Max. Zeilen</span><br />
<input type="text" name="fetch" size="8" value="64" />
</td>
<td>
<select name="lang" size="2">
<option value="de"  selected="1">Deutsch</option>
<option value="en">English</option>
</select>
</td>
<td>
<select name="mode" size="6">
<option value="html" selected="1">html - HTML</option>
<option value="xlsx">xlsx - Excel</option>
<option value="xml">xml - XML</option>
<option value="fix">fix - Spalten fester Breite</option>
<option value="csv">csv - Werte mit Trennzeichen</option>
<option value="sql">sql - SQL INSERTs</option>
<option value="update">update - SQL UPDATEs</option>
<option value="jdbc">jdbc - SQL mit JDBC-Escapes</option>
<option value="json">json - JSON</option>
<option value="spec">spec - Default-Spezifikation</option>
<option value="taylor">taylor - Variablenersetzung</option>
<option value="trans">trans - XML+XSLT</option>
<option value="gen">gen - generate SAX events</option>
<option value="wiki">wiki - MediaWiki-Text</option>
<option value="echo">echo - Echo SQL</option>
<option value="sqlj">sqlj - SQLJ-Generator</option>
<option value="probe">probe - SQL-Syntaxtest</option>
</select>
</td>
<td>
<select name="conn" size="3">
<option value="mysql" selected="1">mysql</option>
<option value="worddb">worddb</option>
<option value="wiki">wiki</option>
</select>
<p /><input type="submit" value="Absenden" />
</td>
</tr>
<tr>
<td class="bold" colspan="4">
SQL
</td>
</tr>
<tr>
<td colspan="4">
<textarea name="intext" cols="80" rows="32"></textarea>
</td>
</tr>
</table>
</form>
</body>
</html>
