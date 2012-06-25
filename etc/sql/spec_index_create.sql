-- Descriptions of all available XML specification scripts
-- @(#) $Id$
-- 2011-02-10, Dr. Georg Fischer <punctum@punctum.com>
--
DROP   TABLE IF EXISTS 	spec_index;
CREATE TABLE 			spec_index
	( subdir		VARCHAR(16) 	NOT NULL
	, name			VARCHAR(32) 	NOT NULL
	, lang	        VARCHAR(8)		NOT NULL
	, title			VARCHAR(64)
	, comment		VARCHAR(512)
	, params		VARCHAR(128)
	, PRIMARY KEY (subdir, lang, name)
	);
COMMIT;
insert into spec_index values('datmod'
	, 'de'
	, 'fdte'
	, 'Fachdatenelemente'
	, ''
	, '&prefix=IBAN '); 
insert into spec_index values('datmod'
	, 'de'
	, 'ensearch'
	, 'Entitäten'
	, ''
	, '&prefix=PROGR '); 
insert into spec_index values('datmod'
	, 'de'
	, 'outim'
	, 'Info-Modelle / Produktnummern'
	, ''
	, ''); 
insert into spec_index values('datmod'
	, 'de'
	, 'relen'
	, 'Relationen zwischen Entitäten'
	, ''
	, '&prefix=PROGR '); 
insert into spec_index values('datmod'
	, 'de'
	, 'tbsearch'
	, 'Tabellen'
	, ''
	, '&prefix=U6CO '); 
COMMIT;
