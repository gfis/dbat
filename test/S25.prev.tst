java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m echo -f ../web/spec/test/selec02.xml
--[01]--dbat.format.EchoSQL 2012-06-21 11:14:02----
SELECT concat(name, concat('*', cast(year as char)))
, concat(cast(year as char), concat('=', name))
, concat(cast(year as char), concat('=e.', cast(year as char)))
, concat(name, concat('=f.', name))
, univ
, gender
, birth 
FROM c01 
WHERE name like '%';
--[99]----------------------------
