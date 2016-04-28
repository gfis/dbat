/*  ValidatePage.java - test form for regular expressions
 *  @(#) $Id$
 *  2012-07-01: subpackage view
 *  2012-02-11, Georg Fischer: copied from help.jsp
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
import  java.io.PrintWriter;
import  java.util.Iterator;
import  java.util.Map;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  org.apache.log4j.Logger;

/** This class prints a form where Java regular expressions
 *  can be tested.
 *  The code is extracted from the former <em>validate.jsp</em>.
 *  @author Dr. Georg Fischer
 */
public class ValidatePage {
    public final static String CVSID = "@(#) $Id$";
    public final static long serialVersionUID = 19470629;

    /** log4j logger (category) */
    private Logger log;

    /** No-argument constructor
     */
    public ValidatePage() {
        log = Logger.getLogger(ValidatePage.class.getName());
    } // constructor()
    
    /** Processes an http GET request
     *  @param request request with header fields
     *  @param response response with writer
     *  @throws IOException
     */
    public void forward(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/xhtml+xml;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n");
            out.write("    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            out.write("<head>\n");
            out.write("<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml;charset=UTF-8\" />\n");
            out.write("<meta name=\"robots\" content=\"noindex, nofollow\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"stylesheet.css\" />\n");

            String value        = "M";
            String regex        = ".+";
            String language     = "en";
            String fieldName    = "";
            Map parameterMap = request.getParameterMap(); // do not! use /*<1.5*/<String, String[]>/*1.5>*/
            Iterator /*<1.5*<String>*1.5>*/ parmIter = parameterMap.keySet().iterator();
            StringBuffer inputFields = new StringBuffer(256);
            while (parmIter.hasNext()) {
                String name = (String) parmIter.next();
                String[] values = request.getParameterValues(name);
                if (values.length <= 0) { // ignore empty value lists
                } else if (name.equals("lang"       )) {
                    language    = values[0];
                } else if (name.equals("name"       )) {
                    fieldName   = values[0];
                } else if (name.equals("regex"      )) {
                    regex       = values[0];
                } else if (name.equals("value"      )) {
                    value       = values[0];
                }
            } // while parmIter
            int index = 0;
            out.write("<title>\n\t");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Validierungsregel-Test");
            } else {
                out.write("Test of Validation Rules");
            }
            out.write("</title>\n");
            out.write("<style>\n\ttd,th\n");
            out.write("\t{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;");
            out.write("padding-right:10px;border:none;}\n</style>\n</head>\n<body>\n<!-- language=\"");
            out.write(language);
            out.write("\", fieldName=\"");
            out.write(fieldName);
            out.write("\", regex=\"");
            out.write(regex);
            out.write("\", regex=\"");
            out.write(regex);
            out.write("\" -->\n<h2><a href=\"index.html\">Dbat</a> - ");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Validierungsregel-Test");
            } else {
                out.write("Test of Validation Rules");
            }
            out.write("</h2>\n");
            if (false) {
            } else if (language.equals("de")) {
                out.write("In den Feldern unten k&#xf6;nnen Sie den eingegeben Wert ver&#xe4;ndern und &#xfc;berpr&#xfc;fen,\n"
                        + "ob er dem angegebenen regul&#xe4;ren Ausdruck entspricht.\n");
            } else {
                out.write("In the fields below you may modify the entered value, and you may check whether it conforms\n"
                        + "to the specified regular expression.\n");
            }
            out.write("<form action=\"servlet\" method=\"post\">\n");
            out.write("<input type = \"hidden\" name=\"view\" value=\"validate\" />\n");
            out.write("<input type = \"hidden\" name=\"lang\" value=\"" + language + "\" />\n");
            out.write("<input type = \"hidden\" name=\"name\" value=\"" + fieldName + "\" />\n");
            out.write("<table cellpadding=\"8\">\n<tr><td>\n");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Regul&#xe4;r Ausdruck:");
            } else {
                out.write("Regular Expression:");
            }
            out.write("</td>\n<td><input name=\"regex\" class=\"valid\" maxsize=\"64\" size=\"16\" value=\"");
            out.write(regex);
            out.write("\" /></td>\n\t</tr>\n\t<tr><td>\n");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Eingabewert:");
            } else {
                out.write("Input Value:");
            }
            out.write("</td>\n<td><input name=\"value\" class=\"valid\" maxsize=\"64\" size=\"16\" value=\"");
            out.write(value);
            out.write("\" /></td>\n</tr>\n<tr><td><input type=\"submit\" value=\"");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Pr&#xfc;fen");
            } else {
                out.write("Match");
            }
            out.write("\" />\n<span class=\"valid\"> &#xa0; -&gt; </span>\n\t</td>\n\t<td><span class=\"valid\"> ");
            out.write(String.valueOf(value.matches(regex)));
            out.write("</span></td>\n</tr>\n</table>\n</form>\n<p>\n");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Der Aufbau von regul&#xe4;ren Ausdr&#xfc;cken ist in der Java-API-Dokumentation beschrieben: Klasse");
            } else {
                out.write("Regular Expressions are described in the Java API documentation: class");
            }
            out.write(" <tt><a href=\"http://docs.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html\""
                    + ">java.util.regex.Pattern</a></tt>.\n</p>\n");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Sonderzeichen und Spezialfunktionen werden mit einem <em>einfachen</em> Backslash"
                        + " - wie in Perl - entwertet.\n"
                        + "<br />Diese Darstellung wird f&#xfc;r das Attribut <tt>valid=\"...\"</tt>\n"
                        + "der XML-Elemente <tt>&lt;input&gt;, &lt;select&gt;</tt> und <tt>&lt;textarea&gt;</tt> in den\n"
                        + "<a href=\"index.html\">Dbat</a>-Spezifikationen verwendet.\n");
            } else {
                out.write("Special characters and functions are escaped with a <em>single</em> backslash, like in Perl.\n"
                        + "<br />\n\tThis representation is needed for the <tt>valid=\"...\"</tt> attribute\n"
                        + "of the XML elements <tt>&lt;input&gt;, &lt;select&gt;</tt> and <tt>&lt;textarea&gt;</tt> in\n"
                        + "<a href=\"index.html\">Dbat</a> specifications.\n");
            }
            out.write("</body></html>\n");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
        }
    } // forward

} // ValidatePage
