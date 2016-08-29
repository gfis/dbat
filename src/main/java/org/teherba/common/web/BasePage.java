/*  BasePage.java - common code for web pages
 *  @(#) $Id$
 *  2016-08-29: writeAuxiliaryLinks
 *  2016-08-25, Georg Fischer
 */
/*
 * Copyright 2016 Dr. Georg Fischer <punctum at punctum dot kom>
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
package org.teherba.common.web;
import  java.io.PrintWriter;
import  java.util.HashMap;
import  java.util.regex.Pattern;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  javax.servlet.http.HttpSession;
import  org.apache.log4j.Logger;

/** This class contains common code for classes which output web pages.
 *  It stores the language-specific message texts,
 *  and prints the text for some numbered message.
 *  The message texts may contain up to 3 parameters.
 *  The following message numbers have a hard-coded interpretation:
 *  <ul>
 *  <li>001 - link to the application's main page</li>
 *  <li>301 - page redirection</li>
 *  <li>405 - unknown request parameter</li>
 *  <li>505 - invalid message number</li>
 *  </ul>
 *  @author Dr. Georg Fischer
 */
public class BasePage {
    public final static String CVSID = "@(#) $Id$";
    public final static long serialVersionUID = 19470629;

    /** log4j logger (category) */
    private Logger log;

    /** the response writer */
    protected PrintWriter out;
    /** (short) application name, for example "Dbat" */
    public    String appName;
    /** store the message patterns here */
    private   HashMap<String, String> hash;
    /** separator for message (hash) keys */
    private static final String SEP = ".";

    /** No-argument constructor
     */
    public BasePage() {
        this("Application");
    } // Constructor()

    /** Constructor with application name
     *  @param applicationName name of the application
     */
    public BasePage(String applicationName) {
        log      = Logger.getLogger(BasePage.class.getName());
        out      = null;
        hash     = new HashMap<String, String>(8);
        appName  = applicationName;
        BasePage basePage = this; // convenient for copy/paste
        //--------
        basePage.add("en", "405", "Unknown request parameter &amp;{parm}=\"{par2}\"");
        basePage.add("de", "405", "Unbekannter Request-Parameter &amp;{parm}=\"{par2}\"");
        //--------
        //
        basePage.add("en", "505", "System error: invalid message number <em>{parm}</em>");
        basePage.add("de", "505", "Systemfehler: Ung&uuml;ltige Meldungsnummer <em>{parm}</em>");
        //--------
    } // Constructor(1)

    /** Gets the application's (short) name
     *  @return "Dbat" for example
     */
    public String getAppName() {
        return appName;
    } // getAppName

    /** Sets the link to the application's main page into the text for error message "001"
     *  @param link HTML <em>a</em> element which point to the main page
     */
    public void setAppLink(String link) {
        BasePage basePage = this; // convenient for copy/paste
        //--------
        basePage.add("en", "001", link);
        basePage.add("de", "001", link);
    } // setAppLink

