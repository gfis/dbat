java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -g sp1 -m tsv "select sp1, sp2 from pivot order by 1, 2"
sp1	sp2
	c1
	c2
	c3
	c4
	c5
sp1	sp2
A	c1
A	c2
A	c3
A	c4
A	c5
sp1	sp2
B	c1
B	c2
B	c3
B	c4
B	c5
sp1	sp2
C	c1
C	c2
C	c3
C	c4
C	c5
sp1	sp2
D	c1
D	c2
D	c3
D	c4
D	c5
