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
, sp2
, concat(sp1, concat('=', concat(sp2, concat('=', sp3)))) 
FROM pivot;
:SQL -->
<Worksheet ss:Name="Select1">
<Table>
<Row><Cell><Data ss:Type="String">Application</Data></Cell>
<Cell><Data ss:Type="String">S1</Data></Cell>
<Cell><Data ss:Type="String">S2</Data></Cell>
<Cell><Data ss:Type="String">S3</Data></Cell>
<Cell><Data ss:Type="String">S4</Data></Cell>
<Cell><Data ss:Type="String">S5</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=A"><Data ss:Type="String">A</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=A&beta=c1&sp3=A1"><Data ss:Type="String">A1</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=A&beta=c2&sp3=A2"><Data ss:Type="String">A2</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=A&beta=c3&sp3=A3"><Data ss:Type="String">A3</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=A&beta=c4&sp3=A4"><Data ss:Type="String">A4</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=A&beta=c5&sp3=A5"><Data ss:Type="String">A5</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=B"><Data ss:Type="String">B</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=B&beta=c1&sp3=B1"><Data ss:Type="String">B1</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=B&beta=c2&sp3=B2"><Data ss:Type="String">B2</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=B&beta=c3&sp3=B3"><Data ss:Type="String">B3</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=B&beta=c4&sp3=B4"><Data ss:Type="String">B4</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=B&beta=c5&sp3=B5"><Data ss:Type="String">B5</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=C"><Data ss:Type="String">C</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=C&beta=c1&sp3=C1"><Data ss:Type="String">C1</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=C&beta=c2&sp3=C2"><Data ss:Type="String">C2</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=C&beta=c3&sp3=C3"><Data ss:Type="String">C3</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=C&beta=c4&sp3=C4"><Data ss:Type="String">C4</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=C&beta=c5&sp3=C5"><Data ss:Type="String">C5</Data></Cell>
</Row>
<Row><Cell ss:HRef="servlet?spec=test/selec01&name=D"><Data ss:Type="String">D</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=D&beta=c1&sp3=D1"><Data ss:Type="String">D1</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=D&beta=c2&sp3=D2"><Data ss:Type="String">D2</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=D&beta=c3&sp3=D3"><Data ss:Type="String">D3</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=D&beta=c4&sp3=D4"><Data ss:Type="String">D4</Data></Cell>
<Cell ss:HRef="servlet?spec=test/selec01&alpha=D&beta=c5&sp3=D5"><Data ss:Type="String">D5</Data></Cell>
</Row>
</Table>
</Worksheet>
<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script test/pivot03, Excel, more
 -->
</Workbook>
