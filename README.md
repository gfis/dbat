Dbat
====

Database Application / Administration / Batch Tool

Dbat is a tool that facilitates the interaction with JDBC-compatible relational databases like mySql, DB2, Oracle, SQLite, ODBC and - with appropriate JDBC drivers - even Excel sheets and plain text files. The tool is called on the commandline, in a makefile or an ant script. It can query tables, issue other SQL statements like CREATE, INSERT, DELETE, and it exports the SQL definition of a table and its contents.

SQL statements can be read from a file. The output may be plain (tab separated) text, HTML, XML or SQL (INSERT statements, optionally with JDBC escapes). For common queries like count(*) or x, count(x) ... group by x there are commandline shortcuts.

The tool can be called from Java programs for a quick and simple query from a database.

The servlet bundled with this application reads and interpretes XML specifications of queries and generates a HTML form that contains the result table. There are elements for parameter input fields, and for the insert/delete/update features of a simple web-based form application. 
 