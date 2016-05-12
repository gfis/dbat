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
enc="ISO-8859-1", mode="html", lang="en", spec="test.index" 
-->
<h3><a href="index.html">Dbat</a> Specification <em><a href="spec/test/index.xml">test.index</a></em>
</h3>
<form action="servlet" method="get">
<input type = "hidden" name="view" value="" />
<input type = "hidden" name="spec" value="test.index" />
<table cellpadding="8"><tr><td class="bold">
Parameter Name
</td><td class="bold">
Value</td>
<td>&nbsp;</td>
</tr>

<tr><td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<tr><td class="bold">Encoding</td>
<td class="bold">
Output Format</td>
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
Language</span>
<br />
<select name="lang" size="2">
<option value="de">Deutsch</option>
<option value="en"  selected="1">English</option>
</select>
</td>
<td>
<select name="mode" size="19">
<option value="csv">csv - Separated Values</option>
<option value="echo">echo - Echo SQL</option>
<option value="fix">fix - Fixed width columns</option>
<option value="gen">gen - generate SAX events</option>
<option value="html" selected="1">html - HTML</option>
<option value="jdbc">jdbc - SQL with JDBC escapes</option>
<option value="json">json - JSON</option>
<option value="probe">probe - Probe SQL</option>
<option value="spec">spec - Default Spec. File</option>
<option value="sql">sql - SQL INSERTs</option>
<option value="sqlj">sqlj - Generate SQLJ</option>
<option value="taylor">taylor - File Tayloring</option>
<option value="trans">trans - XML+XSLT</option>
<option value="tsv">tsv - Separated Values</option>
<option value="update">update - SQL UPDATEs</option>
<option value="wiki">wiki - MediaWiki Text</option>
<option value="xls">xls - Excel</option>
<option value="xlsx">xlsx - Excel</option>
<option value="xml">xml - XML</option>
</select><p />&nbsp;
</td>
<td>
<a href="index.html">Dbat Home</a><br />
<a href="servlet?spec=index">List</a> of available specifications<br />
<a href="servlet?view=con">SQL Console<br />
<a href="servlet?view=validate&value=M&regex=\w">Regex Validation</a><br />
<a href="servlet?spec=describe">describe</a> - DDL of a table or view
<br />
<a href="servlet?view=help&lang=en">Help</a> - Commandline Options<br />
<a href="https://github.com/gfis/dbat/wiki" target="_new">Wiki</a>, <a href="https://github.com/gfis/dbat" target="_new">Git Repository</a> on github.com<br />
<a href="docs/api/index.html">API Documentation</a> (Javadoc)
<br />
<a href="servlet?view=manifest">Manifest</a>, <a href="servlet?view=license">License</a>, <a href="servlet?view=notice"  >References</a><br />
<input type="submit" value="Submit" />
</td></tr>
</table>
</form>
<p><span style="font-size:small">
Questions, remarks: <a href="mailto:punctum@punctum.com">Dr. Georg Fischer</a></span>
</p>
</body>
</html>
