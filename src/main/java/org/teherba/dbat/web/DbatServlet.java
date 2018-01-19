/*  DbatServlet.java - Database administration tool for JDBC compatible RDBMSs.
 *  @(#) $Id$
 *  2018-01-11: property "console=none|SELECT|UPDATE"; ConfigurationPage
 *  2017-06-17: StaticMirror
 *  2017-05-27: javadoc 1.8
 *  2016-12-09: Content-disposition filename with parameter values
 *  2016-10-11: package dbat.web; no try...catch; pass exceptions to common.ErrorServlet
 *  2016-09-15: BasicFactory replaced by XtransFactory again; init() not unchecked
 *  2016-09-12: getDataSourceMap moved from init() to Configuration
 *  2016-08-25: message texts here; new message 405: unknown request parameter &view=...
 *  2016-08-09: pass "conn" in writeStart; mode=spec|view -> *.xml
 *  2016-05-11: read Configuration in init(); Kim = 15
 *  2016-04-28: &uri=... -> config.setInputURI(inputURI);
 *  2014-11-11: handler.setResponse
 *  2014-11-05: transforms and processes dbiv specs also
 *  2012-07-01: subpackage view; ConfigurationPage
 *  2012-03-16: dataSourceMap filled only here, DBCPoolingListener abandonned
 *  2012-02-11: all JSPs replaced by View*.java
 *  2011-12-14: response.setContentType("text/comma-separated-values");
 *  2011-12-03: echo, fix, jdbc, sql, taylor, wiki and all others -> text/plain, *.txt
 *  2011-07-27: path separator "." in specName is the same as "/"; -m wiki
 *  2011-07-21: spec file not found error message
 *  2011-05-09: Version 5 with Configuration, SpecificationHandler etc.
 *  2011-01-21: response.setContentType("application/xhtml+xml") instead of "text/html"
 *  2010-09-23: with map of DBCP DataSources
 *  2010-09-21: thread-safety, new TableFactory() in getResponse; error message from XML parser
 *  2010-07-24: mode 'xls'
 *  2010-05-27: finally dbat.terminate()
 *  2010-05-19: inject mimeType for special treatment of mm-nn-pp values in Excel
 *  2008-02-08: renamed from TableServlet
 *  2007-01-05, Georg Fischer: copied from GrammarServlet and rewritten from dbtab.pl
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
package org.teherba.dbat.web;
import  org.teherba.dbat.Dbat;
import  org.teherba.dbat.Configuration;
import  org.teherba.dbat.SpecificationHandler;
import  org.teherba.dbat.SQLAction;
import  org.teherba.dbat.StaticMirror;
import  org.teherba.dbat.TableMetaData;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.TableFactory;
import  org.teherba.dbat.web.ConfigurationPage;
import  org.teherba.dbat.web.ConsolePage;
import  org.teherba.dbat.web.HelpPage;
import  org.teherba.dbat.web.Messages;
import  org.teherba.dbat.web.MorePage;
import  org.teherba.common.web.BasePage;
import  org.teherba.common.web.MetaInfPage;
import  org.teherba.xtrans.BaseTransformer;
import  org.teherba.xtrans.XtransFactory;
import  java.io.BufferedReader;
import  java.io.Reader;
import  java.io.File;
import  java.io.FileInputStream;
import  java.io.IOException;
import  java.nio.channels.Channels;
import  java.nio.channels.ReadableByteChannel;
import  java.util.ArrayList;
import  java.util.Iterator;
import  java.util.HashMap;
import  java.util.LinkedHashMap;
import  java.util.Map;
import  javax.servlet.ServletContext;
import  javax.servlet.ServletException;
import  javax.servlet.http.HttpServlet;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  javax.sql.DataSource;
import  javax.xml.transform.sax.SAXResult;
import  javax.xml.transform.sax.TransformerHandler;
import  org.apache.log4j.Logger;

/** Database administration tool for JDBC compatible relational databases.
 *  The servlet (SAX-) parses an XML file with descriptions for SQL SELECT statements,
 *  replaces any parameter placeholders in this description,
 *  runs the queries
 *  and displays the resulting rows in HTML tables with
 *  <ul>
 *  <li>specified column headers</li>
 *  <li>optional grouping of rows</li>
 *  <li>colouring of selected cell values</li>
 *  <li>links to other XML descriptions which take the cell value as parameter</li>
 *  </ul>
 *  @author Dr. Georg Fischer
 */
