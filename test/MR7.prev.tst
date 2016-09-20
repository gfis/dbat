<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<link rel="stylesheet" type="text/css" href="stylesheet.css" />
<title>Dbat Test of Validation Rules</title>
<style>
	td,th
	{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;padding-right:10px;border:none;}
</style>
</head>
<body>
<!-- language="en", fieldName="", regex=".+" -->
<h2><a href="index.html">Dbat</a> - Test of Validation Rules</h2>
In the fields below you may modify the entered value, and you may check whether it conforms
to the specified regular expression.
<form action="servlet" method="post">
<input type = "hidden" name="view" value="validate" />
<input type = "hidden" name="lang" value="en" />
<input type = "hidden" name="name" value="" />
<table cellpadding="8">
<tr><td>
Regular Expression:</td>
<td><input name="regex" class="valid" maxsize="64" size="16" value=".+" /></td>
	</tr>
	<tr><td>
Input Value:</td>
<td><input name="value" class="valid" maxsize="64" size="16" value="M" /></td>
</tr>
<tr><td><input type="submit" value="Match" />
<span class="valid"> &#xa0; -&gt; </span>
	</td>
	<td><span class="valid"> true</span></td>
</tr>
</table>
</form>
<p>
Regular Expressions are described in the Java API documentation: class <tt><a href="http://docs.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html">java.util.regex.Pattern</a></tt>.
</p>
Special characters and functions are escaped with a <em>single</em> backslash, like in Perl.
<br />
	This representation is needed for the <tt>valid="..."</tt> attribute
of the XML elements <tt>&lt;input&gt;, &lt;select&gt;</tt> and <tt>&lt;textarea&gt;</tt> in
<a href="index.html">Dbat</a> specifications.
<!-- language="en", features="quest" -->
<p><span style="font-size:small">
Questions, remarks: email to  <a href="mailto:punctum@punctum.com?&subject=Dbat">Dr. Georg Fischer</a></span></p>
</body></html>
