/*  Configuration.java - DataSource and user defineable properties for a JDBC connection
 *  @(#) $Id$ 2016-04-16 14:43:35
 *  2020-11-06: set|getExecSQL, default 1
 *  2018-02-13: set|getEmailAddress
 *  2018-01-19: loadContextEnvironment()
 *  2018-01-11: toString(); getConsoleMap
 *  2017-05-27: javadoc 1.8
 *  2016-10-13: less imports
 *  2016-09-16: log.info() without caller's name; Locale.setDefault; better versionString
 *  2016-09-12: getDataSourceMap (from DbatServlet.init)
 *  2016-05-24: getConnection -> getOpenConnection
 *  2016-05-17: decimalSeparator
 *  2016-04-16: read versionString from classloader's META-INF/MANFEST.MF, scan through all resources
 *  2014-11-11: major version 9; update $\Id content with etc/util/git_version.pl
 *  2014-11-03: always respond with MIME type application/xhtml+xml
 *  2014-02-16: application/xhtml+xml if System.getProperty("os.name"       ).startsWith("Windows 8")
 *  2012-06-19: manner = JDBC or SQLJ; for DB2: TRANSACTION_READ_UNCOMMITTED
 *  2012-06-12: Javadoc cleaned for github
 *  2012-03-12: DBAN_URI; works for Geronimo, WASCE
 *  2012-01-31: BasicDataSource no longer used
 *  2012-01-25: fallback to dbat.properties if dataSourceMap not filled by DBCPoolingListener
 *  2011-11-11: set|getVerbose; Config.DB2 etc.
 *  2011-09-17: default connection = worddb
 *  2011-08-23, major version 6: with writeGenericRow
 *  2011-05-06, Dr. Georg Fischer: extracted from Dbat.java
 */
/*
 * Copyright 2011 Dr. Georg Fischer <punctum at punctum dot kom>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.teherba.dbat;
import  org.teherba.common.CommandTokenizer;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.xtrans.BaseTransformer;
import  java.io.File;
import  java.io.FileInputStream;
import  java.io.InputStream;
import  java.io.Serializable;
import  javax.naming.Context;
import  javax.naming.InitialContext;
import  java.net.URL;
import  java.sql.Connection;
import  java.sql.DriverManager;
import  java.util.Enumeration;
import  java.util.HashMap;
import  java.util.Iterator;
import  java.util.LinkedHashMap;
import  java.util.Locale;
import  java.util.Map;
import  java.util.Properties;
import  javax.sql.DataSource;
import  org.apache.log4j.Logger;

/** This bean encapsulates the JDBC database connection, its properties
 *  and other user defineable properties related with it.
 *  @author Dr. Georg Fischer
 */
public class Configuration implements Serializable {
    public final static String CVSID = "@(#) $Id$";
    /** log4j logger (category) */
    private Logger log;

    //========================
    // Constants
    //========================

    /** Namespace URI of old Dbat specifications */
    public static final String DBAT_URI = "http://www.teherba.org/2007/dbat";
    /** Namespace URI of new Dbat specifications */
    public static final String DBIV_URI = "http://www.teherba.org/2011/dbiv";
    /** Namespace URI of HTML tags (&lt;form&gt;, &lt;select&gt; and so on) */
    public static final String HTML_URI = "http://www.w3.org/1999/xhtml";
    /** Name of the pseudo variable parameter for updateCount = number of affected rows */
    public static final String UPDATE_COUNT = "update_count";
    /** Name of the pseudo variable parameter for remote user */
    public static final String REMOTE_USER = "remote_user";
    /** Name of the pseudo variable parameter for SQL state */
    public static final String SQL_STATE = "sql_state";

    //============================================
    // Bean properties, getters and setters
    // Some are overrideable in SQLProcessor.java
    //============================================

    /** whether the connection {@link #con} is automatically committed */
    private boolean autoCommit;
    /** Gets the commit behaviour of connections
     *  @return whether connections are automatically committed
     */
    public boolean hasAutoCommit() {
        return autoCommit;
    } // hasAutoCommit
    /** Sets commit behaviour
     *  @param auto whether connections are automatically committed
     */
    public void setAutoCommit(boolean auto) {
        autoCommit = auto;
    } // setAutoCommit
    //--------
    /** when called from the commandline - no DB connection pooling */
    public static final int CLI_CALL  = 0;
    /** when called from the web application - Tomcat's DBCP */
    public static final int WEB_CALL  = 1;
    /** when called as a SOAP service - ??? (like CLI_CALL so far) */
    public static final int SOAP_CALL = 2;
    /** when called from the commandline - use BasicDataSource */
    // public static final int DSO_CALL  = 3;
    /** Remembers the type of activation (one of the xxx_CALL constants) */
    private int callType;
    /** Gets the type of activation.
     *  @return one of the constants *_CALL
     */
    public int getCallType() {
        return callType;
    } // getCallType
    //--------
    /** Opened connection to some database */
    private Connection con;

