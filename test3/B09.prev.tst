name	len	content
+noversion	194706	noversion.txt: Test file for URIReader tests
This file contains no SVN Id keyword for better comparision.
2011-07-27, Dr. Georg Fischer. Do not change this date.

+B04.data.tmp	2906	DROP TABLE if exists b04;
  CREATE TABLE            b04
  ( name VARCHAR(16) not null   COMMENT 'key for the LOB'
  , len  INT                    COMMENT 'size of the LOB content'
  , content TEXT                COMMENT 'character large object (CLOB)'
  ) ENGINE=MyISAM CHARACTER SET utf8 COLLATE utf8_bin;
  COMMIT;

+B06.data.tmp	64	+noversion 194706 ../web/noversion.txt
+B04.data.tmp 2906 B04.data.tmp
+B06.data.tmp 64 B06.data.tmp

align01	194706	../web/spec/test/align01.xml
index.jsp	2906	http://localhost:8080/dbat/index.jsp
