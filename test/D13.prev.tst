<?xml version="1.0" encoding="UTF-8"?>
<!--
    Dbat Specification generated by SpecDescription at yyyy-mm-dd hh:mm:ss
    @(#) S/Id/S
-->
<dbat
    xmlns   ="http://www.teherba.org/2007/dbat"
    xmlns:ht="http://www.w3.org/1999/xhtml"
    encoding="UTF-8"
    conn="worddb"
    lang="de"
    title="relatives"
    >
    <ht:h2>Table relatives</ht:h2>
    <ht:form method="get">
        <ht:input label="Prefix" name="prefix" maxsize="20" size="10" />
        <ht:input type="submit" value="Submit" />
    </ht:form>

    <select>
        <col label="name">a.name</col>
        <col label="family">a.family</col>
        <col label="birth">a.birth</col>
        <col label="gender">a.gender</col>
        <col label="place">a.place</col>
        <col label="decease" align="right">a.decease</col>
        <col label="changed">a.changed</col>
        <col label="user">a.user</col>
        <from> relatives a
        </from>
        <where> name || family like '<parm name="prefix" />%'
        </where>
        <counter desc="Row,s" />
    </select>
</dbat>
<!-- Ausgabe am yyyy-mm-dd hh:mm:ss durch Dbat-Skript describe, Excel, mehr
 -->