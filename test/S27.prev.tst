java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m xls -f ../web/spec/test/selec02.xml
<?xml version="1.0" encoding="UTF-8" ?>
<?mso-application progid="Excel.Sheet"?>
<Workbook
   xmlns="urn:schemas-microsoft-com:office:spreadsheet"
   xmlns:o="urn:schemas-microsoft-com:office:office"
   xmlns:x="urn:schemas-microsoft-com:office:excel"
   xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
   xmlns:html="http://www.w3.org/TR/REC-html40">
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
<Worksheet ss:Name="Select1">
<Table>
<Row><Cell><Data ss:Type="String">Year</Data></Cell>
<Cell><Data ss:Type="String">Name</Data></Cell>
<Cell><Data ss:Type="String">Year</Data></Cell>
<Cell><Data ss:Type="String">Name</Data></Cell>
<Cell><Data ss:Type="String">University</Data></Cell>
<Cell><Data ss:Type="String">Gender</Data></Cell>
<Cell><Data ss:Type="String">Birthdate</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Martha&year=1999"><Data ss:Type="String">1999</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1999&name=Martha"><Data ss:Type="String">Martha</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=1999&prefix2=e.1999"><Data ss:Type="String">e.1999</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=Martha&prefix2=f.Martha"><Data ss:Type="String">f.Martha</Data></Cell>
<Cell><Data ss:Type="String">Freiburg</Data></Cell>
<Cell><Data ss:Type="String">&amp;</Data></Cell>
<Cell><Data ss:Type="DateTime">1909-11-17</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Johannes&year=1992"><Data ss:Type="String">1992</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1992&name=Johannes"><Data ss:Type="String">Johannes</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=1992&prefix2=e.1992"><Data ss:Type="String">e.1992</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=Johannes&prefix2=f.Johannes"><Data ss:Type="String">f.Johannes</Data></Cell>
<Cell><Data ss:Type="String">Schramberg</Data></Cell>
<Cell><Data ss:Type="String">&lt;</Data></Cell>
<Cell><Data ss:Type="DateTime">1911-06-03</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Eberhard&year=1945"><Data ss:Type="String">1945</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1945&name=Eberhard"><Data ss:Type="String">Eberhard</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=1945&prefix2=e.1945"><Data ss:Type="String">e.1945</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=Eberhard&prefix2=f.Eberhard"><Data ss:Type="String">f.Eberhard</Data></Cell>
<Cell><Data ss:Type="String">Groß-Gerau</Data></Cell>
<Cell><Data ss:Type="String">&gt;</Data></Cell>
<Cell><Data ss:Type="DateTime">1912-11-17</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Fritz&year=1995"><Data ss:Type="String">1995</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1995&name=Fritz"><Data ss:Type="String">Fritz</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=1995&prefix2=e.1995"><Data ss:Type="String">e.1995</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=Fritz&prefix2=f.Fritz"><Data ss:Type="String">f.Fritz</Data></Cell>
<Cell><Data ss:Type="String">Waldshut</Data></Cell>
<Cell><Data ss:Type="String">&lt;</Data></Cell>
<Cell><Data ss:Type="DateTime">1907-08-08</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Maria&year=1999"><Data ss:Type="String">1999</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1999&name=Maria"><Data ss:Type="String">Maria</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=1999&prefix2=e.1999"><Data ss:Type="String">e.1999</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=Maria&prefix2=f.Maria"><Data ss:Type="String">f.Maria</Data></Cell>
<Cell><Data ss:Type="String">Hermsdorf</Data></Cell>
<Cell><Data ss:Type="String">#</Data></Cell>
<Cell><Data ss:Type="DateTime">1914-09-17</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Ilse&year=1983"><Data ss:Type="String">1983</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1983&name=Ilse"><Data ss:Type="String">Ilse</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=1983&prefix2=e.1983"><Data ss:Type="String">e.1983</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=Ilse&prefix2=f.Ilse"><Data ss:Type="String">f.Ilse</Data></Cell>
<Cell><Data ss:Type="String">Lübars</Data></Cell>
<Cell><Data ss:Type="String">$</Data></Cell>
<Cell><Data ss:Type="DateTime">1909-02-09</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Dorothea&year=1985"><Data ss:Type="String">1985</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1985&name=Dorothea"><Data ss:Type="String">Dorothea</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=1985&prefix2=e.1985"><Data ss:Type="String">e.1985</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=Dorothea&prefix2=f.Dorothea"><Data ss:Type="String">f.Dorothea</Data></Cell>
<Cell><Data ss:Type="String">Lübars</Data></Cell>
<Cell><Data ss:Type="String">$</Data></Cell>
<Cell><Data ss:Type="DateTime">1910-02-07</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Lucie&year=1984"><Data ss:Type="String">1984</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1984&name=Lucie"><Data ss:Type="String">Lucie</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=1984&prefix2=e.1984"><Data ss:Type="String">e.1984</Data></Cell>
<Cell ss:HRef="servlet?spec=spec1&prefix=Lucie&prefix2=f.Lucie"><Data ss:Type="String">f.Lucie</Data></Cell>
<Cell><Data ss:Type="String">Lübars</Data></Cell>
<Cell><Data ss:Type="String">&apos;</Data></Cell>
<Cell><Data ss:Type="DateTime">1887-07-09</Data></Cell>
</Row>
<!-- 8 persons -->
</Table>
</Worksheet>
<!-- Output on 2012-06-15 08:00:55.720 by <a href="index.html">Dbat</a> script ../web/spec/test/selec02.xml,
 -->
</Workbook>
