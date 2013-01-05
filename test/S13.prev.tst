
	<!--Select from test table - with linked values-->
	<!--Abfrage der Test-Tabelle mit Verweisen auf den Werten-->

    
===Select from test table <em>c01</em> - with linked values===

    <!--<input name="spec" type="hidden" value="../web/spec/test/selec02.xml" />

        Name: <input name="name" maxsize="20" size="10" init="%" value="%"></input>
        <input type="submit" value="Submit"></input>
    -->
    
    <!-- SQL:
SELECT concat(name, concat('*', cast(year as char)))
, concat(cast(year as char), concat('=', name))
, concat(cast(year as char), concat('=e.', cast(year as char)))
, concat(name, concat('=f.', name))
, univ
, gender
, birth 
FROM c01 
WHERE name like '%';
:SQL -->

{| border="1"
|Year||Name||Year||Name||University||Gender||Birthdate
|-
|align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Martha&amp;year=1999 1999]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1999&amp;name=Martha Martha]||align="right"|[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=1999&amp;prefix2=e.1999 e.1999]||[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=Martha&amp;prefix2=f.Martha f.Martha]||Freiburg||&amp;||1909-11-17
|-
|align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Johannes&amp;year=1992 1992]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1992&amp;name=Johannes Johannes]||align="right"|[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=1992&amp;prefix2=e.1992 e.1992]||[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=Johannes&amp;prefix2=f.Johannes f.Johannes]||Schramberg||&lt;||1911-06-03
|-
|align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Eberhard&amp;year=1945 1945]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1945&amp;name=Eberhard Eberhard]||align="right"|[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=1945&amp;prefix2=e.1945 e.1945]||[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=Eberhard&amp;prefix2=f.Eberhard f.Eberhard]||Groß-Gerau||&gt;||1912-11-17
|-
|align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Fritz&amp;year=1995 1995]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1995&amp;name=Fritz Fritz]||align="right"|[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=1995&amp;prefix2=e.1995 e.1995]||[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=Fritz&amp;prefix2=f.Fritz f.Fritz]||Waldshut||&lt;||1907-08-08
|-
|align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Maria&amp;year=1999 1999]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1999&amp;name=Maria Maria]||align="right"|[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=1999&amp;prefix2=e.1999 e.1999]||[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=Maria&amp;prefix2=f.Maria f.Maria]||Hermsdorf||#||1914-09-17
|-
|align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Ilse&amp;year=1983 1983]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1983&amp;name=Ilse Ilse]||align="right"|[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=1983&amp;prefix2=e.1983 e.1983]||[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=Ilse&amp;prefix2=f.Ilse f.Ilse]||Lübars||$||1909-02-09
|-
|align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Dorothea&amp;year=1985 1985]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1985&amp;name=Dorothea Dorothea]||align="right"|[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=1985&amp;prefix2=e.1985 e.1985]||[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=Dorothea&amp;prefix2=f.Dorothea f.Dorothea]||Lübars||$||1910-02-07
|-
|align="right"|[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;name=Lucie&amp;year=1984 1984]||[http://localhost:8080/dbat/servlet?spec=test/selec01&amp;year=1984&amp;name=Lucie Lucie]||align="right"|[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=1984&amp;prefix2=e.1984 e.1984]||[http://localhost:8080/dbat/servlet?spec=spec1&amp;prefix=Lucie&amp;prefix2=f.Lucie f.Lucie]||Lübars||'||1887-07-09
|-
|8 persons
|-
|}

<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script ../web/spec/test/selec02.xml, Excel, more
 -->
