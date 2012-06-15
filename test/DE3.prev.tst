java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -v -f DE3.data.tmp
SQL:
insert into de1 values('row3', 123456780123456789, 123456.345, {d '2011-08-01'}, {t '23:13:00'}, {ts '2011-08-01 23:13:00.000'});
:SQL
SQL:
insert into de1 values('row4', 194706290530000000, 194706.290, {d '1981-08-09'}, {t '11:30:00'}, {ts '1981-08-09 11:30:00.000'});
:SQL
SQL:
select * from de1 order by name0;
:SQL
name0	dec1	dec2	date3	time4	ts5
row1	123456780123456789	123456.345	2011-08-01	23:13:00	2011-08-01 23:13:00.000
row2	194706290530000000	194706.290	1981-08-09	11:30:00	1981-08-09 11:30:00.000
row3	123456780123456789	123456.345	2011-08-01	23:13:00	2011-08-01 23:13:00.000
row4	194706290530000000	194706.290	1981-08-09	11:30:00	1981-08-09 11:30:00.000
 executed 3 SQL statements affecting 2 rows in ... ms
