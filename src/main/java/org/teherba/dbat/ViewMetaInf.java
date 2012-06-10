/*  ViewMetaInf.java - show meta data
 *  @(#) $Id$
 *  2012-02-11, Georg Fischer: copied from metaInf.jsp
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
package org.teherba.dbat;
import  java.io.BufferedReader;
import  java.io.InputStreamReader;
import  java.io.PrintWriter;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  org.apache.log4j.Logger;

/** This class prints the metadata for the application:
 *	<ul>
 *	<li>License</li>
 *	<li>JAR Manifest</li>
 *	<li>Notices for included software packages</li>
 *	</ul>
 *	The code is extracted from the former <em>metaInf.jsp</em>.
 *  @author Dr. Georg Fischer
 */
public class ViewMetaInf {
	public final static String CVSID = "@(#) $Id$";
	public final static long serialVersionUID = 19470629;

	/** log4j logger (category) */
	private Logger log;

	/** No-argument constructor
	 */
	public ViewMetaInf() {
		log = Logger.getLogger(ViewMetaInf.class.getName());
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

            out.write("<title>DB Administration Tool</title>\n");
            out.write("</head>\n<body>\n");
            String CVSID = "@(#) $Id$";
            String line = null;
            String fileName = null;
            String view = request.getParameter("view");
            if (view == null) {
                view = "manifest";
            }
            if (view.equals("package")) {
                // Package [] packs = this.getClass().getClassLoader().getPackages();
                Package [] packs = Package.getPackages();
                out.write("<tt>\n<pre>\n");
                for (int ipack = 0; ipack < packs.length; ipack ++) {
                    out.println(packs[ipack].getName());
                } // for ipack
                out.write("</pre>\n</tt>\n");
            } else {
                if (false) {
                } else if (view.equals("license")) {
                    out.write("<a name=\"license\" />\n<h3>License</h3>\n");
                    fileName = "LICENSE.txt";
                } else if (view.equals("notice")) {
                    out.write("<a name=\"notice\" />\n<h3>Included Software Packages</h3>\n");
                    fileName = "NOTICE.txt";
                } else if (view.equals("root")) {
                    out.write("<a name=\"Root Directory\" />\n<h3>License</h3>\n");
                    fileName = ".";
                } else { // if (view.equals("manifest")) {
                    out.write("<a name=\"manifest\" />\n<h3>JAR Manifest</h3>\n");
                    fileName = "META-INF/MANIFEST.MF";
                }
                out.write("\n<tt>\n<pre>\n");
                
                BufferedReader reader = new BufferedReader(new InputStreamReader
                (this.getClass().getClassLoader().getResourceAsStream(fileName)));
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                } // while
            } // not "package"
            out.write("</pre>\n</tt>\n<p>\nBack to the <a href=\"index.html\">DBAT input form</a>\n");
            out.write("<br />\nQuestions, remarks to: <a href=\"mailto:punctum@punctum.com\">Dr. Georg Fischer</a></p>");
            out.write("</body></html>\n");
		} catch (Exception exc) {
			log.error(exc.getMessage(), exc);
		} finally {
		}
	} // forward

} // ViewMetaInf