public class DbatServlet extends HttpServlet {
    public final static String CVSID = "@(#) $Id$";
    public final static long serialVersionUID = 19470629;

    /** log4j logger (category) */
    private Logger log;
    /** URL path to this application, e.g. /var/lib/tomcat8/webapps/dbat[/spec/] */
    private String specPath;
    /** Dbat's configuration data */
    private Configuration config;
    /** Maps connection identifiers to "none", "SELECT" or "UPDATE" (for the behaviour of ConsolePage) */
    private LinkedHashMap<String, String>     consoleMap;
    /** Maps connection identifiers (short database instance ids) to {@link DataSource Datasources} */
    private LinkedHashMap<String, DataSource> dataSourceMap;
    /** common code and messages for auxiliary web pages */
    private BasePage basePage;
    /** name of this application */
    private static final String APP_NAME = "Dbat";

    /** Called by the servlet container to indicate to a servlet
     *  that the servlet is being placed into service.
     *  @throws ServletException if a Servlet error occurs
     */
    public void init() throws ServletException {
        log = Logger.getLogger(DbatServlet.class.getName());
        basePage = new BasePage(APP_NAME);
        Messages.addMessageTexts(basePage);

        config = new Configuration();
        config.configure(config.WEB_CALL);
        config.loadContextEnvironment();
        dataSourceMap = config.getDataSourceMap();
        consoleMap    = config.getConsoleMap();

        ServletContext context = getServletContext();
        specPath = context.getInitParameter("specPath");
        if (specPath == null) {
            specPath = context.getRealPath("/").replaceAll("\\\\", "/");  // e.g. /var/lib/tomcat8/webapps/dbat
            if (! specPath.endsWith("/")) {
                specPath += "/"; // "/" before "spec" for WAS
            }
            specPath += "spec/";
        } else if (! specPath.endsWith("/")) {
            specPath += "/";
        }
        log.info("specPath=" + specPath); // e.g.  specPath=/var/lib/tomcat8/webapps/dbat/spec/

        if (false) { // debugging
            Iterator<String> miter = dataSourceMap.keySet().iterator();
            while (miter.hasNext()) {
                String connectionId = miter.next();
                DataSource ds = dataSourceMap.get(connectionId);
                log.info("init: connectionId=" + connectionId + ", DataSource=" + ds.toString());
                try {
                    log.info("driverURL=" + ds.getConnection().getMetaData().getURL());
                } catch (Exception exc) {
                    log.error(exc.getMessage(), exc);
                }
            } // while miter
        } // debugging
    } // init

