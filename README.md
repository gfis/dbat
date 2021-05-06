Dbat
====

Database Application / Administration / Batch Tool

Dbat is a tool that facilitates the interaction with any JDBC-compatible relational database like mySql, DB2, Oracle, SQLite, MS SQL Server, ODBC and - with appropriate JDBC drivers - even Excel sheets and plain text files. The tool may be called on the commandline, in a makefile or an ant script. It can query tables, issue other SQL statements like CREATE, INSERT, DELETE, and it exports the SQL definition of a table and its contents.

SQL statements can be read from a file. The output may be plain (tab or delimiter separated) text, HTML, XML, Excel, SQL (INSERT statements, optionally with JDBC escapes), Wiki ta bles and several more formats. For common queries like COUNT(\*) OR x, COUNT(x) ... GROUP BY x there are commandline shortcuts.

The tool can be called from Java programs for a quick and simple query from a database.

For web applications there is a servlet that reads and interpretes XML specifications of queries and generates an HTML form that contains the result table. There are elements for parameter input fields, and for the INSERT/DELETE/UPDATE features of a simple web-based form application. 
 
More details can be found in the [Dbat wiki](https://github.com/gfis/dbat/wiki). 

Please write to dr.georg.fischer@gmail.com if you desire write access there.
