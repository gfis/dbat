/*  SpecificationHandler.java - Parser and processor for Dbat XML specifications
    @(#) $Id$
    2024-12-23: setContext() removed
    2022-03-26: config.set|getSpecPath -> error messages
    2021-06-12: attributes <col label2="..." span2="1">
    2021-02-15: with Config.getTrailerElements
    2021-01-04: &execsql=0 only if there is no user http request parameter
    2020-11-16: &execsql=1|0, <select scroll="w,h">
    2020-05-04: attribute target= in <col>
    2017-09-16: default for Excel was mode=xls, now xlsx
    2017-05-27: javadoc 1.8
    2016-10-13: less imports
    2016-08-09: pass "conn" in writeStart; DBIV_TAG; log.info in resolveEntity
    2016-07-27: correction in size= attribute of listbox
    2016-07-15: <db:select multiple="yes" />
    2016-05-24: <connect to="..." />
    2016-05-05: incompatible change: entities with relative systemIds are relative to specDir
    2014-11-10: format="none"
    2014-11-07: <read> element and wrap= attribute in <col>
    2014-03-05: use (ordered) TreeSet for parameterMap, for better reproducibility of web tests
    2012-10-19: use Messages.getDefaultCounterDesc
    2012-09-17: if <dbat stylesheet="..."> attribute is present, it is taken as is
    2012-08-04: pass specUrl to HTML stylesheet declaration
    2012-06-27: Windows drive letter in relative file entity declaration
    2012-06-19: parameter not found in <listbox> => select 1st <option>; <choose> not for static formats
    2012-06-13: <var> for prepared statements in addition to <parm>
    2012-05-03: target="_blank" for Excel, spec
    2012-04-04: listbox
    2012-03-01: pseudo variables remote_user, update_count
    2012-02-16: resolveEntity here and not in separate class; setSpecPaths with 3 parameters
    2012-02-15: <db:div> as element bracket for included system entities is ignored
    2012-01-16: <column> like <col> with all attributes replaced by elements
    2011-12-12: moreTag with &lang=
    2011-12-05: <parm init="..." />, <order>by 1 for fetch only</order>
    2011-11-15: sqlAction.terminate() closes resp. returns the connection
    2011-10-07: <choose><when parm="" match=""><otherwise> conditional compilation
    2011-09-30: <col remark="...">
    2011-08-05: field validation patterns
    2011-08-01: listbox, textarea, init= values, attribute list="box|char|int" for <parm
    2011-05-11: restructured with Configuration, SQLAction
    2011-04-07: repair empty <listparm>, and <parm list="char|int" ...>
    2011-04-01: output of SpecificationHandler.DBAT_URI is suppressed with an attribute <dbat namespace="no" ...>
    2011-03-29: headers="false" => dbat.setWithHeaders(false);
    2011-01-24: root attribute xslt=; encoding -> targetEncoding
    2010-09-10: output specs as text/plain
    2010-06-16: sqlBuffer all the same for optional WITH before SELECT
    2010-05-21: with namespaces (xmlns)
    2010-03-09: Call a Stored Procedure
    2010-02-26: better serializer isolation; WITH
    2010-02-02: different encodings for input and output
    2009-03-09: distinct="yes|true"
    2008-02-13: Java 1.5 types
    2007-01-06, Georg Fischer: copied from xtrans
*/
/*
 * Copyright 2006 Dr. Georg Fischer <dr dot georg dot fischer at gmail dot kom>
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
import  org.teherba.dbat.SQLAction;
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.EchoSQL;
import  org.teherba.dbat.format.GenerateSQLJ;
import  org.teherba.dbat.format.HTMLTable;
import  org.teherba.dbat.format.ProbeSQL;
import  org.teherba.dbat.web.Messages;
import  org.teherba.xtrans.BaseTransformer;
import  java.io.IOException;
import  java.net.URLEncoder;
import  java.sql.Types;
import  java.util.ArrayList;
import  java.util.Iterator;
import  java.util.LinkedHashMap;
import  java.util.Map;
import  java.util.TreeSet;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  org.xml.sax.Attributes;
import  org.xml.sax.InputSource;
import  org.xml.sax.SAXException;
import  org.xml.sax.helpers.AttributesImpl;
import  org.apache.logging.log4j.Logger;
import  org.apache.logging.log4j.LogManager;

/** XML SAX Parser for {@link Dbat} specifications of query forms.
 *  The handler combines XML, HTML and SQL technologies.
 *  It processes an XML file with descriptions for SQL SELECT,
 *  INSERT, UPDATE, DELETE and/or CALL statements,
 *  replaces any parameters in this description,
 *  runs the queries
 *  and returns the resulting rows in an HTML page containing tables with
 *  <ul>
 *  <li>specified column headers</li>
 *  <li>optional grouping of rows</li>
 *  <li>colouring of selected cell values</li>
 *  <li>links to URLs or other XML descriptions which take the cell value as parameter</li>
 *  </ul>
 *  @author Dr. Georg Fischer
 */
public class SpecificationHandler extends BaseTransformer { // DefaultHandler2 {
    public final static String CVSID = "@(#) $Id$";

    /** Debugging switch */
    private int debug = 0;
    /** log4j logger (category) */
    private Logger log;

    //==========================================
    // Bean properties, getters and setters
    // Some are repeated from Configuration.java,
    // with specific values for this SQL action
    //===========================================

    /** JDBC configuration with connection, user defineable properties and serializer */
    private Configuration config;
    /** Sets the configuration.
     *  The configuration contains the database connection, user defineable properties and the table serializer.
     *  @param config configuration
     */
    public void setConfiguration(Configuration config) {
        this.config = config;
    } // setConfiguration

    /** local copy of the output encoding */
    private String targetEncoding;
    /** Sets the output encoding
     *  @param encoding encoding for output, for example "UTF-8"
     */
    public void setEncoding(String encoding) {
        this.targetEncoding = encoding;
        if (tbSerializer != null) {
            tbSerializer.setTargetEncoding(targetEncoding);
        }
    } // setEncoding

    /** Optional linked asterisk leading to the regex validation test form */
    String errorAsterisk;

    /** Overall error condition, 0: none, 1: warning, &gt;= 2: error */
    private int errorCode;
    /** Sets the error code
     *  @param code 0: none, 1: warning, &gt;= 2: error
     */
    public void addErrorCode(int code) {
        this.errorCode += code;
    } // addErrorCode
    /** Checks the error code
     *  @return true iff code = 0 = no error
     */
    public boolean isSuccessful() {
        return errorCode == 0;
    } // isSuccessful

