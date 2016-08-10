/*  Dbat.java - Database administration tool for JDBC compatible RDBMSs.
 *  @(#) $Id$
 *  2016-08-09: pass "conn" in writeStart
 *  2013-01-05: fit for RegressionTester
 *  2012-08-04: outputFormat -> formatMode, becomes variable for -r
 *  2012-01-10: XML parsing error message with line+column numbers
 *  2011-11-11: set|getVerbose; Martini
 *  2011-09-13: prepareSeparator with \n, \t replacement
 *  2011-08-24: -m fix implies -s ""
 *  2011-08-02: -sp is procedure statement separator
 *  2011-07-19: "-s" will no longer imply "-m csv" because of "-m taylor" 
 *  2011-05-31: formalized tests with test/batch_test.pl
 *  2011-04-07: remove schema/catalog comment output
 *  2011-03-29: set/getWithHeaders
 *  2011-03-23: getTableName
 *  2011-02-16: <describe> element
 *  2010-10-18: public openConnection for gramword
 *  2010-09-21: better HTML error messages
 *  2010-09-16: subpackage format, most methods public, Version 4, setDescription used in more/help.jsp
 *  2010-09-15: -a = aggregation and -g = group change / new heading
 *  2010-03-17: major revision with TableMetaData instead of ColumnList
 *  2010-03-08: callStoredProcedure
 *  2010-02-18: trim() for CHAR also
 *  2010-02-02: property values separated by ';'; token instead of password
 *  2009-08-11: commit always (even SELECT) for DB2
 *  2009-08-06: trap MalformedInputException
 *  2008-02-13: Java 1.5 types, completeColumns
 *  2007-02-28: -r didn't know current_column
 *  2007-01-17: use methods derived from BaseTable
 *  2007-01-10: maybe used from 'SpecHandler'; log4j; 'columns' for all column properties
 *  2007-01-03: new code conventions, explicit imports, no accumulators
 *  2006-07-28: readBLOB
 *  2006-07-03: getConnection incorporated, with optional "-c propfile"
 *  2006-06-20: -e UTF-8
 *  2006-05-27: for mysql
 *  2003-05-21, Dr. Georg Fischer: yet another implementation
 *
 *  to do:
 *  Manifest implementation version from SVN revision number, and not from build.number
 *  batch insert 
 */
/*
 * Copyright 2006 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.dbat.Configuration;
import  org.teherba.dbat.Messages;
import  org.teherba.dbat.SpecificationHandler;
import  org.teherba.dbat.SQLAction;
import  org.teherba.dbat.TableMetaData;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.TableFactory;
import  org.teherba.dbat.format.TableGenerator;
import  java.io.Serializable;
import  java.io.BufferedReader;
import  java.io.BufferedOutputStream;
import  java.io.FileInputStream;
import  java.io.OutputStream;
import  java.io.PrintWriter;
import  java.io.Reader;
import  java.nio.channels.Channels;
import  java.nio.channels.ReadableByteChannel;
import  java.nio.channels.WritableByteChannel;
import  java.util.LinkedHashMap;
import  java.util.regex.Pattern;
import  javax.sql.DataSource;
import  javax.xml.parsers.SAXParser;
import  javax.xml.parsers.SAXParserFactory;
import  org.xml.sax.InputSource;
import  org.xml.sax.SAXParseException;
import  org.xml.sax.XMLReader;
import  org.apache.log4j.Logger;

/** Database application tool for JDBC compatible relational databases.
 *  The tool executes a single SQL statement (SELECT, INSERT, CREATE TABLE ...) provided on
 *  the command line, or it reads and processes a series of such statements
 *  from a text file. It is possible to INSERT (comma, tab, whitespace) separated text files,
 *  and to generate DROP/CREATE TABLE and INSERT statements from existing tables.
 *  @author Dr. Georg Fischer
 */
public class Dbat implements Serializable {
    public final static String CVSID = "@(#) $Id$";
    /** log4j logger (category) */
    private Logger log;
    /** Debugging switch */
    private int debug = 0;
    