    /** Gets a session attribute or a default value
     *  @param session request session
     *  @param name name of the attribute
     *  @param defaultValue default value if the attribute is not present in the session
     *  @return a String value for the session attribute
     */
    public String getSessionAttribute(HttpSession session, String name, String defaultValue) {
        String result = (String) session.getAttribute(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    } // getSessionAttribute

    /** Gets the string value of an HTML input (parameter) field, and if it is not set, pass some default value
     *  @param request request for the HTML form
     *  @param name name of the input field
     *  @param defaultValue default value of the parameter
     *  @return non-null (but possibly empty) string value of the input field
     */
    public static String getInputField(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    } // getInputField(,,String)

    /** Gets the integer value of an HTML input (parameter) field, and if it is not set, pass some default value
     *  @param request request for the HTML form
     *  @param name name of the input field
     *  @param defaultValue default value of the parameter
     *  @return non-null (but possibly empty) string value of the input field
     */
    public static int    getInputField(HttpServletRequest request, String name, int defaultValue) {
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
    } // getInputField(,,int)

    /** Prints the start of the HTML page
     *  (XML declaration and beginning of <em>head</em> element,
     *  up to and excluding the <em>title</em> element)
     *  @param request  request with header fields
     *  @param response response with writer
     *  @param language 2-letter code en, de etc.
     *  @return the writer for the response
     */
    public PrintWriter writeHeader(HttpServletRequest request, HttpServletResponse response
            , String language
            ) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            out      = response.getWriter(); // side effect
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n");
            out.write("    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            out.write("<head>\n");
            out.write("<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml;charset=UTF-8\" />\n");
            out.write("<meta name=\"robots\" content=\"noindex, nofollow\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"stylesheet.css\" />\n");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return out;
    } // writeHeader

    /** Pseudo language code for a language-independant link to some auxiliary page */
    public static final String LANG_AUX = "<>";
    /** Start of message numbers for links to auxiliary pages */
    public static final int   START_AUX = 21;

    /** Prints a list of links to auxiliary information pages for the application
     *  @param language 2-letter code en, de etc.
     *  @param view <em>view</em> parameter in the Http request calling this method.
     *  <p>
     *  Assumes that {@link #out} is set by a previous call to {@link #writeHeader}.
     */
    public void writeAuxiliaryLinks(String language, String view) {
        try {
            int imess = START_AUX; // Link messages start here
            boolean busy = true;
            while (busy) {
                String link = this.get(LANG_AUX, String.format("%03d", imess));
                if (link == null) { // no more auxiliary pages
                    busy = false; // break loop
                } else {
                    if (link.indexOf("title=\"" + view + "\"") < 0) { // could skip over entry for calling page
                        String text = this.get(language, String.format("%03d", imess));
                        if (text != null) {
                            text = text.replaceAll(Pattern.quote("{parm}"), link);
                            out.write(text + "<br />\n");
                        } // text != null
                    } // not skipping
                } // != null
                imess ++;
            } // while imess
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeAuxiliaryLinks

    /** Prints the end of the HTML page
     *  @param language 2-letter code en, de etc.
     *  @param features empty String or a string of codes concatenated by "," or " ":
     *  <ul>
     *  <li>back - link back to the application's main page</lI>
     *  <li>quest - questions, remarks ...</li>
     *  </ul>
     *  Assumes that {@link #out} is set by a previous call to {@link #writeHeader}.
     */
    public void writeTrailer(String language, String features) {
        try {
            if (features.indexOf("back") >= 0) {
                out.write("<p>\n");
                if (false) {
                } else if (language.startsWith("de")) {
                    out.write("Zur&uuml;ck zur ");
                    out.write(this.get(language, "001")); // appLink
                    out.write("-Startseite");
                } else {
                    out.write("Back to the ");
                    out.write(this.get(language, "001")); // appLink
                    out.write(" main page");
                }
                out.write("</p>\n");
            } // back

            if (features.indexOf("quest") >= 0) {
                out.write("<p><span style=\"font-size:small\">\n");
                if (false) {
                } else if (language.startsWith("de")) {
                    out.write("Fragen, Hinweise: EMail an ");
                } else {
                    out.write("Questions, remarks: email to ");
                }
                out.write(" <a href=\"mailto:punctum@punctum.com"
                        + "?&subject=" + this.getAppName()
                        + "\">Dr. Georg Fischer</a>");
                out.write("</span></p>\n");
            } // quest

            // close the HTML document in any case
            out.write("</body></html>\n");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeTrailer

    /** Adds a message text under some key
     *  @param lang 2-letter language code, for example "en"
     *  @param messNo 3 digits message number
     *  @param text text of the message, may contain
     *  patterns "{parm}", "{par2}" and "{par3}" which are
     *  replaced by the values of the corresponding session attributes
     */
    public void add(String lang, String messNo, String text) {
        hash.put(lang + SEP + messNo, text);
    } // add(3)

    /** Adds a message text under some key
     *  @param key String of the form ll.nnn
     *  with 2-letter language code ll and 3 digits message number nnn
     *  @param text text of the message, may contain
     *  patterns "{parm}", "{par2}" and "{par3}" which are
     *  replaced by the values of the corresponding session attributes
     */
    public void add(String key, String text) {
        hash.put(key, text);
    } // add(2)

    /** Gets a message text for some key
     *  @param lang 2-letter language code, for example "en"
     *  @param messNo 3 digits message number
     *  @return text of the message, may contain patterns "{parm}", "{par2}" and "{par3}" which are
     *  replaced by the values of the corresponding session attributes
     */
    public String get(String lang, String messNo) {
        return hash.get(lang + SEP + messNo);
    } // get(2)

    /** Gets a message text for some key
     *  @param key String of the form ll.nnn with 2-letter language code and 3 digits message number
     *  @return text of the message, may contain patterns "{parm}", "{par2}" and "{par3}" which are
     *  replaced by the values of the corresponding session attributes
     */
    public String get(String key) {
        return hash.get(key);
    } // get(1)

    /** Output an error message with parameters obtained from the http session
     *  @param request request with header fields
     *  @param response response with writer
     *  @param language 2-letter code en, de etc.
     *  @param parms parameters for the message:
     *  <ul>
     *  <li>[0] = messNo, 3 digits message number</li>
     *  <li>[1] = replacement for {parm}</li>
     *  <li>[2] = replacement for {par2}</li>
     *  <li>[3] = replacement for {par3}</li>
     +  </ul>
     */
    public void writeMessage(HttpServletRequest request, HttpServletResponse response
            , String language
            , String[] parms
            ) {
        try {
            PrintWriter out = this.writeHeader(request, response, language);

            String messNo   = parms[0];
            String text     = this.get(language, messNo);
            if (text == null) { // invalid messNo
                String origMessNo = messNo;
                messNo = "505";
                text = this.get(language, messNo).replaceAll(Pattern.quote("{parm}"), origMessNo);
            } else { // text != null
                int ipar = 1;
                String qName = "{parm}";
                while (ipar < parms.length) {
                    text = text.replaceAll(Pattern.quote(qName), parms[ipar]);
                    ipar ++;
                    qName = "{par" + String.valueOf(ipar) + "}"; // par2, par3 ...
                } // while ipar
            } // text != null
            String messWord = null;
            if (false) {
            } else if (language.equals("de")) {
                messWord = "-Meldung";
            } else {
                messWord = " Message";
            }
            out.write("<title>" + this.getAppName() + messWord + " " + messNo + "</title>\n");
            if (messNo.equals("301")) {
                out.write("<meta http-equiv=\"refresh\" content=\"" + parms[3] + "; URL=" + parms[2] + "\" />\n");
            }
            out.write("</head>\n");
            out.write("<body>\n");
            out.write("<!--lang=" + language + ", messno=" + messNo + ", text=" + text + "-->\n");
            out.write("<h3>" + this.get(language, "001") + messWord + " " + messNo + ": "+ text + "</h3>\n");

            this.writeTrailer(language, "quest");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeMessage

} // BasePage
