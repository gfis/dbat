java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m xml -f ../web/spec/test/selec02.xml
<?xml version="1.0" encoding="UTF-8"?>
<dbat>
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
<table id="tab1" name="table_not_specified">
<tr><th>Year</th>
<th>Name</th>
<th>Year</th>
<th>Name</th>
<th>University</th>
<th>Gender</th>
<th>Birthdate</th>
</tr>
<tr><td>1999</td><td>Martha</td><td>e.1999</td><td>f.Martha</td><td>Freiburg</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td>1992</td><td>Johannes</td><td>e.1992</td><td>f.Johannes</td><td>Schramberg</td><td>&lt;</td><td>1911-06-03</td></tr>
<tr><td>1945</td><td>Eberhard</td><td>e.1945</td><td>f.Eberhard</td><td>Groß-Gerau</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td>1995</td><td>Fritz</td><td>e.1995</td><td>f.Fritz</td><td>Waldshut</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td>1999</td><td>Maria</td><td>e.1999</td><td>f.Maria</td><td>Hermsdorf</td><td>#</td><td>1914-09-17</td></tr>
<tr><td>1983</td><td>Ilse</td><td>e.1983</td><td>f.Ilse</td><td>Lübars</td><td>$</td><td>1909-02-09</td></tr>
<tr><td>1985</td><td>Dorothea</td><td>e.1985</td><td>f.Dorothea</td><td>Lübars</td><td>$</td><td>1910-02-07</td></tr>
<tr><td>1984</td><td>Lucie</td><td>e.1984</td><td>f.Lucie</td><td>Lübars</td><td>'</td><td>1887-07-09</td></tr>
<!-- 8 persons -->
</table>
<!-- Output on 2012-06-15 08:00:38.111 by <a href="index.html">Dbat</a> script ../web/spec/test/selec02.xml,
 -->
</dbat>