    /** No-args Constructor
     */
    public Dbat() {
        log = Logger.getLogger(Dbat.class.getName());
    } // Constructor

    //======================================
    // Bean properties, getters and setters
    //======================================
    /** -f: name of file containing SQL statements */
    private String  srcFileName;
    /** Gets the source filename
     *  @return name of the file to be read
     */
    private String getSourceName() {
        return srcFileName;
    } // getSourceName
    /** Determines whether the source filename has some specified extension
     *  @param extension desired file extension, for example ".xml" (with the dot)
     *  @return whether the filename ends with this extension
     */
    private boolean isSourceType(String extension) {
        return srcFileName.toLowerCase().endsWith(extension);
    } // isSourceType
    /** Sets the source filename
     *  @param name name of the file to be read
     */
    private void setSourceName(String name) {
        srcFileName = name;
    } // setSourceName
    
    /** Code for main action to be performed */
    private char    mainAction;
    /** SQL on the commandline to be executed */
    private String  argsSql;
    /** -v: whether to print verbose remarks */
    private int verbose;

    /** Delivers <em>SomeTable</em>s */
    private TableFactory    tableFactory;

    /** Properties and methods specific for one elementary sequence of SQL instructions */
    private SQLAction       sqlAction;

    /** User defineable properties and the connection for the JDBC database */
    private Configuration config;

    /** Get the configuration (for example in order to open a DB connection)
     *  @return configuration
     */
    public Configuration getConfiguration() {
        return config;
    } // getConfiguration
    
    /** Initializes the class for the 1st (or 2nd, 3rd etc) call of {@link #processArguments} et al.
     *  @param callType whether the class is activated by CLI, WEB or SOAP
     */
    public void initialize(int callType) {
        config                  = new Configuration();
        config.configure        (callType);
        config.setFormatMode  ("def");        // -m, will be changed to tsv below
        config.setSeparator     ("\t");         // -s
        config.setDefaultSchema ("");
        verbose                 = 0;            // -v
        tableFactory            = new TableFactory();
    } // initialize

    /** Initializes the class for the 1st (or 2nd, 3rd etc) call of {@link #processArguments} et al.
     *  @param callType whether the class is activated by CLI, WEB or SOAP
     *  @param dsMap maps connection ids to pre-initialized DataSources, 
     *  see {@link DbatServlet}.
     */
    public void initialize(int callType, LinkedHashMap/*<1.5*/<String, DataSource>/*1.5>*/ dsMap) {
        initialize(callType);
        config                  = new Configuration();
        config.configure        (callType, dsMap);
    } // initialize(2)
    
    /** Terminates the processing of SQL statements, 
     *  clean-up for the next invocation
     */
    public void terminate() {
        config.closeConnection();
    } // terminate
    
    //========================
    // Auxiliary methods 
    //========================
    
    /** Removes optional quotes around a string
     *  @param quoted string with optional single or double quotes
     *  @return string content (without surrounding quotes)
     */
    private String prepareSeparator(String quoted) {
        String result = quoted;
        int len = quoted.length();
        if (len >= 2 && (quoted.startsWith("\"") || quoted.startsWith("\'"))) {
            if (quoted.charAt(0) == quoted.charAt(len - 1)) {
                result = quoted.substring(1, len - 1);
            }
        }
        return result
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\r", "\r")
                .replaceAll("\\\\t", "\t")
                .replaceAll("\\\\\\\\", "\\")
                ;
    } // prepareSeparator
      
    //====================================================================
    // The following 2 methods correspond to the Web interface
    //====================================================================
    
