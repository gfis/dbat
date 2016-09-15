/*  Reader for text file, returns a string without any whitespace
 *  @(#) $Id$
 *  2016-08-10: check for non-empty result file with thisStream.size()
 *  2016-07-11: make -s; implicit macro CASE
 *  2016-05-14: HTTPZ and CALLZ
 *  2016-05-10: optionally set Http request properties
 *  2016-04-22: MAKE command; log stderr
 *  2016-04-16: implicit macro PWD = System.getProperty("user.dir")
 *  2015-09-08: continue lines with "\\" at the end
 *  2015-03-26: cat after cp (if *.prev.tst did not exist)
 *  2014-11-10: SORT=; more Javadoc
 *  2014-03-30: diff -Z
 *  2014-02-16: encoding for all BufferedReaders
 *  2013-09-13: EXIT effective only if testNamePattern != "*"
 *  2013-09-11: SORT instruction
 *  2013-08-14: classpath from System property, URL encoding
 *  2013-07-03: URL encoding
 *  2013-01-05: works with new CommandTokenizer.split()
 *  2012-11-24: copied from ramath/src...
 *  2012-11-06, Georg Fischer: copied from ExpressionReader
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
import  org.teherba.common.CommandTokenizer;
import  org.teherba.common.ReplacingPrintStream;
import  org.teherba.common.URIReader;
import  java.io.BufferedOutputStream;
import  java.io.BufferedReader;
import  java.io.File;
import  java.io.FileInputStream;
import  java.io.FileOutputStream;
import  java.io.InputStreamReader;
import  java.io.PrintStream;
import  java.io.PrintWriter;
import  java.io.StringWriter;
import  java.lang.Process;
import  java.lang.Runtime;
import  java.lang.reflect.Method;
import  java.net.URLEncoder;
import  java.nio.channels.Channels;
import  java.nio.channels.ReadableByteChannel;
import  java.util.ArrayList;
import  java.util.Date;
import  java.util.HashMap;
import  java.util.regex.Matcher;
import  java.util.regex.Pattern;
import  java.text.SimpleDateFormat;
import  org.apache.log4j.Logger;
/*
HttpEntity entity = MultipartEntityBuilder
    .create()
    .addTextBody("number", "5555555555")
    .addTextBody("clip", "rickroll")
    .addBinaryBody("upload_file", new File(filePath), ContentType.create("application/octet-stream"), "filename")
    .addTextBody("tos", "agree")
    .build();

HttpPost httpPost = new HttpPost("http://some-web-site");
httpPost.setEntity(entity);
HttpResponse response = httpClient.execute(httpPost);
HttpEntity result = response.getEntity();
*/


