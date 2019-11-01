#!perl

# Extract last revisions from a MediaWiki dumpBackup XML file
# @(#) $Id $
# 2019-11-01, Georg Fischer

use strict;

my $title = "";
my $state = 0; # search for text; 1 = in text
while (<>) { # read XML
    my $line = $_;
    if (0) {
    } elsif ($state == 0 and ($line =~ m{\A\s*\<title\>([^\<]*)\<\/})) {
        $title = $1;
        $title =~ s/ /\-/g;
    } elsif ($state == 0 and ($line =~ m{\A\s*\<text xml\:space\=\"preserve\" bytes\=\"\d+\"\>(.*)})) {
        $line = &decode($1);
        open(WIKI, ">", "$title.mediawiki");
        print "writing $title.mediawiki\n";
        print WIKI "$line\n";
        $state = 1;
    } elsif ($state == 1) {
        $line = &decode($line);
        if ($line =~ m{\<\/text\>\s*\Z}) {
            $line =~ s{\<\/text\>}{};
            $state = 0; # search for next <text>
            print WIKI $line;
            close(WIKI);
        } else {
            print WIKI $line;
        }
    }
} # while <>

sub decode {
    my ($text) = @_;
    $text =~ s{\&amp;}{\&}g;
    $text =~ s{\&lt\;code\&gt\;}{\<code\>}g;
    $text =~ s{\&lt\;\/code\&gt\;}{\<\/code\>}g;
    if ($text =~ m{\A\s}) {
        $text =~ s{\&lt\;}{\<}g;
        $text =~ s{\&gt\;}{\>}g;
        $text =~ s{\&quot\;}{\"}g;
        $text =~ s{\&amp\;}{\&}g;
    }
    return $text;
} # sub decode
__DATA__
<mediawiki xmlns="http://www.mediawiki.org/xml/export-0.10/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd" version="0.10" xml:lang="en">
  <siteinfo>
    <sitename>dbwiki</sitename>
    <dbname>db_wiki</dbname>
    <base>http://www.teherba.org/dbat/index.php/Main_Page</base>
    <generator>MediaWiki 1.27.0</generator>
    <case>first-letter</case>
    <namespaces>
      <namespace key="-2" case="first-letter">Media</namespace>
      <namespace key="-1" case="first-letter">Special</namespace>
      <namespace key="0" case="first-letter" />
      <namespace key="1" case="first-letter">Talk</namespace>
      <namespace key="2" case="first-letter">User</namespace>
      <namespace key="3" case="first-letter">User talk</namespace>
      <namespace key="4" case="first-letter">Dbwiki</namespace>
      <namespace key="5" case="first-letter">Dbwiki talk</namespace>
      <namespace key="6" case="first-letter">File</namespace>
      <namespace key="7" case="first-letter">File talk</namespace>
      <namespace key="8" case="first-letter">MediaWiki</namespace>
      <namespace key="9" case="first-letter">MediaWiki talk</namespace>
      <namespace key="10" case="first-letter">Template</namespace>
      <namespace key="11" case="first-letter">Template talk</namespace>
      <namespace key="12" case="first-letter">Help</namespace>
      <namespace key="13" case="first-letter">Help talk</namespace>
      <namespace key="14" case="first-letter">Category</namespace>
      <namespace key="15" case="first-letter">Category talk</namespace>
    </namespaces>
  </siteinfo>
  <page>
    <title>Main Page</title>
    <ns>0</ns>
    <id>1</id>
    <revision>
      <id>1</id>
      <timestamp>2016-08-07T09:32:29Z</timestamp>
      <contributor>
        <username>MediaWiki default</username>
        <id>0</id>
      </contributor>
      <model>wikitext</model>
      <format>text/x-wiki</format>
      <text xml:space="preserve" bytes="687">&lt;strong&gt;MediaWiki has been installed.&lt;/strong&gt;

Consult the [//meta.wikimedia.org/wiki/Help:Contents User's Guide] for information on using the wiki software.

== Getting started ==
* [//www.mediawiki.org/wiki/Special:MyLanguage/Manual:Configuration_settings Configuration settings list]
* [//www.mediawiki.org/wiki/Special:MyLanguage/Manual:FAQ MediaWiki FAQ]
* [https://lists.wikimedia.org/mailman/listinfo/mediawiki-announce MediaWiki release mailing list]
* [//www.mediawiki.org/wiki/Special:MyLanguage/Localisation#Translation_resources Localise MediaWiki for your language]
* [//www.mediawiki.org/wiki/Special:MyLanguage/Manual:Combating_spam Learn how to combat spam on your wiki]</text>
      <sha1>d8wxfy88z1e4to5a0a0iwrfhw943fet</sha1>
    </revision>
    <revision>
      <id>3</id>
      <parentid>1</parentid>
      <timestamp>2016-08-07T09:50:09Z</timestamp>
      <contributor>
        <username>Gfis</username>
        <id>2</id>
      </contributor>
      <comment>from github</comment>
      <model>wikitext</model>
      <format>text/x-wiki</format>
      <text xml:space="preserve" bytes="1968">'''Dbat''' is a tool that facilitates the interaction with JDBC-compatible relational databases like mySQL, DB2, Oracle, SQLite, ODBC
and - with appropriate JDBC drivers - even MS-Excel spreadsheets and plain text files.
The tool is called on the commandline, in a ''makefile'' or an ''ant'' script.
It can query tables, issue other SQL statements like CREATE, INSERT, UPDATE, DELETE, and it may export the SQL definition of a table and its contents.

SQL statements can be read from a file. The results can be presented in plain (tab separated) text, XHTML, XML, SQL, MediaWiki tables, JSON
and several other [[Output Formats|formats]]. For common queries like &lt;code&gt;count(*)&lt;/code&gt; or &lt;code&gt;x, count(x) ... group by x&lt;/code&gt;
there are commandline shortcuts.

The tool can be called from Java programs for a quick and simple query from a database.

The servlet bundled with this application reads and interpretes XML specifications of queries and generates a HTML form that contains the result table.
There are elements for parameter input fields, and for the insert/delete/update features of a simple web-based form application.
The web presentation of the result tables may be influenced in specific ways. For example, cells may be colored depending on the cell value,
and links on cell values may be used to provide a comfortable web navigation through a complicated net of relational data.

The English documentation is work in progress. Some pages were already started:
* [[Getting Started]]
* [[Specification Elements]], [[Conditional Interpretation]], [[Include Feature]]
* [[Output Formats]]
* [[Auxiliary Web Pages]]: [[Help Page]], [[SQL Console]]
* [[JDBC Driver Configuration]]
* [[Software Quality]], [[Change Management]], [[Regression Testing]]
* [[SQLJ Support]]

There is a more complete [[Home.de|German documentation]].

The acronym ''Dbat'' can be thought of an abbreviation of ''Database Administration Tool'' or of ''Database Application Tool''.</text>
      <sha1>lqi693otofo6q4yzzhbybitg0kyeedx</sha1>
    </revision>
    <revision>
      <id>22</id>
      <parentid>3</parentid>
      <timestamp>2016-08-07T10:21:28Z</timestamp>
      <contributor>
        <username>Gfis</username>
        <id>2</id>
      </contributor>
      <model>wikitext</model>
      <format>text/x-wiki</format>
      <text xml:space="preserve" bytes="1969">'''Dbat''' is a tool that facilitates the interaction with JDBC-compatible relational databases like mySQL, DB2, Oracle, SQLite, ODBC
and - with appropriate JDBC drivers - even MS-Excel spreadsheets and plain text files.
The tool is called on the commandline, in a ''makefile'' or an ''ant'' script.
It can query tables, issue other SQL statements like CREATE, INSERT, UPDATE, DELETE, and it may export the SQL definition of a table and its contents.

SQL statements can be read from a file. The results can be presented in plain (tab separated) text, XHTML, XML, SQL, MediaWiki tables, JSON
and several other [[Output Formats|formats]]. For common queries like &lt;code&gt;count(*)&lt;/code&gt; or &lt;code&gt;x, count(x) ... group by x&lt;/code&gt;
there are commandline shortcuts.

The tool can be called from Java programs for a quick and simple query from a database.

The servlet bundled with this application reads and interpretes XML specifications of queries and generates a HTML form that contains the result table.
There are elements for parameter input fields, and for the insert/delete/update features of a simple web-based form application.
The web presentation of the result tables may be influenced in specific ways. For example, cells may be colored depending on the cell value,
and links on cell values may be used to provide a comfortable web navigation through a complicated net of relational data.

The English documentation is work in progress. Some pages were already started:
* [[Getting Started]]
* [[Specification Elements]], [[Conditional Interpretation]], [[Include Feature]]
* [[Output Formats]]
* [[Auxiliary Web Pages]]: [[Help Page]], [[SQL Console]]
* [[JDBC Driver Configuration]]
* [[Software Quality]], [[Change Management]], [[Regression Testing]]
* [[SQLJ Support]]

There is an outdated [[Main_Page/de|German documentation]].

The acronym ''Dbat'' can be thought of an abbreviation of ''Database Administration Tool'' or of ''Database Application Tool''.</text>
      <sha1>mii3ba6i5eet8eq45twyvbw7fo9j6ux</sha1>
    </revision>
    <revision>
      <id>108</id>
      <parentid>22</parentid>
      <timestamp>2016-08-28T19:04:08Z</timestamp>
      <contributor>
        <username>Gfis</username>
        <id>2</id>
      </contributor>
      <minor/>
      <comment>Redirection</comment>
      <model>wikitext</model>
      <format>text/x-wiki</format>
      <text xml:space="preserve" bytes="1986">'''Dbat''' is a tool that facilitates the interaction with JDBC-compatible relational databases like mySQL, DB2, Oracle, SQLite, ODBC
and - with appropriate JDBC drivers - even MS-Excel spreadsheets and plain text files.
The tool is called on the commandline, in a ''makefile'' or an ''ant'' script.
It can query tables, issue other SQL statements like CREATE, INSERT, UPDATE, DELETE, and it may export the SQL definition of a table and its contents.

SQL statements can be read from a file. The results can be presented in plain (tab separated) text, XHTML, XML, SQL, MediaWiki tables, JSON
and several other [[Output Formats|formats]]. For common queries like &lt;code&gt;count(*)&lt;/code&gt; or &lt;code&gt;x, count(x) ... group by x&lt;/code&gt;
there are commandline shortcuts.

The tool can be called from Java programs for a quick and simple query from a database.

The servlet bundled with this application reads and interpretes XML specifications of queries and generates a HTML form that contains the result table.
There are elements for parameter input fields, and for the insert/delete/update features of a simple web-based form application.
The web presentation of the result tables may be influenced in specific ways. For example, cells may be colored depending on the cell value,
and links on cell values may be used to provide a comfortable web navigation through a complicated net of relational data.

The English documentation is work in progress. Some pages were already started:
* [[Getting Started]]
* [[Specification Elements]], [[Conditional Interpretation]], [[Include Feature]], [[Redirection]]
* [[Output Formats]]
* [[Auxiliary Web Pages]]: [[Help Page]], [[SQL Console]]
* [[JDBC Driver Configuration]]
* [[Software Quality]], [[Change Management]], [[Regression Testing]]
* [[SQLJ Support]]

There is an outdated [[Main_Page/de|German documentation]].

The acronym ''Dbat'' can be thought of an abbreviation of ''Database Administration Tool'' or of ''Database Application Tool''.</text>
      <sha1>bw1j7m6roy6w3kq7s0rph6yrh13ohiy</sha1>
    </revision>
    <revision>
      <id>127</id>
      <parentid>108</parentid>
      <timestamp>2017-02-12T15:48:40Z</timestamp>
      <contributor>
        <username>Gfis</username>
        <id>2</id>
      </contributor>