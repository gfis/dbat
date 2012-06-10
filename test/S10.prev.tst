java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m spec -t c01
<?xml version="1.0" encoding="UTF-8" ?>
<dbat encoding="UTF-8"
	xmlns="http://www.teherba.org/2007/dbat"
	xmlns:ht="http://www.w3.org/1999/xhtml"
	>
<!-- SQL:
SELECT * FROM c01 -->
<table name="c01">
<tr><th>name</th>
<th>univ</th>
<th>year</th>
<th>gender</th>
<th>birth</th>
</tr>
<tr><td>Martha</td><td>Freiburg</td><td>1999</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td>Johannes</td><td>Schramberg</td><td>1992</td><td>&lt;</td><td>1911-06-03</td></tr>
<tr><td>Eberhard</td><td>Groß-Gerau</td><td>1945</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>Waldshut</td><td>1995</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td>Maria</td><td>Hermsdorf</td><td>1999</td><td>#</td><td>1914-09-17</td></tr>
<tr><td>Ilse</td><td>Lübars</td><td>1983</td><td>$</td><td>1909-02-09</td></tr>
<tr><td>Dorothea</td><td>Lübars</td><td>1985</td><td>$</td><td>1910-02-07</td></tr>
<!-- 7 rows -->
</table>
</dbat>
