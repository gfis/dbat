/*  PrintStream which replaces ISO timestamps by constant strings for RegressionTester
 *  @(#) $Id$
 *  2012-11-09, Georg Fischer: "Wende" in Germany 23 years ago
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
package org.teherba.common;
import  java.io.File;
import  java.io.PrintStream;
import  java.io.FileNotFoundException;
import  java.io.IOException;
import  java.io.OutputStream;
import  java.io.UnsupportedEncodingException;
import  java.util.Date;
import  java.text.SimpleDateFormat;

/** Filters a PrintStream and replaces all ISO timestamps of the form
 *  yyyy-mm-dd?hh:mm:ss[sss] by the constant string "yyyy-MM-dd hh:mm:ss".
 *  @author Dr. Georg Fischer
 */
public class TimestampFilterStream extends PrintStream {
    public final static String CVSID = "@(#) $Id$";

    /* local copy of the parent stream */
    private PrintStream tfStream;
    
    /** Constructor with output stream
     *  @param pStream stream to be filtered for ISO timestamps
     */
    public TimestampFilterStream(PrintStream pStream) {
        super(pStream);
        tfStream = pStream;
    } // Constructor with output stream

    /** Constructor with output stream and encoding
     *  @param ostream stream to be filtered for ISO timestamps
     *  @param csn character set name
     */
    public TimestampFilterStream(String fileName, String csn) 
            throws FileNotFoundException,
            UnsupportedEncodingException {
        super(fileName, csn);
        tfStream = new PrintStream(super.out);
    } // Constructor with output stream and encoding

    /** Flushes the stream
     */
    public void flush() {
        tfStream.flush();
    } // flush

    /** Prints a string, after replacing any ISO timestamp by the constant "yyyy-MM-dd hh:mm:ss".
     *  @param str the string to be printed, possibly containing an ISO timestamp or date
     */
    public void print(String str) {
        tfStream.print(
        	//	"###" + 
        		str
                .replaceAll(" \\d{4}\\-\\d{2}\\-\\d{2} \\d{2}\\:\\d{2}\\:\\d{2}(\\.\\d+)?", " yyyy-mm-dd hh:mm:ss")
            //  .replaceAll(" \\d{4}\\-\\d{2}\\-\\d{2}"                                   , " yyyy-mm-dd")
                .replaceAll(" rows in \\d+ ms", " rows in ... ms")
                .replaceAll("5.1.62-0ubuntu0.11.10.2", "5.1.62-0ubuntu0.11.10.1")
                .replaceAll("web/spec/", "../web/spec/")
                );
    } // print(str)

    /** Prints a string, after replacing any ISO timestamp by the constant "yyyy-MM-dd hh:mm:ss".
     *  @param str the string to be printed, possibly containing an ISO timestamp or date
     */
    public void println(String str) {
        tfStream.println(
        	//	"###" + 
        		str
                .replaceAll(" \\d{4}\\-\\d{2}\\-\\d{2} \\d{2}\\:\\d{2}\\:\\d{2}(\\.\\d+)?", " yyyy-mm-dd hh:mm:ss")
            //  .replaceAll(" \\d{4}\\-\\d{2}\\-\\d{2}"                                   , " yyyy-mm-dd")
                .replaceAll(" rows in \\d+ ms", " rows in ... ms")
                .replaceAll("5.1.62-0ubuntu0.11.10.2", "5.1.62-0ubuntu0.11.10.1")
                .replaceAll("web/spec/", "../web/spec/")
                );
    } // println(str)

} // TimestampFilterStream