    /** Map for parameters (placeholders) embedded in the XML specification.
     *  The SAX handler will take parameter values from here when
     *  it encounters a <code>&lt;parm&gt;</code> element.
     */
    private LinkedHashMap<String, String[]> parameterMap;
    /** Gets the parameter (placeholder) map
     *  @return the locally stored parameter map
     */
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    } // getParameterMap

    /** Sets the parameter (placeholder) map
     *  @param map set the parameter map to this object
     */
    public void setParameterMap(Map<String, String[]>  map) {
        parameterMap = new LinkedHashMap<String, String[]>();
        Iterator<String> piter = (new TreeSet<String>(map.keySet())).iterator();
        while (piter.hasNext()) {
            String key = piter.next();
            if (key.startsWith("amp;")) {
                String ampKey = key;
                key = key.substring(4); // remove "amp;"
                parameterMap.put(key, map.get(ampKey));
                map         .put(key, map.get(ampKey));
                map.remove(ampKey);
            } else {
                parameterMap.put(key, map.get(key));
            }
            // log.error("setParameterMap(" + key + ", " + map.get(key) + ");");
        } // while key
    } // setParameterMap

    /** Set a single parameter
     *  @param name  name of the parameter to be set
     *  @param value value of that parameter
     */
    private void setParameter(String name, String value) {
        parameterMap.put(name, new String[] { value });
    } // setParameter

    /** Dumps the parameter map
     *  @param map set the parameter map to this object
     *  @return a string with all parameter values in legible format
     */
    private String dumpMap(Map<String, String[]>  map) {
        StringBuffer result = new StringBuffer(256);
        Iterator<String> piter = map.keySet().iterator();
        while (piter.hasNext()) {
            String key = piter.next();
            result.append(key);
            result.append("->");
            Object obj = map.get(key);
            if (obj != null) {
                String[] params = (String[]) obj;
                int ival = 0;
                while (ival < params.length) {
                    if (ival > 0) {
                        result.append(",");
                    }
                    result.append(params[ival]);
                    ival ++;
                } // while ival
            } // obj != null
            result.append("\n");
        } // while key
        return result.toString();
    } // dumpMap

    /** Determines whether the parameterMap contains user parameter keys.
     *  @param map map to be investigated.
     *  @return true if there is a key different from execsql,lang,mode,spec,view; false otherwise.
     */
    private boolean hasUserParameters(Map<String, String[]> map) {
        boolean result = false; // assume failure
        Iterator<String> piter = map.keySet().iterator();
        while (! result && piter.hasNext()) {
            String key = piter.next();
            if (   ! key.equals("execsql")
                && ! key.equals("lang")
                && ! key.equals("mode")
                && ! key.equals("spec")
                && ! key.equals("view")
                ) {
                    result = true;
            }
        } // while in keySet
        return result;
    } // hasUserParameters

    /** the remote user calling the web interface,
     *  returned from the Web Server (Apache, mod_auth_sspi, mod_jk, TomCat)
     */
    private String remoteUser;

    /** Request with map for parameters (placeholders) embedded in the XML specification.
     *  The SAX handler will take parameter values from here when
     *  it encounters a <code>&lt;parm&gt;</code> element.
     */
    private HttpServletRequest request;
    /** Sets (the internal copy of) the request with the parameter (placeholder) map
     *  @param request remember this in a local copy
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
        remoteUser = request != null ? request.getRemoteUser() : null;
        if (remoteUser == null) {
            remoteUser = "unknown";
        } else {
            int bslash = remoteUser.indexOf('\\');
            if (bslash >= 0) {
                remoteUser = remoteUser.substring(bslash + 1); // remove any Windows domain name
            }
        }
    } // setRequest

    /** HTTP response to be generated*/
    private HttpServletResponse response;
    /** Sets (the internal copy of) the response.
     *  @param response response object where HTML document is written
     */
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    } // setResponse

    /** Real (filesytem) path to the specification directory, for example "/.../tomcat/webapps/dbat/spec/" */
    private String specPath;
    /** Path to the specification subdirectory for URLs, for example "http://.../dbat/spec/" */
    private String specUrl;
    /** Name of the specification file, with optional subdirectory, without extension ".xml|.xsl|.css|.js",
     *  for example "test/xslt_brackets"
     */
    private String specName;

    /** Sets the pathes to the specification file, and its base name
     *  @param specPath Real (filesytem) path to the specification directory, for example "/.../tomcat/webapps/dbat/spec/"
     *  @param specUrl  URL of the specification subdirectory, for example  "http://.../dbat/spec/";
     *  this equals to <em>specPath</em> if not called from the servlet
     *  @param name Name of the specification file, with optional subdirectory, without extension ".xml|.xsl|.css|.js",
     *  for example "test/xslt_brackets"
     */
    public void setSpecPaths(String specPath, String specUrl, String name) {
        this.specPath   = specPath;
        this.specUrl    = specUrl;
        this.specName   = name;
        config.setSpecPathName(specPath + specName);
    } // setSpecPaths

    /** whitespace separated list of keywords:
     *  "none out time dbat script xls more plain"
     */
    private String trailerSelect;
    /** default value for {@link #trailerSelect} */

    /** Abbreviation for a complicated test during parsing:
     *  this variable is normally 0, but -1 for the static serializers which
     *  should not depend on &lt;choose&gt; elements.
     *  Look at the 'if' statement where this variable is used.
     */
    private int zeroAndNotEcho;

    /** Generates the mode-dependant representation of the result table */
    private BaseTable tbSerializer;
    /** Sets the mode-dependant table serializer
     *  @param tbSerializer one of the subclasses of {@link BaseTable}: HTMLTable, SQLTable, XMLTable etc.
     */
    public void setSerializer(BaseTable tbSerializer) {
        this.tbSerializer = tbSerializer; // remember it locally
    //    this.tbSerializer.setCharWriter(charWriter);
        this.tbSerializer.setTargetEncoding(targetEncoding);
        if (        tbSerializer instanceof EchoSQL
                ||  tbSerializer instanceof GenerateSQLJ
                ||  tbSerializer instanceof ProbeSQL
                ) {
            zeroAndNotEcho = -1; // c.f. the 'if' at the start of the big switches in 'startElement' and 'endElement'
        }
    } // setSerializer

    // charWRiter and byteWriter are inherited from BaseTransformer

    //===================================================
    // Constructor, class initialization and finalization
    //===================================================

    /** No-argument constructor (not used from outside)
     */
    private SpecificationHandler() {
        log = LogManager.getLogger(SpecificationHandler.class.getName());
        zeroAndNotEcho  = 0; // normal output serializer
        specPath        = "/not_found";
        specUrl         = "spec/";
        specName        = "index";
        errorCode       = 0;
        errorAsterisk   = "";
        remoteUser      = "unknown";
        setRequest      (null);
        setResponse     (null);
        tableNo         = 0;
    } // constructor()

    /** Constructor from configuration
     *  @param config overall configuration of a session
     */
    public SpecificationHandler(Configuration config) {
        this();
        setConfiguration(config);
        setEncoding(config.getEncoding(1));
    } // Constructor

    //====================
    // Auxiliary methods
    //====================

    /** Gets the optional trailer with timestamp and links.
     *  This is the language-independant code.
     *  Language-specific words are added in {@link Messages#getTrailerText}.
     *  @param trailerSelect a whitespace (or comma) separated list of keywords:
     *  "none plain out time dbat script xls more"
     *  @param language ISO country code "en", "de"
     *  @param specName base name of the specification with subdirectory, without ".xml"
     *  @return formatted trailer line or empty string if trailer is suppressed by <code>select="none"</code>
     */
    private String getTrailer(String trailerSelect, String language, String specName) {
        String result = "";
        if (trailerSelect == null) { // missing => all, empty => none, separated by ","
            trailerSelect = " " + config.getTrailerElements(); // TRAILER_ALL;
        } else if (trailerSelect.length() == 0) {
            trailerSelect = " none";
        } else {
            trailerSelect = " " + trailerSelect.toLowerCase().replaceAll("\\W+", " "); // \\W+ = sequences of non-word characters
        }
        if (! trailerSelect.contains(" none")) { // trailer is not suppressed
            if (! (tbSerializer instanceof HTMLTable)) {
                trailerSelect += " plain";
            }
            String sourceUrl = "";
            int callType = config.getCallType();
            if (trailerSelect.contains(" script")) {
                if (callType == Configuration.WEB_CALL) { // test for possible link with "view-source:" schema
                    sourceUrl = Messages.getViewSourceLink(request);
                } // WEB_CALL
            } // "script" was present
            sourceUrl      += specUrl + specName + (callType == Configuration.CLI_CALL ? "" : ".xml");
            String xlsUrl   = "servlet?&amp;mode=xlsx"
                            + repeatURLParameters();
            String moreUrl  = "servlet?&amp;view=more&amp;mode=" + tbSerializer.getOutputFormat()
                            + repeatURLParameters();
            result = Messages.getTrailerText(trailerSelect, language, sourceUrl, specName, xlsUrl, moreUrl);
        } // not suppressed
        return result;
    } // getTrailer

    /** Returns all form parameters (except <em>execsql, mode, view</em>)
     *  as URL encoded name=value pairs separated by ampersands.
     *  @return concatenated URL parameters
     */
    private String repeatURLParameters() {
        StringBuffer result = new StringBuffer(128);
        Iterator<String> parmIter = parameterMap.keySet().iterator();
        while (parmIter.hasNext()) {
            String name = parmIter.next();
            if (name != null && ! name.matches("execsql|mode|view")) {
                String[] params = (String[]) (parameterMap.get(name));
                int iparm = 0;
                while (iparm < params.length) {
                    try {
                        result.append("&amp;");
                        result.append(name);
                        result.append("=");
                        result.append(URLEncoder.encode(params[iparm], targetEncoding)); // should be URL encoded
                        // result.append(params[iparm]);
                    } catch (Exception exc) {
                        // ignore any errors
                    }
                    iparm ++;
                } // while iparm
            } // if ! matches
        } // while parmIter
        return result.toString();
    } // repeatURLParameters

    /** Reconstructs an HTML start tag
     *  @param qName name (tag) of the element
     *  @param attrs attributes of the element
     *  @return start tag with attributes
     */
    protected String getStartTag(String qName, Attributes attrs) {
        StringBuffer result = new StringBuffer(128);
        result.append('<');
        result.append(qName);
        if (attrs != null) {
            int nattr = attrs.getLength();
            int iattr = 0;
            while (iattr < nattr) {
                result  .append(' ')
                        .append(attrs.getQName(iattr))
                        .append("=\"")
                        .append(attrs.getValue(iattr))
                        .append("\"")
                        ;
                iattr ++;
            }
        } // attrs != null
        result.append('>');
        return result.toString();
    } // getStartTag

    /** Get the value(s) of a named parameter.
     *  If the parameter name is all uppercase, the values will be uppercased, too.
     *  @param attrs attribute list which should contain a "name=" attribute
     *  @param attrName name of the parameter
     *  @return array of 1 or more parameter value(s), or 0 values if no "name=" attribute was found
     */
    private String[] getParameterList(Attributes attrs, String attrName) {
        String[] values = new String[0]; // assume that the name was not found
        String name  = attrs.getValue(attrName);
        Object obj   = parameterMap.get(name);
        int ival = 0;
        if (obj != null) {
            values = (String[]) obj;
            if (name != null && name.matches("[A-Z0-9_]+")) {
                // special feature: if name is only uppercase letters, digits and underscore
                ival = 0;
                while (ival < values.length) {
                    values[ival] = values[ival].toUpperCase(); // then upshift all input values
                    ival ++;
                } // for ival
            }
        } else { // if a listbox is not checked at all, its parameter name is not put in the QUERY_STRING
            // zero length list set initially
        }
        return values;
    } // getParameterList

    /** Get the value(s) of a named parameter or host variable.
     *  If the parameter name is all uppercase, the values will be uppercased, too.
     *  @param attrs attribute list which should contain a "name=" attribute
     *  A "ix=" attribute gives an index of the parameter, 0 (first), 1, 2 etc.
     *  In addition, a "list=" attribute may be specified which returns a list in parentheses,
     *  suitable for an SQL condition WHERE col IN (list):
     *  <ul>
     *  <li>list="box" joins all instances of this parameter name to a list of trimmed, quotes words separated by commas</li>
     *  <li>list="char" splits the parameter into a list of trimmed, quotes words separated by commas</li>
     *  <li>list="int"  splits the parameter into a list of integers without quotes, separated by commas</li>
     *  </ul>
     *  @param attrName attribute name of the "name=" attribute
     *  @return array of 1 or more parameter value(s), or 0 values if no "name=" attribute was found
     */
    private String getParameterForSQL(Attributes attrs, String attrName) {
        String result = null; // assume that the name was not found
        String[] values  = null;
        String name   = attrs.getValue(attrName);
        Object obj    = parameterMap.get(name);
        if (obj == null && name.compareToIgnoreCase(config.REMOTE_USER) == 0) {
            obj = new String[] { remoteUser };
        }
        String istr   = attrs.getValue("ix");
        int ix = 0;
        if (istr != null) {
            try {
                ix = Integer.parseInt(istr);
                if (ix < 0) {
                    ix = 0;
                }
            } catch (Exception exc) {
            }
        } // istr != 0
        int ival = 0;
        if (obj == null) {
            String initValue = attrs.getValue("init");
            if (initValue == null) {
                // result = "\'\'";
                String typeName = attrs.getValue("type");
                if (typeName == null) {
                    typeName = "char";
                } else {
                    typeName = typeName.toLowerCase();
                }
                if (typeName.startsWith("int") || typeName.startsWith("dec")) {
                    result = "0";
                } else {
                    result = "";
                }
            } else {
                result = initValue;
                setParameter(name, initValue);
            }
        } else { // notabene: if a listbox is not checked at all, the browser will not put its parameter name into the QUERY_STRING
            values = (String[]) obj;
            if (ix >= values.length) {
                ix = 0;
            }
            result = values[ix];
        }
        if (true) {

            String formatCode = attrs.getValue("format");
            if (formatCode == null) {
                if (name.matches("[A-Z0-9_]+")) { // special feature: if name is only uppercase letters, digits and underscore
                    result = result.toUpperCase(); // then upshift all input values
                } // upperCase
            } else { // format= not present
                if (false) {
                } else if (formatCode.toLowerCase().equals("upper")) {
                    result = result.toUpperCase(); // then upshift   all input values
                } else if (formatCode.toLowerCase().equals("lower")) {
                    result = result.toLowerCase(); // then downshift all input values
                } else if (formatCode.toLowerCase().equals("none" )) {
                    // value remains unchanged
                }
                // format= was present
            } // format=

            String listType = attrs.getValue("list");
            if (listType == null) {
                // no list type specified
            } else if (listType.equals("box" )) { // join all array elements to quoted words separated by commas
                StringBuffer buffer = new StringBuffer(64);
                buffer.append("\'");
                buffer.append(values[0].trim());
                ival = 1;
                while (ival < values.length) {
                    buffer.append("\', \'");
                    buffer.append(values[ival].trim());
                    ival ++;
                } // for ival
                buffer.append("\'");
                result = buffer.toString();
            } else if (listType.equals("char")) { // quoted words separated by commas
                result = "\'" + values[0].trim().replaceAll("\\s+", "\', \'").replaceAll(",,", ",") + "\'";
            } else if (listType.equals("int")) { // comma separators instead of whitespace
                result =        values[0].trim().replaceAll("\\s+", ", "    ).replaceAll(",,", ",")       ;
            } // with listType
            // obj != null
        }
        validateField(name, values, new AttributesImpl(attrs));
        return result;
    } // getParameterForSQL

    /** Prefix for the generation of table identifiers */
    private static final String TABLE_ID_PREFIX = "table";
    /** sequential count of generated tables (SELECT statements) */
    private int tableNo;

    /** Gets an id= attribute, or generates one, and stores it in the table metadata
     *  @param attrs set of XML attributes
     *  @param tbMetaData table metadata
     */
    private void getIdAttribute(Attributes attrs, TableMetaData tbMetaData) {
        String tableId = attrs.getValue("id");
        tableNo ++;
        if (tableId == null) {
            tableId = TABLE_ID_PREFIX + String.valueOf(tableNo);
        }
        tbMetaData.setIdentifier(tableId);
    } // getIdAttribute

    /** Gets a space separated list of filenames with proper pathes and extensions
     *  from a list of subdirectory-relative filenames
     *  @param attributeValue of the attribute containing the file specifications
     *  @param subDirectory application relative path to the specification XML, for example "spec/test/"
     *  @param extension file extension if it was omitted
     *  @return space separated list of filenames with proper pathes and extensions
     */
    private String getFilesFromAttribute(String attributeValue, String subDirectory, String extension) {
        String result = null;
        StringBuffer buffer = new StringBuffer(128);
        if (attributeValue != null) { // attribute was set
            String[] names = attributeValue.split("\\s+");
            int iname = 0;
            while (iname < names.length) {
                if (iname >= 1) {
                    buffer.append(' '); // separator
                }
                if (! names[iname].matches("([a-zA-Z]\\:)?(\\/|\\\\).*")) {
                    if (extension.equals("xsl")) { // for XSLT always turn relative path into absolute path
                        buffer.append(specPath.replaceAll("spec\\/?\\Z", ""));
                    } // make absolute
                    buffer.append(subDirectory);
                } else { // has "absolute" path
                    if (config.getCallType() == Configuration.WEB_CALL) { // make it relative to "spec/"
                        names[iname] = names[iname].replaceAll("\\A\\/", specUrl);
                    }
                } // has "absolute" path
                buffer.append(names[iname]);
                if (! names[iname].endsWith(extension)) {
                    buffer.append('.');
                    buffer.append(extension);
                } // with extension
                iname ++;
            } // while inames
            result = buffer.toString();
        } else { // attribute was not set, assume defaults
            if (false) {
            } else if (extension.equals("css")) {
                result = subDirectory + "stylesheet.css";
            } else if (extension.equals("js")) {
            } else if (extension.equals("xsl")) {
            }
        } // not set
        return result;
    } // getFilesFromAttribute

    /** Handles a form field start tag: ht:INPUT_TAG, ht:SELECT_TAG, ht:TEXTAREA_TAG and our db:LISTBOX_TAG
     *  with the additional Dbat attributes "label=", "value=" and "init=".
     *  @param qName qualified name from SAX interface
     *  @param attrs attribute list from SAX interface
     */
    private void startFormField(String qName, Attributes attrs) {
        errorAsterisk = "";
        AttributesImpl attrs2 = new AttributesImpl(attrs);
        String name = null;
        int index = attrs2.getIndex("label");
        if (index >= 0) { // is our variant with "label" attribute
            tbSerializer.writeMarkup(attrs2.getValue(index) + ": ");
            attrs2.removeAttribute(index);
        } // label - our variant

        String value = null; // this is the value shown (or selected) in the field
        index = attrs2.getIndex("value");
        if (index >= 0) {
            value = attrs2.getValue(index);
        } else if (index < 0 &&   qName.equals(LISTBOX_TAG)) { // no value specified - select that of the parameter with the same name
            name = attrs2.getValue("name");
            if (name != null && ! name.equals("")) { // valid name
                Object obj = parameterMap.get(name);
                if (obj != null) { // found a parameter with the same name
                    String [] params = (String[]) obj; // get the value of that parameter
                    value = params[0].trim();
                } else { // take the default initial value from any "init" attribute
                    String initValue = attrs2.getValue("init");
                    if (initValue != null) {
                        value = initValue;
                        setParameter(name, initValue); // effective only for the parameter usages behind this <input> tag
                    }
                } // default initial value
                // now generate the whole HTML code for a listbox
                String code     = attrs2.getValue("code");
                String display  = attrs2.getValue("display");
                String empty    = attrs2.getValue("empty");
                String multiple = attrs2.getValue("multiple");
                int height = 3; // int because we must compute on it
                try {
                    height = Integer.parseInt(attrs2.getValue("height"));
                } catch (Exception exc) { // if non-numeric - ignore
                }
                if (empty != null) { // prefix with an option with empty value, display="(all)" for example
                    height ++;
                    tbSerializer.writeMarkup("<select name=\"" + name
                            + "\" size=\"" + String.valueOf(height) + "\""
                            + (multiple != null ? " multiple=\"" + multiple + "\"" : "")
                            + ">\n");
                    tbSerializer.writeMarkup("<option value=\"\""
                            + ">" + empty + "</option>\n");
                    // prefix
                } else {
                    tbSerializer.writeMarkup("<select name=\"" + name
                            + "\" size=\"" + String.valueOf(height) + "\""
                            + (multiple != null ? " multiple=\"" + multiple + "\"" : "")
                            + ">\n");
                }
                String[] codeParm       = parameterMap.get(code   );
                String[] displayParm    = parameterMap.get(display);
                int len = (codeParm == null) ? 0 : codeParm.length;
                boolean notFound = true; // whether 'value' does not occur in the array 'codeParm'
                int
                ix = 0;
                while (notFound && ix < len) {
                    if (codeParm[ix].equals(value)) {
                        notFound = false;
                    }
                    ix ++;
                } // while ix
                ix = 0;
                while (ix < len) {
                    tbSerializer.writeMarkup("<option value=\""
                            + codeParm[ix] + "\""
                            + ( ( codeParm[ix].equals(value) || (notFound && ix == 0)) // if notFound, then select the first
                                ? " selected=\"yes\""
                                : ""
                              )
                            + ">" + displayParm[ix] + "</option>\n");
                    ix ++;
                } // while ix
                tbSerializer.writeMarkup("</select>\n");
            } // valid name
            // LISTBOX_TAG
        } else if (index < 0 && ! qName.equals(SELECT_TAG)) {
            // no value specified - insert that of the parameter with the same name
            name = attrs2.getValue("name");
            if (name != null && ! name.equals("")) { // valid name
                Object obj = parameterMap.get(name);
                if (obj != null) { // found a parameter with the same name
                    String [] params = (String[]) obj; // get the value of that parameter
                    value = params[0].trim();
                    attrs2.addAttribute("", "value", "value", "CDATA", value);
                } else { // take the default initial value from any "init" attribute
                    String initValue = attrs2.getValue("init");
                    if (initValue != null) {
                        value = initValue;
                        attrs2.addAttribute("", "value", "value", "CDATA", initValue);
                        setParameter(name, initValue); // effective only for the parameter usages behind this <input> tag
                    }
                } // default initial value
            } // valid name
            // SELECT_TAG
        } // no value
        validateField(name, new String[] { value }, attrs2);
        if (index >= 0 || ! qName.equals(LISTBOX_TAG)) {
            tbSerializer.writeMarkup(getStartTag(qName, attrs2));
        }
    } // startFormField

    /** Validate a value against a regular expression
     *  @param name  name  of the field to be checked
     *  @param values value of the field to be checked
     *  @param attrs2 attributes of the start tag
     */
    private void validateField(String name, String[] values, AttributesImpl attrs2) {
        String validPattern = attrs2.getValue("valid");
        if (    true
                &&  validPattern    != null
                &&  validPattern.length() > 0
                &&  values          != null
                &&  values.length   >= 1
                &&  values[0]       != null
                ) { // with validation
            String value = values[0];
            if (debug >= 1) {
                tbSerializer.writeMarkup("<h4>Validation problem for field " + name
                        + ", value " + value + " =~ "
                        + validPattern + " => " + value.matches(validPattern)
                        + "</h4>");
            } // debug
            int code = Messages.validateFormField(config.getLanguage(), attrs2, value, validPattern);
            addErrorCode(code);
            if (code > 0) { // else put a linked asterisk behind the field
                try {
                    errorAsterisk = "<a href=\"servlet?view=validate"
                            + "&amp;lang="  + config.getLanguage()
                            + "&amp;name="  + name
                            + "&amp;regex=" + URLEncoder.encode(validPattern, targetEncoding)
                            + "&amp;value=" + URLEncoder.encode(value       , targetEncoding)
                            + "\" target=\"_blank\">*</a>";
                } catch(Exception exc) {
                    // ignore encoding errors: suppress the asterisk then
                }
            } // code > 0
        } // with validation
    } // validateField

    /** Handles a form field end tag: INPUT_TAG, SELECT_TAG or TEXTAREA_TAG,
     *  @param qName qualified name from SAX interface
     */
    private void endFormField(String qName) {
        if (! qName.equals(LISTBOX_TAG)) {
            tbSerializer.writeMarkup("</" + qName + ">");
        }
        tbSerializer.writeMarkup(errorAsterisk); // is empty if field value did validate
    } // endFormField

    /** Gets the response headers for debugging purposes.
     *  @return a string of lines each containing one header name/values pair
     */
    private String getResponseHeaders() {
        StringBuffer result = new StringBuffer(512);
        if (response != null) {
    /*  would only work in Tomcat 7.0+ with Servlet API 3.0+
            Iterator<String> hiter = response.getHeaderNames().iterator();
            while (hiter.hasNext()) {
                String name = hiter.next();
                result.append(name);
                result.append(":");
                Iterator<String> viter = response.getHeaders(name).iterator();
                while (viter.hasNext()) {
                    String value = viter.next();
                    result.append(" ");
                    result.append(value);
                } // while values
                result.append("\n");
            } // while name
    */
        } // valid response
        return result.toString();
    } // getResponseHeaders

    /** Gets the attribute for the id of a (new) connection and stores it in the configuration
     *  @param attrs the {@link Attributes} attached to the element.
     *  @param attrName name of the attribute: &lt;dbat conn="connid"&gt; or &lt;connect to="connid" /&gt;
     */
    private void saveConnectionAttribute(Attributes attrs, String attrName) {
        String connectionId = attrs.getValue(attrName);
        if (connectionId != null) {
            // config.addProperties(connectionId + ".properties");
            config.setConnectionId(connectionId);
        } else {
            Object obj = parameterMap.get("conn");
            if (obj != null) {
                connectionId = ((String[]) obj)[0]; // override it from the HttpRequest
                // config.addProperties(connectionId + ".properties");
                config.setConnectionId(connectionId);
                setParameter("conn", connectionId); // store it in links and subsequent requests
            } else { // &conn= not set in HttpRequest
                config.setConnectionId(); // default: take first in list
            }
        }
    } // saveConnectionAttribute

    //===========================
    // SAX handler for XML input
    //===========================

    /** current (fictitious) namespace of parsing */
    private String currentNameSpace;

    /** current element */
    private String elem;
    /** type of parent SQL statement (for <code>&lt;col&gt;</code>) elements */
    private String parentStmt;
    /** buffer for the assembly of SQL component strings */
    private StringBuffer colBuffer;
    /** buffer for the assembly of other SQL component strings */
    private StringBuffer valBuffer;
    /** buffer for the types and values of placeholders derived from &lt;var&gt; */
    private ArrayList<String> variables;
    /** number of column elements encountered so far */
    private int columnNo;
    /** <em>name</em> attribute of start tag (used for &lt;textarea&gt; only)*/
    private String startName;
    /** Parameter values of an HTML listbox (&lt;select&gt;) element */
    private String[] listBoxParams;

    /** prefix for <em>&lt;col link="..."&gt;</em> attribute values */
    private static final String SERVLET_SPEC = "servlet?spec=";

    /** Dbat Root element tag */
    public  static final String ROOT_TAG    = "dbat"    ; // target= title= lang=en conn=dbat headers=true
                                                          // encoding=UTF-8
                                                          // javascript=http_request.js
                                                          // xslt=
                                                          // stylesheet=stylesheet.css
                                                          // uri=taylor.html
                                                          // debug=0
                                                          // manner=jdbc|sqlj|stp
    /** Dbiv Root element tag */
    public  static final String DBIV_TAG    = "dbiv"    ; // same as ROOT_TAG, + script="..."

    /** A(nchor) element tag */
    private static final String A_TAG       = "a"       ; // splits link values on "=" for this namespace
    /** Break element tag */
    private static final String BREAK_TAG   = "br"      ; // suppress </ht:br>=
    /** Call element tag */
    private static final String CALL_TAG    = "call"    ; // name=
    /** "choose" conditional compilation construction tag */
    private static final String CHOOSE_TAG  = "choose"  ;

    /** Column element tag with attributes */
    private static final String COL_TAG     = "col"     ; // align= dir= href= key= label= link= name= pseudo= remark= style= target= type= width=
    /** Column element tag without attributes */
    private static final String COLUMN_TAG  = "column"  ;
    /* now the subelements of <column> which correspond to attribute names of <col> */
        private static final String ALIGN_TAG       = "align"       ;
        private static final String DIR_TAG         = "dir"         ;
        private static final String EXPR_TAG        = "expr"        ;
        private static final String HREF_TAG        = "href"        ;
        private static final String JAVASCRIPT_TAG  = "javascript"  ;
        private static final String KEY_TAG         = "key"         ;
        private static final String LABEL_TAG       = "label"       ;
        private static final String LABEL2_TAG      = "label2"      ;
        private static final String LINK_TAG        = "link"        ;
        private static final String NAME_TAG        = "name"        ;
        private static final String PSEUDO_TAG      = "pseudo"      ;
        private static final String REMARK_TAG      = "remark"      ;
        private static final String SPAN2_TAG       = "span2"       ;
        private static final String SQL_TAG         = "sql"         ;
        private static final String STYLE_TAG       = "style"       ;
        private static final String TARGET_TAG      = "target"      ;
        private static final String TYPE_TAG        = "type"        ;
        private static final String WIDTH_TAG       = "width"       ;
        private static final String WRAP_TAG        = "wrap"        ;
    /** whether we are currently inside a &lt;column&gt; element */
    private boolean inColumn;

    /** element tag for comment */
    private static final String COMMENT_TAG = "comment" ; // lang=
    /** connect element tag */
    private static final String CONNECT_TAG = "connect" ; // to=
    /** element tag for counter */
    private static final String COUNTER_TAG = "counter" ; // desc="row,s"
    /** DELETE element tag */
    private static final String DELETE_TAG  = "delete"  ; // ... <from>
    /** DESCRIBE element tag */
    private static final String DESCRIBE_TAG= "describe"; // tables=
    /** div element tag */
    private static final String DIV_TAG     = "div"     ; // as bracket for included system entities
    /** form element tag */
    private static final String FORM_TAG    = "form"    ; // action= and HTML attrs
    /** FROM element tag */
    private static final String FROM_TAG    = "from"    ;
    /** GROUP clause element tag */
    private static final String GROUP_TAG   = "group"   ; // by=
    /** INPUT field element tag */
    private static final String INPUT_TAG   = "input"   ; // label=, init=, and HTML attrs
    /** INSERT element tag */
    private static final String INSERT_TAG  = "insert"  ; // "into", "col" ...
    /** INTO element tag */
    private static final String INTO_TAG    = "into"    ;
    /** List parameter element tag */
    private static final String LISTBOX_TAG = "listbox" ; // label=, name=, height=, code=, display=, empty= multiple=
    /** List parameter element tag */
    private static final String LISTPARM_TAG= "listparm"; // name=, in connection with TEXTAREA_TAG
    /** OPTION subelement of HTML &lt;select name="..." &gt; */
    private static final String OPTION_TAG  = "option"  ; // value= selected=
    /** ORDER clause element tag */
    private static final String ORDER_TAG   = "order"   ; // by=
    /** "otherwise" conditional compilation branch tag */
    private static final String OTHERWISE_TAG = "otherwise";
    /** Parameter element tag (c.f. var), for unprepared statements, with quotes */
    private static final String PARM_TAG    = "parm"    ; // name=, ix=, init=
     /** read element tag (in col element) */
    private static final String READ_TAG    = "read"    ; //
    /** SELECT element tag */
    private static final String SELECT_TAG  = "select"  ; // db:select or ht:select
        // for Dbat: distinct=, limit=, name=, headers=, aggregate=, with=, group=, id=
        // subordinate elements: <col>..., <from>, <where>, <group>, <order>
    /** UPDATE element tag */
    private static final String UPDATE_TAG  = "update"  ; // ... <where>
    /** textarea field element tag */
    private static final String TEXTAREA_TAG= "textarea"; // label=, init=, and HTML attrs
    /** TRAILER element tag */
    private static final String TRAILER_TAG = "trailer" ; // select=
    /** VALUES element tag */
    private static final String VALUES_TAG  = "values"  ;
    /** var element tag (c.f. parm), for prepared statements, without quotes */
    private static final String VAR_TAG     = "var"     ; // name=, init=, type=, list=
    /** "when" conditional compilation branch tag */
    private static final String WHEN_TAG    = "when"    ; // parm=, match=
    /** WHERE clause element tag */
    private static final String WHERE_TAG   = "where"   ;
    /** WITH clause element tag */
    private static final String WITH_TAG    = "with"    ; // for Common Table Expressions

    /** SQL statement to be executed */
    private StringBuffer sqlBuffer;
    /** whether we started a subselect because of "probe" format */
    private boolean probeOpen;
    /** Current column properties */
    private TableColumn column;

    /** Stack for the state of the conditional compilation.
     *  The integer value is interpreted as 3 bits:
     *  <ul>
     *  <li>2^0 = whether the whole <em>choose</em> construction will generate code</li>
     *  <li>2^1 = whether the current <em>when</em> branch will generate code</li>
     *  <li>2^2 = whether there was any <em>when</em> branch so far which generated code (for <em>otherwise</em></li>
     *  </ul>
     */
    private int[] chooseStack = new int[16];
    /** index of active element in {@link #chooseStack} */
    private int chooseTop;
    private static final int CHOOSE_GEN  = 4;
    private static final int CHOOSE_THIS = 2;
    private static final int CHOOSE_SOME = 1;

    /** Properties and methods specific for one elementary sequence of SQL instructions */
    private SQLAction sqlAction;
    /** Table and column metadata */
    private TableMetaData tbMetaData;

    /** Initializes the action and the table's metadata
     */
    private void initializeAction() {
        sqlAction  = new SQLAction    (config);
        tbMetaData = new TableMetaData(config);
    } // initializeAction

    /** Terminates  the action and the table's metadata
     */
    private void terminateAction() {
        sqlAction.terminate();
    } // terminateAction

    /** Pattern for table/view names to be described */
    private String tablePattern;

    /** true disables all further SAX event processing */
    private boolean disabled;

    /** Receive notification of the beginning of the XML document.
     *  Because of an optional preceeding WITH clause, the {@link #sqlBuffer}
     *  is only reset before CALL, DELETE, INSERT and WITH, not before SELECT,
     *  but always    after  CALL, DELETE, INSERT and SELECT
     */
    public void startDocument() {
        disabled        = false; // enable SAX event processing
        elem            = "";
        inColumn        = false;
        listBoxParams   = new String[0]; // empty array
        colBuffer       = new StringBuffer(2048);
        sqlBuffer       = new StringBuffer(4096);
        probeOpen       = false;
        currentNameSpace= config.HTML_URI; // start HTML
        tablePattern    = null;
        parentStmt      = elem;
        targetEncoding  = "UTF-8"; // fix for HTML; previously: dbat.getEncoding(0);
        if (response != null) {
            targetEncoding  = response.getCharacterEncoding();
            config.setEncoding(1, targetEncoding);
        }
        startName       = null;
        errorCode       = 0;
        chooseTop       = 0;
        chooseStack[chooseTop] = CHOOSE_GEN | CHOOSE_THIS; // code generation is active at the beginning
        variables       = new ArrayList<String> ();
    } // startDocument

    /** Receive notification of the end of the XML document.
     */
    public void endDocument() {
        // charWriter.close();
    } // endDocument

    //==================================================================

    /** Receive notification of the start of an element.
     *  Looks for the element which contains encoded strings.
     *  @param uri The Namespace URI, or the empty string if the element has no Namespace URI
     *  or if Namespace processing is not being performed.
     *  @param localName the local name (without prefix),
     *  or the empty string if Namespace processing is not being performed.
     *  @param qName the qualified name (with prefix),
     *  or the empty string if qualified names are not available.
     *  @param attrs the {@link Attributes} attached to the element.
     *  If there are no attributes, it shall be an empty Attributes object.
     */
    public void startElement(String uri, String localName, String qName, Attributes attrs) {
        String tableId = null; // <select id=".."> or sequentially generated
        if (disabled) {
        } else
        try { // if there is any exception, further SAX event processing will be disabled
            int colonPos = qName.indexOf(":");
            if (colonPos >= 0) { // remove prefix
                qName = qName.substring(colonPos + 1);
            }
            elem = qName;
            /*..................................................
                first the start tags for conditional compilation
            */
            if (false) {
            } else if (qName.equals(CHOOSE_TAG  )) { // pop unconditionally
                chooseTop ++;
                chooseStack[chooseTop] = 0; // THIS and OTHER are unset
                if ((chooseStack[chooseTop - 1] & CHOOSE_THIS) != 0) {
                    chooseStack[chooseTop] = CHOOSE_GEN;
                }
            } else if (qName.equals(WHEN_TAG    )) { // conditional compilation branch
                if ((chooseStack[chooseTop] & CHOOSE_GEN) != 0) { // GEN is active
                    String value = "";
                    String name         = attrs.getValue("name");
                    String testPattern  = attrs.getValue("match");
                    if (name != null && ! name.equals("")) { // valid name
                        Object obj = parameterMap.get(name);
                        if (obj != null) { // found a parameter with the same name
                            String [] params = (String[]) obj; // get the value of that parameter
                            value = params[0].trim();
                        } // default initial value
                        if (Messages.validateFormField(config.getLanguage(), new AttributesImpl(), value, testPattern) == 0) { // successful match
                            chooseStack[chooseTop] |= CHOOSE_THIS;
                        } // successful match
                        // else is already 0 or -1
                    } // valid name
                } // GEN is active
                // <when>
            } else if (qName.equals(OTHERWISE_TAG)) {
                if ((chooseStack[chooseTop] & (CHOOSE_GEN | CHOOSE_SOME)) == CHOOSE_GEN) { // GEN is set, and SOME is unset
                    chooseStack[chooseTop] |= CHOOSE_THIS;
                } // ! chooseBranch
                // <otherwise>
            }

            /*..................................................
                now the remaining start tags
            */
            if ((chooseStack[chooseTop] & CHOOSE_THIS) == zeroAndNotEcho) {
                // ignore all normal start tags
            } else if (inColumn) { // align= dir= href= label= label2= link= name= pseudo= remark= span2= style= target= type= wrap=
                colBuffer.setLength(0); // start character assembly for all subordinate elements

            } else if (qName.equals(ROOT_TAG    )) {
                trailerSelect       = " " + config.getTrailerElements(); // assume that there will be no <trailer /> element
                //--------
                String debugAttr    = attrs.getValue("debug");
                debug = 0;
                try {
                    if (debugAttr       != null) {
                        debug = Integer.parseInt(debugAttr);
                    }
                } catch (Exception exc) {
                    // ignore
                }
                //--------
                String encAttr      = attrs.getValue("encoding");
                if (encAttr         != null) {
                    config.setEncoding(1, encAttr);
                }
                //--------
                String contentType  = config.getHtmlMimeType() + ";charset=" + config.getEncoding(1);
                //--------
                String target       = attrs.getValue("target");
                //--------
                String title        = attrs.getValue("title");
                if (title == null) {
                    title = "Dbat";
                }
                //--------
                String execsqlAttr  = attrs.getValue  ("execsql");
                Object
                obj                 = parameterMap.get("execsql");
                if (obj != null) {
                    execsqlAttr     = ((String[]) obj)[0]; // override it from the HttpRequest
                }
                if (hasUserParameters(parameterMap)) {
                    execsqlAttr = "1"; // overwrite any 0 when there is a user parameter
                }
                int execSQL = 1;
                try {
                    if (execsqlAttr != null) {
                        execSQL = Integer.parseInt(execsqlAttr);
                    }
                } catch (Exception exc) {
                    // ignore
                }
                config.setExecSQL(execSQL);
                //--------
                String headers      = attrs.getValue("headers");
                if (headers         == null) {
                    config.setWithHeaders(true);
                } else {
                    config.setWithHeaders(headers.matches("[yYjJtT].*"));
                }
                //--------
                String language     = attrs.getValue  ("lang");
                obj                 = parameterMap.get("lang");
                if (obj != null) {
                    language    = ((String[]) obj)[0]; // override it from the HttpRequest
                } else { // &lang= not set in HttpRequest
                    if (language != null) {
                        // set as <dbat> attribute
                    } else {
                        language    = "en"; // some default
                    }
                    setParameter("lang", language); // store it in links and subsequent requests
                }
                config.setLanguage(language);
               //--------
                String manner       = attrs.getValue("manner");
                if (manner == null) {
                    config.setManner(Configuration.JDBC_MANNER);
                } else if (manner.equals("sqlj")) {
                    config.setManner(Configuration.SQLJ_MANNER);
                } else if (manner.equals("stp") ) {
                    config.setManner(Configuration.STP_MANNER ); // not yet implemented
                } else {
                    config.setManner(Configuration.JDBC_MANNER);
                }
                //--------
                saveConnectionAttribute(attrs, "conn");
                //--------
                int lastSlashPos = specName.lastIndexOf("/");
                String subDirectory = specUrl + (lastSlashPos < 0 ? "" : specName.substring(0, lastSlashPos + 1));
                    // up to and including trailing "/"
                //--------
                String javascript   = getFilesFromAttribute(attrs.getValue("javascript"), subDirectory, "js" );
                String stylesheet   = getFilesFromAttribute(attrs.getValue("stylesheet"), subDirectory, "css");
                String xslt         = getFilesFromAttribute(attrs.getValue("xslt")      , subDirectory, "xsl");
                //--------
                String inputURI     = attrs.getValue("uri"); // for mode="taylor" only
                if (inputURI != null && inputURI.length() > 0 && config.getInputURI() == null) { // can be overwritten by request parameter
                    config.setInputURI(specPath + inputURI);
                    if (inputURI.endsWith(".html") && response!= null) { // adopt the ContentType of the inputURI file
                        response.setContentType("text/html; charset=UTF-8");
                    }
                }
                //--------
                tbSerializer.setParameterMap(parameterMap);
                tbSerializer.writeStart(new String[] // this may throw an exception "could not compile stylesheet"
                            { "encoding"                                    , config.getEncoding(1)
                            , "contenttype"                                 , contentType
                            , "specname"                                    , specName
                            , "specurl"                                     , specUrl
                            , "title"                                       , title
                            , "lang"                                        , language
                            , "conn"                                        , config.getConnectionId()
                            , (javascript != null ? "javascript" : "dummy") , javascript
                            , (stylesheet != null ? "stylesheet" : "dummy") , stylesheet
                            , (target     != null ? "target"     : "dummy") , target
                            , (xslt       != null ? "xslt"       : "dummy") , xslt
                            , (namespace  != null ? "namespace"  : "dummy") , namespace
                            }
                            , parameterMap
                            );
                tbSerializer.setInputURI     (config.getInputURI());
                if (debug >= 1 && config.getCallType() == Configuration.WEB_CALL) {
                    tbSerializer.writeComment("Response Headers: " + getResponseHeaders());
                    tbSerializer.writeComment("SpecificationHandler.parameterMap: " + dumpMap(parameterMap));
                }
                // ROOT_TAG

            } else if (qName.equals(A_TAG       ) && uri.equals(config.DBAT_URI)) {
                currentNameSpace = config.DBAT_URI;
                if (tbSerializer instanceof HTMLTable) {    // split link values on "="
                    String href = attrs.getValue("href");
                    if (href != null) {
                        tbSerializer.getSingleCell().setHref(href);
                    }
                } // for HTML only

            } else if (qName.equals(BREAK_TAG   )) {
                if (currentNameSpace.equals(config.DBAT_URI)) {
                    colBuffer.append("<br />");
                } else {
                    // write empty HTML tag <br />
                    tbSerializer.writeMarkup("<br />");
                }

            } else if (qName.equals(CALL_TAG    )) {
                initializeAction();
                getIdAttribute(attrs, tbMetaData);
                String procName = attrs.getValue("name");
                if (procName == null) { // no name attribute - take "undefined" instead
                    procName = "undefined";
                }
                if (true) {
                    String headerAttr = attrs.getValue("headers");
                    if (headerAttr != null) {
                        boolean withHeaders = sqlAction.isWithHeaders();
                        withHeaders = headerAttr.matches("[yYjJtT].*");
                        sqlAction.setWithHeaders(withHeaders);
                    } // header

                    String limitAttr = attrs.getValue("limit");
                    boolean vertical = false;
                    if (limitAttr != null) {
                        int limit = sqlAction.getFetchLimit();
                        if (limitAttr.startsWith("vert")) {
                            vertical = true;
                            limit = 1;
                        } else { // max. number of rows to be displayed
                            try {
                                limit = Integer.parseInt(limitAttr);
                            } catch (Exception exc) {
                                // ignore, take default FETCH_LIMIT
                            }
                        }
                        sqlAction.setFetchLimit(limit);
                    } // limit

                    String fetchTarget              =    attrs.getValue("into")          ; // null (default) or "parm"
                    if (false) {
                    } else if (fetchTarget != null && fetchTarget.startsWith("par")) {
                        tbMetaData.setAggregateIndex(TableMetaData.AGGR_PARAMS);
                    } else if (vertical) {
                        tbMetaData.setAggregateIndex(TableMetaData.AGGR_VERTICAL);
                    }
                } // dummy if

                parentStmt = elem; // remember it for closing tag
                colBuffer.setLength(0);
                sqlBuffer.setLength(0); // start a new statement
                variables.clear();
                sqlBuffer.append("CALL ");
                sqlBuffer.append(procName);
                columnNo = 0;
                currentNameSpace = config.DBAT_URI; // leave HTML, enter specification syntax
                // CALL_TAG

            } else if (qName.equals(COL_TAG     )) {
                sqlBuffer.append(colBuffer.toString().trim()); // for table name after <update>
                colBuffer.setLength(0);
                column = new TableColumn();
                column.setIndex(columnNo);
                tbMetaData.addColumn(column);
                String align = attrs.getValue("align");
                if (align != null && align.length() > 0) {
                    column.setAlign(align);
                }
                String direction = attrs.getValue("dir");
                column.setDirection(direction);

                String href = attrs.getValue("href");
                if (href != null && href.length() > 0) {
                    column.setHref(href);
                }
                String key = attrs.getValue("key");
                if (key != null && key.length() > 0) {
                    column.setKey(key);
                }
                String name = attrs.getValue("name");
                if (name != null && name.length() > 0) {
                    column.setName(name); // sets label, too, must be evaluated before the label attribute
                }
                String label = attrs.getValue("label");
                if (label != null && label.length() > 0) {
                    column.setLabel(label);
                }
                String label2 = attrs.getValue("label2");
                if (label2 != null && label2.length() > 0) {
                    column.setLabel2(label2);
                }
                String link = attrs.getValue("link");
                if (link != null && link.length() > 0) {
                    column.setHref(SERVLET_SPEC + link);
                }
                String pseudo = attrs.getValue("pseudo");
                if (pseudo != null && pseudo.length() > 0) {
                    column.setPseudo(pseudo);
                }
                String remark = attrs.getValue("remark");
                if (remark != null && remark.length() > 0) {
                    column.setRemark(remark);
                }
                String span2 = attrs.getValue("span2");
                if (span2 != null && span2.length() > 0) {
                    column.setSpan2(span2);
                }
                String style = attrs.getValue("style");
                if (style != null && style.length() > 0) {
                    column.setStyle(style);
                }
                String target = attrs.getValue("target");
                if (target != null && target.length() > 0) {
                    column.setTarget(target);
                }
                String typeName = attrs.getValue("type");
                if (typeName != null && typeName.length() > 0) {
                    column.setTypeName(typeName.toLowerCase());
                    if (typeName.toUpperCase().startsWith("VARCHAR")) {
                        column.setDataType(Types.VARCHAR);
                    }
                } else {
                    typeName = "varchar";
                }
                column.setWidth(attrs.getValue("width"));

                String wrap = attrs.getValue("wrap");
                if (wrap != null && wrap.length() > 0) {
                    String sep = attrs.getValue("sep");
                    if (sep == null || sep.length() == 0) {
                        sep = ","; // this is the default separator
                    } // sep
                    column.setWrap(wrap + ":" + sep);
                }

                if (false) {
                } else if (parentStmt.equals(CALL_TAG  )) {
                    sqlBuffer.append(" -");
                    sqlBuffer.append(column.getDirection());
                    if (! typeName.contains("char")) {
                        sqlBuffer.append(':');
                        sqlBuffer.append(typeName);
                    }
                } else if (parentStmt.equals(INSERT_TAG)) {
                    sqlBuffer.append(columnNo == 0 ? "(" : "\n,");
                    sqlBuffer.append(name);
                } else if (parentStmt.equals(UPDATE_TAG)) {
                    sqlBuffer.append(columnNo == 0 ? " SET " : "\n, ");
                    sqlBuffer.append(name);
                    sqlBuffer.append(" = ");
                }
                // COL_TAG

            } else if (qName.equals(COLUMN_TAG  )) {
                sqlBuffer.append(colBuffer.toString().trim()); // for table name after <update>
                colBuffer.setLength(0);
                column = new TableColumn();
                column.setIndex(columnNo);
                tbMetaData.addColumn(column);
                inColumn = true;

            } else if (qName.equals(COMMENT_TAG )) {
                tbSerializer.writeMarkup("<!--");

            } else if (qName.equals(CONNECT_TAG )) {
                saveConnectionAttribute(attrs, "to");
                config.closeConnection(); // previous
                config.openConnection();  // new one

            } else if (qName.equals(COUNTER_TAG )) {
                String counterDesc = attrs.getValue("desc");
                if (counterDesc == null) {
                    counterDesc = attrs.getValue("name"); // for <select into="parm"> - store likewise
                }
                if (counterDesc == null || counterDesc.length() == 0) {
                    // the element was present, but not the attribute: take default word particles
                    counterDesc = Messages.getDefaultCounterDesc(config.getLanguage());
                }
                tbMetaData.setCounterDesc(counterDesc);

            } else if (qName.equals(DELETE_TAG  )) {
                parentStmt = elem;
                colBuffer.setLength(0);
                sqlBuffer.setLength(0); // start a new statement
                variables.clear();
                sqlBuffer.append("DELETE ");
                columnNo = 0;
                initializeAction();
                getIdAttribute(attrs, tbMetaData);
                currentNameSpace = config.DBAT_URI; // leave HTML, enter specification syntax

            } else if (qName.equals(DESCRIBE_TAG)) {
                parentStmt = elem; // remember it for closing tag
                colBuffer.setLength(0);
                sqlBuffer.setLength(0);
                variables.clear();
                initializeAction();
                currentNameSpace = config.DBAT_URI; // leave HTML, enter specification syntax

            } else if (qName.equals(DIV_TAG     ) &&  uri.equals(config.DBAT_URI)) {
                // ignore

            } else if (qName.equals(FORM_TAG    )) {
                int index = attrs.getIndex("action");
                if (index >= 0) { // with explicit "action" attribute => is not our variant
                    tbSerializer.writeMarkup(getStartTag(qName, attrs));
                } else { // our variant: implied attribute: action="servlet?spec=newspec"
                    AttributesImpl attrs2 = new AttributesImpl(attrs);
                    String newSpec = specName;
                    /** prefix for URLs of such specifications */
                    attrs2.addAttribute("", "action", "action", "CDATA", "servlet?spec=" + newSpec);
                    index = attrs.getIndex("method");
                    if (index < 0 ) { // add method="post"
                        attrs2.addAttribute("", "method", "method", "CDATA", "post");
                    } // default method="post"
                /*  this has catastrophal effects on <listparm>/<textarea>:
                    index = attrs.getIndex("enctype");
                    if (index < 0) { // add enctype
                        attrs2.addAttribute("", "enctype", "enctype", "CDATA", "multipart/form-data");
                    } // default enctype
                */
                    tbSerializer.writeMarkup(getStartTag(qName, attrs2));
                    tbSerializer.writeMarkup("<input name=\"spec\" type=\"hidden\" value=\"" + newSpec + "\" />\n");
                    // repeatParameterFields();
                } // FORM_TAG our variant

            } else if (qName.equals(FROM_TAG    )) { // FROM clause including any JOIN clauses
                sqlBuffer.append(colBuffer.toString().trim());
                colBuffer.setLength(0);
                if (columnNo == 0 && parentStmt.equals(SELECT_TAG)) { // no <col> specified (not for DELETE)
                    sqlBuffer.append("* ");
                }
                sqlBuffer.append(" \nFROM ");

            } else if (qName.equals(GROUP_TAG   )) {
                String expr = attrs.getValue("by");
                sqlBuffer.append(" \nGROUP BY " + expr);

            } else if (qName.equals(INPUT_TAG   )) { // c.f. also TEXTAREA_TAG
                startFormField(qName, attrs);
                // INPUT_TAG

            } else if (qName.equals(INSERT_TAG  )) {
                parentStmt = elem;
                colBuffer.setLength(0);
                sqlBuffer.setLength(0); // start a new statement
                variables.clear();
                sqlBuffer.append("INSERT ");
                columnNo = 0;
                initializeAction();
                getIdAttribute(attrs, tbMetaData);
                currentNameSpace = config.DBAT_URI; // leave HTML, enter specification syntax

            } else if (qName.equals(INTO_TAG    )) {
                colBuffer.setLength(0);
                sqlBuffer.append(" INTO ");

            } else if (qName.equals(LISTBOX_TAG )) {
                startFormField(qName, attrs);

            } else if (qName.equals(LISTPARM_TAG)) {
                // similiar to PARM_TAG, splits <textarea/> value into single-quoted, comma-separated list of words
                String name  = attrs.getValue("name");
                Object obj   = parameterMap.get(name);
                if (obj != null) {
                    String[] params = (String[]) obj;
                    if (name.matches("[A-Z0-9_]+")) {
                        params[0] = params[0].toUpperCase();
                    }
                    if (currentNameSpace.equals(config.HTML_URI)) { // print HTML text unchanged
                        tbSerializer.writeMarkup(params[0]);
                    } else { // result is "('word1', 'word2', ...)"
                        String[] words = null;
                        if (params.length <= 1) {
                            words = params[0].trim().split("\\s+");
                        } else { // result of <db:listbox multiple="yes" />
                            words = params;
                        }
                        colBuffer.append("("); // empty list
                        if (words.length == 0) {
                            colBuffer.append("\'\'");
                        } else {
                            int iword = 0;
                            while (iword < words.length) {
                                if (iword >= 1) {
                                    colBuffer.append(", ");
                                }
                                colBuffer.append("\'");
                                colBuffer.append(words[iword ++]);
                                colBuffer.append("\'");
                            } // while iword
                        } // not empty
                        colBuffer.append(")");
                    }
                } else { // parameter was not found (at first request): create a list with an empty string
                    colBuffer.append("(\'\')");
                }
                // LISTPARM_TAG

            } else if (qName.equals(OPTION_TAG  )) { // part of HTML <select> form field
                String value = attrs.getValue("value");
                if (value != null) { // conditionally add the "selected=" attribute
                    AttributesImpl attrs2 = new AttributesImpl();
                    attrs2.addAttribute("", "value", "value", "CDATA", value); // we ignore all attributes except "value="
                    int ival = 0;
                    while (ival < listBoxParams.length) {
                        // tbSerializer.writeComment("OPTION: " + value + " ? " + listBoxParams[ival]);
                        if (listBoxParams[ival].equals(value)) {
                            attrs2.addAttribute("", "selected", "selected", "CDATA", "yes");
                            ival = listBoxParams.length; // break loop
                        }
                        ival ++;
                    } // while ival
                    tbSerializer.writeMarkup(getStartTag(qName, attrs2));
                    // value != null
                } else { // otherwise we cannot influence the presence of the "selected=" attribute
                    tbSerializer.writeMarkup(getStartTag(qName, attrs));
                }
                // OPTION_TAG

            } else if (qName.equals(ORDER_TAG   )) {
                String expr = attrs.getValue("by");
                if (probeOpen) {
                    probeOpen = false;
                    sqlBuffer.append("\n) probe where 1 = 0 ");
                } // probing
                if (expr != null) {
                    sqlBuffer.append(" \nORDER BY " + expr);
                }
                colBuffer.setLength(0);

            } else if (qName.equals(PARM_TAG    )) { // deprecated, with single quotes around, not suitable for PreparedStatement
                String params = getParameterForSQL(attrs, "name");
                if (currentNameSpace.equals(config.HTML_URI)) { // print HTML text unchanged
                    tbSerializer.writeMarkup(params);
                } else {
                    colBuffer.append(params);
                }
                // PARM_TAG

            } else if (qName.equals(READ_TAG    )) { // in <col>
                // READ_TAG

            } else if (qName.equals(VAR_TAG     )) { // recommended, similiar to PARM_TAG, but for a prepared statement
                String params = getParameterForSQL(attrs, "name");
                if (currentNameSpace.equals(config.HTML_URI)) { // print HTML text unchanged
                    tbSerializer.writeMarkup(params);
                } else { // DBAT_URI
                    String varName  = attrs.getValue("name");
                    String typeName = attrs.getValue("type");
                    if (typeName != null) {
                        typeName = typeName.toUpperCase(); // SQL conventions
                    } else {
                        typeName = "CHAR";
                    }
                    colBuffer.append(BaseTable.PARAMETER_MARKER); // append the placeholder
                    variables.add(varName);
                    variables.add(typeName);
                    variables.add(params);
                    // DBAT_URI
                }
                // VAR_TAG

            } else if (qName.equals(SELECT_TAG  )) {
                initializeAction();
                String name = attrs.getValue("name");
                listBoxParams = getParameterList(attrs, "name");
                if (! uri.equals(config.DBAT_URI)) { // HTML variant: list box form field
                    startFormField(qName, attrs);
                    if (name != null) {
                        String initValue = attrs.getValue("init");
                        Object obj = parameterMap.get(name);
                        if (obj == null && initValue != null) {
                            listBoxParams = initValue.split("\\s+");
                            parameterMap.put(name, listBoxParams);
                        }
                    } // name != null
                } else { // DBAT variant: SQL for a DB table query
                    parentStmt = elem; // remember it for closing tag
                    colBuffer.setLength(0);
                    // sqlBuffer.setLength(0); not: because of optional previous WITH
                    // variables.clear();
                    if (tbSerializer.getOutputFormat().startsWith("probe")) {
                        sqlBuffer.append("select * from (\n");
                        probeOpen = true;
                    } // probing
                    sqlBuffer.append("SELECT ");

                    String distinctAttr = attrs.getValue("distinct");
                    if (distinctAttr != null) {
                        if (distinctAttr.matches("[yYjJtT].*")) {
                            sqlBuffer.append("DISTINCT ");
                        }
                    } // distinct

                    String limitAttr = attrs.getValue("limit");
                    boolean vertical = false;
                    if (limitAttr != null) {
                        int limit = sqlAction.getFetchLimit();
                        if (limitAttr.startsWith("vert")) {
                            vertical = true;
                            limit = 1;
                        } else { // max. number of rows to be displayed
                            try {
                                limit = Integer.parseInt(limitAttr);
                            } catch (Exception exc) {
                                // ignore, take default FETCH_LIMIT
                            }
                        }
                        sqlAction.setFetchLimit(limit);
                    } // limit

                    String headerAttr = attrs.getValue("headers");
                    if (headerAttr != null) {
                        boolean withHeaders = sqlAction.isWithHeaders();
                        withHeaders = headerAttr.matches("[yYjJtT].*");
                        sqlAction.setWithHeaders(withHeaders);
                    } // header

                    columnNo = 0;
                    tbMetaData.setAggregationName       (attrs.getValue("aggregate")    ); // pass null if feature is not desired
                    tbMetaData.setAggregationSeparator  (attrs.getValue("with")         ); // char or "pivot"; pass null if default
                    String fetchTarget              =    attrs.getValue("into")          ; // null (default) or "parm"
                    tbMetaData.setGroupColumns          (attrs.getValue("group")        ); // pass null if feature is not desired
                    tbMetaData.setScrollArea            (attrs.getValue("scroll")       ); // pass null for no scrolling
                    getIdAttribute(attrs, tbMetaData);
                    currentNameSpace = config.DBAT_URI; // leave HTML, enter specification syntax
                    if (false) {
                    } else if (fetchTarget != null && fetchTarget.startsWith("par")) {
                        tbMetaData.setAggregateIndex(TableMetaData.AGGR_PARAMS);
                    } else if (vertical) {
                        tbMetaData.setAggregateIndex(TableMetaData.AGGR_VERTICAL);
                    }
                    if (debug >= 1) {
                        tbSerializer.writeComment("SpecificationHandler.SELECT, aggregateIndex=" + tbMetaData.getAggregateIndex());
                    }
                } // SELECT_TAG, our variant: DB table query

            } else if (qName.equals(TEXTAREA_TAG)) { // similiar to INPUT_TAG
                startFormField(qName, attrs);
                startName  = attrs.getValue("name");

            } else if (qName.equals(TRAILER_TAG )) {
                trailerSelect = attrs.getValue("select");
                // additional reasoning in getTrailer

            } else if (qName.equals(UPDATE_TAG  )) {
                parentStmt = elem;
                sqlBuffer.setLength(0);
                variables.clear();
                sqlBuffer.append("UPDATE ");
                columnNo = 0;
                initializeAction();
                getIdAttribute(attrs, tbMetaData);
                currentNameSpace = config.DBAT_URI; // leave HTML, enter specification syntax

            } else if (qName.equals(VALUES_TAG  )) {
                valBuffer = new StringBuffer(256); // for VALUES

            } else if (qName.equals(WHERE_TAG   )) {
                if (false) {
                } else if (parentStmt.equals(SELECT_TAG  )) {
                    // all <col>s are already appended
                } else if (parentStmt.equals(UPDATE_TAG  )) {
                    sqlBuffer.append(colBuffer.toString().trim());
                } else {
                }
                colBuffer.setLength(0);
                sqlBuffer.append(" \nWHERE ");

            } else if (qName.equals(WITH_TAG    )) {
                if (true) {
                    colBuffer.setLength(0);
                    sqlBuffer.setLength(0); // start a new statement
                    variables.clear();
                    sqlBuffer.append("WITH ");
                    currentNameSpace = config.DBAT_URI; // leave HTML, enter specification syntax
                }

            } else if (qName.equals(CHOOSE_TAG  )) {
            } else if (qName.equals(WHEN_TAG    )) { // conditional compilation branch
            } else if (qName.equals(OTHERWISE_TAG)) {

            } else { // if (uri.equals(config.HTML_URI)) { // reconstruct HTML start tag with all attributes
                if (currentNameSpace.equals(config.HTML_URI)) { // close HTML element
                    tbSerializer.writeMarkup    (getStartTag(qName, attrs));
                } else {
                    colBuffer.append            (getStartTag(qName, attrs));
                }
            }
        } catch (Exception exc) {
            disabled = true; // disable all further SAX event processing
            log.error(config.message(exc), exc);
            tbSerializer.writeMarkup("<h3 class=\"error\">Specification parsing error: "
                    + exc.getMessage() + "</h3><pre class=\"error\">"
                    + qName
                    + "</pre>");
        }
    } // startElement

    //==================================================================

    /** Receive notification of the end of an element.
     *  Looks for the element which contains encoded strings lines.
     *  Terminates the line.
     *  @param uri the Namespace URI, or the empty string if the element has no Namespace URI
     *  or if Namespace processing is not being performed.
     *  @param localName the local name (without prefix),
     *  or the empty string if Namespace processing is not being performed.
     *  @param qName the qualified name (with prefix),
     *  or the empty string if qualified names are not available.
     */
    public void endElement(String uri, String localName, String qName) {
        if (disabled) {
        } else
        try { // if there is any exception, further SAX event processing will be disabled
            int colonPos = qName.indexOf(":");
            if (colonPos >= 0) { // remove prefix
                qName = qName.substring(colonPos + 1);
            }
            /*..................................................
                first the end tags for conditional compilation
            */
            if (false) {
            } else if (qName.equals(CHOOSE_TAG  )) {
                chooseTop --; // pop unconditionally
                // </choose>

            } else if (qName.equals(WHEN_TAG    )) { // conditional compilation branch
                if ((chooseStack[chooseTop] & CHOOSE_THIS) != 0) {
                    // remember that there was at least 1 "true" <when> branch, which will skip the <otherwise> branch
                    chooseStack[chooseTop] |= CHOOSE_SOME;
                }
                chooseStack[chooseTop] &= (0xff ^ CHOOSE_THIS); // reset <when>
                // </when>

            } else if (qName.equals(OTHERWISE_TAG)) {
                // ignore, </choose> will follow
                // </otherwise>
            }
            /*..................................................
                now the remaining end tags
            */
            if ((chooseStack[chooseTop] & CHOOSE_THIS) == zeroAndNotEcho) {
                // ignore all normal end tags
            } else if (qName.equals(COLUMN_TAG     )) {
                if (false) {
                } else if (parentStmt.equals(INSERT_TAG)) {
                    valBuffer.append(columnNo == 0 ? "(" : ",");
                    String value = colBuffer.toString().trim();
                    colBuffer.setLength(0);
                    if (value.length() == 0) {
                        String name = column.getName();
                        if (name != null) {
                            Object obj = parameterMap.get(name);
                            if (obj != null) {
                                String [] params = (String[]) obj;
                                value = params[0];
                            }
                        }
                    }
                    valBuffer.append("\'");
                    valBuffer.append(value);
                    valBuffer.append("\'");

                } else if (parentStmt.equals(SELECT_TAG)) {
                    if (columnNo == 0) {
                    } else {
                        sqlBuffer.append("\n, ");
                    }
                    sqlBuffer.append(column.getExpr());
                    colBuffer.setLength(0);

                } else if (parentStmt.equals(UPDATE_TAG)) {
                    sqlBuffer.append(colBuffer.toString().trim());
                    colBuffer.setLength(0);

                }
                columnNo ++; // increase at end tag </col>
                inColumn = false;
                // /COLUMN_TAG

            } else if (inColumn) {
                // align= dir= href= label= label2= link= name= pseudo= remark= span2= style= type= wrap=
                if (false) {
                } else if (qName.equals(ALIGN_TAG   )) {
                    column.setAlign     (colBuffer.toString().trim());
                } else if (qName.equals(DIR_TAG     )) {
                    column.setDirection (colBuffer.toString().trim());
                } else if (qName.equals(EXPR_TAG) || qName.equals(SQL_TAG)) {
                    column.setExpr      (colBuffer.toString().trim());
                } else if (qName.equals(HREF_TAG    )) {
                    column.setHref      (colBuffer.toString().trim());
                } else if (qName.equals(KEY_TAG     )) {
                    column.setKey       (colBuffer.toString().trim());
                } else if (qName.equals(LABEL_TAG   )) {
                    column.setLabel     (colBuffer.toString().trim());
                } else if (qName.equals(LABEL2_TAG  )) {
                    column.setLabel2    (colBuffer.toString().trim());
                } else if (qName.equals(LINK_TAG    )) {
                    column.setHref      (SERVLET_SPEC + colBuffer.toString().trim());
                } else if (qName.equals(NAME_TAG    )) {
                    column.setName      (colBuffer.toString().trim());
                } else if (qName.equals(PSEUDO_TAG  )) {
                    column.setPseudo    (colBuffer.toString().trim());
                } else if (qName.equals(REMARK_TAG  )) {
                    column.setRemark    (colBuffer.toString().trim());
                } else if (qName.equals(SPAN2_TAG   )) {
                    column.setSpan2     (colBuffer.toString().trim());
                } else if (qName.equals(STYLE_TAG   )) {
                    column.setStyle     (colBuffer.toString().trim());
                } else if (qName.equals(TYPE_TAG    )) {
                    column.setTypeName  (colBuffer.toString().trim().toLowerCase());
                } else if (qName.equals(WIDTH_TAG   )) {
                    column.setWidth     (colBuffer.toString().trim());
                } else if (qName.equals(WRAP_TAG    )) {
                    column.setWrap(colBuffer.toString().trim());
                    // ???
                } // else ignore
                colBuffer.setLength(0); // start character assembly for all subordinate elements

            } else if (qName.equals(ROOT_TAG    )) {
                if (! isSuccessful()) {
                    tbSerializer.writeMarkup(Messages.getErrorNotice(config.getLanguage()));
                } // with errors
                if (debug >= 1 && config.getCallType() == Configuration.WEB_CALL) {
                   tbSerializer.writeComment("SpecificationHandler: specPath=\"" + specPath
                            + "\", specUrl=\"" + specUrl
                            + "\", specName=\"" + specName
                            + "\", requestURL=\"" + request.getRequestURL().toString()
                            + "\", User-Agent=\"" + request.getHeader("User-Agent")
                            );
                }
                tbSerializer.writeTrailer(getTrailer(trailerSelect, config.getLanguage(), specName));
                tbSerializer.writeEnd();
                // </dbat>

            } else if (qName.equals(A_TAG       ) && uri.equals(config.DBAT_URI)) {
                if (tbSerializer instanceof HTMLTable) { // for HTML - outside of SQL statements only ???
                    tbSerializer.getSingleCell().separateWrappedValue(colBuffer.toString().trim()
                            , tbSerializer.getTargetEncoding()
                            , tbSerializer.getEscapingRule()
                            , config.getNullText());
                    colBuffer.setLength(0);
                    tbSerializer.writeSingleCell();
                    currentNameSpace = config.HTML_URI;
                } // outside
                // </a>

            } else if (qName.equals(BREAK_TAG   )) {
                // ignore </ht:br>

            } else if (qName.equals(CALL_TAG    )) {
                // tbSerializer.writeComment("CALL_TAG in SpecificationHandler.endElement: " + sqlBuffer.toString());
                tbMetaData.setFillState(1);
                tbMetaData.setAggregateColumn(); // with previously stored parameters
                sqlAction.execSQLStatement(tbMetaData, sqlBuffer.toString(), variables, parameterMap);
                currentNameSpace = config.HTML_URI; // back to HTML
                terminateAction();
                parentStmt = "";
                colBuffer.setLength(0);
                sqlBuffer.setLength(0);
                variables.clear();

            } else if (qName.equals(COL_TAG     )) {
                if (false) {
                } else if (parentStmt.equals(CALL_TAG)) {
                    String expr = colBuffer.toString().trim();
                    if (expr.length() > 0) {
                        sqlBuffer.append(expr);
                    } else { // append value of parameter with the same name
                        if (column.getDir() == 'i') {
                            // append the value of the parameter with the same name
                            sqlBuffer.append(" \'"); // value may not be empty because of 'tokenize'
                            Object obj = parameterMap.get(column.getName());
                            if (obj != null) {
                                String [] params = (String[]) obj;
                                sqlBuffer.append(params[0].trim());
                            }
                            sqlBuffer.append("\' ");
                        } // dir="in..."
                    }
                    colBuffer.setLength(0);

                } else if (parentStmt.equals(INSERT_TAG)) {
                    valBuffer.append(columnNo == 0 ? "(" : ",");
                    String value = colBuffer.toString().trim();
                    colBuffer.setLength(0);
                    if (value.length() == 0) {
                        String name = column.getName();
                        if (name != null) {
                            Object obj = parameterMap.get(name);
                            if (obj != null) {
                                String [] params = (String[]) obj;
                                value = params[0];
                            }
                        }
                    }
                    // valBuffer.append("\'");
                    valBuffer.append(value);
                    // valBuffer.append("\'");

                } else if (parentStmt.equals(SELECT_TAG)) {
                    String expr = colBuffer.toString().trim();
                    if (expr.length() == 0) {
                        expr = column.getName();
                    }
                    column.setExpr(expr);
                    if (columnNo == 0) {
                    } else {
                        sqlBuffer.append("\n, ");
                    }
                    sqlBuffer.append(column.getExpr());
                    colBuffer.setLength(0);

                } else if (parentStmt.equals(UPDATE_TAG)) {
                    sqlBuffer.append(colBuffer.toString().trim());
                    colBuffer.setLength(0);

                }
                columnNo ++; // increase at end tag </col>
                // /COL_TAG

            } else if (qName.equals(COMMENT_TAG )) {
                tbSerializer.writeMarkup("-->");

            } else if (qName.equals(COUNTER_TAG )) {

            } else if (qName.equals(DELETE_TAG  )) {
                // tbSerializer.writeComment("DELETE_TAG in SpecificationHandler.endElement: " + sqlBuffer.toString());
                tbMetaData.setFillState(1);
                sqlAction.execSQLStatement(tbMetaData, sqlBuffer.toString(), variables, parameterMap);
                currentNameSpace = config.HTML_URI; // back to HTML
                terminateAction();
                parentStmt = "";
                colBuffer.setLength(0);
                sqlBuffer.setLength(0);
                variables.clear();
                // /DELETE_TAG

            } else if (qName.equals(DESCRIBE_TAG  )) {
                // tablePattern was set in startElement
                sqlBuffer.append(colBuffer.toString().trim());
                sqlAction.describeTables(config.getDefaultSchema(), sqlBuffer.toString());
                currentNameSpace = config.HTML_URI; // back to HTML
                parentStmt = "";
                colBuffer.setLength(0);
                sqlBuffer.setLength(0);
                variables.clear();

            } else if (qName.equals(DIV_TAG     ) &&  uri.equals(config.DBAT_URI)) {
                // ignore

            } else if (qName.equals(FROM_TAG    )) {
                sqlBuffer.append(colBuffer.toString().trim());
                colBuffer.setLength(0);

            } else if (qName.equals(INPUT_TAG   )) { // c.f. also TEXTAREA_TAG
                endFormField(qName);
                // INPUT_TAG

            } else if (qName.equals(INSERT_TAG  )) {
                sqlBuffer.append(") VALUES ");
                sqlBuffer.append(valBuffer.toString());
                sqlBuffer.append(")");
                tbMetaData.setFillState(1);
                sqlAction.execSQLStatement(tbMetaData, sqlBuffer.toString(), variables, parameterMap);
                terminateAction();
                currentNameSpace = config.HTML_URI; // back to HTML
                parentStmt = "";
                colBuffer.setLength(0);
                sqlBuffer.setLength(0);
                variables.clear();

            } else if (qName.equals(INTO_TAG    )) {
                sqlBuffer.append(colBuffer.toString());
                colBuffer.setLength(0);
                sqlBuffer.append(" ");

            } else if (qName.equals(LISTBOX_TAG )) {
                endFormField(qName);

            } else if (qName.equals(LISTPARM_TAG)) {

            } else if (qName.equals(ORDER_TAG   )) {
                String order = colBuffer.toString().trim();
                if (order.length() > 0) {
                    sqlBuffer.append(" ORDER ");
                    sqlBuffer.append(order);
                }
                colBuffer.setLength(0);

            // ignore </option>

            } else if (qName.equals(PARM_TAG    )) {
            } else if (qName.equals(READ_TAG    )) { // in <col>
            } else if (qName.equals(VAR_TAG     )) {

            } else if (qName.equals(SELECT_TAG  )) {
                if (! uri.equals(config.DBAT_URI)) { // HTML
                    listBoxParams = new String[0]; // empty
                    endFormField(qName);
                } else { // DBAT_URI
                    // tbSerializer.writeComment("SELECT_TAG in SpecificationHandler.endElement: " + sqlBuffer.toString());
                    tbMetaData.setFillState(1);
                    tbMetaData.setAggregateColumn(); // with previously stored parameters
                    sqlBuffer.append(' ');
                    sqlBuffer.append(colBuffer.toString().trim()); // anything behind/outside <where>, <group>, <order>
                    if (probeOpen) {
                        probeOpen = false;
                        sqlBuffer.append("\n) probe where 1 = 0 ");
                    } // probing
                    if (isSuccessful()) {
                        sqlAction.execSQLStatement(tbMetaData, sqlBuffer.toString(), variables, parameterMap);
                    }
                    terminateAction();
                    currentNameSpace = config.HTML_URI; // back to HTML
                    parentStmt = "";
                    colBuffer.setLength(0);
                    sqlBuffer.setLength(0);
                    variables.clear();
                } // DBAT

            } else if (qName.equals(TEXTAREA_TAG   )) { // similiar to INPUT_TAG
                if (true) { // no value specified - insert that of the corresponding parameter
                    String name = startName;
                    if (name != null && ! name.equals("")) { // valid name
                        Object obj = parameterMap.get(name);
                        if (obj != null) {
                            String[] params = (String[]) obj;
                            tbSerializer.writeMarkup(params[0]);
                        }
                    } // valid name
                } // no value
                endFormField(qName);
                // TEXTAREA_TAG

            } else if (qName.equals(UPDATE_TAG  )) {
                sqlBuffer.append(colBuffer.toString().trim());
                colBuffer.setLength(0);
                // tbSerializer.writeComment("UPDATE_TAG in SpecificationHandler.endElement: " + sqlBuffer.toString());
                tbMetaData.setFillState(1);
                sqlAction.execSQLStatement(tbMetaData, sqlBuffer.toString(), variables, parameterMap);
                terminateAction();
                currentNameSpace = config.HTML_URI; // back to HTML
                parentStmt = "";
                sqlBuffer.setLength(0);
                variables.clear();
                // /UPDATE_TAG

            } else if (qName.equals(TRAILER_TAG )) {
                // ignore - empty element

            } else if (qName.equals(VALUES_TAG  )) {
                // ignore - INSERT processing is done by </insert>

            } else if (qName.equals(WHERE_TAG   )) {
                sqlBuffer.append(colBuffer.toString().trim());
                colBuffer.setLength(0);

            } else if (qName.equals(WITH_TAG    )) {
                sqlBuffer.append(colBuffer.toString().trim());
                colBuffer.setLength(0);
                sqlBuffer.append("\n");

            } else if (qName.equals(CHOOSE_TAG  )) {
            } else if (qName.equals(WHEN_TAG    )) { // conditional compilation branch
            } else if (qName.equals(OTHERWISE_TAG)) {

            } else {
                if (currentNameSpace.equals(config.HTML_URI)) { // close HTML element
                    tbSerializer.writeMarkup("</" + qName + ">");
                } else {
                    colBuffer.append("</" + qName + ">");
                }
            }
        } catch (Exception exc) {
            disabled = true; // disable all further SAX event processing
            log.error(config.message(exc), exc);
            tbSerializer.writeMarkup("<h3 class=\"error\">Specification parsing error: "
                    + exc.getMessage() + "</h3><pre class=\"error\">"
                    + qName
                    + "</pre>");
        }
    } // endElement

    //==================================================================

    /** Receive notification of character data inside an element.
     *  @param ch the characters.
     *  @param start the start position in the character array.
     *  @param length the number of characters to use from the character array.
     */
    public void characters(char[] ch, int start, int length) {
        if (disabled) {
        } else
        try { // if there is any exception, further SAX event processing will be disabled
            if ((chooseStack[chooseTop] & CHOOSE_THIS) == zeroAndNotEcho) {
                // ignore all normal characters
            } else if (currentNameSpace.equals(config.HTML_URI)) { // print HTML text unchanged
                tbSerializer.writeMarkup(new String(ch, start, length));
            } else { // works for our <a> also
                colBuffer.append(new String(ch, start, length));
            }
        } catch (Exception exc) {
            disabled = true; // disable all further SAX event processing
            log.error(config.message(exc), exc);
            tbSerializer.writeMarkup("<h3 class=\"error\">Specification parsing error: "
                    + exc.getMessage() + "</h3><pre class=\"error\">"
                    + new String(ch, start, length)
                    + "</pre>");
        }
    } // characters

    /** Allows applications to map references to external entities into input sources,
     *  or tell the parser it should use conventional URI resolution.
     *  This method is only called for external entities which have been properly declared.
     *  @param publicId The public identifier of the external entity being referenced
     *  (normalized as required by the XML specification), or null if none was supplied.
     *  @param systemId The system identifier of the external entity being referenced;
     *  either a relative or absolute URI. This is never null when invoked by a SAX2 parser;
     *  only declared entities, and any external subset, are resolved by such parsers.
     *  @return An InputSource object describing the new input source to be used by the parser.
     *  Returning null directs the parser to resolve the system ID against the base URI and open a connection to resulting URI.
     *  @throws SAXException Any SAX exception, possibly wrapping another exception.
     *  @throws IOException Probably indicating a failure to create a new InputStream or Reader,
     *  or an illegal URL.
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        InputSource result = null;
        String url = systemId;
        try {
            if (false) { // take URLs with schema as verbatim
            } else if (systemId.startsWith("http://")) {
            } else if (systemId.startsWith("file://")) {
            } else if (systemId.startsWith("ftp://" )) {
            } else { // if no URL schema, assume URL to be relative to specPath
                if (specPath.matches("\\w\\:.*")) { // Windows drive letter
                    specPath = "/" + specPath.substring(0, 1) +  "|/" + specPath.substring(2);
                    // file://e:/webapps... => file:///e|/webapps... - this is understood by MS InternetExplorer also
                } // Windows
                String specDir = "";
                int lastSlashPos = specName.lastIndexOf("/");
                if (lastSlashPos >= 0) {
                    specDir = specName.substring(0, lastSlashPos + 1);
                }
                url = "file://" + (specPath + specDir + systemId).replaceAll("//", "/");
                result = new InputSource(url);
            }
            // log.info("resolveEntity(\"" + publicId + "\", \"" + systemId + "\") -> " + url);
        } catch (Exception exc) {
            log.error(config.message(exc), exc);
        }
        return result;
    } // resolveEntity(2)

    /** Allows applications to map references to external entities into input sources,
     *  or tell the parser it should use conventional URI resolution.
     *  This method is only called for external entities which have been properly declared.
     *  @param name Identifies the external entity being resolved.
     *  Either "[dtd]" for the external subset,
     *  or a name starting with "%" to indicate a parameter entity,
     *  or else the name of a general entity.
     *  This is never null when invoked by a SAX2 parser.
     *  @param publicId The public identifier of the external entity being referenced
     *  (normalized as required by the XML specification), or null if none was supplied.
     *  @param baseURI The URI with respect to which relative systemIDs are interpreted.
     *  This is always an absolute URI, unless it is null
     *  (likely because the XMLReader was given an InputSource without one).
     *  This URI is defined by the XML specification to be the one associated with the
     *  "&lt;" starting the relevant declaration.
     *  @param systemId The system identifier of the external entity being referenced;
     *  either a relative or absolute URI. This is never null when invoked by a SAX2 parser;
     *  only declared entities, and any external subset, are resolved by such parsers.
     *  @return An InputSource object describing the new input source to be used by the parser.
     *  Returning null directs the parser to resolve the system ID against the base URI and open a connection to resulting URI.
     *  @throws SAXException Any SAX exception, possibly wrapping another exception.
     *  @throws IOException Probably indicating a failure to create a new InputStream or Reader,
     *  or an illegal URL.
     */
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId)
            throws SAXException, IOException {
        return resolveEntity(publicId, systemId);
    } // resolveEntity(4)

} // SpecificationHandler
