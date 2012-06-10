java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -a sp2 -sa "/" -m tsv "select sp1, sp2 from pivot order by 1, 2"
sp1	sp2
	c1/c2/c3/c4/c5
A	c1/c2/c3/c4/c5
B	c1/c2/c3/c4/c5
C	c1/c2/c3/c4/c5
D	c1/c2/c3/c4/c5