/** Processes a file with test cases and either generates the test output reference
 *  files (*.prev.tst) or generates new output files (*.this.tst) and compares them
 *  to the reference files. A typical activation is:
 *  <pre>
 *  java -cp dist/dbat.jar org.teherba.common.RegressionTester test/mysql.tests "*" 2>&1 | tee test/regression_mysql.log
 *  </pre>
 *  In order to avoid differences which depend on the date/time, for example,
 *  the program reads a file <em>regression.properties</em>
 *  from the same directory as the test case file.
 *  This properties file contains one or more pairs of lines
 *  <pre>
 *  Pattern to be replaced
 *  Replacement Content
 *  </pre>
 *  For example (<em>dbat/test/replacement.properties</em>)
<pre>
 \d{4}\-\d{2}\-\d{2} \d{2}\:\d{2}\:\d{2}([.,0-9]*)?
 yyyy-mm-dd hh:mm:ss
 rows in \d+ ms
 rows in ... ms
Dbat V\d+\.\w+\/\d{4}\-\d{2}\-\d{2} \- DataBase Application Tool
Dbat Vx.hhhh/yyyy-mm-dd - DataBase Application Tool
</pre>
 *  The test case file contains
 *  <ul>
 *  <li>comments starting with "#"</li>
 *  <li>Macro definitions of the form <em>NAME=value</em></li>
 *  <li>Commands of the form <em>NAME value1 value2 ...</em></li>
 *  <li>empty lines, which are igenored</li>
 *  </ul>
 *  The commands and macro definitions are uppercase by convention.
 *  The following macro definitions are recognized:
 *  <table>
 *  <tr><td>ARGS=   </td><td>commandline arguments which are appended to the CALL command</td></tr>
 *  <tr><td>CASE    </td><td>built-in macro which returns the name of the current testcase</td></tr>
 *  <tr><td>DATA    </td><td>built-in macro which returns the name of the current data file</td></tr>
 *  <tr><td>MAKE=   </td><td>define the make command and its options (default: <em>make -f makefile</em></td></tr>
 *  <tr><td>PACKAGE=</td><td>define the class name prefix for all following CALL commands (default: <em>org.teherba</em>)</td></tr>
 *  <tr><td>REQUEST=</td><td>define a list of pairs <em>key:value</em> (separated by spaces) for Http request properties</td></tr>
 *  <tr><td>PWD     </td><td>built-in macro which returns the current working directory</td></tr>
 *  <tr><td>SORT=   </td><td>define the sort command and its options (default: <em>sort</em></td></tr>
 *  <tr><td>URL=    </td><td>define the URL prefix for all following HTTP commands (default: <em>http://localhost:8080/dbat/servlet</em>) </td></tr>
 *  <tr><td>XSLT=   </td><td>define the XSLT processor command (default: <em>xsltproc</em>)</td></tr>
 *  <tr><td>(other)=</td><td>user defined macro</td></tr>
 *  </table>
 *  The following commands are recognized:
 *  <table>
 *  <tr><td>CALL    </td><td>activate the main method in the specified Java class file</td></tr>
 *  <tr><td>DATA    </td><td>starts "here is" content; lines for an auxiliary test file</td></tr>
 *  <tr><td>ECHO    </td><td>output the rest of the line in the test logfile</td></tr>
 *  <tr><td>EXIT    </td><td>immediately stop execution of test commands</td></tr>
 *  <tr><td>HTTP    </td><td>get a Web page from an URL</td></tr>
 *  <tr><td>MAKE    </td><td>calls some target in a <em>makefile</em> with the <em>make</em> utility</td></tr>
 *  <tr><td>SORT    </td><td>sort a file</td></tr>
 *  <tr><td>TEST    </td><td>start of a test case with description</td></tr>
 *  <tr><td>XSLT    </td><td>execute an XSLT stylesheet</td></tr>
 *  </table>
 *  @author Dr. Georg Fischer
 */
public class RegressionTester {
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;

    /** current date and time when this class is instatiated */
    private String timestamp;

    /** extension of files generated for DATA */
    private static final String DATA_EXTENSION = "tmp";

    /** maps macro names to replacement text */
    private HashMap<String, String> macros;

    /** maps names of request properties to values */
    private HashMap<String, String> requestProps;

    /** System-specific line separator (CR, LF for Unix or CR/LF for Windows)*/
    private static final String nl      = System.getProperty("line.separator");
    /** System-specific file separator ("/" for Unix, "\" for Windows */
    private static final String slash   = "/"; // System.getProperty("file.separator");

