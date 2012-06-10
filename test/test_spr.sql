-- mixed SQL statements
-- @(#) $Id$
 select      'ge' , count(entry), max(entry) from words where entry like 'ge%' ;
 call pr1 -in ge    -out:int      -out:varchar;
 select      'ger', count(entry), max(entry) from words where entry like 'ger%';
 call pr1 -in ger   -out:int      -out:varchar;
