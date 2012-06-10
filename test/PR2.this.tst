java -cp ../dist/dbat.jar org.teherba.dbat.Dbat -c ../etc/worddb.properties -e UTF-8 -m probe -f ../web/spec/test/with_cte.xml
2012-05-08 20:50:11,678 1    [main] ERROR org.teherba.dbat.SQLAction  - You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'WITH cte as
		( select entry, enrel 
		  from words
		  where entry like 'backe%' at line 1
 com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'WITH cte as
		( select entry, enrel 
		  from words
		  where entry like 'backe%' at line 1
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:532)
	at com.mysql.jdbc.Util.handleNewInstance(Util.java:406)
	at com.mysql.jdbc.Util.getInstance(Util.java:381)
	at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:1051)
	at com.mysql.jdbc.MysqlIO.checkErrorPacket(MysqlIO.java:3563)
	at com.mysql.jdbc.MysqlIO.checkErrorPacket(MysqlIO.java:3495)
	at com.mysql.jdbc.MysqlIO.sendCommand(MysqlIO.java:1959)
	at com.mysql.jdbc.MysqlIO.sqlQueryDirect(MysqlIO.java:2113)
	at com.mysql.jdbc.ConnectionImpl.execSQL(ConnectionImpl.java:2687)
	at com.mysql.jdbc.ConnectionImpl.execSQL(ConnectionImpl.java:2616)
	at com.mysql.jdbc.StatementImpl.executeQuery(StatementImpl.java:1464)
	at org.teherba.dbat.SQLAction.execSQLStatement(SQLAction.java:1465)
	at org.teherba.dbat.SpecificationHandler.endElement(SpecificationHandler.java:1781)
	at com.sun.org.apache.xerces.internal.parsers.AbstractSAXParser.endElement(AbstractSAXParser.java:604)
	at com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl.scanEndElement(XMLDocumentFragmentScannerImpl.java:1759)
	at com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl$FragmentContentDriver.next(XMLDocumentFragmentScannerImpl.java:2915)
	at com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl.next(XMLDocumentScannerImpl.java:625)
	at com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl.next(XMLNSDocumentScannerImpl.java:116)
	at com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl.scanDocument(XMLDocumentFragmentScannerImpl.java:488)
	at com.sun.org.apache.xerces.internal.parsers.XML11Configuration.parse(XML11Configuration.java:819)
	at com.sun.org.apache.xerces.internal.parsers.XML11Configuration.parse(XML11Configuration.java:748)
	at com.sun.org.apache.xerces.internal.parsers.XMLParser.parse(XMLParser.java:123)
	at com.sun.org.apache.xerces.internal.parsers.AbstractSAXParser.parse(AbstractSAXParser.java:1208)
	at com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl$JAXPSAXParser.parse(SAXParserImpl.java:525)
	at javax.xml.parsers.SAXParser.parse(SAXParser.java:392)
	at org.teherba.dbat.Dbat.parseXML(Dbat.java:228)
	at org.teherba.dbat.Dbat.processXMLFile(Dbat.java:271)
	at org.teherba.dbat.Dbat.process(Dbat.java:597)
	at org.teherba.dbat.Dbat.processArguments(Dbat.java:677)
	at org.teherba.dbat.Dbat.main(Dbat.java:697)
