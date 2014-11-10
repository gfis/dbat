#!/usr/bin/perl

# Insert git's version hash into $Id keywords in a file (in place)
# @(#) $Id$
# 2014-11-10: insert a timestamp outside the $\Id keyword
# 2014-11-08, Georg Fischer
#
# Usage:
#   perl git_version.pl file ...
#------------------------------------------------------------------
#
#  Copyright 2014 Dr. Georg Fischer <punctum at punctum dot kom>
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
    my $rmcount = 1; # not used
    while (scalar(@ARGV) > 0 && ($ARGV[0] =~ m{\A\-})) {
        my $opt = shift(@ARGV);
        if (0) {
        } elsif ($opt =~ m{\A\-n}) {
            $rmcount = shift(@ARGV);
        } else {
            print STDERR "unknown option $opt\n";
        }
    } # while $opt

    my ($sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdst) = localtime (time);
    my $timestamp = sprintf ("%04d-%02d-%02d %02d:%02d:%02d", $year + 1900, $mon + 1, $mday, $hour, $min, $sec);
    my $githash = `git show-ref refs/heads/master`;
    # yields: "456def596d0e86726a714502ad0405016beb10c5 refs/heads/master\n"
    $githash =~ s/\s.*//s; # remove all behind hash

    while (scalar(@ARGV) > 0) {
        my $inname = shift(@ARGV);
        open (SRC, "<", $inname) || die "cannot read \"$inname\"\n";
        my $buffer = "";
        my $count = 0;
        while (<SRC>) {
            my $line = $_;
            if ($line =~ m/\$\Id: [^\$]*\$/) {
                if ($line =~ m/CVSID =/) {
                    $line =~ s{\$\Id: [^\$]*\$}
                              {\$\Id: $inname $githash ${timestamp} $ENV{"USER"} \$}; # must shield $Id with \I
                } else {
                    $line =~ s{\$\Id: [^\$]*\$.*}
                              {\$\Id: $githash \$ $timestamp $ENV{"USER"}}; # must shield $Id with \I
                } # not the CVSID
            } # with $\Id
            $buffer .= $line;
        } # while <SRC>
        close(SRC);
        open (TAR, ">", $inname) || die "cannot write \"$inname\"\n";
        print TAR $buffer;
        close(TAR);
    } # while ARGS
