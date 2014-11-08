/*  SOAP Client which calls DbatService
    @(#) $Id$
    2014-11-08: remove Axis and all web service functionality
    2010-04-16: call service with commandLine
    2008-12-10: use service.properties
    2005-08-26, Dr. Georg Fischer
*/
/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
import  java.io.File;
import  java.io.FileInputStream;
import  java.util.Properties;
import  javax.xml.namespace.QName;
// import  org.apache.axis.client.Call;
// import  org.apache.axis.client.Service;
import  org.apache.log4j.Logger;

/** SOAP client sample program.
 *  Takes the same commandline arguments as {@link Dbat}, but calls
 *  a service instead of directly connecting to a database.
 */
public class DbatClient {
    public final static String CVSID = "@(#) $Id$";

    /** Activates the {@link DbatService}
     *  @param args commandline arguments, see {@link Dbat}
     */
    public static void main(String [] args) {
        Logger log = Logger.getLogger(DbatClient.class.getName());
    /*
        try {
            Properties props = new Properties();
            String propsName = "service.properties";
            props.load(DbatClient.class.getClassLoader().getResourceAsStream(propsName)); // (1) load from classpath (jar)
            File propsFile = new File(propsName);
            if (propsFile.exists()) {
                props.load(new FileInputStream(propsFile)); // (2) add any properties from a file in the current directory
            }
            String   axisURL = props.getProperty("axis_url", "http://localhost:8080/axis");

            String   endpoint = axisURL + "/services/DbatService";
            Service  service  = new Service();
            Call     call     = (Call) service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(endpoint));
            call.setOperationName(new QName(axisURL, "getResponse"));
            call.addParameter("commandLine", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.setReturnType(              org.apache.axis.Constants.XSD_STRING);
            StringBuffer commandLine = new StringBuffer(256);
            int iargs = 0;
            while (iargs < args.length) {
                if (iargs > 0) {
                    commandLine.append(' ');
                }
                commandLine.append(args[iargs ++]);
            } // while iargs
            String result = (String) call.invoke(new Object[] { commandLine.toString() });
            System.out.println(result);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    */
    } // main

} // DbatClient
