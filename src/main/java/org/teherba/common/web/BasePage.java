/*  BasePage.java - common code for web pages äöüÄÖÜß
 *  @(#) $Id$
 *  2016-12-10: french message texts
 *  2016-10-13: less imports
 *  2016-09-21: stylesheet.css with title="common" attribute for gramword css switching
 *  2016-09-15: getFormFieldCount, getFormIterator
 *  2016-09-12: saveViewParameters
 *  2016-09-02: auxiliary links on same line when ending with space
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
import  java.io.IOException;
import  java.io.PrintWriter;
import  java.util.ArrayList;
import  java.util.TreeMap;
import  java.util.Iterator;
import  java.util.List;
import  java.util.regex.Pattern;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  javax.servlet.http.HttpSession;
import  org.apache.log4j.Logger;
import  org.apache.commons.fileupload.FileItem;
import  org.apache.commons.fileupload.FileItemFactory;
import  org.apache.commons.fileupload.disk.DiskFileItemFactory;
import  org.apache.commons.fileupload.servlet.ServletFileUpload;

/** This class contains common code for classes which output web pages.
 *  It stores the language-specific (message) text fragments,
 *  and prints the text for some numbered message.
 *  The message texts may contain up to 9 parameters.
 *  The following message numbers have a hard-coded interpretation:
 *  <ul>
 *  <li>001 - link to the application's main page</li>
 *  <li>021 ... - links for auxiliary pages</li>
 *  <li>301 - page redirection</li>
 *  <li>401 - invalid value</li>
 *  <li>405 - unknown request parameter</li>
 *  <li>505 - invalid message number</li>
 *  <li>901 ... - internationalized text fragments for web interface</li>
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
    private   String appName;
    /** Stores the message patterns */
    private   TreeMap<String, String> textMap;
    /** Stores the view parameters */
    private   TreeMap<String, String> formMap;
    /** Stores the file items of an Http multipart request */
    private   FileItem[] fileItems;
    /** separator for message (textMap) keys */
    private static final String SEP = ".";
    /** Pseudo language code for a language-independant link to some auxiliary page */
    public static final String LANG_AUX  = "<>";
    /** Start of message numbers for links to auxiliary pages */
    public static final int    START_AUX = 21;
    /** End   of message numbers for links to auxiliary pages */
    public static final int    END_AUX   = 99;

    /** No-argument constructor
     */
    public BasePage() {
        this("Application");
    } // Constructor()

    /** Constructor with application name
     *  @param applicationName name of the application
     */
    public BasePage(String applicationName) {
        log       = Logger.getLogger(BasePage.class.getName());
        out       = null;
        textMap   = new TreeMap<String, String>();
        formMap   = new TreeMap<String, String>();
        fileItems = new FileItem[] {};
        appName   = applicationName;
        BasePage basePage = this; // convenient for copy/paste
        //--------
        basePage.add("en", "401", "{parm}: invalid value <em>{par2}</em>");
        basePage.add("de", "401", "{parm}: Wert <em>{par2}</em> ist ungültig");
        basePage.add("fr", "401", "{parm}: valeur <em>{par2}</em> est invalide");
        //--------
        basePage.add("en", "405", "Unknown request parameter &amp;{parm}=\"{par2}\"");
        basePage.add("de", "405", "Unbekannter Request-Parameter &amp;{parm}=\"{par2}\"");
        basePage.add("fr", "405", "Paramètre de request inconnu &amp;{parm}=\"{par2}\"");
        //--------
        basePage.add("en", "505", "System error: invalid message number <em>{parm}</em>");
        basePage.add("de", "505", "Systemfehler: Ung&uuml;ltige Meldungsnummer <em>{parm}</em>");
        basePage.add("fr", "505", "Erreur de système: numéro de message non valide <em>{parm}</em>");
        //--------
        basePage.add("en", "901", " Message");
        basePage.add("de", "901", "-Meldung");
        basePage.add("fr", "901", " Message");
        //--------
        basePage.add("en", "902", "Back to the {parm} main page");
        basePage.add("de", "902", "Zurück zur {parm}-Startseite");
        basePage.add("fr", "902", "Retour à la page d'accueil de {parm}");
        //--------
        basePage.add("en", "903", "Questions, remarks: email to {parm}");
        basePage.add("de", "903", "Fragen, Hinweise: EMail an {parm}");
        basePage.add("fr", "903", "Questions, remarques: e-mail à {parm}");
        //--------
    } // Constructor(1)

    /** Stores the standard links to auxiliary information pages for the application
     *  @param imess starting message number
     *  @return resulting new value of <em>imess</em>
     */
    public int addStandardLinks(int imess) {
        BasePage basePage = this; // convenient for copy/paste
        String appLink = basePage.get("en", "001");
        String appLowerName = getAppName().toLowerCase();
        String laux = basePage.LANG_AUX;  // pseudo language code for links to auxiliary information
        String
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, appLink);
        basePage.add("en", smess, appLink + " Home");
        basePage.add("de", smess, appLink + "-Startseite");
        basePage.add("fr", smess, "Page d'accueil de " + appLink);
    /*
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"help\"        href=\"servlet?view=help&lang=en\">");
        basePage.add("en", smess, "{parm}Help</a> - Commandline Options");
        basePage.add("de", smess, "{parm}Hilfe</a> - Kommandozeilen-Optionen");
        basePage.add("fr", smess, "{parm}Aide</a> - options de l'interface en ligne de commande");
    */
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"wiki\"        href=\"http://www.teherba.org/index.php/" 
                + getAppName() + "\" target=\"_new\">");
        basePage.add("en", smess, "{parm}Wiki</a> Documentation");
        basePage.add("de", smess, "{parm}Wiki</a>-Dokumentation");
        basePage.add("fr", smess, "{parm}Wiki</a> Documentation");
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"github\"      href=\"https://github.com/gfis/"
                + appLowerName + "\" target=\"_new\">");
        basePage.add("en", smess, "{parm}Git Repository</a>");
        basePage.add("de", smess, "{parm}Git Repository</a>");
        basePage.add("fr", smess, "{parm}Dépot Git</a>");
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"api\"         href=\"docs/api/index.html\">");
        basePage.add("en", smess, "{parm}Java API</a> Documentation");
        basePage.add("de", smess, "{parm}Java API</a>-Dokumentation");
        basePage.add("fr", smess, "{parm}Java API</a> Documentation");

        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"manifest\"    href=\"servlet?view=manifest&lang={parm}\">");
        basePage.add("en", smess, "{parm}Manifest</a>, ");
        basePage.add("de", smess, "{parm}Manifest</a>, ");
        basePage.add("fr", smess, "{parm}Manifest</a>, ");
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"license\"     href=\"servlet?view=license&lang={parm}\">");
        basePage.add("en", smess, "{parm}License</a>, ");
        basePage.add("de", smess, "{parm}Lizenz</a>, ");
        basePage.add("fr", smess, "{parm}Licence</a>, ");
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"notice\"      href=\"servlet?view=notice&lang={parm}\">");
        basePage.add("en", smess, "{parm}References</a>");
        basePage.add("de", smess, "{parm}Referenzen</a>");
        basePage.add("fr", smess, "{parm}Références</a>");
        //--------
        return imess;
    } // addStandardLinks

    /** Prints a list of links to auxiliary information pages for the application
     *  @param language 2-letter code en, de etc.
     *  @param view <em>view</em> parameter in the Http request calling this method.
     *  <p>
     *  Assumes that {@link #out} is set by a previous call to {@link #writeHeader}.
     *  Deprecated, use <em>out.write(getOtherAuxiliaryLinks(language, view);</em> instead.
     */
    public void writeAuxiliaryLinks(String language, String view) throws IOException {
        out.write(getOtherAuxiliaryLinks(language, view));
    } // writeAuxiliaryLinks

    /** Gets a list of links to auxiliary information pages for the application,
     *  but exclude the one for <em>view</em> (or take only that one for <em>=view</em>).
     *  @param language 2-letter code en, de etc.
     *  @param view <em>view</em> parameter in the Http request calling this method.
     */
    public String getOtherAuxiliaryLinks(String language, String view) {
        StringBuffer result = new StringBuffer(512);
        boolean other = ! view.startsWith("="); // whether all other links should be concatenated
        if (! other) {
            view = view.substring(1); // remove leading "="
        }
        int imess = START_AUX; // Link messages start here
        boolean busy = true;
        while (busy && imess <= END_AUX) { // loopcheck
            String link = getReplacedText(LANG_AUX, new String[]
                    { String.format("%03d", imess), language });
            if (link == null) { // no more auxiliary pages
                busy = false; // break loop
            } else {
                if (link.indexOf("title=\"" + view + "\"") * (other ? 1 : -1) < 0) { 
                    // other: < 0 if not found; 
                    // not other: < 0 if found since there is always "<a " before
                    String text = getReplacedText(language, new String[]
                         { String.format("%03d", imess), link });
                    if (text.endsWith(" ")) {
                        if (! other) {
                            text = text.replaceAll("\\,\\s+\\Z", "");
                        }
                    } else if (other) {
                        text += "<br />\n";
                    } 
                    result.append(text);
                } // not skipping
            } // != null
            imess ++;
        } // while imess
        return result.toString();
    } // getOtherAuxiliaryLinks

    /** Gets a specific link to an auxiliary information page for the application.
     *  A trailing comma is removed. 
     *  @param language 2-letter code en, de etc.
     *  @param view <em>view</em> parameter in the Http request calling this method.
     */
    public String getAuxiliaryLink(String language, String view) {
        return getOtherAuxiliaryLinks(language, "=" + view);
    } // getAuxiliaryLink

    /** Prints the end of the HTML page
     *  @param language 2-letter code en, de etc.
     *  @param features empty String or a string of codes concatenated by "," or " ":
     *  <ul>
     *  <li>back - link back to the application's main page</lI>
     *  <li>quest - questions, remarks ...</li>
     *  </ul>
     *  Assumes that {@link #out} is set by a previous call to {@link #writeHeader}.
     */
    public void writeTrailer(String language, String features) throws IOException {
        if (true) { // try {
            out.write("<!-- language=\"" + language + "\", features=\"" + features + "\" -->\n");
            if (features.indexOf("back") >= 0) {
                out.write("<p>\n");
                out.write(this.getReplacedText(language, new String[] 
                        { "902"
                        , getReplacedText(language, new String[] { "001", language }) 
                        } ));
                out.write("</p>\n");
            } // back

            if (features.indexOf("quest") >= 0) {
                out.write("<p><span style=\"font-size:small\">\n");
                out.write(this.getReplacedText(language, new String[] 
                        { "903"
                        , this.getReplacedText(language, new String[] { "002" }) // mailto: link
                        } ));
                out.write("</span></p>\n");
            } // quest

            // close the HTML document in any case
            out.write("</body></html>\n");
    /*
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
    */
        }
    } // writeTrailer

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
            ) throws IOException {
        if (true) { // try {
            PrintWriter out = this.writeHeader(request, response, language);
            String messNo   = parms[0];
            String text     = this.getReplacedText(language, parms);
            String messWord = this.getReplacedText(language, new String[] { "901" });
            out.write("<title>" + this.getAppName() + messWord + " " + messNo + "</title>\n");
            if (messNo.equals("301")) {
                out.write("<meta http-equiv=\"refresh\" content=\"" + parms[3] + "; URL=" + parms[2] + "\" />\n");
            }
            out.write("</head>\n");
            out.write("<body>\n");
            out.write("<!--lang=" + language + ", messno=" + messNo + ", text=" + text + "-->\n");
            out.write("<h3>" + this.get(language, "001") + messWord + " " + messNo + ": "+ text + "</h3>\n");
            this.writeTrailer(language, "quest");
    /*
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
    */
        }
    } // writeMessage

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

    /** Saves the request parameters in the internal map {@link #formMap}.
     *  The values are taken from Http GET/POST request fields,
     *  or from the {@link FileItem}s of a multipart request.
     *  As side effects, the String fields are stored in {@link #formMap}, and
     *  the file items are stored in {@link #fileItems}.
     *  @param request request with header fields
     *  @param pairs pairs of Strings for expected request parameter fields: (field name, default value)
     *  @return the value of any "view" field, or <em>null</em> if there is none
     */
    public String getFilesAndFields(HttpServletRequest request, String[] pairs) throws IOException {
        formMap = new TreeMap<String, String>();
        ArrayList<FileItem> fitemArray = new ArrayList<FileItem>(4);
        int ipair = 0;
        String name  = null;
        String value = null;
        while (ipair < pairs.length) {
            formMap.put(pairs[ipair], pairs[ipair + 1]);
            ipair += 2;
        } // while ipair
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (! isMultipart) {
            ipair = 0;
            while (ipair < pairs.length) {
                name  = pairs[ipair ++];
                value = pairs[ipair ++]; // default
                String formValue = request.getParameter(name);
                if (formValue != null) { // replace default
                    value = formValue;
                }
                formMap.put(name, value);
            } // while ipair
        } else { // multipart
            ipair = 0;
            while (ipair < pairs.length) { // save all default values
                formMap.put(pairs[ipair], pairs[ipair + 1]);
                ipair += 2;
            } // while ipair
            try {
                FileItemFactory fuFactory = new DiskFileItemFactory(); // Create a factory for disk-based file items
                ServletFileUpload upload  = new ServletFileUpload(fuFactory); // Create a new file upload handler
                List<FileItem> formItems = upload.parseRequest(request); // Parse the request
                Iterator<FileItem> fiter = formItems.iterator();
                boolean busy = true; // as long as there is no error
                while (busy && fiter.hasNext()) { // Process the uploaded items
                    FileItem fitem = fiter.next();
                    if (fitem.isFormField()) {
                        name  = fitem.getFieldName();
                        value = fitem.getString();
                        String viewDefault = formMap.get(name);
                        if (false && viewDefault == null) { // this name is not an expected form field name
                            busy = false;
                            formMap.put("view",   "error");
                            formMap.put("messno", "405");
                            formMap.put("parm",   name);
                            formMap.put("par2",   value);
                        } else { // expected field
                            formMap.put(name, value);
                        }
                    } else { // no formField - uploaded fileItem
                        fitemArray.add(fitem);
                        // uploaded file contents
                    }
                } // while uploaded items
            } catch (Exception exc) {
                log.error(exc.getMessage(), exc);
                throw new IOException(exc.getMessage());
            }
        } // multipart
        fileItems = fitemArray.toArray(new FileItem[] {});
        return formMap.get("view");
    } // getFilesAndFields

    /** Gets the String value of a form field obtained by a previous call of
     *  {@link #getFilesAndFields}.
     *  @param name name of the desired field
     *  @return String value of the field
     */
    public String getFormField(String name) {
        return formMap.get(name);
    } // getFormField

    /** Gets a {@link FileItem} obtained by a previous call of
     *  {@link #getFilesAndFields}.
     *  @param fileNo sequential number of the file in the form, starting at 0
     *  @return FileItem for the file if there was one in the request, or null otherwise
     */
    public FileItem getFormFile(int fileNo) {
        return fileNo >= 0 && fileNo < fileItems.length ? fileItems[fileNo] : null;
    } // getFormFile

    /** Gets the number of files obtained by a previous call of
     *  {@link #getFilesAndFields}.
     *  @return the number of {@link FileItem}s, maybe 0
     */
    public int getFormFileCount() {
        return fileItems.length;
    } // getFormFileCount

    /** Gets an Iterator over the names of the form's fields obtained by a previous call of
     *  {@link #getFilesAndFields}.
     *  @return an Iterator over {@link #formMap}
     */
    public Iterator<String> getFormIterator() {
        return formMap.keySet().iterator();
    } // getFormIterator

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
            ) throws IOException {
        if (true) { // try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter(); // side effect
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n");
            out.write("    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            out.write("<head>\n");
            out.write("<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml;charset=UTF-8\" />\n");
            out.write("<meta name=\"robots\" content=\"noindex, nofollow\" />\n");
            out.write("<link rel=\"stylesheet\" title=\"common\" type=\"text/css\" href=\"stylesheet.css\" />\n");
    /*
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
    */
        }
        return out;
    } // writeHeader

    /** Gets the application's (short) name
     *  @return "Dbat" for example
     */
    public String getAppName() {
        return appName;
    } // getAppName

    /** Sets the link to the application's main page into the text for error message "001"
     *  @param link HTML <em>a</em> element which point to the main page
     */
    public void setAppLink_99(String link) {
        BasePage basePage = this; // convenient for copy/paste
        //--------
        basePage.add("en", "001", link);
        basePage.add("de", "001", link);
        basePage.add("fr", "001", link);
    } // setAppLink_99

    /** Adds a message text under some key
     *  @param lang 2-letter language code, for example "en"
     *  @param messNo 3 digits message number
     *  @param text text of the message, may contain
     *  patterns "{parm}", "{par2}" and "{par3}" which are
     *  replaced by the values of the corresponding session attributes
     */
    public void add(String lang, String messNo, String text) {
        textMap.put(lang + SEP + messNo, text);
    } // add(3)

    /** Adds a message text under some key
     *  @param key String of the form ll.nnn
     *  with 2-letter language code ll and 3 digits message number nnn
     *  @param text text of the message, 
     *  may contain patterns "{parm}", "{par2}" and "{par3}" which are
     *  replaced by the values of the corresponding session attributes
     */
    public void add(String key, String text) {
        textMap.put(key, text);
    } // add(2)

    /** Gets a message text for some key
     *  @param lang 2-letter language code, for example "en" (fallback)
     *  @param messNo 3 digits message number
     *  @return text of the message, 
     *  may contain patterns "{parm}", "{par2}" and "{par3}" which are
     *  replaced by the values of the corresponding session attributes
     */
    public String get(String lang, String messNo) {
        String result = textMap.get(lang + SEP + messNo);
        if (result == null) {
            result = textMap.get("en" + SEP + messNo);
        }
        return result;
    } // get(2)

    /** Gets a message text for some key
     *  @param key String of the form ll.nnn with 2-letter language code and 3 digits message number
     *  @return text of the message, may contain patterns "{parm}", "{par2}" and "{par3}" which are
     *  replaced by the values of the corresponding session attributes
     */
    public String get(String key) {
        return textMap.get(key);
    } // get(1)

    /** Retrieves a (message) text and replaces the parameters
     *  @param language 2-letter code "en", "de" etc.
     *  @param parms message number and up to 9 parameters for the text:
     *  <ul>
     *  <li>[0] = 3 digits message number</li>
     *  <li>[1] = replacement for {parm}</li>
     *  <li>[2] = replacement for {par2}</li>
     *  <li>...</li>
     *  <li>[9] = replacement for {par9}</li>
     +  </ul>
     */
    public String getReplacedText(String language, String[] parms) {
        String messNo   = parms[0];
        String result  = this.get(language, messNo);
        if (result == null && ! language.equals(LANG_AUX)) { // invalid messNo or language
            String origMessNo = messNo;
            messNo = "505";
            result = this.get(language, messNo).replaceAll(Pattern.quote("{parm}"), origMessNo);
        } else if (result != null) { // result != null or LANG_AUX
            int ipar = 1;
            String qName = "{parm}";
            while (ipar < parms.length && ipar <= 9) {
                result = result.replaceAll(Pattern.quote(qName), parms[ipar]);
                ipar ++;
                qName = "{par" + String.valueOf(ipar) + "}"; // par2, par3 ...
            } // while ipar
        } //  result != null
        return result;
    } // getReplacedText

} // BasePage
