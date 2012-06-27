wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/selec01&mode=echo"
--[01]--dbat.format.EchoSQL on yyyy-mm-dd hh:mm:ss----
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like '%r%';
--[99]----------------------------
