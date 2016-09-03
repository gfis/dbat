/*  MetaInfPage.java - show meta data
 *  @(#) $Id$
 *  2016-09-03: pass servlet via session
 *  2016-08-26: package independant; param BasePage
 *  2012-07-01: subpackage view
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
package org.teherba.common.web;
import  org.teherba.common.web.BasePage;
import  java.io.BufferedReader;
import  java.io.File;
import  java.io.InputStreamReader;
import  java.io.PrintWriter;
import  java.net.URL;
import  java.util.Enumeration;
import  javax.servlet.http.HttpServlet;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  org.apache.log4j.Logger;

/** This class prints metadata for the application:
 *  <ul>
 *  <li>License,</li>
 *  <li>JAR Manifest</li>
 *  <li>Notices for included software packages</li>
 *  </ul>
 *  The relevant information is read from the JAR file.
 *  @author Dr. Georg Fischer
 */
public class MetaInfPage {
    public final static String CVSID = "@(#) $Id$";
    public final static long serialVersionUID = 19470629;

    /** log4j logger (category) */
    private Logger log;

    /** No-argument constructor
     */
    public MetaInfPage() {
        log = Logger.getLogger(MetaInfPage.class.getName());
    } // Constructor()
    
    /** Shows meta information for the application. 
     *  The manifest and other files are read from the JAR file.
     *  @param request request with header fields
     *  @param response response with writer
     *  @param basePage refers to common web methods and messages
     *  @param language 2-letter code en, de etc.
     *  @param view denotes the particular subpage:
     *  <ul>
     *  <li>license </li>
     *  <li>manifest</li>
     *  <li>notice  </li>
     *  <li>package (not used)</li>
     *  <li>root    (not used)</li>
     *  </ul>
     *  @param callingServlet the HttpServlet instance calling this method, 
     *  for the determination of the proper ClassLoader
     */
    public void showMetaInf(HttpServletRequest request, HttpServletResponse response
            , BasePage    basePage
            , String      language
            , String      view
            , HttpServlet callingServlet
            ) {
        try {
            PrintWriter out = basePage.writeHeader(request, response, language);
            
            out.write("<title>" + basePage.getAppName() + " MetaInf</title>\n");
            out.write("</head>\n<body>\n");
            String line     = null;
            String fileName = null;
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
            } else { // read from a resource in the JAR file
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
                        (callingServlet.getClass().getClassLoader().getResourceAsStream(fileName))
                        );
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                } // while
                out.write("</pre>\n</tt>\n");

                if (false) { // shows the URL of the classloader
                    out.write("<h3>Enumeration from ClassLoader</h3>\n");
                    // from http://stackoverflow.com/questions/5193786/how-to-use-classloader-getresources-correctly
                    Enumeration<URL> en = callingServlet.getClass().getClassLoader().getResources(fileName);
                    if (en.hasMoreElements()) {
                        URL metaInf = en.nextElement();
                        out.write("<tt>" + metaInf.toString() + "</tt><br />\n");
                    /*
                        File fileMetaInf = new File(metaInf.toURI());
                        File[] files = fileMetaInf.listFiles();
                        //or 
                        String[] filenames = fileMetaInf.list();
                        int iname = 0;
                        while (iname < filenames.length) {
                            iname ++;
                        } // while iname
                   */
                    } // hasMoreElements
                } // enumeration from ClassLoader 
            } // not "package"
            
            basePage.writeTrailer(language, "back,quest");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // showMetaInf

} // MetaInfPage
