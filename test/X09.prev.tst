java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m sql -d x07
-- SQL generated by Dbat at 2011-11-21T21:19:43
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
-- finished at 2011-11-21T21:19:43
