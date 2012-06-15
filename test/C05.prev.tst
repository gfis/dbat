wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/color04&mode=xls"
<?xml version="1.0" encoding="UTF-8" ?>
<?mso-application progid="Excel.Sheet"?>
<Workbook
   xmlns="urn:schemas-microsoft-com:office:spreadsheet"
   xmlns:o="urn:schemas-microsoft-com:office:office"
   xmlns:x="urn:schemas-microsoft-com:office:excel"
   xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
   xmlns:html="http://www.w3.org/TR/REC-html40">
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
<Worksheet ss:Name="Select1">
<Table>
<Row><Cell><Data ss:Type="String">Year</Data></Cell>
<Cell><Data ss:Type="String">Name</Data></Cell>
<Cell><Data ss:Type="String">University</Data></Cell>
<Cell><Data ss:Type="String">Gender</Data></Cell>
<Cell><Data ss:Type="String">Birthdate</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Martha&year=1999"><Data ss:Type="String">1999</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1999&name=Martha"><Data ss:Type="String">Martha</Data></Cell>
<Cell><Data ss:Type="String">Freiburg</Data></Cell>
<Cell><Data ss:Type="String">&amp;</Data></Cell>
<Cell><Data ss:Type="DateTime">1909-11-17</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Johannes&year=1992"><Data ss:Type="String">1992</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1992&name=Johannes"><Data ss:Type="String">Johannes</Data></Cell>
<Cell><Data ss:Type="String">Schramberg</Data></Cell>
<Cell><Data ss:Type="String">&lt;</Data></Cell>
<Cell><Data ss:Type="DateTime">1911-06-03</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Eberhard&year=1945"><Data ss:Type="String">1945</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1945&name=Eberhard"><Data ss:Type="String">Eberhard</Data></Cell>
<Cell><Data ss:Type="String">Groß-Gerau</Data></Cell>
<Cell><Data ss:Type="String">&gt;</Data></Cell>
<Cell><Data ss:Type="DateTime">1912-11-17</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Fritz&year=1995"><Data ss:Type="String">1995</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1995&name=Fritz"><Data ss:Type="String">Fritz</Data></Cell>
<Cell><Data ss:Type="String">Waldshut</Data></Cell>
<Cell><Data ss:Type="String">&lt;</Data></Cell>
<Cell><Data ss:Type="DateTime">1907-08-08</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Maria&year=1999"><Data ss:Type="String">1999</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1999&name=Maria"><Data ss:Type="String">Maria</Data></Cell>
<Cell><Data ss:Type="String">Hermsdorf</Data></Cell>
<Cell><Data ss:Type="String">#</Data></Cell>
<Cell><Data ss:Type="DateTime">1914-09-17</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Ilse&year=1983"><Data ss:Type="String">1983</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1983&name=Ilse"><Data ss:Type="String">Ilse</Data></Cell>
<Cell><Data ss:Type="String">Lübars</Data></Cell>
<Cell><Data ss:Type="String">$</Data></Cell>
<Cell><Data ss:Type="DateTime">1909-02-09</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Dorothea&year=1985"><Data ss:Type="String">1985</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1985&name=Dorothea"><Data ss:Type="String">Dorothea</Data></Cell>
<Cell><Data ss:Type="String">Lübars</Data></Cell>
<Cell><Data ss:Type="String">$</Data></Cell>
<Cell><Data ss:Type="DateTime">1910-02-07</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=Lucie&year=1984"><Data ss:Type="String">1984</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&year=1984&name=Lucie"><Data ss:Type="String">Lucie</Data></Cell>
<Cell><Data ss:Type="String">Lübars</Data></Cell>
<Cell><Data ss:Type="String">&apos;</Data></Cell>
<Cell><Data ss:Type="DateTime">1887-07-09</Data></Cell>
</Row>
<!-- 8 persons -->
</Table>
</Worksheet>
<!-- Output on 2012-06-15 08:01:39.448 by <a href="index.html">Dbat</a> script test/color04,
 -->
</Workbook>
