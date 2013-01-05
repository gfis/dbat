<?xml version="1.0" encoding="UTF-8" ?>
<?mso-application progid="Excel.Sheet"?>
<Workbook
   xmlns="urn:schemas-microsoft-com:office:spreadsheet"
   xmlns:o="urn:schemas-microsoft-com:office:office"
   xmlns:x="urn:schemas-microsoft-com:office:excel"
   xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
   xmlns:html="http://www.w3.org/TR/REC-html40">
<!-- SQL:
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like '%r%';
:SQL -->
<Worksheet ss:Name="Select1">
<Table>
<Row><Cell><Data ss:Type="String">Name</Data></Cell>
<Cell><Data ss:Type="String">University</Data></Cell>
<Cell><Data ss:Type="String">Year</Data></Cell>
<Cell><Data ss:Type="String">Gender</Data></Cell>
<Cell><Data ss:Type="String">Birthdate</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Martha</Data></Cell>
<Cell><Data ss:Type="String">Freiburg</Data></Cell>
<Cell><Data ss:Type="String">1999</Data></Cell>
<Cell><Data ss:Type="String">&amp;</Data></Cell>
<Cell><Data ss:Type="DateTime">1909-11-17</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Eberhard</Data></Cell>
<Cell><Data ss:Type="String">Groß-Gerau</Data></Cell>
<Cell><Data ss:Type="String">1945</Data></Cell>
<Cell><Data ss:Type="String">&gt;</Data></Cell>
<Cell><Data ss:Type="DateTime">1912-11-17</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Fritz</Data></Cell>
<Cell><Data ss:Type="String">Waldshut</Data></Cell>
<Cell><Data ss:Type="String">1995</Data></Cell>
<Cell><Data ss:Type="String">&lt;</Data></Cell>
<Cell><Data ss:Type="DateTime">1907-08-08</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Maria</Data></Cell>
<Cell><Data ss:Type="String">Hermsdorf</Data></Cell>
<Cell><Data ss:Type="String">1999</Data></Cell>
<Cell><Data ss:Type="String">#</Data></Cell>
<Cell><Data ss:Type="DateTime">1914-09-17</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Dorothea</Data></Cell>
<Cell><Data ss:Type="String">Lübars</Data></Cell>
<Cell><Data ss:Type="String">1985</Data></Cell>
<Cell><Data ss:Type="String">$</Data></Cell>
<Cell><Data ss:Type="DateTime">1910-02-07</Data></Cell>
</Row>
<!-- 5 Persons -->
</Table>
</Worksheet>
<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script test/selec01, Excel, more
 -->
</Workbook>
