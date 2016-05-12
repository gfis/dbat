<?xml version="1.0" encoding="UTF-8"?>
<dbat>
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
<table id="tab1" name="table2">
<tr><th>Upd.</th>
<th>Del.</th>
<th>Name</th>
<th>Family</th>
<th>Gender</th>
<th>Birthdate</th>
<th>Place</th>
<th>Died</th>
</tr>
<tr><td>upd</td><td>del</td><td>Dorothea</td><td>Fischer</td><td>F</td><td>1910-02-07</td><td>Berlin</td><td>1985</td></tr>
<tr><td>upd</td><td>del</td><td>Eberhard</td><td>Fischer</td><td>M</td><td>1912-11-17</td><td>Gro�-Gerau</td><td>1945</td></tr>
<tr><td>upd</td><td>del</td><td>Fritz</td><td>Fischer</td><td>M</td><td>1907-08-08</td><td>Waldshut</td><td>1995</td></tr>
<tr><td>upd</td><td>del</td><td>Ilse</td><td>Ritter</td><td>F</td><td>1909-02-09</td><td>L�bars</td><td>1983</td></tr>
<tr><td>upd</td><td>del</td><td>Johannes</td><td>Fischer</td><td>M</td><td>1911-06-03</td><td>Schramberg</td><td>1992</td></tr>
<tr><td>upd</td><td>del</td><td>Lucie</td><td>Ritter</td><td>F</td><td>1887-07-09</td><td>L�bars</td><td>1984</td></tr>
<tr><td>upd</td><td>del</td><td>Maria</td><td>Ritter</td><td>F</td><td>1914-09-17</td><td>Berlin-Hermsdorf</td><td>1999</td></tr>
<tr><td>upd</td><td>del</td><td>Martha</td><td>Fischer</td><td>F</td><td>1909-11-17</td><td>Freiburg</td><td>1999</td></tr>
<tr><td>upd</td><td>del</td><td>Teherba</td><td>Ritter</td><td>F</td><td>1886-02-04</td><td>Oranienburg</td><td>1968</td></tr>
<!-- 9 Persons -->
</table>
<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script test/crud03, Excel, more
 -->
</dbat>
