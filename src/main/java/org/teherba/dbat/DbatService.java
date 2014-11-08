/*  SOAP Service interface to Dbat
    @(#) $Id$
    2014-11-08: remove Axis and all web service functionality
    2010-09-23: terminate()
    2010-04-16: takes a commandline and calls Dbat 
    2007-04-17: constant (dummy) response
 
    Service to be called via SOAP, offering the functions of Dbat
*/
/*
 * Copyright 2006 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  java.io.PrintWriter;
import  java.io.StringWriter;
import  org.teherba.dbat.Configuration;
import  org.teherba.dbat.Dbat;
import  org.apache.log4j.Logger;

/** This class is the SOAP service interface to {@link Dbat}, 
 *  and ressembles the functionality of the commandline interface
 *  in that class.
 *  @author Dr. Georg Fischer
 */
public class DbatService { 
    public final static String CVSID = "@(#) $Id$";
    /** log4j logger (category) */
    private Logger log;

    /** No-args constructor
     */
    public DbatService()  {
        log = Logger.getLogger(DbatService.class.getName());
    } // Constructor
    
    /** Returns the results of an activation of {@link Dbat}
     *  to a SOAP client.
     *  @param commandLine line with options, parameters and/or an SQL instruction
     *  @return number word or digit sequence 
     */
    public String getResponse(String commandLine)  {
        Dbat dbat = new Dbat();
        StringWriter writer = new StringWriter(32768);
        try {
            dbat.initialize(Configuration.SOAP_CALL);
            dbat.processCommandLine(new PrintWriter(writer), commandLine); 
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
            dbat.terminate();
        }
        return writer.toString();
    } // getResponse          

} // DbatService
