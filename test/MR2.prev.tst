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
enc="ISO-8859-1", mode="html", lang="de", spec="test.index" 
-->
<h3><a href="index.html">Dbat</a>-Spezifikation <em><a href="spec/test/index.xml">test.index</a></em>
</h3>
<form action="servlet" method="post">
<input type = "hidden" name="view" value="" />
<input type = "hidden" name="spec" value="test.index" />
<table cellpadding="8"><tr><td class="bold">
Parametername
</td><td class="bold">
Wert</td>
<td>&nbsp;</td>
</tr>

<tr><td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<tr><td class="bold">Encoding</td>
<td class="bold">
Ausgabeformat</td>
<td>&nbsp;</td>
</tr>
<tr valign="top">
<td>
<select name="enc" size="2">
<option value="ISO-8859-1"  selected="1">ISO-8859-1</option>
<option value="UTF-8">UTF-8</option>
</select>
<br />
<br />
<span class="bold">
Sprache</span>
<br />
<select name="lang" size="2">
<option value="de"  selected="1">Deutsch</option>
<option value="en">English</option>
</select>
</td>
<td>
<select name="mode" size="19">
<option value="csv">csv - Werte mit Trennzeichen</option>
<option value="echo">echo - Echo SQL</option>
<option value="fix">fix - Spalten fester Breite</option>
<option value="gen">gen - generate SAX events</option>
<option value="html" selected="1">html - HTML</option>
<option value="jdbc">jdbc - SQL mit JDBC-Escapes</option>
<option value="json">json - JSON</option>
<option value="probe">probe - SQL-Syntaxtest</option>
<option value="spec">spec - Default-Spezifikation</option>
<option value="sql">sql - SQL INSERTs</option>
<option value="sqlj">sqlj - SQLJ-Generator</option>
<option value="taylor">taylor - Variablenersetzung</option>
<option value="trans">trans - XML+XSLT</option>
<option value="tsv">tsv - Werte mit Trennzeichen</option>
<option value="update">update - SQL UPDATEs</option>
<option value="wiki">wiki - MediaWiki-Text</option>
<option value="xls">xls - Excel</option>
<option value="xlsx">xlsx - Excel</option>
<option value="xml">xml - XML</option>
</select><p />&nbsp;
</td>
<td>
<a href="index.html">Dbat-Startseite</a><br />
<a href="servlet?spec=index">Liste</a> der abrufbaren Spezifikationen<br />
<a href="servlet?view=con">SQL-Konsole<br />
<a href="servlet?view=validate&value=M&regex=\w">Regex-Validierung</a><br />
<a href="servlet?spec=describe">describe</a> - DDL einer Tabelle oder View
<br />
<a href="servlet?view=help&lang=de">Hilfe</a> - Kommandozeilen-Optionen<br />
<a href="https://github.com/gfis/dbat/wiki" target="_new">Wiki</a>, <a href="https://github.com/gfis/dbat" target="_new">Git Repository</a> auf github.com<br />
<a href="docs/api/index.html">API-Dokumentation</a> (Javadoc)
<br />
<a href="servlet?view=manifest">Manifest</a>, <a href="servlet?view=license">Lizenz</a>, <a href="servlet?view=notice"  >Referenzen</a><br />
<input type="submit" value="Absenden" />
</td></tr>
</table>
</form>
<p><span style="font-size:small">
Fragen, Hinweise: <a href="mailto:punctum@punctum.com">Dr. Georg Fischer</a></span>
</p>
</body>
</html>
