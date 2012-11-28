java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m xml -f ../web/spec/test/selnull.xml
<?xml version="1.0" encoding="UTF-8"?>
<dbat>
<!-- SQL:
SELECT c.univ
, e.display
, e.lang 
FROM c01 c 
        	left join en1 e on substr(c.name, 1, 1) = e.code;
:SQL -->
<table id="tab1" name="table_not_specified">
<tr><th>Town</th>
<th>Gender</th>
<th>Language</th>
</tr>
<tr><td>Freiburg</td><td>male</td><td>eng</td></tr>
<tr><td>Schramberg</td><td isnull="yes"></td><td isnull="yes"></td></tr>
<tr><td>Groß-Gerau</td><td isnull="yes"></td><td isnull="yes"></td></tr>
<tr><td>Waldshut</td><td>female</td><td>eng</td></tr>
<tr><td>Hermsdorf</td><td>male</td><td>eng</td></tr>
<tr><td>Lübars</td><td isnull="yes"></td><td isnull="yes"></td></tr>
<tr><td>Lübars</td><td isnull="yes"></td><td isnull="yes"></td></tr>
<tr><td>Lübars</td><td isnull="yes"></td><td isnull="yes"></td></tr>
</table>
<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script ../web/spec/test/selnull.xml, Excel, more
 -->
</dbat>
