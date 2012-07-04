/*  HelpPage.java - show the language specific commandline help text
 *  @(#) $Id$
 *	2012-07-01: subpackage view
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
import  org.teherba.dbat.format.TableFactory;
import  java.io.PrintWriter;
import  java.util.Enumeration;
import  java.util.Iterator;
import  java.util.Map;
import  java.util.Properties;
import  java.util.TreeSet;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  javax.servlet.http.HttpSession;
import  org.apache.log4j.Logger;

/** This class prints the language-specific commandline help text.
 *	The code is extracted from the former <em>help.jsp</em>.
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
	} // constructor()
	
	/** Processes an http GET request
	 *  @param request request with header fields
	 *  @param response response with writer
	 *	@param tableFactory factory for table serializers
	 *  @throws IOException
	 */
	public void forward(HttpServletRequest request, HttpServletResponse response, TableFactory tableFactory) {
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

            out.write("<title>Dbat help</title>\n");
            out.write("<script src=\"script.js\" type=\"text/javascript\">\n</script>\n</head>\n");
            String[] optLang = new String []
	            	/*  0 */ { "de"
            	    /*  1 */ , "en"
    		        } ;
            String[] enLang = new String []
		            /*  0 */ { "Deutsch"
                    /*  1 */ , "English"
                    } ;
            String language = "en";
            Map parameterMap = request.getParameterMap(); // do not! use /*<1.5*/<String, String[]>/*1.5>*/
            Iterator /*<1.5*<String>*1.5>*/ parmIter = parameterMap.keySet().iterator();
            StringBuffer inputFields = new StringBuffer(256);
            while (parmIter.hasNext()) {
                String name = (String) parmIter.next();
                String[] values = request.getParameterValues(name);
                if (values.length <= 0) { // ignore empty value lists
                } else if (name.equals("lang"		)) {
                    language = values[0];
                }
            } // while parmIter
            out.write("\n<body>\n<!--\nlang=\"");
            out.write(language);
            out.write("\" \n-->\n<h3><a href=\"/index.html\">Dbat</a>");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("-Optionen auf der Kommandzeile");
            } else {
                out.write(" Commandline Options");
            }
            out.write("</h3>\n<pre>\n");
            out.write(Messages.getHelpText(language, tableFactory));
            out.write("\n</pre>\n<p><a href=\"servlet?view=more&lang=");
            out.write(language);
            out.write("\">");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("mehr ...");
            } else {
                out.write("more ...");
            }
            
            if (true) { // environment
                out.write("<!-- Environment:\n");
                Map/*<1.5*/<String, String>/*1.5>*/ env = System.getenv();
                TreeSet/*<1.5*/<String>/*1.5>*/ keys = new TreeSet/*<1.5*/<String>/*1.5>*/(env.keySet());
                Iterator/*<1.5*/<String>/*1.5>*/ iter = keys.iterator();
                while (iter.hasNext()) {
                    String key   = (String) iter.next();
                    String value = (String) env.get(key);
					out.write(key + "=" + value + "\n");
                } // while iter
                out.write(":Environment -->\n");
            } // environment

            if (true) { // Http session attributes
                out.write("<!-- SessionAttributes:\n");
	            HttpSession session = request.getSession();
                Enumeration attrs = session.getAttributeNames();                 
                while (attrs.hasMoreElements()) {
                	String  attr = (String) attrs.nextElement();
	                out.write(attr + "=" + session.getAttribute(attr) + "\n");
                } // while attrs
                out.write(":SessionAttributes -->\n");
			} // session attributes

            out.write("</a></p>\n");
            out.write("<span style=\"font-size:small\">\n");
            if (false) {
            } else if (language.startsWith("de")) {
                out.write("Fragen, Hinweise:");
            } else {
                out.write("Questions, remarks:");
            }
            out.write("\n<a href=\"mailto:punctum@punctum.com\">Dr. Georg Fischer</a>\n</span>\n</p>\n</body>\n</html>\n");
		} catch (Exception exc) {
			log.error(exc.getMessage(), exc);
		} finally {
		}
	} // forward

} // HelpPage
