wget -q -O - "http://localhost:8080/dbat/servlet?spec=test.var02&mode=echo"
SELECT name
, family
, gender
, birth
, decease 
FROM relatives 
WHERE name 	>=  ? 
			and birth	>=  ? 
			and decease	>=  ? 
			and changed	>=  ? 
ORDER BY 1;
