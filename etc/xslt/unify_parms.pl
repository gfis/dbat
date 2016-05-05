#!/usr/bin/perl

# postprocess data for spec_index: squash multiple occurrences of parameters
# @(#) $Id$
# 2016-05-05: append "COMMIT;"
# 2016-04-16, Dr. Georg Fischer
#------------------------------------------------------------------ 
# Usage: c.f. makefile
#   perl -i*.bak unify_parms.pl etc/sql/spec_index_insert.sql
#------------------------------------------------------------------------------- 
use strict;

    while (<>) {
        my $line = $_;
        if (m{\A\t\,\'(\&[^\']+)\'}) {
            my $parms_column = $1;
            my $rest = $2;
            my @parms = split(/\&/, $parms_column);
            my %hash = ();
            foreach my $parm(@parms) {              
                my ($name, $value) = split(/\=/, $parm);
                if (0) {
                } elsif (! defined($hash{$name})) {
                    $hash{$name} = $value;
                } elsif (length($hash{$name}) == 0) {
                    $hash{$name} = $value;
                }                   
            } # foreach $parm
            $parms_column = "";
            foreach my $key(sort(keys(%hash))) {
                if (length($key) > 0) {
                    $parms_column .= "\&$key=$hash{$key}";
                }
            } # foreach $key
            print "\t,\'$parms_column\');\r\n";
        } else {
            print "$line";
        }
    } # while <>

    print <<GFis;
--
COMMIT;
GFis

__DATA__
# Example before processing by this Perl program

INSERT INTO spec_index (subdir, name, lang, title, comment, params) VALUES (
    'test','fragment01','de','fragment01',
    'Abfrage mit Fragment im Link '
    ,'&name=% &name= &name= ');
COMMIT;
INSERT INTO spec_index (subdir, name, lang, title, comment, params) VALUES (
    'test','grouping','de','grouping',
    'Test der wiederholten Überschriften beim Gruppenwechsel '
    ,'');
COMMIT;
INSERT INTO spec_index (subdir, name, lang, title, comment, params) VALUES (
    'test','highlight','de','highlight',
    'Liste der Spezifikationen mit Hervorhebung eines Schlagworts '
    ,'&keyword=color &keyword= &keyword= &keyword= ');
COMMIT;
INSERT INTO spec_index (subdir, name, lang, title, comment, params) VALUES (
    'test','image01','de','image01',
    'Abfrage einer Tabelle mit Bildern '
    ,'');
COMMIT;
