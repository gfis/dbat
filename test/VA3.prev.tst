wget -q -O - "http://localhost:8080/dbat/servlet?spec=test.var02&mode=echo"
SELECT name
, family
, gender
, birth
, decease 
FROM relatives 
WHERE name    >=  'A' 
            and birth   >=  '1800-01-01' 
            and decease >=  1800 
            and changed >=  '1900-01-01 00:00:00' 
ORDER BY 1;
