/*  HelpPage.java - show the language specific commandline help text
 *  @(#) $Id$
 *  2017-05-27: javadoc
 *  2016-10-13: less imports
 *  2016-08-26: param BasePage
 *  2016-04-11: link to index.html was not relative
 *  2012-11-22: disable comment output for environment and session attributes
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
package org.teherba.dbat.web;
import  org.teherba.dbat.web.Messages;
import  org.teherba.dbat.format.TableFactory;
import  org.teherba.common.web.BasePage;
import  java.io.IOException;
import  java.io.PrintWriter;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  org.apache.log4j.Logger;

/** This class prints the language-specific commandline help text.
 *  The code is extracted from the former <em>help.jsp</em>.
 *  @author Dr. Georg Fischer
 */
public class HelpPage {
    public final static String CVSID = "@(#) $Id$";
    public final static long serialVersionUID = 19470629;

    /** log4j logger (category) */
    private Logger log;

    /** No-argument constructor
     */
    public HelpPage() {
        log = Logger.getLogger(HelpPage.class.getName());
    } // Constructor()

    /** Shows usage information for the commandline utility
     *  @param request request with header fields
     *  @param response response with writer
     *  @param basePage refers to common web methods and messages
     *  @param language 2-letter code en, de etc.
     *  @param tableFactory factory for table serializers
     *  @throws IOException if an IO error occurs
     */
    public void showHelp(HttpServletRequest request, HttpServletResponse response
            , BasePage basePage
            , String language
            , TableFactory tableFactory
            ) throws IOException {
        if (true) { // try {
            PrintWriter out = basePage.writeHeader(request, response, language);

            out.write("<title>Dbat help</title>\n");
            out.write("<script src=\"script.js\" type=\"text/javascript\">\n</script>\n");
            out.write("</head>\n");
            String[] optLang = new String []
                    /*  0 */ { "de"
                    /*  1 */ , "en"
                    } ;
            String[] enLang = new String []
                    /*  0 */ { "Deutsch"
                    /*  1 */ , "English"
                    } ;
            out.write("<body>\n");
            out.write("<!--lang=\"" + language + "\"-->\n");
            out.write("<h3><a href=\"index.html\">Dbat</a>");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("-Optionen auf der Kommandzeile");
            } else {
                out.write(" Commandline Options");
            }
            out.write("</h3>\n");
            out.write("<pre>\n");
            out.write(Messages.getHelpText(language, tableFactory));
            out.write("\n</pre>\n");

            out.write("<p><a href=\"servlet?view=more&lang=" + language + "\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("mehr ...");
            } else {
                out.write("more ...");
            }
            out.write("</a></p>\n");
            basePage.writeTrailer(language, "quest");
    /*
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
    */
        }
    } // showHelp

} // HelpPage
