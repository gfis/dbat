wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/listbox&mode=echo"
SELECT name
, univ
, birth 
FROM c01 
WHERE substr(name, 1, 1) in ('M', 'L') 
ORDER BY 3;
SELECT name
, univ
, birth 
FROM c01 
WHERE year(birth) in (1887, 1914) 
ORDER BY 3;
SELECT name
, univ
, birth 
FROM c01 
WHERE cast(year(birth) as char) in ('1910', '1911') 
ORDER BY 3;
