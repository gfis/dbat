java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m echo -f ../web/spec/test/selec01.xml
SELECT name
, univ
, year
, gender
, birth 
FROM c01 
WHERE name like '%r%'

