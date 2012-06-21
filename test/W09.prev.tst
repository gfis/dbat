wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/selec01&mode=echo"
--[01]--dbat.format.EchoSQL 2012-06-21 11:10:08----
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like '%r%';
--[99]----------------------------
