java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m sql -d x07
-- SQL generated by Dbat at 2012-06-01T22:56:11
DROP   PROCEDURE x07 $
CREATE PROCEDURE x07
	( IN	iname0	VARCHAR(16)	NOT NULL
	, OUT	odec1	DECIMAL(12)	NOT NULL
	, OUT	odec2	DECIMAL(18,3)	NOT NULL
	, OUT	odate3	DATE	NOT NULL
	, OUT	otime4	TIME	NOT NULL
	, OUT	ots5	TIMESTAMP	NOT NULL
	)
	BEGIN
	END;
	$
-- finished at 2012-06-01T22:56:12
