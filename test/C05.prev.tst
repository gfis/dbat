
    <!--Select from test table - with colors and linked values-->
    <!--Abfrage der Test-Tabelle mit farbigem Hintergrund und Verweisen auf den Werten-->

    
===Select from test table c01 - with colors and linked values===

    <!--<input name="spec" type="hidden" value="test/color04" />

        Name: <input name="name" maxsize="20" size="10" init="%r" value="%r"></input>
        <input type="submit" value="Submit"></input>
    -->

    <!-- SQL:
SELECT case when year < 1980 then 'gry' else 'wht' end
, concat(name, concat('=', cast(year as char)))
, case when name like '%r%' then 'blu' else 'wht' end
, concat(cast(year as char), concat('=', name))
, case when univ like 'L%' then 'red' else 'lye' end
, univ
, gender
, birth 
FROM c01;
:SQL -->

{| border="1"
||Year||Name||University||Gender||Birthdate
|-
||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Martha&amp;year=1999 1999]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1999&amp;name=Martha Martha]||Freiburg||&amp;||1909-11-17
|-
||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Johannes&amp;year=1992 1992]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1992&amp;name=Johannes Johannes]||Schramberg||&lt;||1911-06-03
|-
||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Eberhard&amp;year=1945 1945]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1945&amp;name=Eberhard Eberhard]||Groß-Gerau||&gt;||1912-11-17
|-
||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Fritz&amp;year=1995 1995]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1995&amp;name=Fritz Fritz]||Waldshut||&lt;||1907-08-08
|-
||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Maria&amp;year=1999 1999]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1999&amp;name=Maria Maria]||Hermsdorf||#||1914-09-17
|-
||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Ilse&amp;year=1983 1983]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1983&amp;name=Ilse Ilse]||Lübars||$||1909-02-09
|-
||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Dorothea&amp;year=1985 1985]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1985&amp;name=Dorothea Dorothea]||Lübars||$||1910-02-07
|-
||align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Lucie&amp;year=1984 1984]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1984&amp;name=Lucie Lucie]||Lübars||'||1887-07-09
|-
|8 persons
|-
|}

<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script test/color04, Excel, more
 -->
