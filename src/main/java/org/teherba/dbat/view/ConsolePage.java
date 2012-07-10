/*  ConsolePage.java - run a query or SQL instruction from a web form
 *  @(#) $Id$
 *  2012-07-01, Georg Fischer: copied from MorePage.java
 */
/*
 * Copyright 2012 Dr. Georg Fischer <punctum at punctum dot kom>
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
package org.teherba.dbat.view;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.TableFactory;
import  java.io.PrintWriter;
import  java.util.Iterator;
import  java.util.LinkedHashMap;
import  java.util.Map;
import  javax.sql.DataSource;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  org.apache.log4j.Logger;

/** Prints a form for the selection of 
 *  <ul>
 *  <li>a language,</li>
 *  <li>an encoding, </li>
 *  <li>an output format (mode),</li>
 *  <li>the maximum number of rows to be fetched,</li>
 *  <li>the database connection id to be used,</li>
 *  <li>a text area where an SQL statement can be input.</li>
 *  </ul>
 *  The statement is executed, and the result set is shown in a separate page.
 *  @author Dr. Georg Fischer
 */
public class ConsolePage {
    public final static String CVSID = "@(#) $Id$";
    public final static long serialVersionUID = 19470629;

    /** log4j logger (category) */
    private Logger log;

    /** No-argument constructor
     */
    public ConsolePage() {
        log = Logger.getLogger(ConsolePage.class.getName());
    } // constructor()
    
    /** Processes an http GET request
     *  @param request request with header fields
     *  @param response response with writer
     *  @param tableFactory factory for table serializers
     *  @param dsMap maps connection identifiers (short database instance ids) to {@link DataSource Datasources}
     *  @throws IOException
     */
    public void forward(HttpServletRequest request, HttpServletResponse response, TableFactory tableFactory, LinkedHashMap/*<1.5*/<String, DataSource>/*1.5>*/ dsMap) {
        try {
            PrintWriter out = response.getWriter();
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n");
            out.write("    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            out.write("<head>\n");
            out.write("<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml;charset=UTF-8\" />\n");
            out.write("<meta name=\"robots\" content=\"noindex, nofollow\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"stylesheet.css\" />\n");

            out.write("<title>Database Administration Tool</title>\n");
            out.write("<style>\ntd,th\n");
            out.write("{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;padding-right:10px;border:none;}\n</style>\n<script src=\"script.js\" type=\"text/javascript\">\n</script>\n</head>\n");
            String[] optEnc    = new String []
                    /*  0 */ { "ISO-8859-1"
                    /*  1 */ , "UTF-8"
                    } ;
            String[] enEnc    = new String []
                    /*  0 */ { "ISO-8859-1"
                    /*  1 */ , "UTF-8"
                    } ;
            String[] optLang = new String []
                    /*  0 */ { "de"
                    /*  1 */ , "en"
                    } ;
            String[] enLang = new String []
                    /*  0 */ { "Deutsch"
                    /*  1 */ , "English"
                    } ;
            String encoding     = "ISO-8859-1";
            String mode         = "html";
            String language     = "en";
            String connectionId = "mysql";
            if (dsMap != null && ! dsMap.isEmpty()) {
                Iterator /*<1.5*/<String>/*1.5>*/ citer = dsMap.keySet().iterator();
                boolean busy = true;
                while (busy && citer.hasNext()) {
                    connectionId = (String) citer.next();
                    busy = false; // take first only
                } // while citer
            }
            String intext       = "";
            int fetchLimit      = 64;
            
            Map parameterMap = request.getParameterMap(); // do not! use /*<1.5*/<String, String[]>/*1.5>*/
            Iterator /*<1.5*<String>*1.5>*/ parmIter = parameterMap.keySet().iterator();
            StringBuffer inputFields = new StringBuffer(256);

            while (parmIter.hasNext()) {
                String name = (String) parmIter.next();
                String[] values = request.getParameterValues(name);
                if (values.length <= 0) { // ignore empty value lists
                } else if (name.equals("enc"        )) {
                    encoding = values[0];
                } else if (name.equals("mode"       )) {
                    mode     = values[0];
                } else if (name.equals("lang"       )) {
                    language = values[0];
                } else if (name.equals("view"       )) {
                    // ignore
                } else if (name.equals("conn"       )) {
                    connectionId = values[0];
                } else if (name.equals("intext"     )) {
                    intext   = values[0].trim();
                } else if (name.equals("fetch"      )) {
                    try {
                        fetchLimit = Integer.parseInt(values[0].trim());
                    } catch (Exception exc) {
                        fetchLimit = 64;
                    }
                } else { // unknown parameter name - ignore
                } // unknown
            } // while parmIter
            int index = 0;
            out.write("<body>\n<!--\nenc=\"");
            out.write(encoding);
            out.write("\", mode=\"");
            out.write(mode);
            out.write("\", lang=\"");
            out.write(language);
            out.write("\" \n-->\n");
            out.write("<h3><a href=\"index.html\">Dbat</a>");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("-Konsole ");
            } else {
                out.write(" Console ");
            }
            out.write("</h3>\n");
            
            out.write("<form action=\"servlet\" method=\"get\">\n");
            out.write("<input type = \"hidden\" name=\"view\" value=\"con2\" />\n");

            out.write("<table cellpadding=\"8\">\n<tr>");

            out.write("<td class=\"bold\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Codierung");
            } else {
                out.write("Encoding");
            }
            out.write("</td>\n");

            out.write("<td class=\"bold\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Sprache");
            } else {
                out.write("Language");
            }
            out.write("</td>\n");

