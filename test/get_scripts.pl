#!/usr/bin/perl

# extract the names of the test scripts
# @(#) $Id$
# 2011-12-07, Dr. Georg Fischer
#------------------------------------------------------------------ 
# Usage: 
#   perl get_scripts.pl test/all.tca | sort | uniq
#------------------------------------------------------------------------------- 
use strict;

	while (<>) {
		if (0) {
		} elsif (m{\AWGET\s+([\w\.\/]+)}) {
			print "$1.xml\n";
		} elsif (m{\AJRUN}) {
			if (m{\-f\s+\.\.\/web\/spec\/([\w\/]+)}) {
				print "$1.xml\n";
			}
		}
	} # while <>
	
__DATA__
# these are examples only

ECHO A00 

TEST A01 parameterized link
GREP on (\d{4}\-\d{2}\-\d{2})
JRUN -m html -f ../web/spec/test/parmlink.xml

TEST B04 create table with CLOB
DATA DROP TABLE if exists b04;
  CREATE TABLE            b04 
  ( name VARCHAR(16) not null 	COMMENT 'key for the LOB'
  , len  INT 					COMMENT 'size of the LOB content'
  , content TEXT 				COMMENT 'character large object (CLOB)'
  ) ENGINE=MyISAM CHARACTER SET utf8 COLLATE utf8_bin;
  COMMIT;
JRUN -f B04.data.tmp

TEST P04 Web pivot test with HTML
WGET test/pivot03
