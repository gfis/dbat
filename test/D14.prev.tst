<?xml version="1.0" encoding="UTF-8"?>
<!--
    Dbiv Interactive View generated by ViewDescription at yyyy-mm-dd hh:mm:ss
    @(#) S/Id/S
-->
<dbiv
    xmlns   ="http://www.teherba.org/2011/dbiv"
    xmlns:iv="http://www.teherba.org/2011/dbiv"
    xmlns:db="http://www.teherba.org/2007/dbat"
    xmlns:ht="http://www.w3.org/1999/xhtml"
    encoding="UTF-8"
    conn="worddb"
    lang="de"
    title="relatives"
    >
    <view name="relatives">
        <title>Interaktive Sicht <em>relatives</em></title>
        <counter desc="Zeile,n" />
        <action name="ins"        label="Neuer Eintrag" remark="Einfügen" position="middle" />
        <action name="upd"        label="Modif."        remark="Ändern"   position="result" />
        <action name="del"        label="Lösch."        remark="Löschen"  position="result" />

        <field name="name" key="1" label="Name" init="" type="char" size="40" valid=".*" remark="Christian Name" />
        <field name="family" key="" label="Family" init="" type="char" size="40" valid=".*" remark="Family Name" />
        <field name="birth" key="" label="Birth" init="" type="date" size="10" valid=".*" remark="" />
        <field name="gender" key="" label="Gender" init="" type="char" size="1" valid=".*" remark="M or F" />
        <field name="place" key="" label="Place" init="" type="char" size="40" valid=".*" remark="Town" />
        <field name="decease" key="" label="Decease" init="" align="right" type="int" size="10" valid=".*" remark="Decease Year" />
        <field name="changed" key="" label="Changed" init="CURRENT_TIMESTAMP" type="timestamp" size="19" valid=".*" remark="last update time" />
        <field name="user" key="" label="User" init="" type="char" size="8" valid=".*" remark="of last change" />
        <where></where>
        <order by="1" />
    </view>
</dbiv>
<!-- Ausgabe am yyyy-mm-dd hh:mm:ss durch Dbat-Skript describe, Excel, mehr
 -->
</dbiv>
