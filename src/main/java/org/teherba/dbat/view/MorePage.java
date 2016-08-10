/*  MorePage.java - replacement for more.jsp: input form with all parameters
 *  @(#) $Id$
 *  2016-08-09: Wiki => www.teherba.org/dbat
 *  2016-07-30: <form method="post"> for accented field values
 *  2016-05-12: view-source link
 *  2012-11-22: superfluous quote behind lang= caused format descriptions "null"
 *  2012-07-01: subpackage view
 *  2012-02-11, Georg Fischer: copied from more.jsp
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
import  org.teherba.dbat.Messages;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.TableFactory;
import  java.io.PrintWriter;
import  java.util.Iterator;
import  java.util.Map;
import  java.util.TreeMap;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  org.apache.log4j.Logger;

/** This class prints a form which contains all parameters
 *  and options for table output, together with links to 
 *  overview, API, notices and other documentation.
 *  The code is extracted from the former <em>more.jsp</em>.
 *  @author Dr. Georg Fischer
 */
public class MorePage {
    public final static String CVSID = "@(#) $Id$";
    public final static long serialVersionUID = 19470629;

    /** log4j logger (category) */
    private Logger log;

    /** No-argument constructor
     */
    public MorePage() {
        log = Logger.getLogger(MorePage.class.getName());
    } // constructor()
    
