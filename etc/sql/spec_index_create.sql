-- Descriptions of all available XML specification scripts
-- @(#) $Id$
-- 2016-05-24: INSERTs removed
-- 2016-04-16: params was VARCHAR(128)
-- 2011-02-10, Dr. Georg Fischer <punctum@punctum.com>
--
DROP   TABLE IF EXISTS  spec_index;
CREATE TABLE            spec_index
    ( subdir        VARCHAR(16)     NOT NULL
    , name          VARCHAR(32)     NOT NULL
    , lang          VARCHAR(8)      NOT NULL
    , title         VARCHAR(64)
    , comment       VARCHAR(512)
    , params        VARCHAR(1024)
    , PRIMARY KEY (subdir, lang, name)
    );
COMMIT;
