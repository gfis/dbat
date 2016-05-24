/*  Configuration.java - DataSource and user defineable properties for a JDBC connection
 *  @(#) $Id$ 2016-04-16 14:43:35
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
 *  2012-01-25: fallback to dbat.properties if dsMap not filled by DBCPoolingListener
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
import  java.io.BufferedReader;
import  java.io.File;
import  java.io.FileInputStream;
import  java.io.InputStreamReader;
import  java.io.Serializable;
import  java.net.URL;
import  java.sql.Connection;
import  java.sql.DriverManager;
import  java.util.Enumeration;
import  java.util.HashMap;
import  java.util.Iterator;
import  java.util.LinkedHashMap;
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
    /** Sets the connection id to the default: take first key in {@link #dsMap}
     */
    public void setConnectionId() {
        if (dsMap != null) {
            boolean busy = true;
            Iterator/*<1.5*/<String>/*1.5>*/ miter = dsMap.keySet().iterator();
            while (busy && miter.hasNext()) {
                this.connectionId = miter.next();
                busy = false; // take first only
            } // while busy
        } // dsMap != null
    } // setConnectionId(0)
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
    protected BaseTransformer generator;
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
     *  @return iso 2 letter ISO country code ("en", "de")
     */
    public String getLanguage() {
        return language;
    } // getLanguage
    /** Sets the language (for message)
     *  @param iso2 2 letter ISO country code ("en", "de")
     */
    public void setLanguage(String iso2) {
        language = iso2;
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
     *  @return manner
     */
    public int getManner() {
        return this.manner;
    } // getManner
    /** Sets the manner of SQL processing
     *  @param manner
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
    private HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap;
    /** Gets the parameter (placeholder) map
     *  @return the locally stored parameter map
     */
    public HashMap/*<1.5*/<String, String[]>/*1.5>*/ getParameterMap() {
        return parameterMap;
    } // getParameterMap
    /** Sets the parameter (placeholder) map
     *  @param map set the parameter map to this object
     */
    public void setParameterMap(Map/*<1.5*/<String, String[]>/*1.5>*/  map) {
        parameterMap = new HashMap/*<1.5*/<String, String[]>/*1.5>*/();
        Iterator/*<1.5*/<String>/*1.5>*/ piter = map.keySet().iterator();
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
    private String  propFileName;
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
    /** Version String to be used in Messages */
    private static String versionString;

    /** Sets the program's version, which is a String of the form "Dbat Vm.hhhh/isodate",
     *  where m is the major version,
     *  and hhhh are the build number as 4 digits (zero padded)
     */
    public void setVersionString() {
        String result = "Dbat V";
        try {
            /*
            Name: dbat
            Specification-Title: Dbat
            Specification-Version: 10 for JDK 1.6
            Specification-Vendor: Dr. Georg Fischer
            Implementation-Title: Dbat
            Implementation-Version: Dbat 730 2016-04-16 19:56:58
            Implementation-Vendor: Dr. Georg Fischer
            Implementation-Vendor-Id: @(#) $Id$
            */
            ClassLoader cloader = this.getClass().getClassLoader();
            Enumeration<URL> resources = cloader.getResources("META-INF/MANIFEST.MF");
            boolean busy = true;
            while (busy && resources.hasMoreElements()) {
                URL url = resources.nextElement();
                // System.out.println("Configuration.0: url=" + url.toString());
                if (url.toString().indexOf("/dbat") >= 0) { // our MANIFEST.MF
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\\s+");
                        if (false) {
                        } else if (line.startsWith("Specification-Version:" )) {
                            if (parts.length >= 2) {
                                result += parts[1];
                            } else {
                                result += "10";
                            }
                        } else if (line.startsWith("Implementation-Version:")) {
                            if (parts.length >= 4) {
                                String buildNo = parts[2].replaceAll("\\.", "");
                                if (false) { // force buildNo to 4 digits
                                } else if (buildNo.length() > 4) {
                                    buildNo = buildNo.substring(buildNo.length() - 4);
                                } else if (buildNo.length() < 4) {
                                    buildNo = ("0000".substring(buildNo.length())) + buildNo;
                                }
                                result += "." + buildNo + "/" + parts[3];
                            } // >= 4 parts
                        } // Implementation-Version
                    } // our MANIFEST.MF
                } // while reading lines from MANIFEST.MF
            } // while resources
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        versionString = result;
    } // getVersionString

    /** Gets the program's version.
     *  The value returned is reasonable only if this source file was changed and git committed before the build!
     *  Target <em>identify</em> in <em>dbat/makefile</em> should be run before!
     *  @return a string of the form "Dbat Vm.hhhh/isodate",
     *  where m is the major version,
     *  and hhhh are the build number as 4 digits (zero padded)
     */
    public static String getVersionString() {
        return versionString;
    } // getVersionString
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
    /** Maps connection identifiers (short database instance ids) to {@link DataSource Datasources} */
    private LinkedHashMap/*<1.5*/<String, DataSource>/*1.5>*/ dsMap;

    //================================
    // Constructor and initialization
    //================================

    /** No-args Constructor
     */
    public Configuration() {
        log = Logger.getLogger(Configuration.class.getName());
        setVersionString();
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
        dsMap           = null;
        setAutoCommit   (callType == WEB_CALL); // only Web queries are always autocommitted (for DB/2)
        setConnectionId ("worddb");
        setRdbmsId      ("jdbc:mysql");
        setDefaultSchema("");
        setNamespacePrefix("");
        setWithHeaders  (true);
        setFetchLimit   (1947062906); // very high
        encoding        = new String[2]; // [0] = input, [1] = output
        setEncoding     (0, "ISO-8859-1"); // -e default for input and output
        setInputURI     (null); // no default
        setLanguage     ("en");
        setManner       (JDBC_MANNER);
        setMaxCommit    (getFetchLimit()); // very high - never reached
        setNullText     (1); // write "null"
        setTrimSides    (2); // trim on both sides
        setParameterMap (new HashMap/*<1.5*/<String, String[]>/*1.5>*/());
        setProcSeparator(null); // no default
        setDecimalSeparator("."); // US-English convention
        propFileName    = ""; // default (built-in) connection properties
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

    /** Initializes the class for the 1st (or 2nd, 3rd etc) call.
     *  @param callType whether the class is activated by CLI, WEB or SOAP
     *  @param dsMap maps connection ids to pre-initialized DataSources,
     *  see {@link DbatServlet}.
     */
    public void configure(int callType, LinkedHashMap/*<1.5*/<String, DataSource>/*1.5>*/ dsMap) {
        configure(callType);
        this.dsMap = dsMap;
    } // configure(callType, dsMap)

    //========================
    // Auxiliary methods
    //========================

    /** Evaluates a set of non-JDBC properties and remembers them in local variables:
     *  <ul>
     *  <li>decimal="." the character to be used as decimal separator</li>
     *  <li>commit=250 number of rows after which a COMMIT statement is inserted by formats -sql/jdbc, -update</li>
     *  <li>null=1(0) if the <em>null</em> value should be (not) written by text formats</li>
     *  <li>schema= the default DB schema</li>
     *  <li>trim=2 how CHAR and VARCHAR values are trimmed: 0 = never, 1 = rtrim, 2 = trim on both sides</li>
     *  </ul>
     */
    public void evaluateProperties() {
        // (3) evaluate non-JDBC oriented properties
        String prop = null;
        try {
            prop =       props.getProperty("commit"   , "250").trim();
            setMaxCommit(Integer.parseInt(prop));
        } catch (Exception exc) {
            setMaxCommit(getFetchLimit());
        }
        { // try not necessary
            prop =       props.getProperty("decimal"  , "."  ).trim();
            if (prop != null) {
                setDecimalSeparator(prop);
            }
        }
        try {
            prop =       props.getProperty("null"     , "1"  ).trim();
            setNullText(Integer.parseInt(prop));
        } catch (Exception exc) {
            setNullText(1);
        }
        setDefaultSchema(props.getProperty("schema"   , ""   ).trim());
        try {
            prop =       props.getProperty("trim"     , "2"  ).trim();
            setTrimSides(Integer.parseInt(prop));
        } catch (Exception exc) {
            setTrimSides(2);
        }
    } // evaluateProperties

    /** Sets a single property in this configuration,
     *  and evaluates it.
     *  @param name name of the property
     *  @param value value of property <em>name</em>
     */
    public void setProperty(String name, String value) {
        props.setProperty(name, value);
        evaluateProperties(); // maybe it was one of the non-JDBC-oriented
    } // addProperties

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
     *  Oracle >= 9.0: ojdbc14.jar
     *  driver = "oracle.jdbc.driver.OracleDriver";
     *  url = "jdbc:oracle:thin:@localhost:1521:worddb";
     *
     *  SQLite, sqlite.jar + sqlite_jni.dll
     *  driver = "SQLite.JDBCDriver";
     *  url = jdbc:sqlite:worddb";
     *  </pre>
     */
    public void addProperties(String propsName) {
        // (1) try to get properties from the classpath (jar-file)
        try {
            props.load(Configuration.class.getClassLoader().getResourceAsStream(propsName));
        } catch (Exception exc) {
            // ignore errors
        }
        // (2) add properties from file with same name in the current directory
        try {
            File propsFile = new File(propsName);
            if (propsFile.canRead()) {
                FileInputStream propsStream = new FileInputStream(propsFile);
                props.load(propsStream);
                propsStream.close();
            } // load from current dir
        } catch (Exception exc) {
            // ignore errors
        }
        String driverURL = props.getProperty("url");
        if (driverURL != null) {
            setRdbmsId(driverURL);
        }
    } // addProperties

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
        } else if (callType == WEB_CALL && dsMap != null) { // assume that dsMap was prefilled by web container
            try {
                ds = dsMap.get(connectionId);
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
