/*  ConsolePage.java - run a query or SQL instruction from a web form
 *  @(#) $Id$
 *  2016-08-26: param BasePage
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
package org.teherba.dbat.web;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.TableFactory;
import  org.teherba.common.web.BasePage;
import  java.io.IOException;
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
    } // Constructor()

    /** Shows an input form for SQL command execution
     *  @param request request with header fields
     *  @param response response with writer
     *  @param basePage refers to common web methods and messages
     *  @param language 2-letter code en, de etc.
     *  @param tableFactory factory for table serializers
     *  @param dsMap maps connection identifiers (short database instance ids) to {@link DataSource Datasources}
     */
    public void showConsole(HttpServletRequest request, HttpServletResponse response
            , BasePage basePage
            , String language
            , TableFactory tableFactory
            , LinkedHashMap<String, DataSource> dsMap
            ) throws IOException {
        if (true) { // try {
            PrintWriter out = basePage.writeHeader(request, response, language);

            String connectionId  = null;
            if (dsMap != null && ! dsMap.isEmpty()) {
                Iterator<String> citer = dsMap.keySet().iterator();
                boolean busy = true;
                while (busy && citer.hasNext()) {
                    connectionId = (String) citer.next();
                    busy = false; // take first only
                } // while citer
            } // valid dsMap
            String encoding     = BasePage.getInputField(request, "enc"   , "ISO-8859-1");
            String mode         = BasePage.getInputField(request, "mode"  , "html"      );
            // String language     = BasePage.getInputField(request, "lang"  , "en"        );
            connectionId        = BasePage.getInputField(request, "conn"  , "mysql"     );
            String intext       = BasePage.getInputField(request, "intext", ""          );
            int    fetchLimit   = BasePage.getInputField(request, "fetch" , 64          );

            String consoleWord = null;
            if (false) {
            } else if (language.startsWith("de")) {
                consoleWord = "-SQL-Konsole";
            } else {
                consoleWord = " SQL console";
            }

            out.write("<title>" + basePage.getAppName() + consoleWord + "</title>\n");
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
            int index = 0;
            out.write("<body>\n");
            out.write("<!--enc=\"" + encoding + "\", mode=\"" + mode + "\", lang=\"" + language + "\"-->\n");
            out.write("<h3>" + basePage.get(language, "001") + consoleWord + "</h3>\n"); // link to main page

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
            Iterator <BaseTable> titer = tableFactory.getIterator();
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
            Iterator <String> diter = dsMap.keySet().iterator();
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
            basePage.writeTrailer(language, "");
    /*
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
    */
        }
    } // showConsole

} // ConsolePage