    /** Processes an http GET request
     *  @param request request with header fields
     *  @param response response with writer
     *  @param tableFactory factory for table serializers
     *  @throws IOException
     */
    public void forward(HttpServletRequest request, HttpServletResponse response, TableFactory tableFactory) {
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
            out.write("<style>\ntd,th\n{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;padding-right:10px;border:none;}\n</style>\n");
            out.write("<script src=\"script.js\" type=\"text/javascript\">\n</script>\n</head>\n");
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
            String specName     = "?";
            Map parameterMap = request.getParameterMap(); // do not! use <String, String[]>
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
                } else if (name.equals("spec"       )) {
                    specName = values[0];
                } else if (name.equals("view"       )) {
                    // ignore
                } else { // unknown parameter name - must be an input field
                    inputFields.append("<tr><td>");
                    inputFields.append(name);
                    inputFields.append("</td><td><input name=\"");
                    inputFields.append(name);
                    inputFields.append("\" value=\"");
                    inputFields.append(values[0]);
                    inputFields.append("\" /></td><td>&nbsp;</td></tr>\n");
                } // unknown
            } // while parmIter
            
            int index = 0;
            out.write("<body>\n<!--\nenc=\"" + encoding);
            out.write("\", mode=\"" + mode);
            out.write("\", lang=\"" + language);
            out.write("\", spec=\"" + specName);
            out.write("\" \n-->\n");
            //----------------------------------------
            out.write("<h3><a href=\"index.html\">Dbat</a>");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("-Spezifikation ");
            } else {
                out.write(" Specification ");
            }
            out.write("<em><a href=\"" + Messages.getViewSourceLink(request) 
                    + "spec/" + specName.replaceAll("\\.", "/") + ".xml\">"
                    + specName
                    +"</a></em>\n</h3>\n");
            //----------------------------------------
            out.write("<form action=\"servlet\" method=\"post\">\n");
            out.write("<input type = \"hidden\" name=\"view\" value=\"\" />\n");
            out.write("<input type = \"hidden\" name=\"spec\" value=\"" + specName + "\" />\n");

            out.write("<table cellpadding=\"8\"><tr><td class=\"bold\">\n");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Parametername");
            } else {
                out.write("Parameter Name");
            }
            out.write("\n</td><td class=\"bold\">\n");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Wert");
            } else {
                out.write("Value");
            }
            out.write("</td>\n<td>&nbsp;</td>\n</tr>\n");
            out.write(inputFields.toString());
            out.write("\n");
            out.write("<tr><td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n</tr>\n");
            //----------------------------------------
            out.write("<tr><td class=\"bold\">Encoding</td>\n<td class=\"bold\">\n");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Ausgabeformat");
            } else {
                out.write("Output Format");
            }
            out.write("</td>\n<td>&nbsp;</td>\n</tr>\n");
            //----------------------------------------
            out.write("<tr valign=\"top\">\n<td>\n<select name=\"enc\" size=\"");
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
            out.write("</select>\n<br />\n<br />\n");
            //----------------------------------------
            out.write("<span class=\"bold\">\n");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Sprache");
            } else {
                out.write("Language");
            }
            out.write("</span>\n<br />\n");
            out.write("<select name=\"lang\" size=\"");
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
            //----------------------------------------
            TreeMap<String,String> options = new TreeMap<String,String>();          
            Iterator<BaseTable> iter = tableFactory.getIterator();
            while (iter.hasNext()) {
                BaseTable tableFormat = (BaseTable) iter.next();
                String[] codes = tableFormat.getFormatCodes().split(",");
                int icode = 0;
                while (icode < codes.length) {
                    String code = codes[icode];
                    options.put(code
                          , "<option value=\"" + code + "\""
                          + (code.equals(mode) ? " selected=\"1\"" : "") + ">"
                          + code + " - " + tableFormat.getDescription(language) 
                          + "</option>\n");
                    icode ++;
                } // while icode
            } // while iter
            out.write("<td>\n<select name=\"mode\" size=\"" + String.valueOf(options.size()) + "\">\n");
            Iterator<String> oiter = options.keySet().iterator();
            while (oiter.hasNext()) {
                String key = oiter.next();
                out.write(options.get(key));
            } // while oiter
            out.write("</select><p />&nbsp;\n</td>\n");
            //----------------------------------------
            out.write("<td>\n");
            //----------------------------------------
            out.write("<a href=\"index.html\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Dbat-Startseite");
            } else {
                out.write("Dbat Home");
            }
            out.write("</a><br />\n");
            //----------------------------------------
            out.write("<a href=\"servlet?spec=index\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Liste</a> der abrufbaren Spezifikationen");
            } else {
                out.write("List</a> of available specifications");
            }
            out.write("<br />\n");
            //----------------------------------------
            out.write("<a href=\"servlet?view=con\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("SQL-Konsole");
            } else {
                out.write("SQL Console");
            }
            out.write("<br />\n");
            //----------------------------------------
            out.write("<a href=\"servlet?view=validate&value=M&regex=\\w\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Regex-Validierung</a>");
            } else {
                out.write("Regex Validation</a>");
            }
            out.write("<br />\n");
            //----------------------------------------
            out.write("<a href=\"servlet?spec=describe\">describe</a> - ");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("DDL einer Tabelle oder View");
            } else {
                out.write("DDL of a table or view");
            }
            out.write("\n<br />\n");
            //----------------------------------------
            out.write("<a href=\"servlet?view=help&lang=");
            out.write(language);
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("\">Hilfe</a> - Kommandozeilen-Optionen");
            } else {
                out.write("\">Help</a> - Commandline Options");
            }
            out.write("<br />\n");
            //----------------------------------------
            out.write("<a href=\"http://www.teherba.org/dbat\" target=\"_new\">Wiki</a>");
            out.write(", <a href=\"https://github.com/gfis/dbat\" target=\"_new\">Git Repository</a>");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write(" auf");
            } else {
                out.write(" on");
            }
            out.write(" github.com<br />\n");
            //----------------------------------------
            out.write("<a href=\"docs/api/index.html\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("API-Dokumentation</a>");
            } else {
                out.write("API Documentation</a>");
            }
            out.write(" (Javadoc)\n<br />\n");
            //----------------------------------------
            out.write("<a href=\"servlet?view=manifest\">Manifest</a>");
            out.write(", <a href=\"servlet?view=license\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Lizenz</a>");
            } else {
                out.write("License</a>");
            }
            out.write(", <a href=\"servlet?view=notice\"  >");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Referenzen</a>");
            } else {
                out.write("References</a>");
            }
            out.write("<br />\n");
            //----------------------------------------
            out.write("<input type=\"submit\" value=\"");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Absenden");
            } else {
                out.write("Submit");
            }
            out.write("\" />\n");
            //----------------------------------------
            out.write("</td></tr>\n</table>\n</form>\n");
            out.write("<p><span style=\"font-size:small\">\n");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Fragen, Hinweise:");
            } else {
                out.write("Questions, remarks:");
            }
            out.write(" <a href=\"mailto:punctum@punctum.com\">Dr. Georg Fischer</a></span>\n</p>\n");
            out.write("</body>\n</html>\n");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
        }
    } // forward

} // MorePage
