<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8" />
<meta name="robots" content="noindex, nofollow" />
<link rel="stylesheet" title="common" type="text/css" href="stylesheet.css" />
<title>Dbat help</title>
<script src="script.js" type="text/javascript">
</script>
</head>
<body>
<!--lang="en"-->
<h3><a href="index.html">Dbat</a> Commandline Options</h3>
<pre>
Dbat Vx.hhhh/yyyy-mm-dd - DataBase Application Tool
usage:
  java -jar dbat.jar [-acdfghlnrstvx] (table | "sql" | file | - | parameter ...)
  java org.teherba.dbat.Dbat "SELECT entry, morph FROM words"
  -29 table       SELECT * FROM table FETCH FIRST 29 ROWS ONLY
  -a  column      aggreate (concatenate) the values of this column
  -c  propfile    properties file with connection a.o. parameters
  -d  table       print DDL description (DROP/CREATE TABLE) for table
  -e  encoding    ISO-8859-1 (default), UTF-8 etc. (twice for input/output)
  -f  input.sql   process SQL statements from this file
  -f  spec.xml    test a web specification file (with -p parameters) 
  -g  columns     comma separated list of columns which cause a group change (new headings)
  -h              print this usage help text
  -i  table       print INSERT statements for "SELECT * FROM table"
  -l  l1,l2,...   define output column widths (for -m fix)
  -m  mode        output mode: tsv (TAB-separated values, default), csv (c.f. -s),
                  dbiv, echo, fix (c.f. -l), htm(l), jdbc, json, probe,
                  spec, sql, sqlj, taylor, trans, xls(x), xml
  -n  table       SELECT count(*) FROM table
  -p  name=val    optional parameter setting (repeatable)
  -r  table [url] load raw ([whitespace] separated) values from an URL (or from STDIN) into table
  -s  sep         separator string for mode csv
  -sa sep         separator string for -a aggregation
  -sp sep         separator string for CREATE PROCEDURE
  -t  table       table name (for -g)
  -v              verbose: print SQL statements and execution time
  -x              print no headings/summary
Options and actions are evaluated from left to right.
SQL statements must contain a space. Enclose them in double quotes.
Filenames may not contain spaces. '-' is STDIN.
Included JDBC drivers:
  SQLServerDriver:1 V8.2
  com.mysql.jdbc.Driver V5.1
Implemented output formats (-m):
  html      HTML
  xml       XML
  xlsx,xls  Microsoft Excel
  json      JSON
  wiki      MediaWiki Text
  csv,tsv   Separated Values
  fix       Fixed width columns
  taylor    File Tayloring
  sql       SQL INSERTs
  update    SQL UPDATEs
  jdbc      SQL with JDBC escapes
  trans     XML+XSLT
  gen       generate SAX events
  spec      Dbat Spec. File
  dbiv,view Dbiv Spec. File
  echo      Echo SQL
  sqlj      Generate SQLJ
  probe     Probe SQL
Email address: punctum@punctum.com

</pre>
<p><a href="servlet?view=more&lang=en">more ...</a></p>
<!-- language="en", features="quest" -->
<p><span style="font-size:small">
Questions, remarks: email to  <a href="mailto:punctum@punctum.com?&subject=Dbat">punctum@punctum.com</a></span></p>
</body></html>
