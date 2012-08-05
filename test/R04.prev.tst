java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 "select * from pivot where sp2 = 'fix' order by 1,2,3;"
sp1	sp2	sp3
test	fix	0001
test	fix	0002
test	fix	0003
test	fix	0004
