/*  DbatServlet.java - Database administration tool for JDBC compatible RDBMSs.
 *  @(#) $Id$
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
import  org.teherba.dbat.view.ConsolePage;
import  org.teherba.dbat.view.HelpPage;
import  org.teherba.dbat.view.MessagePage;
import  org.teherba.dbat.view.MetaInfPage;
import  org.teherba.dbat.view.MorePage;
import  org.teherba.dbat.view.ValidatePage;
import  org.teherba.xtrans.BaseTransformer;
import  org.teherba.xtrans.BasicFactory;
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
import  javax.naming.Context;
import  javax.naming.InitialContext;
import  javax.servlet.RequestDispatcher;
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
    /** Environment naming context obtained from <em>lookup("java:comp/env")</em> */
    private Context envContext;
    /** Whether the response is binary */
    private boolean binary;
    /** Dbat's configuration data */
    Configuration config;

    /** Called by the servlet container to indicate to a servlet
     *  that the servlet is being placed into service.
     *  @throws ServletException
     */
    @SuppressWarnings(value="unchecked")
    public void init() throws ServletException {
        log = Logger.getLogger(DbatServlet.class.getName());
        ServletContext context = getServletContext();
        config = new Configuration();
        try {
            envContext = (Context) new InitialContext().lookup("java:comp/env"); // get the environment naming context
            dsMap = new LinkedHashMap/*<1.5*/<String, DataSource>/*1.5>*/(4);
            String dsList = ((String) envContext.lookup("dataSources")).replaceAll("\\s+", "");
            log.info("DbatServlet: dsList=\"" + dsList + "\"");

            String[] pairs = dsList.split("\\,");
            int ipair = 0;
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
                    if (dsName.length() > 6) { // unusual, external DS names - use heuristics
                        if (false) {
                        } else if (dsName.indexOf("COSM") >= 0) {
                            connectionId = "cosm";
                        } else if (dsName.indexOf("DB2T") >= 0) {
                            connectionId = "db2t";
                        } else if (dsName.indexOf("DB2")  >= 0) {
                            connectionId = "db2a";
                        }
                    } // if heuristics
                } else if (parts.length == 2) { // explicit renaming of connectionId, e.g. "worddb:DBAT_Word_DataSource
                    connectionId = parts[0];
                    dsName       = parts[1];
                } else { // more than one ":"
                    // ignore
                }
                log.info("DbatServlet: connectionId=\"" + connectionId + "\" mapped to \"" + dsName + "\"");
                dsMap.put(connectionId, (DataSource) envContext.lookup("jdbc/" + dsName));
                ipair ++;
            } // while ipair
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }

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

    /** Gets the string value of an HTML input (parameter) field, and if it is not set, pass some default value
     *  @param request request for the HTML form
     *  @param name name of the input field
     *  @param defaultValue default value of the parameter
     *  @return non-null (but possibly empty) string value of the input field
     */
    private String getInputField(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    } // getInputField

    /** Gets the integer value of an HTML input (parameter) field, and if it is not set, pass some default value
     *  @param request request for the HTML form
     *  @param name name of the input field
     *  @param defaultValue default value of the parameter
     *  @return non-null (but possibly empty) string value of the input field
     */
    private int  getInputField(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        int result = defaultValue;
        if (value != null) {
            try {
                result = Integer.parseInt(value);
            } catch (Exception exc) {
                // take the default for NumberFormatExceptions
            }
        }
        return result;
    } // getInputField

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
                    || mode.startsWith("spec")
                    || mode.startsWith("trans")
                    ) {
                targetFileName +=     ".xml";
                mimeType =            "text/xml";
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
        handler.setParameterMap((Map/*<1.5*/<String, String[]>/*1.5>*/) request.getParameterMap());
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
        boolean isDbiv = false; // whether the input file is a Dbiv specification
        String specName             = getInputField(request, "spec"     , "index").replaceAll("\\.", "/");
                // spec subdirectories may be separated by "/" or by "."
        if (specName.endsWith("/iv")) {
            isDbiv = true;
            specName = specName.substring(0, specName.length() - 3) + ".iv"; // repair the ending
        } // isDbiv
        String connectionId         = getInputField(request, "conn"     , "");
        if (connectionId.length() > 0) {
            config.addProperties(connectionId + ".properties");
            config.setConnectionId(connectionId);
        }

        String view = request.getParameter("view");
        // The empty view is the default, and the DBIV view are handled likewise
        if (view == null || view.length() == 0 || view.matches("del|del2|dat|ins|ins2|upd|upd2|sear")) {
            try {
                HttpSession session = request.getSession();
                String waitTime     = "3"; // default
                String mode         = getInputField(request, "mode"     , "html"    );
                String encoding     = getInputField(request, "enc"      , "UTF-8"   );
                String language     = getInputField(request, "lang"     , "en"      );
                String separator    = getInputField(request, "sep"      , ";"       );
                String inputURI     = getInputField(request, "uri"      , null      );
                if (mode.compareToIgnoreCase("tsv") == 0) {
                    separator   = "\t";
                }
                int fetchLimit      = getInputField(request, "fetch"    , 0x7fffffff); // "unlimited"
                // response.setContentType(config.getHtmlMimeType()); // default
                binary = setResponseHeaders(response, mode, specName, encoding);

                ReadableByteChannel channel = null;
                String sourceFileName = realPath + specName + ".xml";
                File specFile = new File(sourceFileName);
                boolean found = specFile.exists();
                if (! found) { // spec file not found
                    session.setAttribute("lang", language);
                    session.setAttribute("parm", specName);
                    specFile = new File(realPath + specName + ".redir");
                    if (specFile.exists()) { // 304 - redirection found
                        channel = (new FileInputStream (specFile)).getChannel();
                        BufferedReader charReader = new BufferedReader(Channels.newReader(channel, "UTF-8"));
                                // assume all spec files in UTF-8 XML
                        String line = charReader.readLine();
                        charReader.close();
                        if (line != null) {
                            String[] parts = line.split(";"); // up to 3 parts: [wait;[lang;]]redir-spec
                            String targetName = parts[parts.length - 1];
                            switch (parts.length) {
                                default:
                                case 1:
                                    waitTime        = "3";
                                    language        = "en";
                                    break;
                                case 2:
                                    waitTime        = parts[0];
                                    language        = "en";
                                    break;
                                case 3:
                                    waitTime        = parts[0];
                                    language        = parts[1];
                                    break;
                            } // switch parts
                            sourceFileName = realPath + targetName + ".xml";
                            specFile = new File(sourceFileName);
                            session.setAttribute("messno", "301"); // moved permanently
                            session.setAttribute("par2" , targetName);
                            session.setAttribute("lang" , language);
                            session.setAttribute("par3" , waitTime);
                            session.setAttribute("wait" , waitTime);
                            // redir line != null
                        } else { // error, => 404
                            session.setAttribute("messno", "404"); // spec not found
                        }
                    } else { // 404 - really not found
                        session.setAttribute("messno", "404"); // spec not found
                    } // 404
                    (new MessagePage    ()).forward(request, response);
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
                        XtransFactory xtransFactory = new BasicFactory(); // knows XML only
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
                            session.setAttribute("view", view);
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
            (new ConsolePage    ()).forward(request, response, tableFactory, dsMap);

        } else if (view.equals("con2")) { // View "con2" is for the result of the SQL console
            try {
                HttpSession session = request.getSession();
                String waitTime = "3";
                String mode     = getInputField(request, "mode"     , "html"    );
                String encoding = getInputField(request, "enc"      , "UTF-8"   );
                String language = getInputField(request, "lang"     , "en"      );
                String separator= getInputField(request, "sep"      , ";"       );
                String intext   = getInputField(request, "intext"   , ";"       );
                int fetchLimit  = getInputField(request, "fetch"    , 64        );
                if (mode.compareToIgnoreCase("tsv") == 0) {
                    separator   = "\t";
                }
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
                            { "encoding"                                    , encoding
                            , "specname"                                    , specName
                            , "title"                                       , "Title"
                            }
                            , new HashMap/*<1.5*/<String, String[]>/*1.5>*/() // parameterMap
                            );
                sqlAction.execSQLStatement(tbMetaData, intext
                        , new ArrayList/*<1.5*/<String>/*1.5>*/() // variables
                        , new HashMap/*<1.5*/<String, String[]>/*1.5>*/() // parameterMap
                        );
                sqlAction.terminate();
                tbSerializer.writeEnd();
                tbSerializer.close();
            } catch (Exception exc) {
                log.error(exc.getMessage(), exc);
            } finally {
            }
            // view "con2"

        // now the views for the auxiliary pages
        } else if (view.equals("help")) {
            (new HelpPage       ()).forward(request, response, tableFactory);
        } else if (view.equals("license")
                || view.equals("manifest")
                || view.equals("notice")
                ) {
            (new MetaInfPage    ()).forward(request, response);
        } else if (view.equals("more")) {
            (new MorePage       ()).forward(request, response, tableFactory);
        } else if (view.equals("validate")) {
            (new ValidatePage   ()).forward(request, response);
        } else {
            // unknown view
        }
    } // generateResponse

} // DbatServlet
