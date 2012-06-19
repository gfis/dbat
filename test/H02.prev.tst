java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 
Dbat V8.5be5 - DataBase Application Tool
usage:
  java -jar dbat.jar [-acdfghlnrstvx] (table | "sql" | file | - | parameter ...)
  java org.teherba.dbat.Dbat "SELECT entry, morph FROM words"
  -29 table       SELECT * FROM table FETCH FIRST 29 ROWS ONLY
  -a  column      aggreate (concatenate) the values of this column
  -c  propfile    properties file with connection a.o. parameters
  -d  table       print DDL description (DROP/CREATE TABLE) for table
  -e  encoding    ISO-8859-1 (default), UTF-8 etc. (twice for input/output)
  -f  inputfile   process SQL statements from this file
  -f  spec.xml    test a web specification file (with -p parameters) 
  -g  columns     comma separated list of columns which cause a group change (new headings)
  -h              print this usage help text
  -i  table       print INSERT statements for "SELECT * FROM table"
  -l  l1,l2,...   define output column widths (for -m fix)
  -m  mode        output mode: tsv (TAB-separated values, default),
			csv (c.f. -s), echo, fix (c.f. -l), htm(l), jdbc, json, probe, spec, sql, sqlj, taylor, trans, xls, xml
  -n  table       SELECT count(*) FROM table
  -p  name=val    optional parameter setting (repeatable)
  -r  table       insert raw ([whitespace] separated) values from STDIN into table
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
  com.mysql.jdbc.Driver 5.1
  com.ibm.db2.jcc.DB2Driver 4.8
  org.sqlite.JDBC 3.6
Implemented output formats (-m):
  html	HTML
  xls	Excel
  xml	XML
  fix	Fixed width columns
  csv,tsv	Separated Values
  sql	SQL INSERTs
  update	SQL UPDATEs
  jdbc	SQL with JDBC escapes
  json	JSON
  spec	Default Spec. File
  taylor	File Tayloring
  trans	XML+XSLT
  gen	generate SAX events
  wiki	MediaWiki Text
  echo	Echo SQL
  sqlj	Generate SQLJ
  probe	Probe SQL

