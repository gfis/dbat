#!/usr/bin/perl

# Test Dbat functions on the commandline and from the local webserver, and XSLT scripts 
# @(#) $Id$
# 2012-11-22: QUERY_STRING modified if not starting with "test/"
# 2012-10-19: "am" before timestamp hindered the substitution
# 2012-06-27: previous version was batch_test.pl which suppressed lines with timestamps;
#             now the timestamps are replaced by "yyyy-mm-dd hh:mm:ss"
# 2012-06-20:  . "\" 2>\&1"; after all commands
# 2012-04-25: XSLT command 
# 2012-04-02: diff --strip-trailing-cr
# 2011-11-15: general filtering of "-- MySQL 5."
# 2011-08-10: testcases now in separate files, pattern for tc number
# 2011-08-02: remove ms count in "affecting ... rows in ... ms"
# 2011-07-27: WGET without implied subdirectory "test/"; -single
# 2011-05-23, Dr. Georg Fischer
#------------------------------------------------------------------
#
#  Copyright 2011 Dr. Georg Fischer <punctum at punctum dot kom>
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
#
#------------------------------------------------------------------ 
# Test driver which runs specific test cases from the commandline 
# or from the web application, stores the results in xnn.prev.tst
# (for -fill|-ref), or stores them in xnn.this.test, and compares them 
# with the results previously stored in xnn.prev.tst (for -comp).
# The reference results in xnn.prev.tst should be put under 
# version control (e.g. svn, git), together with the 
# testcase definitions in all.tests.
#
# Usage: 
#   perl all_tester.pl [-fill|-ref|-comp] [-only testnr] [<] file1 file2 ...
#       -fill               (re-)create test/*.prev.tst files
#       -ref                same as -fill  
#       -comp               compare test/*.prev.tst with test/*.this.tst
#       -only pattern       process only the test numbers matching this pattern (Perl, but % -> .*)
#       file1, file2 ...    text files containing testcase definitions, or STDIN
# 
# The testcases are built from text lines which start with a 3- or 4-letter action verb.
# This test driver knows the following verbs:
# 
#   TEST xnn comment        Start test case xnn
#   DATA data               "here is" data for a temporary file xnn.data.tmp;
#                           these data may be continued on subsequent lines
#                           as long as they do not start with a Letter.
#   XML  data               Same as DATA, but name the file xnn.data.xml
#   JRUN options            Run a CLI test with the main method of the "Main-Class" of the Jar file  
#   CRUN options            Run a CLI test with the main method of the specified class 
#   WGET name=value ...     Run a Web interface test with parameters
#   ECHO xnn                Write a start message with command versions into the test log
#   GREP pattern            Filter test output, ignore all lines matching pattern
#   XSLT xslfile args       Run an XSLT transformation with xsltproc
#   # comment               Perl-style comments and empty lines are ignored.
#
# Used external utilities / commands (from Unix or cygwin (under Windows)):
#   java        >= V1.6 commandline activation of Java classes
#   dbat.jar    containing Dbat.main and other main methods to be tested, c.f. $jrun, $crun below
#   wget        fetch a web page from the commandline, c.f. $wget below
#   Tomcat      >= V6.0 servlet container, for example on http://localhost:8080/dbat/, where 
#   dbat.war        is deployed together with the Dbat test specifications web/spec/test/*.xml, c.f. $wget below
#   diff -C0    file comparision
#   perl        >= V5.8 (of course for this script)
#   MySQL       >= V5.0 (or another JDBC database) on the local host, as configured in etc/dbat.properties
#   make        (optional) + grep, head, rm, wc for suitable evaluation of test runs
#
# A typical makefile contains the following targets for regression testing:
#
#   fill:
#       cd test ; perl all_tester.pl -fill all.tests
#   comp:
#       cd test ; perl all_tester.pl -comp all.tca | tee regression.log 
#   eval:
#       rm -f [pF]*[dD].tests
#       grep -E "FAILED" test/regression.log
#       grep -E "passed" test/regression.log > passed.tests
#       grep -E "FAILED" test/regression.log > FAILED.tests
#       wc -l *.tests | head -2
#   fill_only:
#       cd test ; perl all_tester.pl -fill -only $(TEST) all.tests
#   only: 
#       cd test ; perl all_tester.pl -comp -only $(TEST) all.tests
#   show:
#       make -i show1 TEST=$(TEST) | less
#   show1:
#       diff -C0 test/$(TEST).prev.tst test/$(TEST).this.tst
#       head -2000 test/$(TEST).*.tst 
#   
#------------------------------------------------------------------------------- 
use strict;

    my $version = ""; # fixed, was "1" in an intermediate state 
    my $err_redir = ""; # or  " 2>\&1";
    my $action = "comp"; # default - is harmless, but "fill" destroys the test history 
    my $only = ".*"; # run all tests, otherwise a pattern for a subset; "%" works like ".*"
    my ($sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdst) = localtime (time);
    my $timestamp = sprintf ("%04d-%02d-%02d %02d:%02d:%02d", $year + 1900, $mon + 1, $mday, $hour, $min, $sec);
    while (scalar(@ARGV) > 0 && ($ARGV[0] =~ m{\A\-})) {
        my $opt = shift(@ARGV);
        if (0) {
        } elsif ($opt =~ m{\A\-comp}) {
            $action = substr($opt, 1);
        } elsif ($opt =~ m{\A\-fill}) {
            $action = substr($opt, 1);
        } elsif ($opt =~ m{\A\-ref}) {
            $action = "fill";
        } elsif ($opt =~ m{\A\-only}) {
            $only = shift(@ARGV);
            $only =~ s{\%}{\.\*};
        } elsif ($opt =~ m{\A\-v}) {
            $version = shift(@ARGV); # 1
        } else {
            print STDERR "unknown option $opt\n";
        }
    } # while $opt

    # the following 3 commands should be modified for tests of other applications
    my $jrun = "java -cp ../dist/dbat.jar org.teherba.dbat.Dbat$version -c ../etc/worddb.properties -e UTF-8"; # commandline activation
    my $wget = "wget -q -O - \"http://localhost:8080/dbat/servlet$version?"; # prefix of the command for a web request
    my $crun = "java -cp ../dist/dbat.jar org.teherba"; # prefix for the activation of the main method of a different class
    my $xslt = "xsltproc "; # prefix of the command for an XSLT transformation
    
    my $grep; # contains the variable expression from GREP command for filtering of dates, versions etc.
    my $default_grep; # contains the fixed expression for WGET commands
    my $subst;          # contains the variable expression from SUBST command for substituting of timestamps, versions etc.
    my $default_subst;  # contains the fixed    expression for WGET commands
    $default_subst = 's{(on|at|am) 2[0-1]\d\d\-[0-1]\d\-[0-3]\d [0-2]\d\:[0-5]\d\:[0-5]\d}{$1 yyyy-mm-dd hh:mm:ss}g';

    my $buffer = ""; # for file contents
    my $file_extension; # of temporary SQL or XML file
    my $command; # final command to be issued
    my $testcase; # number of the testcase
    my $description; # of the testcase
    
    while (<>) {
        if (m{\A(\w+)\s*(.*)}) {
            my $verb    = $1;
            my $options = $2;
            $options =~ s/\s+\Z//;
            if (length($buffer) > 0) { 
                my $filename = "$testcase.data.$file_extension";
                open(OUT, ">", $filename) || die "cannot write $filename";
                print OUT $buffer;
                close(OUT);
                $buffer = "";
            } 
            if (0) {
            } elsif ($verb =~ m/CRUN/i) { # java -cp dist/dbat.jar classname ...
                my @args = split(/\s+/, $options);
                $command = "$crun." . join(' ', @args) . $err_redir;
            #   $default_grep = qr/Output\s+on\s+\d{4}\-\d{2}\-\d{2}/;
                &execute_test($command);
            } elsif ($verb =~ m/DATA/i) {
                $buffer = "$options\n";
                $file_extension = "tmp";
            } elsif ($verb =~ m/JRUN/i) {
                my @args = split(/\s+/, $options);
                $command = "$jrun " . join(" ", @args) . $err_redir;
                $default_grep = "\A\Z";
                &execute_test($command);
            } elsif ($verb =~ m/ECHO/i) {
                $grep = "\A\Z";
                ($testcase, $description) = split(/\s+/, $options, 2);
                $command = "echo $jrun $wget $timestamp";
                sleep(1); # in order to force a change of the timestamp
                &execute_test($command);
            } elsif ($verb =~ m/GREP/i) {
                $grep = $options;
            } elsif ($verb =~ m/TEST/i) {
                $grep = "";
                $default_grep = qr/on\s+\d{4}\-\d{2}\-\d{2}/;
                ($testcase, $description) = split(/\s+/, $options, 2);
                $testcase = uc($testcase);
            } elsif ($verb =~ m/WGET/i) {
                my @args = split(/\s+/, $options);
                $command = "$wget" 
                    # . ( ($args[0] =~ m{\A(test[\.\/]|describe)}) ? "spec=" : "") 
                    . join('&', @args) . "\"$err_redir";
            #   $default_grep = qr/Output\s+on\s+\d{4}\-\d{2}\-\d{2}/;
                &execute_test($command);
            } elsif ($verb =~ m/XML/i) {
                $buffer = "$options\n";
                $file_extension = lc($verb);
            } elsif ($verb =~ m/XSLT/i) {
                my @args = split(/\s+/, $options);
                $command = $xslt . join(" ", @args) . $err_redir;
                $default_grep = qr/\s+at\s+\d{4}\-\d{2}\-\d{2}/;
                &execute_test($command);
            } else {
                print STDERR "unknown verb $verb, options=\"$options\"\n";
            }
        } elsif (m{\A\s*\#}) { # comment - ignore
        } elsif (m{\A\s*\Z}) { # empty line - ignore
        } else { # does not start with a verb - continuation of last line
            $buffer .= $_;
        }
    } # while <>
    
sub execute_test {
    if ($testcase !~ m{\A$only}) {
        return;
    }
    my ($command) = @_;
    # print STDERR "execute: $command\n";
    my $command_d = $command;
    $command_d =~ s{\Ajava}{java -Djdk.net.registerGopherProtocol=true};
    my $this_result = `$command_d 2>&1`;   
    my $subst = $default_subst;
    my $expr = '$this_result =~ ' . $default_subst . ';';
    # lestprint STDERR "evaluate: " . $expr . "\n";
    eval($expr);
    $this_result =~ s{affecting (\d+) rows in \d+ ms}{affecting $1 rows in ... ms}g;
    my $this_name = "$testcase." . ($action eq "comp" ? "this" : "prev") . ".tst";
    my $prev_name = $this_name;
    $prev_name =~ s/\.this\./.prev./;
    open(THIS, ">", $this_name) || die "cannot write $this_name";
    print THIS "$command\n";
    print THIS $this_result;
    close(THIS);
    my @diff2 = ();
    my @diff = split(/\r?\n/, `diff -C0 --strip-trailing-cr $prev_name $this_name 2>\&1`);
    if (1) { # regular filter expression was specified
        my $empty = 1;
        @diff2 = map { 
                if (m{\A[\!\+\-] }) { # if it is a significant changed/added/deleted difference line
                    $empty = 0;
                }
                $_
        #   } grep { # filter by default reg. expr. if WGET
        #       ($command !~ m{\A(wget|xslt)}i) or ($_ !~ m{$default_grep}i)
        #   } grep { # filter by regular expression from GREP command, if any
        #       length($grep) == 0 or (! m{$grep}) 
            } grep { # filter database product line
                ! m{\-\-\s+MySQL \d\.\d}i
            } @diff; 
        if ($empty) {
            @diff2 = ();
        }
    } # $grep ne ""
    print "Test $testcase $description\n$command\n", join("\n", @diff2),  (scalar(@diff2) > 0 ? "\n" : "");
    if ($action eq "comp") {
        print "" . (scalar(@diff2) == 0 ? "++ passed" : "-- FAILED") . " $testcase $description\n";
    }
} # execute_test
    
__DATA__
# these are examples only

ECHO A00 

TEST A01 parameterized link
GREP on (\d{4}\-\d{2}\-\d{2})
JRUN -m html -f ../web/spec/test/parmlink.xml

TEST B04 create table with CLOB
DATA DROP TABLE if exists b04;
  CREATE TABLE            b04 
  ( name VARCHAR(16) not null   COMMENT 'key for the LOB'
  , len  INT                    COMMENT 'size of the LOB content'
  , content TEXT                COMMENT 'character large object (CLOB)'
  ) ENGINE=MyISAM CHARACTER SET utf8 COLLATE utf8_bin;
  COMMIT;
JRUN -f B04.data.tmp

TEST P04 Web pivot test with HTML
WGET test/pivot03