    /** Eventually open the connection and return it
     *  @return open database connection
     */
    public Connection getOpenConnection() {
        if (con == null) {
            con = openConnection();
        }
        return con;
    } // getOpenConnection

    /** Short identifier of the connection to some database */
    private String connectionId;
    /** Gets the connection id
     *  @return a short identifier for a database connection
     */
    public String getConnectionId() {
        return this.connectionId;
    } // getConnectionId
    /** Sets the connection id
     *  @param connectionId short identifier for a database connection
     */
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    } // setConnectionId(1)
    /** Sets the connection id to the default: take first key in {@link #dataSourceMap}
     */
    public void setConnectionId() {
        if (dataSourceMap != null) {
            boolean busy = true;
            Iterator<String> miter = dataSourceMap.keySet().iterator();
            while (busy && miter.hasNext()) {
                this.connectionId = miter.next();
                busy = false; // take first only
            } // while busy
        } // dataSourceMap != null
    } // setConnectionId(0)
    //--------
    /** Property for the behaviour of the SQL console window */
    private String console;
    /** no console window (default) */
    public static final String CONSOLE_NONE   = "no access";
    /** console window with read access */
    public static final String CONSOLE_SELECT = "SELECT";
    /** console window with modify access */
    public static final String CONSOLE_UPDATE = "UPDATE";
    /** Gets the console property
     *  @return one of {@link #CONSOLE_NONE}, {@link #CONSOLE_SELECT}, {@link #CONSOLE_UPDATE}
     */
    private String getConsole() {
        return this.console;
    } // getConsole
    /** Sets the console access property
     *  @param console one of "none", "select" or "update"
     */
    private void setConsole(String console) {
        if (false) {
        } else if (console.equals("none"  )) {
            this.console = CONSOLE_NONE;
        } else if (console.equals("select")) {
            this.console = CONSOLE_SELECT;
        } else if (console.equals("update")) {
            this.console = CONSOLE_UPDATE;
        } else { // default
            this.console = CONSOLE_NONE;
        }
    } // setConsole
    /** Whether the SQL console window should be shown
     *  @return false if "console=none", true otherwise
     */
    public boolean hasConsole() {
        return ! this.console.equals(CONSOLE_NONE);
    } // hasConsole

    /** Maps connection identifiers (short database instance ids) to a console access property */
    private LinkedHashMap<String, String    > consoleMap;
    /** Maps connection identifiers (short database instance ids) to {@link DataSource Datasources} */
    private LinkedHashMap<String, DataSource> dataSourceMap;

    /** Determine the mapping from connectionIds to CONSOLE_* properties
     *  from the environment variable "java:comp/env/console" set in <em>dbat/etc/META-INF/context.xml</em>
     *  @return Mapping from short strings to constants "SELECT|UPDATE";
     *  connections with "none" are not stored
     */
    public LinkedHashMap<String, String> getConsoleMap() {
        return consoleMap;
    } // getConsoleMap

    /** Determine the mapping from connectionIds to {@link DataSource}s
     *  from the environment variable "java:comp/env/dataSources" set in <em>dbat/etc/META-INF/context.xml</em>
     *  @return Mapping from short strings to data sources
     */
    public LinkedHashMap<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    } // getDataSourceMap

    //--------
    /** Character for the decimal point or comma */
    private String  decimalSeparator;
    /** Gets the decimal separator
     *  @return a point or comma
     */
    public String getDecimalSeparator() {
        return this.decimalSeparator;
    } // getDecimalSeparator
    /** Sets the decimal separator
     *  @param separator a point or comma
     */
    public void setDecimalSeparator(String separator) {
        this.decimalSeparator = separator;
    } // setDecimalSeparator
    //--------
    /** Schema which is used when none is specified with the table's name */
    private String  defaultSchema;
    /** Gets the default schema
     *  @return schema which is used when none is specified with the table's name
     */
    public String getDefaultSchema() {
        return this.defaultSchema;
    } // getDefaultSchema
    /** Sets the default schema
     *  @param schema schema which is used when none is specified with the table's name
     */
    public void setDefaultSchema(String schema) {
        this.defaultSchema = schema;
    } // setDefaultSchema
    //--------
    /** URL of the JDBC driver */
    private String  driverURL;
    /** Gets the JDBC driver's URL
     *  @return an URN which identifies the JDBC driver
     */
    public String getDriverURL() {
        return this.driverURL;
    } // getDriverURL
    /** Sets the JDBC driver's URL
     *  @param urn a URN which identifies the JDBC driver
     */
    public void setDriverURL(String urn) {
        this.driverURL = urn;
    } // setDriverURL
    //--------
    /** which emailAddress to use on meta pages */
    private String emailAddress;
    /** Gets the emailAddress
     *  @return for example "Dr.Georg.Fischer@gmail.com"
     */
    public String getEmailAddress() {
        return emailAddress;
    } // getEmailAddress
    /** Sets the emailAddress for meta pages
     *  @param emailAddress a valid email address
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    } // setEmailAddress
    //--------
    /** which encodings to use for source [0] and target [1]: ISO-8859-1 (default), UTF-8 and so on */
    private String[] encoding;
    /** Gets the encoding for source or target
     *  @param side 0 = source, 1 = target
     *  @return ISO-8859-1, UTF-8 etc.
     */
    public String getEncoding(int side) {
        return encoding[side];
    } // getEncoding
    /** Sets the encoding
     *  @param side 0 = source, 1 = target
     *  @param enc encoding (ISO-8859-1, UTF-8 ...)
     */
    public void setEncoding(int side, String enc) {
        encoding[side] = enc;
        if (side == 0) { // assume identical encodings for both sides
            encoding[1] = enc;
        }
    } // setEncoding
    //--------
    /** whether to execute the SQL when loading the page */
    private int     execSQL;
    /** Gets the flag for initial SQL execution
     *  @return  1 if SQL is executed, 0 if not
     */
    public int getExecSQL() {
        return this.execSQL;
    } // getExecSQL
    /** Sets the flag for initial SQL execution
     *  @param execSQL 1 if SQL is executed, 0 if not
     */
    public void setExecSQL(int execSQL) {
        this.execSQL = execSQL;
    } // setExecSQL
    //--------
    /** maximum number of rows to be fetched; default "infinite" */
    private int     fetchLimit;
    /** Gets the maximum number of rows to be fetched
     *  @return  maximum number of rows to be fetched
     */
    public int getFetchLimit() {
        return this.fetchLimit;
    } // getFetchLimit
    /** Sets the maximum number of rows to be fetched
     *  @param fetchLimit maximum number of rows to be fetched
     */
    public void setFetchLimit(int fetchLimit) {
        this.fetchLimit = fetchLimit;
    } // setFetchLimit
    //--------
    /** input/output format = mode: tsv (default), csv, fix, html and so on*/
    private String  formatMode;
    /** Gets the format
     *  @return target format: html, sql, xml, tsv ...
     */
    public String getFormatMode() {
        return this.formatMode;
    } // getFormatMode
    /** Sets the format
     *  @param mode target format: html, sql, xml, tsv ...
     */
    public void setFormatMode(String mode) {
        this.formatMode = mode;
    } // setFormatMode
    //--------
    /** generator for SAX events */
    private BaseTransformer generator;
    /** Gets the generator
     *  @return applicable Xtrans transformer
     */
    public BaseTransformer getGenerator() {
        return this.generator;
    } // getGenerator
    /** Sets the generator
     *  @param generator set the generator transformer to this Xtrans transformer
     */
    public void setGenerator(BaseTransformer generator) {
        this.generator = generator;
    } // setGenerator
    //--------
    /** MIME type for HTML output (constant per application) */
    private String htmlMimeType;
    /** Gets the MIME type to be returned for HTML target format
     *  @return MIME type
     */
    public String getHtmlMimeType() {
        return this.htmlMimeType;
    } // getHtmlMimeType
    //--------
    /** URI for additional input file */
    private   String inputURI;
    /** Gets the URI of an additional input file (-u option)
     *  @return URI with http:, file: or data: schema or relative path
     */
    public String getInputURI() {
        return inputURI;
    } // getInputURI
    /** Sets the URI of an additional input file (-u option)
     *  @param uri URI with http:, file: or data: schema or relative path
     */
    public void setInputURI(String uri) {
        inputURI = uri;
    } // setInputURI
    //--------
    /** Language to be used for footer and messages */
    private String language;
    /** Gets the language (for messages)
     *  @return 2 letter ISO country code ("en", "de")
     */
    public String getLanguage() {
        return language;
    } // getLanguage
    /** Sets the language (for message)
     *  @param language 2 letter ISO country code ("en", "de")
     */
    public void setLanguage(String language) {
        this.language = language;
        Locale.setDefault(new Locale(language));
    } // setLanguage
    //--------
    /** when processing SQL by JDBC */
    public static final int JDBC_MANNER = 0;
    /** when processing SQL by SQLJ */
    public static final int SQLJ_MANNER = 1;
    /** when processing SQL by Stored Procedures */
    public static final int STP_MANNER  = 2;
    /** Remembers the manner of SQL processing */
    private int manner;
    /** Gets the manner of SQL processing
     *  @return code for JDBC, SQLJ, STP
     */
    public int getManner() {
        return this.manner;
    } // getManner
    /** Sets the manner of SQL processing
     *  @param manner code for JDBC, SQLJ, STP
     */
    public void setManner(int manner) {
        this.manner = manner;
    } // setManner
    //--------
    /** insert a COMMIT statement after this number of rows in a result set (modes -sql/jdbc, -update) */
    private int maxCommit;
    /** Gets the commit limit
     *  @return number of rows after which a COMMIT is written
     */
    public int getMaxCommit() {
        return  this.maxCommit;
    } // getMaxCommit
    /** Sets the commit limit
     *  @param maxCommit number of rows after which a COMMIT is written
     */
    public void setMaxCommit(int maxCommit) {
        this.maxCommit = maxCommit;
    } // setMaxCommit
    //--------
    /** Namespace prefix (for XML output) */
    private String  namespacePrefix;
    /** Gets the namespace prefix
     *  @return short (lowercase) prefix as defined by xmlns pseudo attribute
     */
    public String getNamespacePrefix() {
        return this.namespacePrefix;
    } // getNamespacePrefix
    /** Sets the namespace prefix
     *  @param namespacePrefix short (lowercase) prefix as defined by xmlns pseudo attribute
     */
    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    } // setNamespacePrefix
    //--------
    /** whether to write the value <em>null</em> in text formats: 0 = omit, 1 = write "null" */
    private int nullText;
    /** Tells whether the value <em>null</em> should be written in text formats;
     *  corresponds to property "null"
     *  @return 0 = omit, 1 = write "null"
     */
    public int getNullText() {
        return nullText;
    } // getNullText
    /** Tells whether the value <em>null</em> should be written in text formats
     *  @param nullText 0 = omit, 1 = write "null"
     */
    public void setNullText(int nullText) {
        this.nullText = nullText;
    } // setNullText
    //--------
    /** Map for parameters (placeholders) embedded in the XML specification.
     *  The SAX handler will take parameter values from here when
     *  it encounters a <code>&lt;parm&gt;</code> element.
     */
    private HashMap<String, String[]> parameterMap;
    /** Gets the parameter (placeholder) map
     *  @return the locally stored parameter map
     */
    public HashMap<String, String[]> getParameterMap() {
        return parameterMap;
    } // getParameterMap
    /** Sets the parameter (placeholder) map
     *  @param map set the parameter map to this object
     */
    public void setParameterMap(Map<String, String[]>  map) {
        parameterMap = new HashMap<String, String[]>();
        Iterator<String> piter = map.keySet().iterator();
        while (piter.hasNext()) {
            String key = piter.next();
            if (key.startsWith("amp;")) {
                String ampKey = key;
                key = key.substring(4); // remove "amp;"
                map.put(key, map.get(ampKey));
                map.remove(ampKey);
            }
            parameterMap.put(key, map.get(key));
        } // while key
    } // setParameterMap
    //--------
    /** Name of a file with user defineable properties (related to the connection) */
    private String  lastPropsName;
    /** Gets the name of the last properties file which was read
     */
    public String getLastPropsName() {
        return lastPropsName;
    } // getLastPropsName
    /** Sets the name of the last properties file which was read
     *  @param lastPropsName for example "dbat.properties"
     */
    public void setLastPropsName(String lastPropsName) {
        this.lastPropsName = lastPropsName;
    } // setLastPropsName
    //--------
    /** User defineable properties, database connection parameters and other properties.
     *  These are retrieved from:
     *  <ol>
     *  <li>default values in this program,</li>
     *  <li>a file "dbat.properties" in the jar file classpath (in WEB-INF/classes),</li>
     *  <li>the same filename in the current directory,</li>
     *  <li>from a property file named behind the "-c" commandline argument and/or</li>
     *  <li>from "-p name=value" commandline arguments.</li>
     *  </ol>
     */
    private Properties props;
    //--------
    /** Constant for syntax of MySQL RDBMS */
    public static final int MYSQL   = 1;
    /** Constant for syntax of IBM's DB2 RDBMS */
    public static final int DB2     = 2;
    /** Constant for syntax of SQLite RDBMS */
    public static final int SQLITE  = 3;
    /** Constant for syntax of Oracle RDBMS */
    public static final int ORACLE  = 4;
    /** Constant for syntax of Java DB (= Derby) RDBMS */
    public static final int JAVADB  = 5;
    /** the underlying database system, derived from the JDBC driver's URL */
    private int rdbmsId;
    /** Sets the underlying database system
     *  @param driverURL identifies the DB system
     */
    public void setRdbmsId(String driverURL) {
        // Example: url = "jdbc:db2://localhost:50004/DBxy
        if (false) {
        } else if (driverURL.startsWith("jdbc:mysql"    )) {
            rdbmsId                         = MYSQL;
        } else if (driverURL.startsWith("jdbc:db2"      )) {
            rdbmsId                         = DB2;
        } else if (driverURL.startsWith("jdbc:sqlite"   )) {
            rdbmsId                         = SQLITE;
        } else if (driverURL.startsWith("jdbc:oracle"   )) {
            rdbmsId                         = ORACLE;
        } else if (driverURL.startsWith("jdbc:javadb"   )) {
            rdbmsId                         = JAVADB;
        }
    } // setRdbmsId
    /** Gets the underlying database system (as <em>int</em>, to be used in Java <em>switch</em> statements)
     *  @return one of the constants {@link #DB2}, {@link #MYSQL} and so on
     */
    public int getRdbmsId() {
        return rdbmsId;
    } // getRdbmsId
    //--------
    /** input/output format separator for tsv, csv modes */
    private String  separator;
    /** Gets the input/output format separator (for csv, tsv)
     *  @return separator: ",", ";", " " and so on
     */
    public String getSeparator() {
        return this.separator;
    } // getSeparator
    /** Sets the input/output format separator (for csv, tsv)
     *  @param separator  ",", ";", " " and so on
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    } // setSeparator
    //--------
    /** Stored Procedure text separator */
    private String  procSeparator;
    /** Gets the Stored Procedure text separator
     *  @return separator: "$", ";" and so on
     */
    public String getProcSeparator() {
        return this.procSeparator;
    } // getProcSeparator
    /** Sets the Stored Procedure text separator
     *  @param separator  "$", ";" and so on
     */
    public void setProcSeparator(String separator) {
        this.procSeparator = separator;
    } // setProcSeparator
    //--------
    /** Output format serializer */
    private BaseTable tableSerializer;
    /** Gets the output format serializer
     *  @return tableSerializer an output format class
     */
    public BaseTable getTableSerializer() {
        return tableSerializer;
    } // getTableSerializer
    /** Sets the output format serializer
     *  @param tableSerializer an output format class
     */
    public void setTableSerializer(BaseTable tableSerializer) {
        this.tableSerializer = tableSerializer;
    } // setTableSerializer
    //--------
    /** how to trim CHAR and VARCHAR column values */
    private int trimSides;
    /** Tells how CHARs and VARCHARs should be trimmed by INSERT and SELECT
     *  @return how to trim CHARs and VARCHARs: 0 = never, 1 = right, 2 = both sides
     */
    public int getTrimSides() {
        return trimSides;
    } // getTrimSides
    /** Sets the trimming behaviour
     *  @param trimSides how to trim CHARs and VARCHARs: 0 = never, 1 = right, 2 = both sides
     */
    public void setTrimSides(int trimSides) {
        this.trimSides = trimSides;
    } // setTrimSides
    //--------
    /** whether to print verbose messages: 0 = none, 1 = some, 2 = many */
    private int verbose;
    /** Sets the level of verbose output
     *  @param verbose how many messages should be output: 0 = none, 1 = some, 2 = many
     */
    public void setVerbose(int verbose) {
        this.verbose = verbose;
    } // setVerbose
    /** Gets the level of verbose output
     *  @return how many messages should be output: 0 = none, 1 = some, 2 = many
     */
    public int getVerbose() {
        return this.verbose;
    } // getVerbose
    //--------
    /** whether to print header and trailer */
    private boolean withHeaders;
    /** Tells whether to output table header rows
     *  @return false if no table header rows should be written (default: true)
     */
    public boolean isWithHeaders() {
        return withHeaders;
    } // isWithHeaders
    /** Defines whether to output table header rows
     *  @param withHeaders false if no table header rows should be written  (default: true)
     */
    public void setWithHeaders(boolean withHeaders) {
        this.withHeaders = withHeaders;
    } // setWithHeaders
    //--------
    /** name of ZIP file for (B)LOB values */
    private String  zipFileName;
    /** Gets the name of the ZIP file for LOBs
     *  @return zipFileName a name of a ZIP file
     */
    public String getZipFileName() {
        return zipFileName;
    } // getZipFileName
    /** Sets the name of the ZIP file for LOBs
     *  @param zipFileName a name of a ZIP file
     */
    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    } // setZipFileName
    //--------

    /** Shows all configuration properties
     *  @return a printable list of parameters and their values
     */
    public String toString() {
        StringBuffer dsList = new StringBuffer(64);
        String key = null;
        Iterator<String> diter = dataSourceMap.keySet().iterator();
        while (diter.hasNext()) {
            dsList.append(",");
            key = diter.next();
            dsList.append(key);
        } // while diter
        StringBuffer conList = new StringBuffer(64);
        Iterator<String> citer = consoleMap.keySet().iterator();
        while (citer.hasNext()) {
            conList.append(",");
            key = citer.next();
            conList.append(key);
            conList.append(":");
            conList.append(consoleMap.get(key));
        } // while citer
        String result = "";
        result += "autoCommit="         + hasAutoCommit()           + "\n";
        result += "callType="           + getCallType()             + "\n";
        result += "connectionId="       + getConnectionId()         + "\n";
        result += "consoleMap="         + conList.toString().substring(1) + "\n";
        result += "decimalSeparator="   + getDecimalSeparator()     + "\n";
        result += "defaultSchema="      + getDefaultSchema()        + "\n";
        result += "driverURL="          + getDriverURL()            + "\n";
        result += "dataSourceMap="      + dsList.toString().substring(1) + "\n";
        result += "emailAddress="       + getEmailAddress()         + "\n";
        result += "encoding="           + getEncoding(0) + "," + getEncoding(1) + "\n";
        result += "fetchLimit="         + getFetchLimit()           + "\n";
        result += "htmlMimeType="       + getHtmlMimeType()         + "\n";
        result += "inputURI="           + getInputURI()             + "\n";
        result += "language="           + getLanguage()             + "\n";
        result += "lastPropsName="      + getLastPropsName()        + "\n";
        result += "manner="             + getManner()               + "\n";
        result += "maxCommit="          + getMaxCommit()            + "\n";
        result += "namespacePrefix="    + getNamespacePrefix()      + "\n";
        result += "nullText="           + getNullText()             + "\n";
    //  result += "parameterMap="       + getProcSeparator()        + "\n";
        result += "procSeparator="      + getProcSeparator()        + "\n";
    //  result += "props="              + getProcSeparator()        + "\n";
        result += "rdbmsId="            + getRdbmsId()              + "\n";
        result += "separator="          + getSeparator()            + "\n";
    //  result += "tableSerializer="    + getTableSerializer().getDescription(getLanguage()) + "\n";
        result += "trimSides="          + getTrimSides()            + "\n";
        result += "verbose="            + getVerbose()              + "\n";
        result += "withHeaders="        + isWithHeaders()           + "\n";
        result += "zipFileName="        + getZipFileName()          + "\n";
        if (true) { // debug
            try {
                ClassLoader cloader = this.getClass().getClassLoader();
                Enumeration<URL> resEnum = cloader.getResources("dbat.properties");
                boolean busy = true;
                while (busy && resEnum.hasMoreElements()) {
                    URL url = resEnum.nextElement();
                    String urlst = url.toString();
                /*
                    if (    urlst.indexOf(appName + ".jar!/")            >= 0 ||
                            urlst.indexOf(appName + "/WEB-INF/classes/") >= 0) {
                        busy = false;
                    } else { // ignore appl-core.jar
                    }
                */
                    result += "properties=" + urlst;
                } // while resEnum
            } catch (Exception exc) {
                log.error(exc.getMessage(), exc);
            }
        } // debug
        return result;
    } // toString

    //================================
    // Constructor and initialization
    //================================
    /** No-args Constructor
     */
    public Configuration() {
        log = Logger.getLogger(Configuration.class.getName());
    } // Constructor

    /** Initializes the class for the 1st (or 2nd, 3rd etc) call.
     *  @param callType whether the class is activated by CLI, WEB or SOAP
     */
    public void configure(int callType) {
        this.callType   = callType;
        con             = null;
        // htmlMimeType    =  System.getProperty("file.separator").startsWith("/")
        //                 || System.getProperty("os.name"       ).startsWith("Windows 8")
        //                 ? "application/xhtml+xml"   // for Unix, Firefox
        //                 : "text/html";              // for Windows, InternetExplorer <= V6.x
        htmlMimeType    = "application/xhtml+xml"; // now works for IE also
        con             = null;
        consoleMap      = new LinkedHashMap<String, String    >(4);
        dataSourceMap   = new LinkedHashMap<String, DataSource>(4);
        setAutoCommit   (callType == WEB_CALL); // only Web queries are always autocommitted (for DB/2)
        setConnectionId ("worddb");
        setConsole      ("none");
        setRdbmsId      ("jdbc:mysql");
        setDefaultSchema("");
        setNamespacePrefix("");
        setWithHeaders  (true);
        setEmailAddress ("punctum@punctum.com");
        setFetchLimit   (1947062906); // very high
        encoding        = new String[2]; // [0] = input, [1] = output
        setEncoding     (0, "ISO-8859-1"); // -e default for input and output
        setInputURI     (null); // no default
        setLanguage     ("en");
        setManner       (JDBC_MANNER);
        setMaxCommit    (getFetchLimit()); // very high - never reached
        setNullText     (1); // write "null"
        setTrimSides    (2); // trim on both sides
        setParameterMap (new HashMap<String, String[]>());
        setProcSeparator(null); // no default
        setDecimalSeparator("."); // US-English convention
        setLastPropsName("undefined"); // default (built-in) connection properties
        props           = new Properties();
        // (0) set hardcoded default values
        props.setProperty("driver"      , "org.sqlite.JDBC"                 );
        props.setProperty("url"         , "jdbc:sqlite:sqlite3.db"          );
        props.setProperty("password"    , ""                                );
        // (1) try to get properties from the classpath (jar-file)
        // (2) add properties from file with same name in the current directory
        addProperties("dbat.properties");
        evaluateProperties();
    } // configure

    //========================
    // Auxiliary methods
    //========================

    /** Evaluates a set of non-JDBC properties and remembers them in local variables:
     *  <ul>
     *  <li>decimal="." the character to be used as decimal separator</li>
     *  <li>commit=250 number of rows after which a COMMIT statement is inserted by formats -sql/jdbc, -update</li>
     *  <li>console=none|read|modify behaviour ot the SQL console window</li>
     *  <li>null=1(0) if the <em>null</em> value should (not) be written by text formats</li>
     *  <li>schema= the default DB schema</li>
     *  <li>trim=2 how CHAR and VARCHAR values are trimmed: 0 = never, 1 = rtrim, 2 = trim on both sides</li>
     *  </ul>
     */
    public void evaluateProperties() {
        // (3) evaluate non-JDBC oriented properties
        setDecimalSeparator(props.getProperty("decimal"  , "."  ).trim());
        setDefaultSchema   (props.getProperty("schema"   , ""   ).trim());
        setEmailAddress    (props.getProperty("email"    , "punctum@punctum.com").trim());
        setExecSQL         (1);
        String prop = null;
        try {
            prop =          props.getProperty("commit"   , "250").trim();
            setMaxCommit(Integer.parseInt(prop));
        } catch (Exception exc) {
            setMaxCommit(getFetchLimit());
        }
        try {
            prop =          props.getProperty("null"     , "1"  ).trim();
            setNullText(Integer.parseInt(prop));
        } catch (Exception exc) {
            setNullText(1);
        }
        try {
            prop =          props.getProperty("trim"     , "2"  ).trim();
            setTrimSides(Integer.parseInt(prop));
        } catch (Exception exc) {
            setTrimSides(2);
        }
    } // evaluateProperties

    /** Sets a single property in this configuration,
     *  and evaluates it.
     *  Used only in <em>Dbat</em>.
     *  @param name name of the property
     *  @param value value of property <em>name</em>
     */
    public void setProperty(String name, String value) {
        props.setProperty(name, value);
        evaluateProperties(); // maybe it was one of the non-JDBC-oriented
    } // setProperty

    /** Adds properties from a file in the classpath, or in the current directory
     *  (of the context in case of the web application).
     *  @param propsName name of the properties file, for example "dbat.properties"
     *  Among others, the properties define the JDBC connection parameters.
     *  Popular values for <em>driver</em> and <em>url</em> are:
     *  <pre>
     *  DB2 V8, db2jcc.jar + db2jcc_license_cisuz.jar
     *  driver = "com.ibm.db2.jcc.DB2Driver";
     *  url = "jdbc:db2://localhost:50004/DBxy
     *
     *  hsqldb, hsqldb.jar
     *  driver = "org.hsqldb.jdbcDriver";
     *  url = "jdbc:hsqldb:worddb";
     *
     *  MySQL, mysql-connector-java-3.1.11-bin.jar
     *  driver = "com.mysql.jdbc.Driver";
     *  url = "jdbc:mysql://localhost/worddb";
     *
     *  Oracle &gt;= 9.0: ojdbc14.jar
     *  driver = "oracle.jdbc.driver.OracleDriver";
     *  url = "jdbc:oracle:thin:@localhost:1521:worddb";
     *
     *  SQLite, sqlite.jar + sqlite_jni.dll
     *  driver = "SQLite.JDBCDriver";
     *  url = jdbc:sqlite:worddb";
     *  </pre>
     */
    public void addProperties(String propsName) {
        InputStream propsStream = null;
        // (1) try to get properties from the classpath (jar-file)
        try {
            String path = getCallType() == CLI_CALL ? "" : "../../../";
                    // move upwards: WEB-INF/classes/org/teherba/dbat/Configuration.class -> WEB-INF/classes/dbat.properties
            path = "";
            propsStream = Configuration.class.getClassLoader().getResourceAsStream(path + propsName);
            if (propsStream != null) {
                props.load(propsStream);
                setLastPropsName("jar:file:" + path + propsName);
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        // (2) add properties from file with same name in the current directory
        try {
            File propsFile = new File(propsName);
            if (propsFile.canRead()) {
                propsStream = new FileInputStream(propsFile);
                props.load(propsStream);
                propsStream.close();
                setLastPropsName("./" + propsName);
            } // load from current dir
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        String driverURL = props.getProperty("url");
        if (driverURL != null) {
            setRdbmsId(driverURL);
        }
        // evaluateProperties();
    } // addProperties

    /** Gets environment variables from the servlet context (defined inf file etc/META_INF/context.xml).
     *  <ul>
     *  <li>java:comp/env/console</li>
     *  <li>java:comp/env/dataSources</li>
     *  </ul>
     */
    public void loadContextEnvironment() {
        try {
            Context envContext = (Context) new InitialContext().lookup("java:comp/env"); // get the environment naming context
            //-------- consoleAccess per connId
            String consList = ((String) envContext.lookup("console")).replaceAll("[ \\,\\;]+", ",");
            String[]
            pairs = consList.split("\\,");
            int
            ipair = 0;
            while (ipair < pairs.length) {
                String[] parts = pairs[ipair].split("\\:");
                String connectionId = null;
                if (false) {
                } else if (parts.length == 0) {
                    // ignore
                } else if (parts.length == 1) { // connId, but no behaviour specified, defaults to CONSOLE_SELECT
                    connectionId = parts[0];
                    consoleMap.put(connectionId, Configuration.CONSOLE_SELECT);
                } else if (parts.length >= 2) { // explicit behaviour (-> CONSOLE_SELECT or CONSOLE_UPDATE)
                    connectionId = parts[0];
                    setConsole(parts[1]);
                    consoleMap.put(connectionId, getConsole());
                }
                ipair ++;
            } // while ipair

            //-------- dataSources per connId
            String dsList = ((String) envContext.lookup("dataSources")).replaceAll("[ \\,\\;]+", ",");
            // log.info(" dsList=\"" + dsList + "\"");

            pairs = dsList.split("\\,");
            ipair = 0;
            while (ipair < pairs.length) {
                String[] parts = pairs[ipair].split("\\:");
                String connectionId = "mysql";
                String dsName       = connectionId;
                if (false) {
                } else if (parts.length == 0) {
                    // ignore
                } else if (parts.length == 1) { // direct connectionId, e.g. "worddb"
                    connectionId = parts[0];
                    dsName       = connectionId;
                } else if (parts.length == 2) { // explicit renaming of connectionId, e.g. "worddb:DBAT_Word_DataSource
                    connectionId = parts[0];
                    dsName       = parts[1];
                } else { // more than one ":"
                    // ignore
                }
                log.info("connectionId=\"" + connectionId + "\" mapped to \"" + dsName + "\"");
                dataSourceMap.put(connectionId, (DataSource) envContext.lookup("jdbc/" + dsName));
                ipair ++;
            } // while ipair
            // envContext.close(); - caused an exception: context is not writeable
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // loadContextEnvironment

/* BasicDataSourceFactory contains:
   52       private final static String PROP_DRIVERCLASSNAME    = "driverClassName";
   64       private final static String PROP_PASSWORD           = "password";
   65       private final static String PROP_URL                = "url";
   66       private final static String PROP_USERNAME           = "username";
*/
    //==============================
    // Open and close connection
    //==============================

    /** Opens the database connection.
     *  @return opened database connection, or null if connection could not be opened
     */
    public Connection openConnection() throws IllegalArgumentException {
        Connection result = null;
        DataSource ds = null;
        if (false) {
        } else if (callType == WEB_CALL && dataSourceMap != null) { // assume that dataSourceMap was prefilled by web container
            try {
                ds = dataSourceMap.get(connectionId);
                if (ds != null) { // known connection id
                    result = ds.getConnection();
                } else {
                    log.error("connection id " + connectionId + " is not known");
                }
            } catch (Exception exc) {
                log.error(exc.getMessage(), exc);
                // wasCommitted = true; // avoid a final COMMIT
                throw new IllegalArgumentException(exc);
            }
        } else {
            String user         = props.getProperty("user"      );
            if (user == null) {
                user            = props.getProperty("username"  );
            }
            if (user != null) {
                user = user.trim();
            }
            String password     = props.getProperty("password"  );
            if (password == null || password.length() == 0) {
                String token    = props.getProperty("token"     );
                if (token != null && token.length() > 2) {
                    password = CommandTokenizer.decode(token.trim());
                }
            } else {
                password = password.trim();
            }
            String driver       = props.getProperty("driver"    );
            if (driver == null) {
                driver          = props.getProperty("driverClassName");
            }
            if (driver != null) {
                driver = driver.trim();
            }
            driverURL           = props.getProperty("url"       );
            if (driverURL != null) {
                driverURL = driverURL.trim();
            }

            try {
                Class.forName(driver).newInstance();
                result = DriverManager.getConnection(driverURL, user, password);
            } catch (Exception exc) {
                log.error("Dbat.openConnection"
                        + "(user=\""        + user
                        + "\",driver=\""    + driver
                        + "\",url=\""       + driverURL
                        + "\"): " + exc.getMessage()); // , exc);
                // wasCommitted = true; // avoid a final COMMIT
                throw new IllegalArgumentException(exc);
                // System.exit(1); // terminate immediately
            }
        } // switch callType
        if (result != null) {
            try {
                result.setAutoCommit(autoCommit);
                if (getRdbmsId() == DB2) {
                    result.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                }
            } catch (Exception exc) {
                log.error(exc.getMessage(), exc);
                throw new IllegalArgumentException(exc);
            }
        }
        return result;
    } // openConnection

    /** Closes the database connection, also if there are previous exceptions
     */
    public  void closeConnection() {
        try {
            if (con != null) {
                if (! autoCommit) {
                    con.commit();
                }
                con.close();
                con = null;
            }
        } catch (Exception exc) { // could not commit or close
            log.error(exc.getMessage(), exc);
            try { // try rollback
                con.rollback();
                con.close();
                con = null;
            } catch (Exception exc2) { // could not rollback
                log.error(exc2.getMessage(), exc2);
                if (con != null) {
                    try {
                        con.close();
                    } catch (Exception exc3) {
                        log.error(exc3.getMessage(), exc3);
                    }
                }
            } // try rollback
        } finally {
            con = null;
        }
    } // closeConnection

    //====================
    // Main method (Test)
    //====================

    /** Test driver -
     *  call it with -h to display possible options and arguments.
     *  The result is printed to STDOUT.
     *  @param args command line arguments: options, strings, table- or filenames
     */
    public static void main(String[] args) {
        Logger log = Logger.getLogger(Configuration.class.getName());
        Configuration config = new Configuration();
        try {
            config.configure(Configuration.CLI_CALL);
            Connection con = config.openConnection();
            config.closeConnection();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
        }
    } // main

} // Configuration
