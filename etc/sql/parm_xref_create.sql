-- Compare parameters, input fieldnames and links in available XML specification scripts
-- @(#) $Id$
-- 2011-02-18, Dr. Georg Fischer <punctum@punctum.com>
--
DROP   TABLE IF EXISTS 	parm_xref;
CREATE TABLE 			parm_xref
	( element		VARCHAR(  8) 	NOT NULL -- root, link, href, parm, input, select, textarea
	, name			VARCHAR(128) 	NOT NULL -- parameter name
	, spec	        VARCHAR( 64)	NOT NULL -- spec file referred to in link
	, file	        VARCHAR( 64)	NOT NULL -- spec file which contains the element
	);
COMMIT;
