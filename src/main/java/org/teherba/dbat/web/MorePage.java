/*  MorePage.java - replacement for more.jsp: input form with all parameters äöüÄÖÜ
 *  @(#) $Id$
 *  2018-01-19: console (comments)
 *  2017-05-27: javadoc
 *  2016-12-09: no script.js
 *  2016-10-11: IOException
 *  2016-08-29: many code moved to Messages.addErrorMessageTexts for basePage.writeAuxiliaryLinks
 *  2016-08-26: param BasePage
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
package org.teherba.dbat.web;
import  org.teherba.dbat.web.Messages;
import  org.teherba.dbat.Configuration;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.TableFactory;
import  org.teherba.common.web.BasePage;
import  java.io.IOException;
import  java.io.PrintWriter;
import  java.util.Iterator;
import  java.util.Map;
import  java.util.TreeMap;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  org.apache.logging.log4j.Logger;
import  org.apache.logging.log4j.LogManager;

/** This class prints a detailed form which contains all parameters
 *  and options for table output, together with links to 
 *  overview, API, notices and other documentation.
 *  <p>
 *  Moreover, another form for Java regular expression evaluation can be output.
 *  <p>
 *  The code is extracted from the former <em>more.jsp</em> and <em>validate.jsp</em>.
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
        log = LogManager.getLogger(MorePage.class.getName());
    } // Constructor()
    
    /** Shows the detailed input form for the activation of a Dbat specification
     *  @param request request with header fields
     *  @param response response with writer
     *  @param basePage refers to common web methods and messages
     *  @param language natural language for messages
     *  @param tableFactory factory for table serializers
     *  @param config pointer to Dbat's configuration 
     *  @throws IOException if an IO error occurs
     */
    public void showMore(HttpServletRequest request, HttpServletResponse response
            , BasePage basePage
            , String language
            , TableFactory tableFactory
            , Configuration config
            ) throws IOException {
        if (true) { // try {
            PrintWriter out = basePage.writeHeader(request, response, language);

            out.write("<title>Dbat more</title>\n");
            out.write("<style>\ntd,th\n{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;padding-right:10px;border:none;}\n</style>\n");
        //  out.write("<script src=\"script.js\" type=\"text/javascript\">\n</script>\n");
            out.write("</head>\n");
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
                    /*  2 */ , "fr"
                    } ;
            String[] enLang = new String []
                    /*  0 */ { "Deutsch"
                    /*  1 */ , "English"
                    /*  2 */ , "Français"
                    } ;
            String encoding     = "ISO-8859-1";
            String mode         = "html";
            String specName     = "?";

            // unknown inputFieldNames - cannot use BasePage.getInputField
            Map parameterMap = request.getParameterMap(); // do NOT! use <String, String[]>
            Iterator parmIter = parameterMap.keySet().iterator();
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
            out.write("<body>\n");
            out.write("<!--\n" + "enc=\"" + encoding + "\", mode=\"" + mode + "\", lang=\"" + language);
            out.write("\", spec=\"" + specName + "\" \n-->\n");
            //----------------------------------------
            out.write("<h3><a href=\"index.html\">Dbat</a>");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("-Spezifikation ");
            } else if (language.startsWith("fr")) {
                out.write(" Spécification ");
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
            } else if (language.startsWith("fr")) {
                out.write("Nom du paramètre");
            } else {
                out.write("Parameter Name");
            }
            out.write("\n</td><td class=\"bold\">\n");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Wert");
            } else if (language.startsWith("fr")) {
                out.write("Valeur");
            } else {
                out.write("Value");
            }
            out.write("</td>\n<td>&nbsp;</td>\n</tr>\n");
            out.write(inputFields.toString());
            out.write("\n");
            out.write("<tr><td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n</tr>\n");
            //----------------------------------------
            out.write("<tr><td class=\"bold\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Kodierung");
            } else if (language.startsWith("fr")) {
                out.write("Codage");
            } else {
                out.write("Encoding");
            }
            out.write("</td>\n<td class=\"bold\">\n");
            //----------------------------------------
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Ausgabeformat");
            } else if (language.startsWith("fr")) {
                out.write("Format de sortie");
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
            } else if (language.startsWith("fr")) {
                out.write("Langue");
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
            out.write(basePage.getOtherAuxiliaryLinks(language, "more"));
            out.write("<br /><input type=\"submit\" value=\"");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Absenden");
            } else if (language.startsWith("fr")) {
                out.write("Envoyer");
            } else {
                out.write("Submit");
            }
            out.write("\" />\n</td></tr>\n</table>\n</form>\n");
            //----------------------------------------
            
            basePage.writeTrailer(language, "quest");
        } // true / try
    } // showMore

    /** Shows the form for Java regular expression evaluation
     *  @param request request with header fields
     *  @param response response with writer
     *  @param basePage refers to common web methods and messages
     *  @param language natural language for messages
     */
    public void showValidate(HttpServletRequest request, HttpServletResponse response
            , BasePage basePage
            , String language
            ) {
        try {
            PrintWriter out = basePage.writeHeader(request, response, language);

            String value        = BasePage.getInputField(request, "value" , "M"         );
            String regex        = BasePage.getInputField(request, "regex" , ".+"        );
            String fieldName    = BasePage.getInputField(request, "name"  , ""          );
            int index = 0;
            out.write("<title>Dbat");
            if (false) {
            } else if (language.equals("de")) {
                out.write("-Validierungsregel-Test");
            } else if (language.equals("fr")) {
                out.write(" Test d'une règle de validation");
            } else {
                out.write(" Test of Validation Rules");
            }
            out.write("</title>\n");
            out.write("<style>\n\ttd,th\n");
            out.write("\t{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;");
            out.write("padding-right:10px;border:none;}\n</style>\n</head>\n");
            out.write("<body>\n");
            out.write("<!-- language=\"" + language + "\", fieldName=\"" + fieldName);
            out.write("\", regex=\"" + regex + "\" -->\n");
            out.write("<h2><a href=\"index.html\">Dbat</a> - ");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Validierungsregel-Test");
            } else if (language.equals("fr")) {
                out.write(" Test d'une règle de validation");
            } else {
                out.write("Test of Validation Rules");
            }
            out.write("</h2>\n");
            if (false) {
            } else if (language.equals("de")) {
                out.write("In den Feldern unten k&#xf6;nnen Sie den eingegeben Wert ver&#xe4;ndern und &#xfc;berpr&#xfc;fen,\n"
                        + "ob er dem angegebenen regul&#xe4;ren Ausdruck entspricht.\n");
            } else if (language.equals("fr")) {
                out.write("Dans les champs suivants vous pouvez modifier et vérifier une valeur\n"
                        + "si elle correspond à une éxpression régulière.\n");
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
            } else if (language.equals("fr")) {
                out.write("Éxpression régulière:");
            } else {
                out.write("Regular Expression:");
            }
            out.write("</td>\n<td><input name=\"regex\" class=\"valid\" maxsize=\"64\" size=\"16\" value=\"");
            out.write(regex);
            out.write("\" /></td>\n\t</tr>\n\t<tr><td>\n");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Eingabewert:");
            } else if (language.equals("fr")) {
                out.write("Valeur entrée:");
            } else {
                out.write("Input Value:");
            }
            out.write("</td>\n<td><input name=\"value\" class=\"valid\" maxsize=\"64\" size=\"16\" value=\"");
            out.write(value);
            out.write("\" /></td>\n</tr>\n<tr><td><input type=\"submit\" value=\"");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Pr&#xfc;fen");
            } else if (language.equals("fr")) {
                out.write("Vérifier");
            } else {
                out.write("Match");
            }
            out.write("\" />\n<span class=\"valid\"> &#xa0; -&gt; </span>\n\t</td>\n\t<td><span class=\"valid\"> ");
            out.write(String.valueOf(value.matches(regex)));
            out.write("</span></td>\n</tr>\n</table>\n</form>\n<p>\n");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Der Aufbau von regul&#xe4;ren Ausdr&#xfc;cken ist in der Java-API-Dokumentation beschrieben: Klasse");
            } else if (language.equals("fr")) {
                out.write("La construction d'expressions régulières est décrite dans la documentation de l'API Java: Classe");
            } else {
                out.write("Regular Expressions are described in the Java API documentation: class");
            }
            out.write(" <tt><a href=\"https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html\""
                    + ">java.util.regex.Pattern</a></tt>.\n</p>\n");
            if (false) {
            } else if (language.equals("de")) {
                out.write("Sonderzeichen und Spezialfunktionen werden mit einem"
                        + " <em>einfachen</em> Backslash - wie in Perl - entwertet.\n"
                        + "<br />Diese Darstellung wird f&#xfc;r das Attribut <tt>valid=\"...\"</tt>\n"
                        + "der XML-Elemente <tt>&lt;input&gt;, &lt;select&gt;</tt> und <tt>&lt;textarea&gt;</tt> in den\n"
                        + "<a href=\"index.html\">Dbat</a>-Spezifikationen verwendet.\n");
            } else if (language.equals("fr")) {
                out.write ( "Caractères spéciaux et des fonctions spéciales sont annulés avec un"
                        + " <em>single</em> backslash - comme en Perl.\n"
                        + "<br />Cette représentation est nécessaire pour l'attribut <tt>valid=\"...\"</tt>\n"
                        + "des éléments XML <tt>&lt;input&gt;, &lt;select&gt;</tt> and <tt>&lt;textarea&gt;</tt> dans les \n"
                        + "spécifications de <a href=\"index.html\">Dbat</a>.\n");
            } else {
                out.write("Special characters and functions are escaped with a"
                        + " <em>single</em> backslash, like in Perl.\n"
                        + "<br />\n\tThis representation is needed for the <tt>valid=\"...\"</tt> attribute\n"
                        + "of the XML elements <tt>&lt;input&gt;, &lt;select&gt;</tt> and <tt>&lt;textarea&gt;</tt> in\n"
                        + "<a href=\"index.html\">Dbat</a> specifications.\n");
            }
            
            basePage.writeTrailer(language, "quest");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // showValidate

} // MorePage
