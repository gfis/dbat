wget -q -O - "http://localhost:8080/dbat/servlet?spec=test/selec01&mode=jdbc"
-- SQL generated by Dbat at 2012-06-01T22:56:16
INSERT INTO table_not_specified (name,univ,year,gender,birth)
VALUES ('Martha','Freiburg',1999,'&',{d'1909-11-17'});
INSERT INTO table_not_specified (name,univ,year,gender,birth)
VALUES ('Eberhard','Groß-Gerau',1945,'>',{d'1912-11-17'});
INSERT INTO table_not_specified (name,univ,year,gender,birth)
VALUES ('Fritz','Waldshut',1995,'<',{d'1907-08-08'});
INSERT INTO table_not_specified (name,univ,year,gender,birth)
VALUES ('Maria','Hermsdorf',1999,'#',{d'1914-09-17'});
INSERT INTO table_not_specified (name,univ,year,gender,birth)
VALUES ('Dorothea','Lübars',1985,'$',{d'1910-02-07'});
-- 5 Persons
COMMIT;
-- Output on 2012-06-01 22:56:16.919 by <a href="index.html">Dbat</a> script test/selec01,
--
-- finished at 2012-06-01T22:56:16
