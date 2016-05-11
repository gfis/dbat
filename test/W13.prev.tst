
    <!--Select from test table-->
    <!--Abfrage der Test-Tabelle-->
    
    
===Select from test table c01===

    <!--<input name="spec" type="hidden" value="test/selec01" />

        Name: <input name="name" maxsize="20" size="10" init="%r" value="%r"></input>
        <input type="submit" value="Submit"></input>
    -->
    <!-- SQL:
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like '%r%';
:SQL -->

{| border="1"
|Name||University||Year||Gender||Birthdate
|-
|Martha||Freiburg||align="right"|1999||&amp;||1909-11-17
|-
|Eberhard||Groß-Gerau||align="right"|1945||&gt;||1912-11-17
|-
|Fritz||Waldshut||align="right"|1995||&lt;||1907-08-08
|-
|Maria||Hermsdorf||align="right"|1999||#||1914-09-17
|-
|Dorothea||Lübars||align="right"|1985||$||1910-02-07
|-
|5 Persons
|-
|}

<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script test/selec01, Excel, more
 -->
