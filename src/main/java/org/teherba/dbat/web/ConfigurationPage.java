/*  ConfigurationPage.java - Show Dbat's configuration variiables
 *  @(#) $Id$
 *  2018-01-11, Georg Fischer: copied from ConsolePage.java
 */
/*
 * Copyright 2018 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.dbat.Configuration;
import  org.teherba.common.web.BasePage;
import  java.io.IOException;
import  java.io.PrintWriter;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  org.apache.log4j.Logger;

/** Prints a list of the configuration parameters of Dbat
 *  @author Dr. Georg Fischer
 */
public class ConfigurationPage {
    public final static String CVSID = "@(#) $Id$";
    public final static long serialVersionUID = 19470629;

    /** log4j logger (category) */
    private Logger log;

    /** No-argument constructor
     */
    public ConfigurationPage() {
        log = Logger.getLogger(ConfigurationPage.class.getName());
    } // Constructor()

    /** Shows an input form for SQL command execution
     *  @param request request with header fields
     *  @param response response with writer
     *  @param basePage refers to common web methods and messages
     *  @param language 2-letter code en, de etc.
     *  @param config general configuration data
     *  @throws IOException if an IO error occurs
     */
    public void showConfiguration(HttpServletRequest request, HttpServletResponse response
            , BasePage basePage
            , String language
            , Configuration config
            ) throws IOException {
        if (true) { // try {
            PrintWriter out = basePage.writeHeader(request, response, language);
            String configWord = " ";
            if (false) {
            } else if (language.equals("de")) {
                configWord += "Konfiguration";
            } else if (language.equals("fr")) {
                configWord += "Configuration";
            } else {
                configWord += "Configuration";
            }
            out.write("<title>" + basePage.getAppName() + configWord + "</title>\n");
            out.write("</head>\n");
            out.write("<body>\n");
            out.write("<h3>" + basePage.get(language, "001") + configWord + "</h3>\n"); // link to main page
            out.write("<pre>\n");
            out.write(config.toString());
            out.write("</pre>\n");
            basePage.writeTrailer(language, "back,quest");
        }
    } // showConfiguration

} // ConfigurationPage
