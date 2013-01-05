/*  PrintStream which replaces some patterns (ISO timestamps) by constant strings for RegressionTester
 *  @(#) $Id$
 *  2013-01-05: redefine write methods
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

/** Filters a PrintStream and replaces a set of patterns (ISO timestamps of the form
 *  yyyy-mm-dd?hh:mm:ss[.sss] and others) by constants (string "yyyy-MM-dd hh:mm:ss" and so on).
 *  @author Dr. Georg Fischer
 */
public class TimestampFilterStream extends PrintStream {
    public final static String CVSID = "@(#) $Id$";

    /* local copy of the parent stream */
    private static PrintStream tfStream;
    /* local copy of the encoding */
    private String encoding;
    
    /** Constructor with output stream
     *  @param pStream stream to be filtered for ISO timestamps
     */
    public TimestampFilterStream(PrintStream pStream) 
            throws FileNotFoundException,
            UnsupportedEncodingException {
        super(pStream);
        tfStream = pStream;
    } // Constructor with output stream

    /** Constructor with output file name and encoding
     *  @param fileName name of the file to be written
     *  @param enc character set name
     */
    public TimestampFilterStream(String fileName, String enc) 
            throws FileNotFoundException,
            UnsupportedEncodingException {
        super(fileName, enc);
        encoding = enc;
        tfStream = new PrintStream(super.out, true, enc);
    } // Constructor with output stream and encoding

	/** Replaces a set of patterns by constant strings
	 *  @param str input string where replacements take place
	 *  @return output string with constants
	 */
	private String replacePatterns(String str) {
		return  str
                .replaceAll(" \\d{4}\\-\\d{2}\\-\\d{2} \\d{2}\\:\\d{2}\\:\\d{2}(\\.\\d+)?", " yyyy-mm-dd hh:mm:ss")
                .replaceAll(" rows in \\d+ ms", " rows in ... ms")
                .replaceAll("5.1.62-0ubuntu0.11.10.1", "5.1.66-0ubuntu0.11.10.3")
                .replaceAll("web/spec/", "../web/spec/")
                ;
	} // replacePatterns
	
    /** Flushes the stream
     */
    public void flush() {
        tfStream.flush();
    } // flush()

    /** Prints a string, after replacing any ISO timestamp by the constant "yyyy-MM-dd hh:mm:ss".
     *  @param str the string to be printed, possibly containing an ISO timestamp or date
     */
    public void print(String str) {
        tfStream.print(replacePatterns(str));
    } // print(String)

    /** Prints a string, after replacing any ISO timestamp by the constant "yyyy-MM-dd hh:mm:ss".
     *  @param str the string to be printed, possibly containing an ISO timestamp or date
     */
    public void println(String str) {
        tfStream.println(replacePatterns(str));
    } // println(String)

    /** Writes b.length bytes from the specified byte array to this file output stream.
     *  @param b byte array containing the bytes to be written to the stream.
  	 */
	public void write(byte[] b) {
		try {
			tfStream.write('[');
			tfStream.write(b);
			tfStream.write(64);
			tfStream.write(b);
			tfStream.write(']');
		} catch (Exception exc) {
		}
	} // write(byte[])
	
    /** Writes <em>len</em> bytes from the specified byte array to this file output stream, 
     *  starting at offset <em>off</em>.
     *  @param b byte array containing the bytes to be written to the stream.
     *  @param off offset (0 based) of first byte to be written 
     *  @param len number of bytes to be written
     */
	public void write(byte[] b, int off, int len) {
		try {
			// tfStream.write('(');
			byte[] b2 = replacePatterns(new String(b, off, len, encoding)).getBytes(encoding);
			int len2 = b2.length;
			int off2 = 0;
			while (len2 > 0) {
				tfStream.write(b2[off2 ++]);
				len2 --;
			} // while len2
			// tfStream.write(')');
		} catch (Exception exc) {
		}
	} // write(byte[],int,int)
    
    /** Writes b.length bytes from the specified byte array to this file output stream.
	*/
	public void write(int b) {
		try {
			tfStream.write(b);
			tfStream.write('.');
		} catch (Exception exc) {
		}
	} // write(byte)
	
} // TimestampFilterStream
