<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<link rel="stylesheet" title="common" type="text/css" href="stylesheet.css" />
<title>Dbat more</title>
<style>
td,th
{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;padding-right:10px;border:none;}
</style>
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
<tr><td class="bold">Kodierung</td>
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
<select name="lang" size="3">
<option value="de"  selected="1">Deutsch</option>
<option value="en">English</option>
<option value="fr">Français</option>
</select>
</td>
<td>
<select name="mode" size="21">
<option value="csv">csv - Werte mit Trennzeichen</option>
<option value="dbiv">dbiv - Dbiv-Spezifikation</option>
<option value="echo">echo - Echo SQL</option>
<option value="fix">fix - Spalten fester Breite</option>
<option value="gen">gen - SAX-Event-Generator</option>
<option value="html" selected="1">html - HTML</option>
<option value="jdbc">jdbc - SQL mit JDBC-Escapes</option>
<option value="json">json - JSON</option>
<option value="probe">probe - SQL-Syntaxtest</option>
<option value="spec">spec - Dbat-Spezifikation</option>
<option value="sql">sql - SQL INSERTs</option>
<option value="sqlj">sqlj - SQLJ-Generator</option>
<option value="taylor">taylor - Variablenersetzung</option>
<option value="trans">trans - XML+XSLT</option>
<option value="tsv">tsv - Werte mit Trennzeichen</option>
<option value="update">update - SQL UPDATEs</option>
<option value="view">view - Dbiv-Spezifikation</option>
<option value="wiki">wiki - MediaWiki-Text</option>
<option value="xls">xls - Microsoft Excel</option>
<option value="xlsx">xlsx - Microsoft Excel</option>
<option value="xml">xml - XML</option>
</select><p />&nbsp;
</td>
<td>
<a title="index"       href="servlet?spec=index">Liste</a> der abrufbaren Spezifikationen<br />
<a title="con"         href="servlet?view=con">SQL-Konsole</a><br />
<a title="describe"    href="servlet?spec=describe">describe </a> - DLL einer Tabelle oder View<br />
<a title="help"        href="servlet?view=help&lang=en">Hilfe</a> - Kommandozeilen-Optionen<br />
<a title="validate"    href="servlet?view=validate&lang=en">Validierung</a> von regulären Ausdrücken<br />
<a title="main" href="index.html">Dbat</a>-Startseite<br />
<a title="wiki"        href="http://www.teherba.org/index.php/Dbat" target="_new">Wiki</a>-Dokumentation<br />
<a title="github"      href="https://github.com/gfis/dbat" target="_new">Git Repository</a><br />
<a title="api"         href="docs/api/index.html">Java API</a>-Dokumentation<br />
<a title="manifest"    href="servlet?view=manifest&lang=de">Manifest</a>, <a title="license"     href="servlet?view=license&lang=de">Lizenz</a>, <a title="notice"      href="servlet?view=notice&lang=de">Referenzen</a><br />
<br /><input type="submit" value="Absenden" />
</td></tr>
</table>
</form>
<!-- language="de", features="quest" -->
<p><span style="font-size:small">
Fragen, Hinweise: EMail an  <a href="mailto:punctum@punctum.com?&subject=Dbat">punctum@punctum.com</a></span></p>
</body></html>