    /** Parses an XML with a character reader and a SAX handler, and
     *  executes the generated SQL against the database
     *  @param charReader reader for input, already opened
     *  @param handler SAX handler for Dbat specifications, must be already configured
     *  @param tbSerializer serializer for output format
     */
    public void parseXML(Reader charReader, SpecificationHandler handler, BaseTable tbSerializer) 
            throws Exception {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setFeature("http://xml.org/sax/features/namespaces"           , true);
            xmlReader.setFeature("http://xml.org/sax/features/validation"           , true);
            // xmlReader.setFeature("http://xml.org/sax/features/use-entity-resolver2" , true);
            xmlReader.setEntityResolver(handler.getEntityResolver());
            try { // protect against XML errors
                saxParser.parse(new InputSource(charReader), handler);
            } catch (SAXParseException exc) { // XML errors
                tbSerializer.writeMarkup("<h3 class=\"error\">XML SAX parsing error: " 
                        + exc.getMessage() 
                        + " in Dbat specification, line " + exc.getLineNumber()
                        + ", column " + exc.getColumnNumber()
                        + ", cause: " + exc.getCause()
                        +  "</h3>");
                // exc.printStackTrace();
                tbSerializer.writeEnd();
                throw exc;
            } catch (Exception exc) { // XML errors
                tbSerializer.writeMarkup("<h3 class=\"error\">XML processing error: " 
                        + exc.getMessage() 
                //      + " in Dbat specification, line " + exc.getLineNumber()
                //      + ", column " + exc.getColumnNumber()
                        + ", cause: " + exc.getCause()
                        +  "</h3>");
                // exc.printStackTrace();
                tbSerializer.writeEnd();
                throw exc;
            } // catch XML errors
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw exc;
        }
    } // parseXML

    /** Parses an XML specification (-f name.xml) with {@link SpecificationHandler} and 
     *  executes the generated SQL against the database
     *  @param specFileName filename of the XML specification
     *  @param formatMode output format, for example "html", "tsv"
     */
    public void processXMLFile(String specFileName, String formatMode) {
        try {
            SpecificationHandler handler = new SpecificationHandler(config);
            handler.setParameterMap(config.getParameterMap());
            handler.setEncoding(config.getEncoding(1));
            handler.setSerializer(config.getTableSerializer());
            handler.setSpecPaths("./", "./", specFileName);
            ReadableByteChannel channel = (new FileInputStream (specFileName)).getChannel();
            BufferedReader charReader = new BufferedReader(Channels.newReader(channel, config.getEncoding(0)));
            parseXML(charReader, handler, config.getTableSerializer());
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // processXMLFile

    //==============================================================
    // Methods dealing with the commandline arguments
    //==============================================================
     
    /** Configures the class from commandline arguments
     *  @param args command line arguments: options, strings, table- or filenames
     *  @param config configuration properties to be set for this session
     *  @param tbMetaData properties to be set for one SQL action
     *  @return index of procedure name in <em>args</em> behind <em>-call</em>
     */
    private int evaluateOptions(String[] args, Configuration config, TableMetaData tbMetaData) {
        String formatMode = "def";
        String propFileName = "dbat.properties";
        String defaultSchema = config.getDefaultSchema();
        String pair = null; // for options -o and -p
        int eqPos = 0;
        argsSql = "";
        mainAction = '?'; // undefined so far
        int ienc = 0;
        int iarg = 0; // argument index
        int karg = 0; // where arguments for stored procedure start: name -in:type1 value1 ...
        if (iarg >= args.length) { // show help if no arguments
            mainAction = 'h';
        } else {
            while (iarg < args.length) {
                String opt = args[iarg ++]; // main operation letters and modifiers
                if (opt.startsWith("-") && opt.length() >= 2) { // it is an option string
                    opt = opt.substring(1).toLowerCase();
                    // test for modifiers first, several of them may be present
 
                    if (opt.startsWith("a")) { // aggregate column name
                        if (iarg < args.length) {
                            tbMetaData.setAggregationName(args[iarg ++]);
                        } else {
                            log.error("Option -a and no following column name for aggregation");
                        }
                    }
                    
                    if (opt.startsWith("call")) { // call of stored procedure, all remaining arguments are special
                        mainAction = 'c';
                        karg = iarg;
                        iarg = args.length; // break args loop
                        // call
                    } else // important, and -call must be tested first
                    if (opt.startsWith("c")) { // explicit connection id
                        if (iarg < args.length) {
                            String connId = args[iarg ++];
                            if (! connId.endsWith(".properties")) {
                                propFileName = connId + ".properties";
                            } else {
                                propFileName = connId;
                            }
                            config.addProperties(propFileName);
                        } else {
                            log.error("Option -c and no following connection id");
                        }
                        defaultSchema = config.getDefaultSchema();
                    } // c
                    
                    if (opt.startsWith("e")) { // character encoding for input or output
                        if (iarg < args.length) {
                            config.setEncoding(ienc ++, args[iarg ++]);
                            if (ienc >= 2) {
                                ienc = 1; // fix on output format
                            }
                        } else {
                            log.error("Option -e and no following encoding");
                        }
                    } // e
                    
                    if (opt.startsWith("g")) { // list of column names for group change
                        if (iarg < args.length) {
                            tbMetaData.setGroupColumns(args[iarg ++]);
                        } else {
                            log.error("Option -g and no following column name(s) for grouping");
                        }
                    }
                    
                    if (opt.startsWith("h")) { // help - show usage
                        mainAction = 'h';
                    } // h
                    
                    if (opt.startsWith("l")) { // column lengths
                        if (iarg < args.length) {
                            tbMetaData.fillColumnWidths(args[iarg ++]);
                        } else {
                            log.error("Option -l and no following column lengths");
                        }
                    } // l
                    
                    if (opt.startsWith("m")) { // input/output mode
                        if (iarg < args.length) {
                            formatMode = args[iarg ++].toLowerCase();
                            config.setFormatMode(formatMode);
                        } else {
                            log.error("Option -m and no following output mode");
                        }
                    } // m
                    
                    if (opt.startsWith("nsp")) { // namespace prefix
                        if (iarg < args.length) {
                            config.setNamespacePrefix(args[iarg ++].toLowerCase());
                        } else {
                            log.error("Option -nsp and no following namespace prefix");
                        }
                    } // m
                    
                    if (opt.startsWith("o")) {
                        // property setting: "-o name=value" 
                        if (iarg < args.length) {
                            pair = args[iarg ++];
                            eqPos = pair.indexOf('=');
                            if (eqPos >= 0) {
                                config.setProperty(pair.substring(0, eqPos), pair.substring(eqPos + 1));
                            } else {
                                log.error("Option -o with invalid property assigment");
                            }
                        } else {
                            log.error("Option -o and no following property assignment");
                        }
                    } // p
                    
                    if (opt.startsWith("p")) {
                        // parameter setting: "-p name=value" or "-p name" (implies "name=true")
                        if (iarg < args.length) {
                            pair = args[iarg ++];
                            eqPos = pair.indexOf('=');
                            if (eqPos >= 0) {
                                config.getParameterMap().put(pair.substring(0, eqPos), new String[]{pair.substring(eqPos + 1)});
                            } else {
                                config.getParameterMap().put(pair, new String[]{"true"});
                            }
                        } else {
                            log.error("Option -p and no following parameter");
                        }
                    } // p
                    
                    if (opt.startsWith("sa")) { // separator string for aggregation
                        if (iarg < args.length) {
                            tbMetaData.setAggregationSeparator(prepareSeparator(args[iarg ++]));
                        } else {
                            log.error("Option -sa and no following separator");
                        }
                        // sa
                    } else
                    if (opt.startsWith("sp")) { // stored procedure text separator string 
                        if (iarg < args.length) {
                            config.setProcSeparator(prepareSeparator(args[iarg ++]));
                        } else {
                            log.error("Option -sp and no following separator");
                        }
                        // sp
                    } else // important, and -sa, -sp must be checked first  
                    if (opt.startsWith("s")) { // separator string
                        if (iarg < args.length) {
                            config.setSeparator(prepareSeparator(args[iarg ++]));
                        } else {
                            log.error("Option -s and no following separator");
                        }
                    } // s
                    
                    if (opt.startsWith("v")) { // verbose remarks
                        verbose = 1;
                        config.setVerbose(verbose);
                    } // v

                    if (opt.startsWith("x")) { // no headers
                        config.setWithHeaders(false);
                    } // x
 
                    /*------------------------------------------------
                        now the codes which result in database actions
                    */
                    if (opt.length() >= 1 && Character.isDigit(opt.charAt(0))) {
                        // -29: show first 29 rows
                        mainAction = 't';
                        try {
                            int flimit = Integer.parseInt(opt);
                            config.setFetchLimit(flimit);
                        } catch (NumberFormatException exc) {
                            // ignore, leave configured default value
                        }
                    } // 29
                    if (opt.startsWith("d")) { // DESCRIBE table
                        mainAction = 'd';
                        if (iarg < args.length) {
                            tbMetaData.setTableName(defaultSchema, args[iarg ++]);
                        } else {
                            tbMetaData.setTableName(defaultSchema, "%"); // describe all tables in the database
                        }
                    } // d
                    if (opt.startsWith("f")) { // SQL from file
                        mainAction = 'f';
                        if (iarg < args.length) {
                            setSourceName(args[iarg ++]); // file "-" => read from STDIN
                        } else {
                            log.error("Option -f and no following filename");
                        }
                    } // f
                    if (opt.startsWith("n") && ! opt.startsWith("nsp")) { // row count
                        mainAction = 'n';
                        if (iarg < args.length) {
                            tbMetaData.setTableName(defaultSchema, args[iarg ++]);
                        } else {
                            log.error("Option -n and no following table name");
                        }
                    }
                    if (opt.startsWith("r")) {
                        mainAction = 'r';
                        // insert raw CHAR values into table
                        if (iarg < args.length) {
                            tbMetaData.setTableName(defaultSchema, args[iarg ++]);
                        } else {
                            log.error("Option -r and no following table name");
                        }
                        if (iarg < args.length) {
                            if (args[iarg].startsWith("-")) {
                                setSourceName("-"); // option follows, read from STDIN
                            } else {
                                setSourceName(args[iarg ++]); // read from filename
                            }
                        } else {
                            setSourceName("-"); // read from STDIN
                        }
                    }
                    if (opt.startsWith("t")) {
                        if (mainAction == '?') {
                            mainAction = 't';
                        }
                        // config.setFetchLimit(1947062906);
                        if (iarg < args.length) { // 1 more argument
                            tbMetaData.setTableName(defaultSchema, args[iarg ++]);
                        } else {
                            log.error("Option -t and no following table name");
                        }
                    }
                    if (opt.startsWith("u")) { // URL for additional input file
                        if (iarg < args.length) {
                            config.setInputURI(args[iarg ++]); 
                        } else {
                            log.error("Option -u and no following URL");
                        }
                    } // u
                    if (opt.startsWith("z")) { // ZIP file for (B|C)LOB values
                        if (iarg < args.length) {
                            config.setZipFileName(args[iarg ++]);
                        } else {
                            log.error("Option -z and no following filename");
                        }
                    } // z
                    if (opt.equals("")) { // special case: single hyphen = STDIN
                        mainAction = 'f';
                        setSourceName("-");
                    }
                    // end of options with leading hyphen
                    
                } else if (opt.indexOf(' ') >= 0) { // contains space => is an SQL statement
                    argsSql = opt;
                    while (iarg < args.length) { // append (concatenate) all remaining arguments
                        argsSql += " " + args[iarg ++];
                    } // while appending
                    argsSql = argsSql.trim();
                    mainAction = 'q';

                } else { // is a table name
                    if (mainAction == '?') {
                        mainAction = 't';
                    }
                    tbMetaData.setTableName(defaultSchema, opt);
                } // end of case for arguments
            } // while args[]
 
            if (formatMode.equals("def")) {
                if (false) {
                } else if (mainAction == 'd') {
                    formatMode = "sql";
                } else if (mainAction == 'f' && isSourceType(".xml")) {
                    formatMode = "html";
                } else {
                    formatMode = "tsv";
                }
            } // default
 
            config.evaluateProperties();
        /*
            if (! config.getSeparator().equals("\t") && ! formatMode.equals("csv")) {  // -s was present
                formatMode = "csv";
            }
        */
            if (formatMode.equals("fix")) { 
                config.setWithHeaders(false);
                config.setSeparator("");
            }
            config.setFormatMode(formatMode);
            BaseTable tbSerializer = tableFactory.getTableSerializer(formatMode);
            tbSerializer.setTargetEncoding(config.getEncoding(1));
            tbSerializer.setSeparator     (config.getSeparator());
            tbSerializer.setInputURI      (config.getInputURI());
            config.setTableSerializer     (tbSerializer);
        } // at least 1 argument
        return karg;
    } // evaluateOptions
    
    /** Performs the main action on a configured instance of sqlAction.
     *  @param writer PrintWriter for result output
     *  @param karg index of procedure name in <em>args</em> behind <em>-call</em>, or 0
     *  @param args command line arguments: options, strings, table- or filenames,
     *  @param config configuration properties to be set for this session
     *  @param tbMetaData properties to be set for one SQL action
     *  needed only if <em>karg &gt; 0</em>
     */
    private void process(PrintWriter writer, int karg, String[] args, Configuration config, TableMetaData tbMetaData) {
        BaseTable tbSerializer = config.getTableSerializer();
        boolean binary = tbSerializer.isBinaryFormat();
        PrintWriter  charWriter; // Internal writer for character output
        OutputStream byteWriter; // Internal writer for binary    output
        try {  
            if (writer == null) { // write to System.out
                if (binary) {
                    byteWriter = new BufferedOutputStream(System.out);
                    tbSerializer.setByteWriter(byteWriter);
                } else {
                    WritableByteChannel target = Channels.newChannel(System.out);
                    charWriter = new PrintWriter(Channels.newWriter(target, config.getEncoding(1)), true); // autoFlush
                    tbSerializer.setCharWriter(charWriter);
                }
            } else { // writer from caller
                if (binary) {
                    byteWriter = null; // ??
                    tbSerializer.setByteWriter(byteWriter);
                } else {
                    charWriter = writer; // servlet response or other writer opened by the caller
                    tbSerializer.setCharWriter(charWriter);
                }
            } // writer from caller
            
            tbSerializer.setGenerator(config.getGenerator());
            config.setTableSerializer(tbSerializer);
            sqlAction  = new SQLAction(config);
            long startTime = System.nanoTime();
            if (writer == null 
                    && ! (tbSerializer instanceof TableGenerator)
                    && ! (mainAction == 'f' && isSourceType(".xml"))) {
                tbSerializer.setParameterMap(config.getParameterMap());
                String language = "en";
                Object obj      = config.getParameterMap().get("lang");
                if (obj != null) {
                    language    = ((String[]) obj)[0]; // override it from the HttpRequest
                }
                config.setLanguage(language);
                tbSerializer.writeStart(new String[] 
                        { "encoding"    , config.getEncoding(1)
                        , "contenttype" , config.getHtmlMimeType() 
                        , "nsp"         , config.getNamespacePrefix()
                        , "lang"        , language
                        , "conn"        , config.getConnectionId()
                        }
                        , config.getParameterMap()
                        );
            }
            switch (mainAction) {
                case ' ':
                    // ignore; set by -h
                    break;
                case 'c':
                    sqlAction.callStoredProcedure(config.getOpenConnection(), tbMetaData, karg, args);
                    break;
                case 'd':
                    String tablePattern = tbMetaData.getTableName();
                    if (config.getDriverURL() != null && config.getDriverURL().startsWith("jdbc:db2:") && tablePattern != null) {
                        tablePattern = tablePattern.toUpperCase();
                    }
                    sqlAction.describeTables(config.getDefaultSchema(), tablePattern);
                    break;
                case 'f':
                    if (isSourceType(".xml")) { // specification for SpecificationHandler
                        processXMLFile(getSourceName(), config.getFormatMode());
                    } else {
                        sqlAction.execSQLfromURI(tbMetaData
                                , getSourceName()
                                , config.getParameterMap());
                    }
                    break;
                default:
                case '?':
                case 'h':
                    // Messages.usage(config.getLanguage());
                    System.out.println(Messages.getHelpText(config.getLanguage(), tableFactory));
                    break;
                case 'n':
                    sqlAction.setWithHeaders(false);
                    sqlAction.execSQLStatement(tbMetaData
                            , "SELECT COUNT(*) FROM " + tbMetaData.getTableName()
                            , config.getParameterMap());
                    break;
                case 'r':
                    // sqlAction.processRawData(tbMetaData);
                    sqlAction.insertFromURI(tbMetaData, getSourceName());
                    break;
                case 't':
                    sqlAction.execSQLStatement(tbMetaData
                            , "SELECT * FROM " + tbMetaData.getTableName()
                            , config.getParameterMap());
                    break;
                case 'q':
                    sqlAction.execSQLStatement(tbMetaData
                            , argsSql
                            , config.getParameterMap());
                    break;
            } // switch mainAction

            if (verbose > 0) { // System.err is mapped to System.out in RegressionTester, do not move this code
                System.err.println(Messages.getTimingMessage(config.getLanguage()
                        , startTime
                        , sqlAction.getInstructionSum()
                        , sqlAction.getManipulatedSum()
                        , config.getDriverURL())
                        );
            }
            if (writer == null 
                    && ! (tbSerializer instanceof TableGenerator)
                    && ! (mainAction == 'f' && isSourceType(".xml"))) { // channel from System.out - flush it
                tbSerializer.writeEnd();
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            sqlAction.terminate();
            terminate();
            tbSerializer.close();
        }
    } // process

    /** Executes a {@link Dbat} command line as if it were passed by the shell,
     *  and prints the resulting rows in the specified format.
     *  This method is used by the deprecated {@link DbatService}.
     *  @param writer PrintWriter for result output
     *  @param commandLine command line with options, SQL, file- and/or tablenames
     */
    public void processCommandLine(PrintWriter writer, String commandLine) {
        processArguments(writer, Pattern.compile("\\s+").split(commandLine)); // split on whitespace
    } // processCommandLine

    /** Executes a single SQL statement (one string),
     *  and prints the resulting rows in the specified format
     *  @param writer PrintWriter for result output
     *  @param stmtSql options, file- and/or tablenames
     */
/*
    public void processStatement(PrintWriter writer, String stmtSql) {
        processArguments(writer, new String [] { stmtSql });
    } // processStatement
*/
    /** Processes all command line arguments, executes the SQL, 
     *  and prints the resulting rows in the specified format.
     *  Only one main action (SQL instruction) will be executed if no
     *  SQL or XML file is specified.
     *  @param writer PrintWriter for result output
     *  @param args command line arguments: options, strings, table- or filenames
     */
    public void processArguments(PrintWriter writer, String[] args) {
        TableMetaData tbMetaData = new TableMetaData();
        int karg = evaluateOptions(args, config, tbMetaData);
        // System.err.println("main action=" + mainAction + ", argsSql=" + argsSql);
        process(writer, karg, args, config, tbMetaData);
    } // processArguments
    
    //======================
    // Main method
    //======================
    
    /** Database Administration Tool -
     *  call it with -h to display possible options and arguments.
     *  The result is printed to STDOUT.
     *  @param args command line arguments: options, strings, table- or filenames
     */
    public static void main(String[] args) {
        Logger log = Logger.getLogger(Dbat.class.getName());
        Dbat dbat = new Dbat();
        if (args.length == 0) {
            args = new String[] { "-h" }; // usage message
        }
        try {
            dbat.initialize(Configuration.CLI_CALL);
            if (false) { // debug output
                StringBuffer commandline = new StringBuffer(128);
                int iarg = 0;
                commandline.append("Dbat");
                while (iarg < args.length) {
                    commandline.append(' ');
                    commandline.append(args[iarg ++]);
                } // while iarg
                System.out.println(commandline.toString());
            } // debug output
            dbat.processArguments(null, args); // null = write to System.out
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            dbat.terminate();
        }
    } // main

} // Dbat
