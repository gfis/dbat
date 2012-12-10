<?xml version="1.0" encoding="UTF-8"?>
<dbat>
<!-- SQL:
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like '%r%';
:SQL -->
<table id="tab1" name="table_not_specified">
<tr><th>Name</th>
<th>University</th>
<th>Year</th>
<th>Gender</th>
<th>Birthdate</th>
</tr>
<tr><td>Martha</td><td>Freiburg</td><td>1999</td><td>&amp;</td><td>1909-11-17</td></tr>
<tr><td>Eberhard</td><td>Groß-Gerau</td><td>1945</td><td>&gt;</td><td>1912-11-17</td></tr>
<tr><td>Fritz</td><td>Waldshut</td><td>1995</td><td>&lt;</td><td>1907-08-08</td></tr>
<tr><td>Maria</td><td>Hermsdorf</td><td>1999</td><td>#</td><td>1914-09-17</td></tr>
<tr><td>Dorothea</td><td>Lübars</td><td>1985</td><td>$</td><td>1910-02-07</td></tr>
<!-- 5 Persons -->
</table>
<!-- Output on yyyy-mm-dd hh:mm:ss by Dbat script test/selec01, Excel, more
 -->
</dbat>
