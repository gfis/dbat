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
<!--lang="de"-->
<h3><a href="index.html">Dbat</a>-Optionen auf der Kommandzeile</h3>
<pre>
Dbat Vx.hhhh/yyyy-mm-dd - DataBase Application Tool
Aufruf:
  java -jar dbat.jar [-acdfghlnrstvx] (table | "sql" | file | - | parameter ...)
  java org.teherba.dbat.Dbat "SELECT entry, morph FROM words"
  -29 table       SELECT * FROM table FETCH FIRST 29 ROWS ONLY
  -a  column      Zusammenfassung der Werte dieser Spalte pro Gruppe in einer Zeile
  -c  propfile    Properties-Datei mit Verbindungs- u.a. Parametern
  -d  table       DDL-Beschreibung (DROP/CREATE TABLE) der Tabelle ausgeben
  -e  encoding    ISO-8859-1 (default), UTF-8 etc. (2 x fuer Ein-/Ausgabe)
  -f  input.sql   SQL-Anweisungen in dieser Datei ausfuehren
  -f  spec.xml    Test einer Web-Spezifikationsdatei (mit Parametern nach -p) 
  -g  columns     Liste von Spalten fuer einen Gruppenwechsel (mit neuen Ueberschriften)
  -h              Ausgabe dieses Hilfetexts
  -i  table       INSERT-Anweisungen fuer "SELECT * FROM table" ausgeben
  -l  l1,l2,...   Definition der Spalten-Breiten bei -m fix
  -m  mode        Ausgabe-Modus: tsv (TAB-getrennt, Default), csv (vgl. -s),
                  dbiv, echo, fix (vgl. -l), htm(l), jdbc, json, probe,
                  spec, sql, sqlj, taylor, trans, xls(x), xml
  -n  table       SELECT count(*) FROM table
  -p  name=val    Parameterwert setzen (wiederholbar)
  -r  table [url] Roh-Werte (durch Leerraum getrennt) von URL (oder von STDIN) in table laden
  -s  sep         Trennzeichen(kette) fuer Modus csv
  -sa sep         Trennzeichen fuer Zusammenfassung mit -a
  -sp sep         Trennzeichen fuer CREATE PROCEDURE
  -t  table       Tabellenname (fuer -g)
  -v              SQL-Anweisungen und Ausfuehrungszeiten ausgeben
  -x              Keine Spaltenueberschriften und Zeilenanzahl ausgeben
Optionen und Aktionen werden von links nach rechts abgearbeitet.
SQL-Anweisungen muessen ein Leerzeichen enthalten und in doppelte Anf.zeichen eingeschlossen werden. Dateinamen duerfen keine Leerzeichen enthalten. '-' ist STDIN.
Eingebundene JDBC-Treiber:
  SQLServerDriver:1 V8.2
  com.mysql.jdbc.Driver V5.1
Implementierte Ausgabeformate (-m):
  html      HTML
  xml       XML
  xlsx,xls  Microsoft Excel
  json      JSON
  wiki      MediaWiki-Text
  csv,tsv   Werte mit Trennzeichen
  fix       Spalten fester Breite
  taylor    Variablenersetzung
  sql       SQL INSERTs
  update    SQL UPDATEs
  jdbc      SQL mit JDBC-Escapes
  trans     XML+XSLT
  gen       SAX-Event-Generator
  spec      Dbat-Spezifikation
  dbiv,view Dbiv-Spezifikation
  echo      Echo SQL
  sqlj      SQLJ-Generator
  probe     SQL-Syntaxtest
Email-Adresse: punctum@punctum.com

</pre>
<p><a href="servlet?view=more&lang=de">mehr ...</a></p>
<!-- language="de", features="quest" -->
<p><span style="font-size:small">
Fragen, Hinweise: EMail an  <a href="mailto:punctum@punctum.com?&subject=Dbat">punctum@punctum.com</a></span></p>
</body></html>
