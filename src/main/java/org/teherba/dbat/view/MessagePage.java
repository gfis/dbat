/*  MessagePage.java - show the language specific message text
 *  @(#) $Id$
 *	2012-07-01: subpackage view
 *  2012-02-11, Georg Fischer: copied from message.jsp
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
import  java.io.PrintWriter;
import  java.util.HashMap;
import  java.util.regex.Pattern;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  javax.servlet.http.HttpSession;
import  org.apache.log4j.Logger;

/** This class stores the language-specific message texts,
 *	and prints the text for some numbered message.
 *	The code is extracted from the former <em>help.jsp</em>.
 *  @author Dr. Georg Fischer
 */
public class MessagePage {
	public final static String CVSID = "@(#) $Id$";
	public final static long serialVersionUID = 19470629;

	/** log4j logger (category) */
	private Logger log;
	
	/** Map which stores the language-specific message text patterns.
	 *	The texts are stored under keys of the form ll.nnn, with a 
	 *	2-letter language code, and 3 digits for the message number.
	 *	The texts may contain patterns "{parm}" and "{par2}" which are
	 *	replaced by the values of up to 2 parameters.
	 */
	private HashMap<String, String> hash;

	/** No-argument constructor
	 */
	public MessagePage() {
		log = Logger.getLogger(MessagePage.class.getName());
		initialize();
	} // constructor()
	
	/** Initializes the hashmap for message texts
	 */
	public void initialize() {
        hash = new HashMap<String, String>();
        hash.put("en.301", "Specification file <em>{parm}</em> was moved to <em><a href=\"/dbat/servlet?spec={par2}\">{par2}</a></em>."
		        + "<br />Please update your bookmarks."
        		+ "<br />You will be redirected to the new page in {par3} s.");
        hash.put("de.301", "Die Spezifikationsdatei <em>{parm}</em> wurde nach <em><a href=\"/dbat/servlet?spec={par2}\">{par2}</a>"
		        + "</em> verschoben."
        		+ "<br />Bitte &auml;ndern Sie Ihre Favoriten/Lesezeichen."
		        + "<br />Sie werden in {par3} s auf die neue Seite umgelenkt.");
        hash.put("en.404", "A specification file <em>{parm}</em> was not found."
        		+ "<br />Please check the <a href=\"/dbat\">Dbat</a> home page.");
        hash.put("de.404", "Eine Spezifikationsdatei <em>{parm}</em> wurde nicht gefunden."
		        + "<br />Bitte rufen Sie die <a href=\"/dbat\">Dbat</a>-Startseite auf.");
        hash.put("en.505", "System error: invalid message number <em>{parm}</em>");
        hash.put("de.505", "Systemfehler: Ung&uuml;ltige Meldungsnummer <em>{parm}</em>");
	} // initialize
	
	/** Processes an http GET request
	 *  @param request request with header fields
	 *  @param response response with writer
	 *  @throws IOException
	 */
	public void forward(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String lang		= (String) session.getAttribute("lang");
        if (lang == null) {
            lang = "en";
        } // invalid language
        String messno   = (String) session.getAttribute("messno");
        if (messno == null) {
        	messno = "505";
        }
        String parm		= (String) session.getAttribute("parm");
        if (parm == null) {
        	parm = "";
        }
        String par2		= (String) session.getAttribute("par2");
        if (par2 == null) {
        	par2 = "";
        }
        String par3		= (String) session.getAttribute("par3");
        if (par3 == null) {
        	par3 = "";
        }
        String text = hash.get(lang + "." + messno);
        if (text == null) { // invalid messno
            text = hash.get(lang + "." + "505");
            text = text.replaceAll(Pattern.quote("{parm}"), messno);
            messno = "505";
        } else {
            text = text.replaceAll(Pattern.quote("{parm}"), parm);
            text = text.replaceAll(Pattern.quote("{par2}"), par2);
            text = text.replaceAll(Pattern.quote("{par3}"), par3);
        }

		try {
			PrintWriter out = response.getWriter();
 			response.setCharacterEncoding("UTF-8");
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

            out.write("<title>Message " + messno + "</title>\n");

            if (messno.equals("301")) {
            	String waitTime = (String) session.getAttribute("wait");
                text = text.replaceAll(Pattern.quote("{par2}"), par2);
                out.write("<meta http-equiv=\"refresh\" content=\"" + waitTime 
                		+ "; URL=/dbat/servlet?spec=" + par2 + "\" />\n");
            }
            out.write("</head>\n<body>\n<!--lang=" + lang);
            out.write(", messno=" + messno);
            out.write(", text=" + text);
            out.write(", parm=" + parm);
            out.write(", par2=" + par2);
            out.write(", par3=" + par3);
            out.write("-->\n<h3>");
            out.write(messno);
            out.write(": ");
            out.write(text);
            out.write("</h3>\n");
            out.write("</body></html>\n");
		} catch (Exception exc) {
			log.error(exc.getMessage(), exc);
		} finally {
		}
	} // forward

} // MessagePage