            out.write("<td class=\"bold\">\n");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Ausgabeformat");
            } else {
                out.write("Output Format");
            }
            out.write("</td>\n");

            out.write("<td class=\"bold\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Verbindung");
            } else {
                out.write("Connection");
            }
            out.write("</td>\n");

            out.write("</tr>\n<tr valign=\"top\">\n");
            
            out.write("<td>\n<select name=\"enc\" size=\"");
            out.write(String.valueOf(optEnc.length));
            out.write("\">\n");
            index = 0;
            while (index < optEnc.length) {
                out.write("<option value=\""
                          + optEnc[index] + "\""
                          + (optEnc[index].equals(encoding) ? "  selected=\"1\"" : "")
                          + ">"
                          + enEnc[index] + "</option>\n");
                index ++;
            } // while index
            out.write("</select>\n<p />");

            out.write("<span class=\"bold\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Max. Zeilen");
            } else {
                out.write("Fetch Limit");
            }
            out.write("</span><br />\n");
            out.write("<input type=\"text\" name=\"fetch\" size=\"8\" value=\"");
            out.write(String.valueOf(fetchLimit));
            out.write("\" />\n");

            out.write("</td>\n");
            
            out.write("<td>\n<select name=\"lang\" size=\"");
            out.write(String.valueOf(optLang.length));
            out.write("\">\n");
            index = 0;
            while (index < optLang.length) {
                out.write("<option value=\""
                          + optLang[index] + "\""
                          + (optLang[index].equals(language) ? "  selected=\"1\"" : "")
                          + ">"
                          + enLang[index] + "</option>\n");
                index ++;
            } // while index
            out.write("</select>\n</td>\n");

            out.write("<td>\n<select name=\"mode\" size=\"");
            out.write("6"); // all formats: String.valueOf(factory.getCount()));
            out.write("\">\n");
            Iterator /*<1.5*/<BaseTable>/*1.5>*/ titer = tableFactory.getIterator();
            while (titer.hasNext()) {
                BaseTable tableFormat = (BaseTable) titer.next();
                String code = tableFormat.getFirstFormatCode();
                out.write("<option value=\"" + code + "\""
                          + (code.equals(mode) ? " selected=\"1\"" : "")
                          + ">"
                          + code + " - " + tableFormat.getDescription(language) + "</option>\n");
            } // while titer
            out.write("</select>\n</td>\n");

            out.write("<td>\n<select name=\"conn\" size=\"");
            out.write("3"); // all connection Ids
            out.write("\">\n");
            Iterator /*<1.5*/<String>/*1.5>*/ diter = dsMap.keySet().iterator();
            while (diter.hasNext()) {
                String connId = (String) diter.next();
                out.write("<option value=\"" + connId + "\""
                          + (connId.equals(connectionId) ? " selected=\"1\"" : "")
                          + ">"
                          + connId + "</option>\n");
            } // while diter
            out.write("</select>\n<p />");
            
            out.write("<input type=\"submit\" value=\"");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Absenden");
            } else {
                out.write("Submit");
            }
            out.write("\" />\n</td>\n");

            out.write("</tr>\n<tr>\n<td class=\"bold\" colspan=\"4\">\n");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("SQL");
            } else {
                out.write("SQL");
            }
            out.write("\n</td>\n");

            out.write("</tr>\n<tr>\n<td colspan=\"4\">\n");
            out.write("<textarea name=\"intext\" cols=\"80\" rows=\"32\">");
            out.write(intext);
            out.write("</textarea>\n</td>\n");
            out.write("</tr>\n");
            out.write("</table>\n</form>\n");
            //====================================
        /*
            sqlAction  = new SQLAction    (config);
            tbMetaData = new TableMetaData(config);
        */
            if (intext.trim().length() > 0) {
                out.write(intext);
            }
            out.write("</body>\n</html>\n");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
        }
    } // forward

} // ConsolePage
