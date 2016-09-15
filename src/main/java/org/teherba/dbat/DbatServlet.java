/*  DbatServlet.java - Database administration tool for JDBC compatible RDBMSs.
 *  @(#) $Id$
 *  2016-09-15: BasicFactory replaced by XtransFactory again
 *  2016-09-12: getDataSourceMap moved from init() to Configuration
 *  2016-08-25: message texts here; new message 405: unknown request parameter &view=...
 *  2016-08-09: pass "conn" in writeStart; mode=spec|view -> *.xml
 *  2016-05-11: read Configuration in init(); Kim = 15
 *  2016-04-28: &uri=... -> config.setInputURI(inputURI);
 *  2014-11-11: handler.setResponse
 *  2014-11-05: transforms and processes dbiv specs also
 *  2012-07-01: subpackage view; ConsolePage
 *  2012-03-16: dsMap filled only here, DBCPoolingListener abandonned
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
package org.teherba.dbat;
import  org.teherba.dbat.Dbat;
import  org.teherba.dbat.Configuration;
import  org.teherba.dbat.SpecificationHandler;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.TableFactory;
import  org.teherba.dbat.web.ConsolePage;
import  org.teherba.dbat.web.HelpPage;
import  org.teherba.dbat.web.Messages;
import  org.teherba.dbat.web.MorePage;
import  org.teherba.common.web.BasePage;
import  org.teherba.common.web.MetaInfPage;
import  org.teherba.xtrans.BaseTransformer;
import  org.teherba.xtrans.XMLTransformer;
import  org.teherba.xtrans.XtransFactory;
import  java.io.BufferedReader;
import  java.io.Reader;
import  java.io.File;
import  java.io.FileInputStream;
import  java.io.IOException;
import  java.nio.channels.Channels;
import  java.nio.channels.ReadableByteChannel;
import  java.util.ArrayList;
import  java.util.Enumeration;
import  java.util.Iterator;
import  java.util.HashMap;
import  java.util.LinkedHashMap;
import  java.util.Map;
import  javax.servlet.ServletContext;
import  javax.servlet.ServletException;
import  javax.servlet.http.HttpServlet;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  javax.servlet.http.HttpSession;
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
    /** URL path to this application */
    private String realPath;
    /** Maps connection identifiers (short database instance ids) to {@link DataSource Datasources} */
    private LinkedHashMap<String, DataSource> dsMap;
    /** Whether the response is binary */
    private boolean binary;
    /** Dbat's configuration data */
    private Configuration config;
    /** common code and messages for auxiliary web pages */
    private BasePage basePage;
    /** name of this application */
    private static final String APP_NAME = "Dbat";

    /** Called by the servlet container to indicate to a servlet
     *  that the servlet is being placed into service.
     *  @throws ServletException
     */
    @SuppressWarnings(value="unchecked")
    public void init() throws ServletException {
        log = Logger.getLogger(DbatServlet.class.getName());
        basePage = new BasePage(APP_NAME);
        Messages.addMessageTexts(basePage);

        config = new Configuration();
        dsMap = config.getDataSourceMap();        

        ServletContext context = getServletContext();
        realPath = context.getInitParameter("specPath");
        if (realPath == null) {
            realPath = context.getRealPath("/").replaceAll("\\\\", "/");
            if (! realPath.endsWith("/")) {
                realPath += "/"; // "/" before "spec" for WAS
            }
            realPath += "spec/";
        }
        if (! realPath.endsWith("/")) {
            realPath += "/";
        }
        log.info("realPath=" + realPath);

        if (true) { // debugging
            Iterator<String> miter = dsMap.keySet().iterator();
            while (miter.hasNext()) {
                String connectionId = miter.next();
                DataSource ds = dsMap.get(connectionId);
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
     *  @throws IOException
     */
    public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        generateResponse(request, response);
    } // doGet

    /** Processes an http POST request
     *  @param request request with header fields
     *  @param response response with writer
     *  @throws IOException
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        generateResponse(request, response);
    } // doPost

    /** Sets the content-disposition with target filename and the content-type
     *  of the response depending on the output format (mode)
     *  @param response response of the Http request (headers are set therein)
     *  @param mode output format (default: set to "html" if it was not set)
     *  @param specName name of the specification file
     *  @param encoding target/response encoding
     */
    private boolean setResponseHeaders(HttpServletResponse response, String mode, String specName, String encoding) {
        boolean result = false; // assume legible character response output
        String targetFileName = specName.replaceAll("/", ".");
        String mimeType = "";
        try {
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
            response.setHeader("Content-Disposition", "inline; filename=\"" + targetFileName + "\"");
            response.setCharacterEncoding(encoding);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result;
    } // setResponseHeaders

    /** Sets the parameter map for the SAX handler
     *  @param handler instance of the SAX handler where to store the parameter map
     *  @param request take the parameter map from this request
     */
    @SuppressWarnings(value="unchecked")
    private void uncheckedSetParameterMap(SpecificationHandler handler, HttpServletRequest request) {
        handler.setParameterMap((Map<String, String[]>) request.getParameterMap());
    } // uncheckedSetParameterMap

    /** Generates the response (HTML page) for an HTTP request
     *  @param request request with header fields
     *  @param response response with writer
     *  @throws IOException
     */
    public void generateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        binary = false; // assume legible character response output
        config.configure(config.WEB_CALL, dsMap);
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
                binary = setResponseHeaders(response, mode, specName, encoding);

                ReadableByteChannel channel = null;
                String sourceFileName = realPath + specName + ".xml";
                File specFile = new File(sourceFileName);
                boolean found = specFile.exists();
                if (! found) { // spec file not found
                    // session.setAttribute("lang", language);
                    // session.setAttribute("parm", specName);
                    specFile = new File(realPath + specName + ".redir"); // try same name with ".redir"
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
                            sourceFileName = realPath + targetName + ".xml";
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
                    uncheckedSetParameterMap(handler, request);
                    tbSerializer.setMimeType(response.getContentType());
                    tbSerializer.setSeparator(separator);
                    handler.setSerializer(tbSerializer);
                    config.setTableSerializer(tbSerializer);
                    handler.setSpecPaths(realPath, "spec/", specName);
                    if (inputURI != null) {
                        config.setInputURI(realPath + inputURI);
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
                        TransformerHandler styler = xtransFactory.getXSLHandler(realPath + "../xslt/dbiv_dbat.xsl");
                        generator.setContentHandler(styler);
                        generator.setProperty("http://xml.org/sax/properties/lexical-handler", styler);
                        styler.setResult(new SAXResult(serializer));
                        generator.openFile(0, realPath + specName + ".xml");
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
            } finally {
            }
            // if (view == null || view.length() == 0 || view.matches("del|del2|dat|ins|ins2|upd|upd2|sear"))

        } else if (view.equals("con")) { // View "con" collects the parameters from the SQL Console
            (new ConsolePage    ()).showConsole(request, response, basePage, language, tableFactory, dsMap);

        } else if (view.equals("con2")) { // View "con2" is for the result of the SQL console
            try {
                String intext   = BasePage.getInputField(request, "intext"   , ";"       );
                fetchLimit      = BasePage.getInputField(request, "fetch"    , 64        );
                // response.setContentType(config.getHtmlMimeType()); // default
                setResponseHeaders(response, mode, specName, encoding);
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
                if (connectionId    != null) {
                    config.addProperties(connectionId + ".properties");
                    config.setConnectionId(connectionId);
                } else {
                    config.setConnectionId(); // default: take first in list
                }
                /* initializeAction */
                SQLAction sqlAction  = new SQLAction(config);
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
                sqlAction.execSQLStatement(tbMetaData, intext
                        , new ArrayList<String>() // variables
                        , new HashMap<String, String[]>() // parameterMap
                        );
                sqlAction.terminate();
                tbSerializer.writeEnd();
                tbSerializer.close();
            } catch (Exception exc) {
                log.error(exc.getMessage(), exc);
            }
            // view "con2"

        // now the views for the auxiliary pages
        } else if (view.equals("help")) {
            (new HelpPage       ()).showHelp    (request, response, basePage, language, tableFactory);
        } else if (view.equals("license")
                || view.equals("manifest")
                || view.equals("notice")
                ) {
            (new MetaInfPage    ()).showMetaInf (request, response, basePage, language, view, this);
        } else if (view.equals("more")) {
            (new MorePage       ()).showMore    (request, response, basePage, language, tableFactory);
        } else if (view.equals("validate")) {
            (new MorePage       ()).showValidate(request, response, basePage, language);
        } else { // unknown view
            basePage.writeMessage(request, response, language, new String[] { "405", "view", view });
        }
    } // generateResponse

} // DbatServlet
