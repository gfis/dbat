#!/usr/bin/perl

# Insert XML namespace declarations and prefixes for DBAT_URI and HTML_URI
# @(#) $Id$
# 2011-01-24: wrong URI
# 2010-12-04: replace umlauts by XML entities
#
# Usage: 
#   perl -i.bak insert_xmlns.pl web/spec/*.xml
#------------------------------------------------------------------ 
#
#  Copyright 2010 Dr. Georg Fischer <punctum at punctum dot kom>
# 
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
# 
#       http://www.apache.org/licenses/LICENSE-2.0
# 
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#------------------------------------------------------------------ 
#
use strict;

    # Namespace URI of Dbat specifications */
	my $DBAT_URI = "http://www.teherba.org/2007/dbat";
    # Namespace URI of HTML tags (&lt;select&gt; etc.) */
    my $HTML_URI = "http://www.w3.org/1999/xhtml";
    my $current_namespace = $HTML_URI;
    my $HTML_PREFIX = "ht";
    
    while (<>) {
    	my $line = $_;
    	$line =~ s/ä/\&#xe4;/g;
    	$line =~ s/ö/\&#xf6;/g;
    	$line =~ s/ü/\&#xfc;/g;
    	$line =~ s/Ä/\&#xc4;/g;
    	$line =~ s/Ö/\&#xd6;/g;
    	$line =~ s/Ü/\&#xdc;/g;
    	$line =~ s/ß/\&#xdf;/g;
    	if ($line =~ m{\<\?xml}) {
    	} elsif ($line =~ m{\<dbat}) {
    		$line =~ s{\>}{\n\txmlns   =\"$DBAT_URI\"\n\txmlns:$HTML_PREFIX=\"$HTML_URI\"\n\t\>};
    	} elsif ($line =~ m{\<\/dbat}) {
    	} elsif ($current_namespace eq $HTML_URI) {
    		if (0) {
	    	} elsif ($line =~ m{\<select}) {
	    		if (0) {
	    		} elsif ($line =~ m{\<select\s+name\s*\=}) {
	    			$line =~ s{\<select}{\<$HTML_PREFIX:select};
	    		} elsif ($line =~ m{\</select}) {
	    			$current_namespace = $HTML_URI;
	    		} else {
	    			$current_namespace = $DBAT_URI;
	    		}
	    	} elsif ($line =~ m{\<(call|insert|update|delete)}) {
	    			$current_namespace = $DBAT_URI;
	    	} elsif ($line =~ m{\<\/(call|insert|update|delete)}) {
    				$current_namespace = $DBAT_URI;
	    	} else {
	    		$line =~ s{\<(\w)}{\<$HTML_PREFIX:$1}g;
	    		$line =~ s{\<\/(\w)}{\<\/$HTML_PREFIX:$1}g;
	    	}
    	} elsif ($current_namespace eq $DBAT_URI) {
	    	if ($line =~ m{\<\/select}) {
    			$current_namespace = $HTML_URI;
	    	} # </select
    	} else {
    	} # if dbat 
    	print $line;
    } # while <>
__DATA__
<?xml version="1.0" encoding="UTF-8" ?>
<dbat conn="worddb" lang="en" encoding="UTF-8">
<!--
    Dbat specification - test 1
    @(#) $Id$
    2008-02-11: &amp;amp;
    2007-01-06: copied from unit
-->
    <h2>DBAT Test - Words</h2>
    <form>
        <input label="Prefix" name="prefix" maxsize="20" size="10" />
        <input type="submit" value="Submit" />
    </form>

    <select>
        <col label="Word"    name="Wordname" align="right" href="servlet?spec=spec2&amp;amp;prefix=">entry</col>
        <col label="Morphem"                 align="center"                            >morph</col>
        <col label="Related" link="specrel&amp;amp;entry=&amp;amp;enrel">concat(entry, concat('=', enrel))</col>
	<!--
        <col label="Related" href="servlet?spec=specrel&amp;amp;entry=;&amp;amp;enrel=">concat(entry, concat(';', enrel))</col>
    -->
        <from>words</from>
        <where>entry like '<parm name="prefix"/>%'</where>
        <counter desc="word,s"/>
    </select>

</dbat>