    /** Processes an http GET request
     *  @param request request with header fields
     *  @param response response with writer
     *  @throws IOException if an IO error occurs
     */
    public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        generateResponse(request, response);
    } // doGet

    /** Processes an http POST request
     *  @param request request with header fields
     *  @param response response with writer
     *  @throws IOException if an IO error occurs
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        generateResponse(request, response);
    } // doPost

    /** Sets the content-disposition with target filename and the content-type
     *  of the response depending on the output format (mode)
     *  @param request  the Http request (with parameters)
     *  @param response response of the Http request (headers are set therein)
     *  @param mode     output format (default: set to "html" if it was not set)
     *  @param specName name of the specification file
     *  @param encoding target/response encoding
     *  @return whether response is binary
     */
    private boolean setResponseHeaders(HttpServletRequest request, HttpServletResponse response,
            String mode, String specName, String encoding) {
        boolean result = false; // assume legible character response output
        String targetFileName = specName.replaceAll("/", ".");

        if (true) { // append parameter values
            Map parameterMap = request.getParameterMap(); // do NOT! use <String, String[]>
            Iterator parmIter = parameterMap.keySet().iterator();
            StringBuffer buffer = new StringBuffer(256);
            while (parmIter.hasNext()) {
                String name = (String) parmIter.next();
                String[] values = request.getParameterValues(name);
                if (values.length <= 0) { // ignore empty value lists
                } else if (name.equals("enc"        )) {
                } else if (name.equals("mode"       )) {
            //  } else if (name.equals("lang"       )) {
                } else if (name.equals("spec"       )) {
            //  } else if (name.equals("view"       )) {
                    // ignore
                } else if (values[0].length() > 0) { // unknown, filled parameter name - must be an input field
                    buffer.append(".");
                    buffer.append(values[0]);
                } // unknown
            } // while parmIter
            targetFileName += buffer.toString().replaceAll("[^a-zA-Z0-9.\\-]", "_");
        } // append parameter values

        String mimeType = "";
        if (true) { // try {
            if (false) {
            } else if (mode.startsWith("html")) {
                targetFileName +=     ".html";
                mimeType =            "text/html";
            } else if (mode.startsWith("tsv" )) {
                targetFileName +=     ".csv";
                mimeType =            "application/vnd.ms-excel";
            } else if (mode.startsWith("csv" )) {
                targetFileName +=     ".txt";
                mimeType =            "text/comma-separated-values";
            } else if (mode.startsWith("xlsx")) {
                targetFileName +=     ".xlsx";
                mimeType =            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if (mode.startsWith("xls")) {
                targetFileName +=     ".xls";
                mimeType =            "application/vnd.ms-excel;charset=";
            } else if (mode.startsWith("xml" )
                    || mode.startsWith("trans")
                    ) {
                targetFileName +=     ".xml";
                mimeType =            "text/xml";
            } else if (mode.startsWith("spec")
                    || mode.startsWith("view")
                    ) {
                targetFileName +=     ".xml";
                mimeType =            "text/plain";
            } else { // echo, fix, jdbc, json, sql, taylor, wiki and all others
                targetFileName +=     ".txt";
                mimeType =            "text/plain";
            }
            response.setContentType(mimeType + ";charset=" + encoding);
            response.setHeader("Content-Disposition", "inline;filename=\"" + targetFileName + "\""); // attachment would store immediately
            response.setCharacterEncoding(encoding);
     /*
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
     */
        }
        return result;
    } // setResponseHeaders

    /** Generates the response (HTML page) for an HTTP request
     *  @param request request with header fields
     *  @param response response with writer
     *  @throws IOException if an IO error occurs
     */
    public void generateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean binary = false; // assume legible character response output
        // config.configure(config.WEB_CALL, dataSourceMap); // why? because of withHeaders
        TableFactory tableFactory = new TableFactory();
        request.setCharacterEncoding("UTF-8");
        // HttpSession session = request.getSession();

        boolean isDbiv = false; // whether the input file is a Dbiv specification
        String specName     = BasePage.getInputField(request, "spec"     , "index")
                .replaceAll("\\.", "/"); // spec subdirectories may be separated by "/" or by "."
        if (specName.endsWith("/iv")) {
            isDbiv = true;
            specName = specName.substring(0, specName.length() - 3) + ".iv"; // repair the ending
        } // isDbiv
        String connectionId = BasePage.getInputField(request, "conn"     , "");
        if (connectionId.length() > 0) {
            config.addProperties(connectionId + ".properties");
            config.setConnectionId(connectionId);
        } // with conn
        connectionId        = config.getConnectionId();
        String console      = consoleMap.get(connectionId);
        if (console == null) {
            console         = config.CONSOLE_NONE;
        }
        String encoding     = BasePage.getInputField(request, "enc"      , "UTF-8"   );
        int    fetchLimit   = BasePage.getInputField(request, "fetch"    , 0x7fffffff); // "unlimited"
        String inputURI     = BasePage.getInputField(request, "uri"      , null      );
        String mode         = BasePage.getInputField(request, "mode"     , "html"    );
        String language     = BasePage.getInputField(request, "lang"     , "en"      );
        String separator    = BasePage.getInputField(request, "sep"      , ";"       );
        if (mode.compareToIgnoreCase("tsv") == 0) {
            separator   = "\t";
        }
        String view         = BasePage.getInputField(request, "view"     , "");
            // The empty view is the default, and the DBIV views are handled likewise
        String waitTime     = "3"; // default

        if (view == null || view.length() == 0 || view.matches("del|del2|dat|ins|ins2|upd|upd2|sear")) {
            try {
                // response.setContentType(config.getHtmlMimeType()); // default
                binary = setResponseHeaders(request, response, mode, specName, encoding);

                ReadableByteChannel channel = null;
                String sourceFileName = specPath + specName + ".xml";
                File specFile = new File(sourceFileName);
                boolean found = specFile.exists();
                if (! found) { // spec file not found
                    // session.setAttribute("lang", language);
                    // session.setAttribute("parm", specName);
                    specFile = new File(specPath + specName + ".redir"); // try same name with ".redir"
                    if (specFile.exists()) { // 304 - redirection found
                        channel = (new FileInputStream (specFile)).getChannel();
                        BufferedReader charReader = new BufferedReader(Channels.newReader(channel, "UTF-8"));
                                // assume all spec files in UTF-8 XML
                        String line = charReader.readLine();
                        charReader.close();
                        if (line != null) { // at least one line
                            String[] parts = line.split(";"); // up to 3 parts: [wait;[lang;]]redir-spec
                            String targetName = parts[parts.length - 1];
                            switch (parts.length) {
                                default:
                                case 1:
                                    waitTime        = "3";
                                    // language        = "en";
                                    break;
                                case 2:
                                    waitTime        = parts[0];
                                    // language        = "en";
                                    break;
                                case 3:
                                    waitTime        = parts[0];
                                    language        = parts[1];
                                    break;
                            } // switch parts
                            sourceFileName = specPath + targetName + ".xml";
                            specFile = new File(sourceFileName);
                            basePage.writeMessage(request, response, language, new String[]
                                    { "301", specName, "/dbat/servlet?spec=" + targetName, waitTime });
                            // redir line != null
                        } else { // .redir has no line - error, => 404
                            basePage.writeMessage(request, response, language, new String[]
                                    { "404", specName });
                        }
                    } else { // .redir also not found: 404 - really not found
                        basePage.writeMessage(request, response, language, new String[]
                                { "404", specName });
                    } // 404
                } // not found

                if (found)  {
                    config.setFormatMode(mode);
                    config.setLanguage(language);
                    config.setFetchLimit(fetchLimit);
                    BaseTable tbSerializer = tableFactory.getTableSerializer(mode);
                    SpecificationHandler handler = new SpecificationHandler(config);
                    if (tbSerializer.isBinaryFormat()) {
                        tbSerializer.setByteWriter(response.getOutputStream());
                    } else {
                        tbSerializer.setCharWriter(response.getWriter());
                    }
                    // handler.setCharWriter(response.getWriter());
                    handler.setEncoding(response.getCharacterEncoding());
                    handler.setRequest (request );
                    handler.setResponse(response);
                    handler.setParameterMap((Map<String, String[]>) request.getParameterMap());
                    // uncheckedSetParameterMap(handler, request);
                    tbSerializer.setMimeType(response.getContentType());
                    tbSerializer.setSeparator(separator);
                    handler.setSerializer(tbSerializer);
                    config.setTableSerializer(tbSerializer);
                    handler.setSpecPaths(specPath, "spec/", specName);
                    if (inputURI != null) {
                        config.setInputURI(specPath + inputURI);
                    }

                    if (! isDbiv) { // conventional Dbat XML syntax
                        channel = (new FileInputStream (specFile)).getChannel();
                        Reader charReader = new BufferedReader(Channels.newReader(channel, "UTF-8"));
                                // assume all spec files in UTF-8 XML
                        (new Dbat()).parseXML(charReader, handler, tbSerializer);
                    } else { // Dbiv XML syntax for interactive views
                        XtransFactory xtransFactory = new XtransFactory(); // knows XML only
                        BaseTransformer generator  = xtransFactory.getTransformer("xml");
                        BaseTransformer serializer = handler;
                        TransformerHandler styler = xtransFactory.getXSLHandler(specPath + "../xslt/dbiv_dbat.xsl");
                        generator.setContentHandler(styler);
                        generator.setProperty("http://xml.org/sax/properties/lexical-handler", styler);
                        styler.setResult(new SAXResult(serializer));
                        generator.openFile(0, specPath + specName + ".xml");
                        generator.generate();
                        generator.closeAll();
                    } // isDbiv

                    if (! handler.isSuccessful()) {
                        if (view.matches("(del|ins|upd)2?")) {
                            view = view.substring(0, 3); // without "2" => back to the input form
                            // session.setAttribute("view", view);
                        }
                    } // ! successful
                    tbSerializer.close();
                } // found
            } catch (Exception exc) {
                log.error(exc.getMessage(), exc);
                throw new IOException(exc.getMessage());
            } finally {
            }
            // if (view == null || view.length() == 0 || view.matches("del|del2|dat|ins|ins2|upd|upd2|sear"))

        } else if (view.equals("con")) { // View "con" collects the parameters from the SQL Console
            if (consoleMap.size() <= 0) { // not any connection allowed
                basePage.writeMessage(request, response, language, new String[] { "410" });
                        // "Execution of SQL instructions is not allowed."
            } else {
                (new ConsolePage()).showConsole(request, response, basePage, language, tableFactory, consoleMap);
            }
        } else if (view.equals("con2")) { // View "con2" is for the result of the SQL console
            if (consoleMap.size() <= 0) { // not any connection allowed
                basePage.writeMessage(request, response, language, new String[] { "410" });
            } else if (console.equals(Configuration.CONSOLE_NONE)) {
                basePage.writeMessage(request, response, language, new String[] { "411", connectionId });
                        // Execution of SQL instructions for DB connection <em>{parm}</em> is not allowed."
            } else { // CONSOLE_SELECT || CONSOLE_UPDATE
                String intext   = BasePage.getInputField(request, "intext"   , ";"       );
                fetchLimit      = BasePage.getInputField(request, "fetch"    , 64        );
                // response.setContentType(config.getHtmlMimeType()); // default
                setResponseHeaders(request, response, mode, specName, encoding);
                BaseTable tbSerializer = tableFactory.getTableSerializer(mode);
                tbSerializer.setMimeType(response.getContentType());
                tbSerializer.setSeparator(separator);
                tbSerializer.setTargetEncoding(encoding);
                if (tbSerializer.isBinaryFormat()) {
                    tbSerializer.setByteWriter(response.getOutputStream());
                } else {
                    tbSerializer.setCharWriter(response.getWriter());
                }
                config.setFormatMode(mode);
                config.setLanguage(language);
                config.setFetchLimit(fetchLimit);
                config.setTableSerializer(tbSerializer);
                SQLAction sqlAction  = new SQLAction(config, console); // possibly readonly if in SQL console window
                TableMetaData tbMetaData = new TableMetaData(config);
                tbMetaData.setFillState(1);
                tbSerializer.writeStart(new String[] // this may throw an exception "could not compile stylesheet"
                            { "encoding", encoding
                            , "specname", specName
                            , "conn"    , config.getConnectionId()
                            , "title"   , "Title"
                            }
                            , new HashMap<String, String[]>() // parameterMap
                            );
                String verb = sqlAction.execSQLStatement(tbMetaData, intext
                        , new ArrayList<String>() // variables
                        , new HashMap<String, String[]>() // parameterMap
                        );
                // update c01 set gender='A' where name='Lucie'
                // select * from c01
                if (! verb.equals("SELECT")) {
                    tbSerializer.writeMarkup("<ht:h4>"
                            + sqlAction.getManipulatedSum()
                            + " row(s) affected</ht:h4>\n");
                } // ! SELECT
                sqlAction.terminate();
                tbSerializer.writeEnd();
                tbSerializer.close();
            } // allowed
            // view "con2"
        } else if (view.equals("config")) {
                (new ConfigurationPage    ()).showConfiguration(request, response, basePage, language, config);

        // now the views for the auxiliary pages
        } else if (view.equals("help")) {
            (new HelpPage       ()).showHelp    (request, response, basePage, language, tableFactory);
        } else if (view.equals("license")
                || view.equals("manifest")
                || view.equals("notice")
                ) {
            (new MetaInfPage    ()).showMetaInf (request, response, basePage, language, view, this);
        } else if (view.equals("more")) {
            (new MorePage       ()).showMore    (request, response, basePage, language, tableFactory, config);
        } else if (view.equals("mirror")) {
            /*
                nyi
            */
        } else if (view.equals("validate")) {
            (new MorePage       ()).showValidate(request, response, basePage, language);
        } else { // unknown view
            basePage.writeMessage(request, response, language, new String[] { "405", "view", view });
        }
    } // generateResponse

} // DbatServlet
