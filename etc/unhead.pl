#!/usr/bin/perl

# Remove first n lines from a file
# @(#) $Id$
# 2012-11-24, Georg Fischer
#
# Usage: 
#   perl unhead.pl [-n i] file...
# Removes first 'i' lines (default: 1st) from all files in place.
#------------------------------------------------------------------ 
#
#  Copyright 2012 Dr. Georg Fischer <punctum at punctum dot kom>
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
    my $rmcount = 1; # default: remove 1st line only

    while (scalar(@ARGV) > 0 && ($ARGV[0] =~ m{\A\-})) {
        my $opt = shift(@ARGV);
        if (0) {
        } elsif ($opt =~ m{\A\-n}) {
            $rmcount = shift(@ARGV);
        } else {
            print STDERR "unknown option $opt\n";
        }
    } # while $opt

	while (scalar(@ARGV) > 0) {
		my $inname = shift(@ARGV);
		open (SRC, "<", $inname) || die "cannot read \"$inname\"\n";
		my $buffer = "";
		my $count = 0;
		while (<SRC>) {
			$count ++;
			if ($count > $rmcount) {
				$buffer .= $_;
			} else {
				# skip first lines
			}
		} # while <SRC>
		close(SRC);
		open (TAR, ">", $inname) || die "cannot write \"$inname\"\n";
		print TAR $buffer;
		close(TAR);
	} # while ARGS
