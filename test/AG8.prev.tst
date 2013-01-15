<?xml version="1.0" encoding="UTF-8" ?>
<?mso-application progid="Excel.Sheet"?>
<Workbook
   xmlns="urn:schemas-microsoft-com:office:spreadsheet"
   xmlns:o="urn:schemas-microsoft-com:office:office"
   xmlns:x="urn:schemas-microsoft-com:office:excel"
   xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
   xmlns:html="http://www.w3.org/TR/REC-html40">
<!-- SQL:
SELECT sp1
, concat(sp1, concat('=', concat(sp2, concat('=', sp3)))) 
FROM pivot;
:SQL -->
<Worksheet ss:Name="Select1">
<Table>
<Row><Cell><Data ss:Type="String">Application</Data></Cell>
<Cell><Data ss:Type="String">Aggr. Column</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name="><Data ss:Type="String"></Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=&beta=c5&gamma=S5"><Data ss:Type="String">S1, S2, S3, S4, S5</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=A"><Data ss:Type="String">A</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=A&beta=c5&gamma=A5"><Data ss:Type="String">A1, A2, A3, A4, A5</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=B"><Data ss:Type="String">B</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=B&beta=c5&gamma=B5"><Data ss:Type="String">B1, B2, B3, B4, B5</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=C"><Data ss:Type="String">C</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=C&beta=c5&gamma=C5"><Data ss:Type="String">C1, C2, C3, C4, C5</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=D"><Data ss:Type="String">D</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=D&beta=c5&gamma=D5"><Data ss:Type="String">D1, D2, D3, D4, D5</Data></Cell>
</Row>
</Table>
</Worksheet>
<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script test/aggr01, Excel, more
 -->
</Workbook>
