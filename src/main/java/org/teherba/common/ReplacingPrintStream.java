/*  PrintStream which replaces some patterns (ISO timestamps) by constant strings for RegressionTester
 *  @(#) $Id$
 *  2016-10-13: less imports
 *  2016-08-10: size()
 *  2016-05-14: renamed from TimestampFilterStream; cleaned
 *  2014-11-16: pattern for milliseconds
 *  2013-01-06: HashMap for replacement patterns
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
import  java.io.PrintStream;
import  java.io.FileNotFoundException;
import  java.io.UnsupportedEncodingException;

/** Filters a PrintStream and replaces a set of patterns (ISO timestamps of the form
 *  yyyy-mm-dd?hh:mm:ss[.sss] and others) by constants (string "yyyy-MM-dd hh:mm:ss" and so on).
 *  @author Dr. Georg Fischer
 */
public class ReplacingPrintStream extends PrintStream {
    public final static String CVSID = "@(#) $Id$";

    /** local copy of the parent stream */
    private static PrintStream tfStream;
    /** local copy of the encoding */
    private String encoding;
    /** replacement patterns and their substitutions in consecutive elements */
    private String[] replacements = null;
    /** whether the replacements should be done */
    private boolean replacing;
    /** accumulated length of the output; because of the replacements, the size may not be exact */
    private long streamSize;

    /** Constructor with output file
     *  @param fileName name of the file to be written
     *  @param enc character set name
     */
    public ReplacingPrintStream(String fileName, String enc)
            throws FileNotFoundException,
            UnsupportedEncodingException {
        this(fileName, enc, new String[]
                { " \\d{4}\\-\\d{2}\\-\\d{2} \\d{2}\\:\\d{2}\\:\\d{2}[\\.0-9]*"
                , " yyyy-mm-dd hh:mm:ss"
                , " rows in \\d+ ms"
                , " rows in ... ms"
                }
                );
    } // Constructor(2)

    /** Constructor with file and replacements
     *  @param fileName name of the file to be written
     *  @param enc character set name
     *  @param replacements replacement patterns and their substitutions in consecutive elements
     */
    public ReplacingPrintStream(String fileName, String enc, String[] replacements)
            throws FileNotFoundException,
            UnsupportedEncodingException {
        super(fileName, enc);
        encoding = enc;
        tfStream = new PrintStream(super.out, true, enc);
        this.replacements = replacements;
        setReplacing(true);
        streamSize = 0;
    } // Constructor(3)
    
    /** Sets the replacement mode
     *  @param mode true (false) - do (not) replace
     */
    public void setReplacing(boolean mode) {
        replacing = mode;
    } // setReplacing

    /** Replaces a set of patterns by constant strings
     *  @param str input string where replacements take place
     *  @return output string with constants
     */
    private String replacePatterns(String str) {
        int irepl = 0;
        String result = str;
        if (replacing) {
            while (irepl < replacements.length) {
               result = result.replaceAll(replacements[irepl], replacements[irepl + 1]);
                irepl += 2;
            } // while irepl
        } // replacing
        return result;
    } // replacePatterns

    /** Flushes the stream
     */
    public void flush() {
        tfStream.flush();
    } // flush()

    /** Prints a string after replacing the patterns from <em>test/regression.properties</em>
     *  @param str the string to be printed, possibly containing an ISO timestamp, date or some other pattern.
     */
    public void print(String str) {
        tfStream.print(replacePatterns(str));
        streamSize += str.length();
    } // print(String)

    /** Prints a string after replacing the patterns from <em>test/regression.properties</em>, and a newline.
     *  @param str the string to be printed, possibly containing an ISO timestamp, date or some other pattern.
     */
    public void println(String str) {
        tfStream.println(replacePatterns(str));
        streamSize += str.length() + 1;
    } // println(String)

	/** Get the accumulated stream size 
	 *  @return character size
	 */
	public long size() {
		return streamSize;
	} // size
	
    /** Writes <em>len</em> bytes from the specified byte array to this file output stream,
     *  starting at offset <em>off</em>.
     *  @param b byte array containing the bytes to be written to the stream.
     *  @param off offset (0 based) of first byte to be written
     *  @param len number of bytes to be written
     *  Caution, this is not really clean since replacement patterns could span byte chunk boundaries.
     */
    public void write(byte[] b, int off, int len) {
        try {
            byte[] b2 = replacePatterns(new String(b, off, len, encoding)).getBytes(encoding);
            tfStream.write(b2, 0, b2.length);
        } catch (Exception exc) {
        }
        streamSize += len;
    } // write(byte[],int,int)

} // ReplacingPrintStream
