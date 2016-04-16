<?xml version="1.0" encoding="UTF-8" ?>
<?mso-application progid="Excel.Sheet"?>
<Workbook
   xmlns="urn:schemas-microsoft-com:office:spreadsheet"
   xmlns:o="urn:schemas-microsoft-com:office:office"
   xmlns:x="urn:schemas-microsoft-com:office:excel"
   xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
   xmlns:html="http://www.w3.org/TR/REC-html40">
<!-- SQL:
SELECT DISTINCT gender
, case 
						when gender = 'M' then 'male' 
						else                   'female' end 
FROM relatives 
ORDER BY gender;
:SQL -->
<!-- SQL:
SELECT '' || '=' 
         || '' || '=' 
         || '' || '=' 
         || name || '=' 
         || family || '=' 
         || gender || '=' || 'upd'
, '' || '=' 
         || '' || '=' 
         || '' || '=' 
         || name || '=' 
         || family || '=' 
         || gender || '=' || 'del'
, name
, family
, gender
, birth
, place
, decease 
FROM relatives 
WHERE name like '%'
        and family like '%'
        and gender like '%' 
ORDER BY name,family;
:SQL -->
<Worksheet ss:Name="Select1">
<Table>
<Row><Cell><Data ss:Type="String">Name</Data></Cell>
<Cell><Data ss:Type="String">Family</Data></Cell>
<Cell><Data ss:Type="String">Gender</Data></Cell>
<Cell><Data ss:Type="String">Birthdate</Data></Cell>
<Cell><Data ss:Type="String">Place</Data></Cell>
<Cell><Data ss:Type="String">Died</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Dorothea</Data></Cell>
<Cell><Data ss:Type="String">Fischer</Data></Cell>
<Cell><Data ss:Type="String">F</Data></Cell>
<Cell><Data ss:Type="DateTime">1910-02-07</Data></Cell>
<Cell><Data ss:Type="String">Berlin</Data></Cell>
<Cell><Data ss:Type="String">1985</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Eberhard</Data></Cell>
<Cell><Data ss:Type="String">Fischer</Data></Cell>
<Cell><Data ss:Type="String">M</Data></Cell>
<Cell><Data ss:Type="DateTime">1912-11-17</Data></Cell>
<Cell><Data ss:Type="String">Gro�-Gerau</Data></Cell>
<Cell><Data ss:Type="String">1945</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Fritz</Data></Cell>
<Cell><Data ss:Type="String">Fischer</Data></Cell>
<Cell><Data ss:Type="String">M</Data></Cell>
<Cell><Data ss:Type="DateTime">1907-08-08</Data></Cell>
<Cell><Data ss:Type="String">Waldshut</Data></Cell>
<Cell><Data ss:Type="String">1995</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Ilse</Data></Cell>
<Cell><Data ss:Type="String">Ritter</Data></Cell>
<Cell><Data ss:Type="String">F</Data></Cell>
<Cell><Data ss:Type="DateTime">1909-02-09</Data></Cell>
<Cell><Data ss:Type="String">L�bars</Data></Cell>
<Cell><Data ss:Type="String">1983</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Johannes</Data></Cell>
<Cell><Data ss:Type="String">Fischer</Data></Cell>
<Cell><Data ss:Type="String">M</Data></Cell>
<Cell><Data ss:Type="DateTime">1911-06-03</Data></Cell>
<Cell><Data ss:Type="String">Schramberg</Data></Cell>
<Cell><Data ss:Type="String">1992</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Lucie</Data></Cell>
<Cell><Data ss:Type="String">Ritter</Data></Cell>
<Cell><Data ss:Type="String">F</Data></Cell>
<Cell><Data ss:Type="DateTime">1887-07-09</Data></Cell>
<Cell><Data ss:Type="String">L�bars</Data></Cell>
<Cell><Data ss:Type="String">1984</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Maria</Data></Cell>
<Cell><Data ss:Type="String">Ritter</Data></Cell>
<Cell><Data ss:Type="String">F</Data></Cell>
<Cell><Data ss:Type="DateTime">1914-09-17</Data></Cell>
<Cell><Data ss:Type="String">Berlin-Hermsdorf</Data></Cell>
<Cell><Data ss:Type="String">1999</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Martha</Data></Cell>
<Cell><Data ss:Type="String">Fischer</Data></Cell>
<Cell><Data ss:Type="String">F</Data></Cell>
<Cell><Data ss:Type="DateTime">1909-11-17</Data></Cell>
<Cell><Data ss:Type="String">Freiburg</Data></Cell>
<Cell><Data ss:Type="String">1999</Data></Cell>
</Row>
<Row><Cell><Data ss:Type="String">Teherba</Data></Cell>
<Cell><Data ss:Type="String">Ritter</Data></Cell>
<Cell><Data ss:Type="String">F</Data></Cell>
<Cell><Data ss:Type="DateTime">1886-02-04</Data></Cell>
<Cell><Data ss:Type="String">Oranienburg</Data></Cell>
<Cell><Data ss:Type="String">1968</Data></Cell>
</Row>
<!-- 9 Persons -->
</Table>
</Worksheet>
<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script test/crud03, Excel, more
 -->
</Workbook>
