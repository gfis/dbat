java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m sql -t tartable -4 "select * from c01"
-- SQL generated by Dbat on yyyy-mm-dd hh:mm:ss
INSERT INTO tartable (name,univ,year,gender,birth)
VALUES ('Martha','Freiburg',1999,'&','1909-11-17');
INSERT INTO tartable (name,univ,year,gender,birth)
VALUES ('Johannes','Schramberg',1992,'<','1911-06-03');
INSERT INTO tartable (name,univ,year,gender,birth)
VALUES ('Eberhard','Groß-Gerau',1945,'>','1912-11-17');
INSERT INTO tartable (name,univ,year,gender,birth)
VALUES ('Fritz','Waldshut',1995,'<','1907-08-08');
COMMIT;
