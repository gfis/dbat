<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<link rel="stylesheet" title="common" type="text/css" href="stylesheet.css" />
<title>Dbat SQL Console</title>
<style>
td,th
{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;padding-right:10px;border:none;}
</style>
<script src="script.js" type="text/javascript">
</script>
</head>
<body>
<!--enc="ISO-8859-1", mode="html", lang="en"-->
<h3><a title="main" href="index.html">Dbat</a> SQL Console</h3>
<form action="servlet" method="get">
<input type = "hidden" name="view" value="con2" />
<table cellpadding="8">
<tr><td class="bold">Encoding</td>
<td class="bold">Language</td>
<td class="bold">
Output Format</td>
<td class="bold">Connection</td>
</tr>
<tr valign="top">
<td>
<select name="enc" size="2">
<option value="ISO-8859-1"  selected="1">ISO-8859-1</option>
<option value="UTF-8">UTF-8</option>
</select>
<p /><span class="bold">Fetch Limit</span><br />
<input type="text" name="fetch" size="8" value="64" />
</td>
<td>
<select name="lang" size="2">
<option value="de">Deutsch</option>
<option value="en"  selected="1">English</option>
</select>
</td>
<td>
<select name="mode" size="6">
<option value="html" selected="1">html - HTML</option>
<option value="xml">xml - XML</option>
<option value="xlsx">xlsx - Microsoft Excel</option>
<option value="json">json - JSON</option>
<option value="wiki">wiki - MediaWiki Text</option>
<option value="csv">csv - Separated Values</option>
<option value="fix">fix - Fixed width columns</option>
<option value="taylor">taylor - File Tayloring</option>
<option value="sql">sql - SQL INSERTs</option>
<option value="update">update - SQL UPDATEs</option>
<option value="jdbc">jdbc - SQL with JDBC escapes</option>
<option value="trans">trans - XML+XSLT</option>
<option value="gen">gen - generate SAX events</option>
<option value="spec">spec - Dbat Spec. File</option>
<option value="dbiv">dbiv - Dbiv Spec. File</option>
<option value="echo">echo - Echo SQL</option>
<option value="sqlj">sqlj - Generate SQLJ</option>
<option value="probe">probe - Probe SQL</option>
</select>
</td>
<td>
<select name="conn" size="2">
<option value="mysql" selected="1">mysql:SELECT</option>
<option value="worddb">worddb:UPDATE</option>
</select>
<p /><input type="submit" value="Submit" />
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
<!-- language="en", features="back.quest" -->
<p>
Back to the <a title="main" href="index.html">Dbat</a> main page</p>
<p><span style="font-size:small">
Questions, remarks: email to  <a href="mailto:Dr.Georg.Fischer@gmail.com?&subject=Dbat">Dr.Georg.Fischer@gmail.com</a></span></p>
</body></html>