    /** No-args Constructor
     */
    public RegressionTester() {
        log          = Logger.getLogger(RegressionTester.class.getName());
        timestamp    = (new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")).format(new java.util.Date());
        macros       = new HashMap<String, String>(16);
        requestProps = new HashMap<String, String>(16);
        String pwd   = System.getProperty("user.dir");
        if (pwd.substring(1, 3).equals(":\\")) { // Windows
            pwd = "/" + pwd;
        }
        macros.put("PWD", pwd);
    } // no-args Constructor

    //************************
    // Utility methods
    //************************

    /** Replaces macro calls of the form <em>$(macroname)</em> by the content of the macro
     *  which was previously defined by a line
     *  <pre>
     *      macroname=content
     *  </pre>
     *  @param line string where macro calls should be replaced
     *  @return line with macro calls replaced
     */
    public String replaceMacros(String line) {
        StringBuffer result = new StringBuffer(line);
        int pos1 = 0;
        String replacement = "";
        while ((pos1 = result.indexOf("$(")) >= 0) {
            int pos2 = result.indexOf(")", pos1);
            if (pos2 > pos1 + 2) { //
                String macroName = result.substring(pos1 + 2, pos2); // at least 1 char
                replacement = macros.get(macroName);
                if (replacement == null) {
                    replacement = "#(" + macroName + ")";
                }
            } else {
                replacement = "#(?)";
                pos2 = pos1 + 2;
            }
            result.replace(pos1, pos2 + 1, replacement);
        } // while pos1 found
        return result.toString();
    } // replaceMacros

    /** Replaces percent encoding by the corresponding ASCII character.
     *  @param line string with embedded percent encoded ASCII characters
     *  @return string with replaced characters
     */
    public String unPercent(String line) {
        StringBuffer result = new StringBuffer(line);
        int pos1 = 0;
        while ((pos1 = result.indexOf("%", pos1)) >= 0) {
            pos1 ++;
            int ich = 32; // ' '
            if (pos1 < line.length() - 1 && Character.isLetterOrDigit(line.charAt(pos1))) { // hex pair follows
                try {
                    ich = Integer.parseInt(result.substring(pos1, pos1 + 2), 16);
                    if (ich != 0x2b) { // '+'
                        result.replace(pos1 - 1, pos1 + 2, new String(new byte[] { (byte) ich }));
                    }
                } catch (Exception exc) { // no digits - continue
                }
            } // hex pair follows
            // pos1 is already behind the inserted character
        } // while pos1
        return result.toString();
    } // unPercent

    /** Joins a String array
     *  @param separator String which separates the elements
     *  @param elements array of Strings
     *  @return elements joined by the separator
     */
    public String join(String separator, String[] elements) {
        StringBuffer result = new StringBuffer(256);
        int ielem = 0;
        if (ielem < elements.length) {
            result.append(elements[ielem ++]);
        }
        while (ielem < elements.length) {
            result.append(separator);
            result.append(elements[ielem ++]);
        } // while ielem
        return result.toString();
    } // join

    /** Runtime environment */
    private Runtime runtime = Runtime.getRuntime(); // for command execution
    /** Encoding of the logfile */
    private String logEncoding = "UTF-8"; // encoding for the result files and the log file
    /** Where to write the standard output */
    private PrintStream realStdOut = System.out; // System.out is redirected (with setOut)
    /** Where to write standard error */
    private PrintStream realStdErr = System.err; // System.err is redirected (with setErr)
    /** Intermediate stream for test output to be filtered for inevitable differences */
    private ReplacingPrintStream thisStream = null;

    /** Runs a shell command
     *  @param cmd command line to be executed
     */
    public void runShellCommand(String cmd) {
        try {
            String logText = cmd;
            realStdOut.println(logText);
            Process process = runtime.exec(cmd);

            BufferedReader
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), logEncoding));
            String line = null;
            while ((line = reader.readLine()) != null) {
                thisStream.println(line);
            } // while readLine
            reader.close();

            reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), logEncoding));
            while ((line = reader.readLine()) != null) {
                thisStream.println(line);
            } // while readLine
            reader.close();
        } catch (Exception exc) {
            try {
                log.error(exc.getMessage(), exc);
            } catch (Exception exc2) {
                System.setOut(realStdOut);
                System.setErr(realStdErr);
                log.error(exc.getMessage(), exc2);
            }
        } // try
    } // runShellCommand

    /** Unzips a file in place
     *  @param thisName name of file to be unzipped
     *  @param enc target encoding
     */
    public void unzipFile(String thisName,  String enc) {
        try {
            String cmd = "unzip -p " + thisName;
            String logText = cmd;
            realStdOut.println(logText);
            Process process = runtime.exec(cmd);
            StringWriter bufferWriter = new StringWriter(4096);

            BufferedReader
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), logEncoding));
            String line = null;
            while ((line = reader.readLine()) != null) {
                bufferWriter.write(line);
                bufferWriter.write(nl);
            } // while readLine
            reader.close();

            reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), logEncoding));
            while ((line = reader.readLine()) != null) {
                bufferWriter.write(line);
                bufferWriter.write(nl);
            } // while readLine
            reader.close();

            PrintWriter thisFile = new PrintWriter(new File(thisName), enc);
            thisFile.print(bufferWriter.toString());
            thisFile.close();
        } catch (Exception exc) {
            try {
                log.error(exc.getMessage(), exc);
            } catch (Exception exc2) {
                System.setOut(realStdOut);
                System.setErr(realStdErr);
                log.error(exc.getMessage(), exc2);
            }
        } // try
    } // unzipFile(2)

    /** Unzips a file in place
     *  @param thisName name of file to be unzipped
     *  @param thisStream replacing PrintStream which writes to File <em>thisName</em>
     *  @param enc target encoding
     */
    public void unzipFile(String thisName, PrintStream thisStream, String enc) {
        try {
            BufferedReader reader = (new URIReader()).unzipStream(new FileInputStream(thisName));
            String line = null;
            while ((line = reader.readLine()) != null) {
                thisStream.println(line);
            } // while readLine
            reader.close();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } // try
    } // unzipFile(3)

    //************************
    // Workhorse method
    //************************

    /** Reads a text file and interprets the test instructions therein.
     *  @param args name of a file (or "-" for STDIN) which contains the test cases, and an optional
     *  test case name pattern (with Linux or SQL wildcard characters)
     */
    public void runTests(String[] args) {
        long startMillis        = System.currentTimeMillis();
        String directory         = ".";
        String fileName         = "-";
        boolean skipping        = false; // whether to skip test cases because their names do not match 'testNamePattern'
        String tcaEncoding      = "UTF-8"; // encoding for the test case input file and the generated data files
        String testName         = "UNDEF"; // name of the current test case
        String testDesc         = "UNDEF test case"; // comment for the test case
        String testLine         = null; // current line from test case file
        StringBuffer dataBuffer = new StringBuffer(1024); // for DATA assembly
        String thisName         = null; // filename for this test's results
        String prevName         = null; // filename for previous test's results
        String dataName         = "XXX.data." + DATA_EXTENSION; // DATA file name for current test case
        File  thisFile          = null;
        File  prevFile          = null;
        String ext              = ".tst"; // extension for result files
        Process process         = null;
        Class<?> targetClass    = null; // for reflective method invocation
        Method mainMethod       = null;
        String classPrefix      = "org.teherba."; // default for PACKAGE macro
        String argsPrefix       = ""; // default for ARGS macro
        String diffPrefix       = "diff -Z "; // -Z = ignore line ends
        String baseURL          = "http://localhost:8080/dbat/servlet"; // default for URL macro
        String makePrefix       = "make -s -f makefile";
        String sortPrefix       = "sort ";
        String xsltPrefix       = "xsltproc ";
        String cmd              = null; // system command to be executed
        BufferedReader reader   = null; // reader for stdout from 'cmd'
        String line             = null; // a line read from some Reader
        int    passedCount      = 0; // number of tests which were run successfully
        int    failedCount      = 0; // number of tests which showed a 'diff'erence
        int    recreatedCount   = 0; // number of tests for which there was no previous result file
        String logText          = null; // for test files and STDOUT
        String[] replacements   = null; /* replacement patterns and their substitutions in consecutive elements */

        int iarg = 0;
        if (args.length > iarg) {
            fileName = args[iarg ++];
        }
        String testNamePattern = "*";
        if (args.length > iarg) {
            testNamePattern = args[iarg ++];
        }
        testNamePattern = testNamePattern
                .replaceAll("[\\*\\%]", ".*")
                .replaceAll("[\\?]", ".")
                ;
        try {
            Pattern verbPattern = Pattern.compile("(\\w+=?)\\s*(.*)"); // Pattern for verbs at start of testLine
            Pattern callPattern = Pattern.compile("(\\S+)\\s*(.*)"); // CALL className arguments
            Pattern testPattern = Pattern.compile("(\\S+)\\s*(.*)"); // TEST testName comment

            // Open the file with the testcases
            BufferedReader tcaReader = null;
            if(fileName.equals("-")) { // STDIN
                tcaReader = new BufferedReader(new InputStreamReader(System.in, tcaEncoding));
            } else {
                File testCases = new File(fileName);
                directory = testCases.getParent();
                ReadableByteChannel tcaChannel = (new FileInputStream(fileName)).getChannel();
                tcaReader = new BufferedReader(Channels.newReader(tcaChannel, tcaEncoding));
            } // not STDIN
            // System.err.println("fileName=" + fileName + ", directory=" + directory);

            // read the replacement patterns, if possible
            String replName = directory + "/regression.properties";
            BufferedReader replReader = null;
            File replFile = new File(replName);
            if (replFile.exists()) {
                ReadableByteChannel replChannel = (new FileInputStream(replName)).getChannel();
                replReader = new BufferedReader(Channels.newReader(replChannel, tcaEncoding));
                ArrayList<String> result = new ArrayList<String>(16);
                while ((line = replReader.readLine()) != null) {
                    if (! line.matches("\\A\\s*\\#.*")) { // ignore comments
                        // System.err.println("\"" + line + "\"");
                        result.add(line);
                    }
                } // while lines
                replReader.close();
                replacements = result.toArray(new String[0]);
                // System.err.println(replacements.length + " elements in replacements[]");
            } // replFile exists

            boolean busy = true; // used to read a fictional "TEST" line at the end
            while ((testLine = tcaReader.readLine()) != null || busy) { // read and process lines
                if (testLine == null && busy) {
                    testLine = "TEST END";
                    busy = false;
                }
                if (testLine.trim().endsWith("\\")) {
                    // followed by continuation line(s) - read and append them to testLine
                    testLine = testLine.replaceAll("\\\\\\s*\\Z", " ").trim(); // a single space
                    String testLine2 = null;
                    boolean continued = true;
                    while (continued && (testLine2 = tcaReader.readLine()) != null) { // read and process lines
                        if (testLine2.trim().endsWith("\\")) { // followed by continuation line(s)
                            testLine2 = testLine2.replaceAll("\\\\\\s*\\Z", " ") ; // a single space
                        } else {
                            continued = false;
                        }
                        testLine += " " + testLine2.trim();
                    } // while testLine2
                } // followed by continuatin lines

                if (false) {
                } else if (testLine.matches("\\s*#.*") || testLine.matches("\\s*")) { // comment line or empty line
                    // ignore
                } else if (testLine.matches("[^A-Za-z].*")) { // starts with non-letter => continuation of DATA
                    dataBuffer.append(testLine.replaceAll("^ +", "")); // remove leadings spaces, but no tabs
                    dataBuffer.append(nl);
                } else { // verb starting in column 1
                    testLine = replaceMacros(testLine);
                    Matcher verbMatcher = verbPattern.matcher(testLine);
                    if (verbMatcher.matches()) {
                        String verb     = verbMatcher.group(1).toUpperCase();
                        String rest     = verbMatcher.group(2).trim();
                        String separ    = " ";
                        if (verb.endsWith("=")) {
                            separ = "=";
                            verb = verb.substring(0, verb.length() - 1);
                        } // endsWith =
                        int ipart = 0;
                        // System.err.println("verb=" + verb + ", rest=" + rest);
                        if (! skipping && dataBuffer.length() > 0) { // data buffer is filled
                            File dataFile = new File(dataName);
                            PrintStream dataStream = new PrintStream(dataFile, tcaEncoding);
                            dataStream.print(dataBuffer.toString());
                            dataStream.close();
                            dataBuffer.setLength(0);
                        } // data buffer was filled

                        if (separ.startsWith("=")) { // macro definition
                            if (false) {
                            } else if (verb.equals("ARGS")) {
                                argsPrefix  = rest + (rest.length() == 0 ? "" : " ");
                                macros.put(verb, argsPrefix);
                            } else if (verb.equals("DIFF")) {
                                diffPrefix  = rest;
                                macros.put(verb, diffPrefix);
                            } else if (verb.equals("MAKE")) {
                                makePrefix  = rest;
                                macros.put(verb, makePrefix);
                            } else if (verb.equals("PACKAGE")) {
                                classPrefix = rest + (rest.endsWith(".") ? "" : ".");
                                macros.put(verb, classPrefix);
                            } else if (verb.equals("REQUEST")) {
                                requestProps.clear();
                                int colonPos = rest.indexOf(':');
                                if (colonPos >= 1) {
                                    String key   = rest.substring(0, colonPos ).trim();
                                    String value = rest.substring(colonPos + 1).trim();
                                    if (value.length() > 0) { // new or modified value
                                        requestProps.put(key, value);
                                    } else { // delete value
                                        requestProps.remove(key);
                                    }
                                } // ':' present
                            } else if (verb.equals("SORT")) {
                                sortPrefix  = rest;
                                macros.put(verb, sortPrefix);
                            } else if (verb.equals("URL")) {
                                baseURL     = rest + (rest.endsWith("?") ? "" : "?");
                                macros.put(verb, baseURL);
                            } else if (verb.equals("XSLT")) {
                                xsltPrefix  = rest;
                                macros.put(verb, xsltPrefix);
                            } else { // other, user defined macro
                                macros.put(verb, rest);
                            }
                            // macro definition

                        } else if (verb.equals("TEST")) {
                            if (thisStream != null) { // is still open - process results of previous TEST
                                boolean passed = thisStream.size() > 0L; // must be non-empty
                                thisStream.close();
                                System.setOut(realStdOut);
                                System.setErr(realStdErr);
                                prevName = thisName.replaceAll("\\.this\\.", ".prev.").replaceAll(".bad", "");
                                prevFile = new File(prevName);
                                if (! skipping) {
                                    if (! passed) {
                                        realStdOut.println("?? failed because of empty output");
                                    }
                                    if (prevFile.exists()) { // run diff prev this
                                        cmd = diffPrefix + " -C0 " + prevName + " " + thisName; // -Z = ignore line ends
                                            // context diffs may be used only here, but not in our DIFF command
                                        process = runtime.exec(cmd);
                                        reader = new BufferedReader(new InputStreamReader(process.getInputStream(), logEncoding));
                                        int iline = 0;
                                        while ((line = reader.readLine()) != null) {
                                            System.out.println(line);
                                            iline ++;
                                        } // while readLine
                                        reader.close();
                                        passed = passed && (iline == 0);
                                        if (passed) {
                                            passedCount ++;
                                        } else {
                                            failedCount ++;
                                        }
                                        realStdOut.println("========> " + (passed ? "passed " : "FAILED ")  + testName + " " + testDesc);
                                    } else { // no prevFile - copy this file to prevFile
                                        cmd = "cp "         + thisName + " " + prevName;
                                        process = runtime.exec(cmd);
                                        System.out.println(cmd);
                                        cmd = "cat "         + thisName;
                                        process = runtime.exec(cmd);
                                        reader = new BufferedReader(new InputStreamReader(process.getInputStream(), logEncoding));
                                        while ((line = reader.readLine()) != null) {
                                            System.out.println(line);
                                        } // while readLine
                                        reader.close();
                                        recreatedCount ++;
                                        realStdOut.println("========> recreated  "                          + testName + " " + testDesc);
                                    } // copy
                                } // ! skipping
                            } // previous TEST

                            // now start of a new TEST
                            Matcher testMatcher = testPattern.matcher(rest);
                            if (testMatcher.matches()) {
                                testName = testMatcher.group(1);
                                testDesc = testMatcher.group(2);
                            } else {
                                System.err.println("TEST verb syntax error: " + testLine);
                            }
                            skipping = false;
                            if (! testName.matches(testNamePattern)) {
                                skipping = true;
                            } else if (! testName.equals("END")) { // this a test case which should be run
                                realStdOut.print  ("Test " + testName + " " + testDesc);
                                realStdOut.println();
                                thisName = directory + slash + testName + ".this" + ext;
                                if (replacements == null) {
                                    thisStream = new ReplacingPrintStream(thisName, logEncoding); // with default replacements
                                } else {
                                    thisStream = new ReplacingPrintStream(thisName, logEncoding, replacements);
                                }
                                System.setOut(thisStream);
                                System.setErr(System.out);
                                dataBuffer.setLength(0);
                                macros.put("CASE", testName);
                            } // not END
                            // TEST

                        } else if (skipping) {
                            // ignore all verbs except "TEST"

                        } else if (verb.startsWith("CALL")) { // or "CALLZ"
                            Matcher callMatcher = callPattern.matcher(rest);
                            if (callMatcher.matches()) {
                                String className = classPrefix + callMatcher.group(1);
                                String argsStr   = argsPrefix  + callMatcher.group(2);
                                String[] parts   = CommandTokenizer.split(argsStr);
                                logText = "java -cp " + System.getProperty("java.class.path")
                                        + " " + className + " " + argsStr
                                        + (verb.endsWith("Z") ? " | unzip -p" : "");
                                realStdOut.println(logText);
                                targetClass = Class.forName(className); //, true, RegressionTester.class.getClassLoader());
                                mainMethod = targetClass.getMethod("main", String[].class);
                                if (verb.endsWith("Z")) {
                                    PrintStream pfos = new PrintStream(new FileOutputStream(thisName));
                                    System.setOut(pfos);
                                    mainMethod.invoke(null, (Object) parts);
                                    pfos.close();
                                    unzipFile(thisName, thisStream, logEncoding);
                                } else {
                                    mainMethod.invoke(null, (Object) parts);
                                }
                            } else {
                                System.err.println("CALL verb syntax error: " + testLine);
                            }

                        } else if (verb.equals("DATA")) {
                            dataBuffer.setLength(0);
                            if (rest.length() > 0 && ! rest.startsWith("#") && ! rest.startsWith("--")) { // append rest only if it is not a comment or empty
                                dataBuffer.append(rest); // just behind "DATA"
                                dataBuffer.append(nl);
                            }
                            dataName = directory + "/" + testName + ".data.tmp";
                            macros.put("DATA", dataName);

                        } else if (verb.equals("DIFF")) {
                            runShellCommand(diffPrefix + " " + rest.trim());

                        } else if (verb.equals("ECHO")) {
                            System.out.println(verb + " " + timestamp);

                        } else if (verb.equals("EXIT")) { // skip all remaining lines
                            if (testNamePattern.matches("\\W+")) { // no specific tests are selected
                                while ((testLine = tcaReader.readLine()) != null) {
                                    // ignore
                                } // while ignoring
                            } // only if no specific tests selected

                        } else if (verb.startsWith("HTTP")) { // or "HTTPZ"
                            String requestURL = baseURL +
                                    // unPercent
                                    (rest.trim()
                                    .replaceAll("\\s+", "&")
                                    .replaceAll("\\+", " ")
                                    );
                            logText = "wget \"" + requestURL + "\""
                                        + (verb.endsWith("Z") ? " | unzip -p" : "");
                            realStdOut.println(logText);
                            URIReader urlReader = new URIReader(requestURL
                                    , (verb.endsWith("Z") ? "zip" : "UTF-8")
                                    , requestProps);
                            String urlLine = null;
                            while ((urlLine = urlReader.readLine()) != null) {
                                thisStream.println(urlLine);
                            } // while urlLine

                        } else if (verb.equals("MAKE")) {
                            runShellCommand(makePrefix + " " + rest.trim());

                        } else if (verb.equals("SORT")) {
                            runShellCommand(sortPrefix + " " + rest.trim());

                        } else if (verb.equals("UNZIP")) {
                            unzipFile(thisName, logEncoding); // unzip in place

                        } else if (verb.equals("XSLT")) {
                            runShellCommand(xsltPrefix + " " + rest.trim());

                        } else { // maybe it is a defined macro activation
                            String value = macros.get(verb);
                            if (value != null) { // macro is defined

                            } // defined macro
                        } // maybe macro
                    } else if (! skipping) { // no verb starts in column 1
                        dataBuffer.append(testLine.substring(4)); // just behind "DATA"
                        dataBuffer.append(nl);
                    }
                } // verb starting in column 1
            } // while ! eof

            // Print trailer
            realStdOut.printf("%4d tests recreated," , recreatedCount);
            realStdOut.println();
            realStdOut.printf("%4d tests FAILED,"    , failedCount);
            realStdOut.println();
            realStdOut.printf("%4d tests passed"     , passedCount);
            realStdOut.print(" in " + (System.currentTimeMillis() - startMillis) + " ms");
            realStdOut.println();
            tcaReader.close();
            // restore output streams
            System.setOut(realStdOut);
            System.setErr(realStdErr);
        } catch (Exception exc) {
            try {
                log.error(exc.getMessage(), exc);
            } catch (Exception exc2) {
                System.setOut(realStdOut);
                System.setErr(realStdErr);
                log.error(exc.getMessage(), exc2);
            }
        } // try
    } // runTests

    /** Commandline activation:
     *  <pre>
     *  java -cp dist/dbat.jar org.teherba.dbat.common.RegressionTester [test/mysql.tests [testNamePattern]]
     *  </pre>
     *  @param args command line arguments: filename for testcases (or "-" for STDIN),
     *  and testNamePattern for testcase names to be run, where the
     *  testNamePattern may use Linux or SQL wildcard characters (% = wildcard).
     */
    public static void main(String[] args) {
        (new RegressionTester()).runTests(args);
    } // main

} // RegressionTester
