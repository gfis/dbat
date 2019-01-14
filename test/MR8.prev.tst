<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<link rel="stylesheet" title="common" type="text/css" href="stylesheet.css" />
<title>Dbat Test d'une règle de validation</title>
<style>
	td,th
	{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;padding-right:10px;border:none;}
</style>
</head>
<body>
<!-- language="fr", fieldName="", regex=".+" -->
<h2><a href="index.html">Dbat</a> -  Test d'une règle de validation</h2>
Dans les champs suivants vous pouvez modifier et vérifier une valeur
si elle correspond à une éxpression régulière.
<form action="servlet" method="post">
<input type = "hidden" name="view" value="validate" />
<input type = "hidden" name="lang" value="fr" />
<input type = "hidden" name="name" value="" />
<table cellpadding="8">
<tr><td>
Éxpression régulière:</td>
<td><input name="regex" class="valid" maxsize="64" size="16" value=".+" /></td>
	</tr>
	<tr><td>
Valeur entrée:</td>
<td><input name="value" class="valid" maxsize="64" size="16" value="M" /></td>
</tr>
<tr><td><input type="submit" value="Vérifier" />
<span class="valid"> &#xa0; -&gt; </span>
	</td>
	<td><span class="valid"> true</span></td>
</tr>
</table>
</form>
<p>
La construction d'expressions régulières est décrite dans la documentation de l'API Java: Classe <tt><a href="http://docs.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html">java.util.regex.Pattern</a></tt>.
</p>
Caractères spéciaux et des fonctions spéciales sont annulés avec un <em>single</em> backslash - comme en Perl.
<br />Cette représentation est nécessaire pour l'attribut <tt>valid="..."</tt>
des éléments XML <tt>&lt;input&gt;, &lt;select&gt;</tt> and <tt>&lt;textarea&gt;</tt> dans les 
spécifications de <a href="index.html">Dbat</a>.
<!-- language="fr", features="quest" -->
<p><span style="font-size:small">
Questions, remarques: e-mail à  <a href="mailto:punctum@punctum.com?&subject=Dbat">punctum@punctum.com</a></span></p>
</body></html>
