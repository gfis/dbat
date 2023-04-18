/*  (Pseudo-abstract) class for file format transformers
    @(#) $Id$
    2023-04-18: remove Boolean(boolean)
 *  2017-05-27: javadoc 1.8
    2016-10-13: less imports
    2016-09-07: public setMimeType, getOption, getIntOption
    2012-01-10: instantiate Logger in Constructor
    2010-06-01: do not close System.out if processing commands from -f, close at the end only
    2008-06-27: openStream
    2008-05-30: with fatalError()
    2007-11-05: also with LexicalHandler
    2007-09-26: toAttributes(ArrayList)
    2007-08-28: getPrefix was getLocalName
    2007-03-23: implements DefaultHandler2, XMLFilter and Locator interfaces
    2006-11-17: from XML works too
    2006-09-19: copied from numword.BaseSpeller

 *  The AbstractXMLReader portion was taken from the
 *  file javaxslt_examples.zip from chapter 5 in the book
 *  "Java and XSLT", O'Reilly, ISBN: 0596001436, Author: Eric M. Burke, September 2001
 *  (c.f. http://www.perfectxml.com/Oreilly/chapter.asp?row_id=9)
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

package org.teherba.xtrans;
import  org.teherba.xtrans.XMLTransformer;
import  java.io.BufferedReader;
import  java.io.BufferedInputStream;
import  java.io.BufferedOutputStream;
import  java.io.FileInputStream;
import  java.io.FileOutputStream;
import  java.io.InputStream;
import  java.io.InputStreamReader;
import  java.io.IOException;
import  java.io.OutputStream;
import  java.io.OutputStreamWriter;
import  java.io.PrintWriter;
import  java.io.Reader;
import  java.net.URL;
import  java.nio.channels.Channels;
import  java.nio.channels.ReadableByteChannel;
import  java.nio.channels.WritableByteChannel;
import  java.util.ArrayList;
import  java.util.HashMap;
import  java.util.Iterator;
import  java.util.Map;
import  java.util.Properties;
import  java.util.prefs.Preferences;
import  java.util.Stack;
import  javax.xml.transform.Result;
import  javax.xml.transform.Transformer;
import  javax.xml.transform.sax.SAXResult;
import  javax.xml.transform.sax.TransformerHandler;
import  org.xml.sax.Attributes;
import  org.xml.sax.ContentHandler;
import  org.xml.sax.DTDHandler;
import  org.xml.sax.EntityResolver;
import  org.xml.sax.ErrorHandler;
import  org.xml.sax.InputSource;
import  org.xml.sax.Locator;
import  org.xml.sax.SAXException;
import  org.xml.sax.SAXParseException;
import  org.xml.sax.SAXNotSupportedException;
import  org.xml.sax.SAXNotRecognizedException;
import  org.xml.sax.XMLFilter;
import  org.xml.sax.XMLReader;
import  org.xml.sax.helpers.AttributesImpl;
import  org.xml.sax.ext.DefaultHandler2;
import  org.xml.sax.ext.LexicalHandler;
import  org.apache.logging.log4j.Logger;
import  org.apache.logging.log4j.LogManager;

/** Base class for file format transformers
 *  defining common properties and methods.
 *  All Xtrans transformer transformer classes are derived from this class.
 *  A <em>transformer</em> is divided in two parts:
 *  <ul>
 *  <li>the <em>generator</em> which reads some external file format and fires SAX events,</li>
 *  <li>the <em>serializer</em> which accepts SAX events and writes the same external output file format.</li>
 *  </ul>
 *  <p>
 *  Some of the transformers implement general formats for all types of SAX events,
 *  among them the {@link XMLTransformer} and the PYXTransformer.
 *  For the programming language formats, the TokenTransformer
 *  implements a common, tabular representation.
 *  <p>
 *  Except for some members of the <em>org.teherba.xtrans.pseudo</em> subpackage, the transformers will
 *  reproduce the input file if their serializer receives the SAX events fired by their generator.
 *  This is tested by a sequence of the form:
 *  <blockquote>input file -&gt; XYZ generator -&gt; XML serializer
 *      -&gt; XML file -&gt; XML generator -&gt; XYZ serializer -&gt; output file,
 *  </blockquote>
 *  where the input and the output file do not differ (sometimes except for whitespace).
 *  <p>
 *  XML processing pipes can be built with some transformer's generator at the start and
 *  another (or the same) transformer's serializer at the end. In between, XSLT transformations
 *  can be performed, or the stream of SAX events may be modified by filters.
 *
 *  @author Dr. Georg Fischer
 */
public class BaseTransformer
        extends DefaultHandler2
        implements XMLFilter, TransformerHandler, Locator, LexicalHandler {

    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;

    /** whether XML escaping is done by Apache serializer, or must be done explicitely */
    protected boolean mustAmpEscape;

    //--------------------------------
    // local variables
    //--------------------------------
    /** input  file encoding, empty for binary (byte) data */
    protected String sourceEncoding;

    /** output file encoding, empty for binary (byte) data */
    protected String resultEncoding;

    /** reader for text   files */
    protected Reader        charReader;
    /** writer for text   files */
    protected PrintWriter   charWriter;
    /** reader for binary files */
    protected InputStream   byteReader;
    /** writer for binary files */
    protected OutputStream  byteWriter;

    /** whether to append to output file */
    protected boolean append;

    /** whether the file format is binary */
    protected boolean binary;

    /** namespace for XML (prefix including colon), maybe empty */
    protected String namespace;
    /** corresponding prefix (without colon), or empty */
    protected String namespacePrefix;
    /** corresponding URI, or empty */
    protected String namespaceURI;

    /** comma separated list of applicable file formats */
    private   String formatCodeList;

    /** comma separated list of usual file extensions for this format */
    private   String fileExtensionList;

    /** List of options for a transformation */
    private   Properties options;

    /** description for the format */
    protected String description;

    /** root element name */
    protected String rootTag;

    /** MIME type for display/store of the foreign format */
    protected String mimeType;

    /** empty attribute list */
    private static final AttributesImpl EMPTY_ATTRS = new AttributesImpl();

    /** preferences node for Base64 encoding */
    private Preferences base64Node;

    /** name for a temporary node used for Base64 conversion */
    private static final String BASE64_NODE = "BASE64_NODE";

    /** Maps tags in the source to XML tags */
    private HashMap<String, String> sourceTagMap;
    /** Maps XML tags to tags in the result (inverse of <em>sourceTagMap</em>) */
    private HashMap<String, String> resultTagMap;
    /** Map for simple replacements in source strings */
    private HashMap<String, String> sourceReplaceMap;
    /** inverse Map for simple replacements */
    private HashMap<String, String> resultReplaceMap;

    /** system-specific representation of a new line characters sequence */
    public String newline;

    /** Data element tag */
    protected static final String DATA_TAG    = "td";
    /** Head element tag */
    protected static final String HEAD_TAG    = "th";
    /** Table element tag */
    protected static final String TABLE_TAG   = "table";
    /** Row element tag */
    protected static final String ROW_TAG     = "tr";
    /** Info element tag */
    protected static final String INFO_TAG    = "info";

    //--------------------------------
    // Constructor
    //--------------------------------
    /** Constructor with no arguments, initializes the local variables.
     */
    public BaseTransformer() {
        log = LogManager.getLogger(BaseTransformer.class.getName());
        setFormatCodes("xml");
        setDescription("XML");
        setFileExtensions("xml");
    } // constructor

    /** Initializes the (quasi-constant) global structures and variables.
     *  This method is called by the {@link XtransFactory} once for the
     *  selected generator and serializer.
     *  All heavy-weight initialization should be done here and in the
     *  derived methods. All derived constructors must be kept aa leightweight as possible,
     *  since they are all called in the constructor of {@link org.teherba.xtrans.XtransFactory}.
     */
    public void initialize() {
        mustAmpEscape = true; // used in XMLTransformer
        setSourceEncoding("UTF-8"); // ASCII + Western European
        setResultEncoding("UTF-8"); // for XML
        // all filehandles unopened so far:
        charReader  = null;
        charWriter  = null;
        byteReader  = null;
        byteWriter  = null;
        options     = new Properties();
        setAppend      (false);
        setBinaryFormat(false);
        setNamespace   ("", "");
        setMimeType    (""); // default: depends on isBinaryFormat
        base64Node          = Preferences.userNodeForPackage(BaseTransformer.class);
        int MAX_MAP         = 32;
        sourceTagMap        = new HashMap<String, String>(MAX_MAP);
        resultTagMap        = new HashMap<String, String>(MAX_MAP);
        sourceReplaceMap    = new HashMap<String, String>(MAX_MAP);
        resultReplaceMap    = new HashMap<String, String>(MAX_MAP);
        tagStack            = new Stack<String>();
        newline             = System.getProperty("line.separator");
        rootTag             = "document";
        // putEntityReplacements();
    } // initialize

    /*==========*/
    /*  Utility */
    /*==========*/
    /** a bunch of spaces */
    protected final static String SPACES = // 80 blanks, do   n e v e r   replace them by tabs!
            "                                                                                ";

    /** Create a string of spaces of a specified length
     *  @param spaceCount number of spaces to be created
     *  @return string of spaces
     */
    public String spaces(int spaceCount) {
        StringBuffer result = new StringBuffer(256);
        while (spaceCount > 0) { // advance - insert so many spaces
            int portion = spaceCount;
            if (portion > SPACES.length()) {
                portion = SPACES.length();
            }
            result.append(SPACES.substring(0, portion));
            spaceCount -= portion;
        } // while advancing
        return result.toString();
    } // spaces

    //--------------------------------
    // Encoding
    //--------------------------------
    /** Sets the input  file encoding
     *  @param encoding name of the encoding (UTF-8, ISO-8859-1),
     *  or empty for binary data
     */
    public void setSourceEncoding(String encoding) {
        sourceEncoding = encoding;
    } // setSourceEncoding

    /** Sets the output file encoding
     *  @param encoding name of the encoding (UTF-8, ISO-8859-1),
     *  or empty for binary data
     */
    public void setResultEncoding(String encoding) {
        resultEncoding = encoding;
    } // setResultEncoding

    /** Gets the input  file encoding
     *  @return encoding name of the source encoding (UTF-8, ISO-8859-1),
     *  or empty for binary data
     */
    public String getSourceEncoding() {
        return sourceEncoding;
    } // getSourceEncoding

    /** Gets the output file encoding
     *  @return encoding name of the result encoding (UTF-8, ISO-8859-1),
     *  or empty for binary data
     */
    public String getResultEncoding() {
        return resultEncoding;
    } // getResultEncoding

    //--------------------------------
    // Source and Result formats
    //--------------------------------
    /** Sets the input  file format
     *  @param format name of the format (UTF-8, ISO-8859-1),
     *  or empty for binary data
     */
/*
    public void setSourceFormat(String format) {
        sourceFormat = format;
    }
*/
    /** Sets the output file format
     *  @param format name of the format (UTF-8, ISO-8859-1),
     *  or empty for binary data
     */
/*
    public void setResultFormat(String format) {
        resultFormat = format;
    }
*/
    //--------------------------------------------------
    /** Gets the MIME type
     *  @return "major/minor", e.g. "application/octet-stream" or "text/plain"
     */
    public String getMimeType() {
        String result;
        if (mimeType.equals("")) { // we don't know it better
            result = isBinaryFormat() ? "application/octet-stream" : "text/plain";
        } else {
            result = mimeType;
        }
        return result;
    } // getMimeType

    /** Sets the MIME type
     *  @param mime MIME type in the form "major/minor"
     */
    public void setMimeType(String mime) {
        this.mimeType = mime;
    } // setMimeType
    //--------------------------------------------------
    /** Gets the first file extension
     *  @return file type (extension) without dot, normally 3 lowercase letters
     */
    public String getFileExtension() {
        int comma = fileExtensionList.indexOf(',');
        if (comma < 0) {
            comma = fileExtensionList.length();
        }
        return (comma > 0) ? fileExtensionList.substring(0, comma) : "dat";
    } // getFileExtension

    /** Sets the file extensions used for this file format
     *  @param extensionList comma separated list of usual file extensions
     *  for this format
     */
    protected void setFileExtensions(String extensionList) {
        this.fileExtensionList = extensionList;
    } // setFileExtensions
    //--------------------------------------------------
    /** Gets the namespace prefix
     *  @return namespace prefix without colon, or empty string
     */
    public String getNamespacePrefix() {
        return namespacePrefix;
    } // getNamespacePrefix

    /** Sets the namespace prefix
     *  @param prefix namespace prefix without colon, or empty string
     */
    public void setNamespacePrefix(String prefix) {
        this.namespacePrefix = prefix;
    } // setNamespacePrefix

    /** Gets the XML namespace URI
     *  @return Universal Resource Identifier for the namespace, or empty string
     */
    public String getNamespaceURI() {
        return namespaceURI;
    } // getNamespaceURI

    /** Sets the XML namespace URI
     *  @param uri Universal Resource Identifier for the namespace
     */
    public void setNamespaceURI(String uri) {
        this.namespaceURI = uri;
    } // setNamespaceURI

    /** Sets the XML namespace with prefix and URI
     *  @param prefix namespace prefix without colon, or empty string
     *  @param uri Universal Resource Identifier for the namespace, or empty String
     */
    public void setNamespace(String prefix, String uri) {
        this.namespacePrefix = prefix;
        this.namespace       = prefix.replaceAll(":", "");
        this.namespaceURI    = uri;
    } // setNamespace

    //--------------------------------------------------
    /** Sets the file open mode
     *  @param append true if write methods should append to output file
     */
    public void setAppend(boolean append) {
        this.append = append;
    } // setAppend

    /** Opens some named (ordinary) input or output file
     *  @param ifile 0 for source file, 1 for result file
     *  @param fileName name of the (ordinary) file to be opened, or null for STDIN/STDOUT
     *  @return whether the operation was successful
     */
    public boolean openFile(int ifile, String fileName) {
        boolean result = true;
        boolean isBinary = isBinaryFormat();
        setSourceEncoding(getOption("enc1", "UTF-8")); // should be symmetrical for testing
        setResultEncoding(getOption("enc2", "UTF-8"));
        try {
            // log.debug("openFile(" + isBinary + ", " + ifile + ", " + fileName + ");");
            switch (ifile) {
                case 0: // open input  from file
                    // log.debug("sourceEncoding = " + sourceEncoding + ";");
                    if (! isBinary) { // character mode
                        if (charReader != null) {
                            charReader.close();
                        }
                        ReadableByteChannel channel = (fileName != null)
                                ? (new FileInputStream (fileName)).getChannel()
                                : Channels.newChannel(System.in);
                        charReader = new BufferedReader(Channels.newReader(channel, sourceEncoding));
                    } else { // byte mode
                        if (byteReader != null) {
                            byteReader.close();
                        }
                        if (fileName == null) {
                            byteReader = new BufferedInputStream(System.in);
                        }
                        else {
                            byteReader = new BufferedInputStream(new FileInputStream(fileName));
                        }
                    } // byte input file
                    break;
                case 1:
                default: // open output into file
                    // log.debug("resultEncoding = " + resultEncoding + ";");
                    if (! isBinary) { // character mode
                        if (fileName == null) { // stdout
                            if (charWriter == null) {
                                charWriter = new PrintWriter(Channels.newWriter(Channels.newChannel(System.out), resultEncoding));
                            } // else leave stdout open, close it with main program
                        } else { // not stdout
                            if (charWriter != null) {
                                charWriter.close();
                            }
                            WritableByteChannel channel = (new FileOutputStream (fileName, append)).getChannel();
                            charWriter = new PrintWriter(Channels.newWriter(channel, resultEncoding));
                        } // not stdout
                    } else { // byte mode
                        if (fileName == null) { // stdout
                            if (byteWriter == null) {
                                byteWriter = new BufferedOutputStream(System.out);
                            } // else leave stdout open, close it with main program
                        } else { // not stdout
                            if (byteWriter != null) {
                                byteWriter.close();
                            }
                            byteWriter = new BufferedOutputStream(new FileOutputStream(fileName, append));
                        } // not stdout
                    } // byte output
                    break;
            } // switch ifile
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            result = false;
        }
        return result;
    } // openFile

    /** Opens some input or output stream
     *  @param ifile 0 for source, 1 for result
     *  @param stream object for stream input or output, respectively (may not be null)
     *  @return whether the operation was successful
     */
    public boolean openStream(int ifile, Object stream) {
        boolean result = true;
        boolean isBinary = isBinaryFormat();
        setSourceEncoding(getOption("enc1", "UTF-8")); // should be symmetrical for testing
        setResultEncoding(getOption("enc2", "UTF-8"));
        try {
            switch (ifile) {
                case 0: // open input  from stream
                    if (! isBinary) { // character mode
                        if (charReader != null) {
                            charReader.close();
                        }
                        charReader = new BufferedReader(new InputStreamReader ((InputStream)  stream, sourceEncoding));
                    } else { // byte mode
                        if (byteReader != null) {
                            byteReader.close();
                        }
                        byteReader = new BufferedInputStream                  ((InputStream)  stream);
                    } // byte input file
                    break;
                default: // open output into stream
                    if (! isBinary) { // character mode
                        if (charWriter != null) {
                            charWriter.close();
                        }
                        charWriter = new PrintWriter   (new OutputStreamWriter((OutputStream) stream, resultEncoding));
                    } else { // byte mode
                        if (byteWriter != null) {
                            byteWriter.close();
                        }
                        byteWriter = new BufferedOutputStream                 ((OutputStream) stream);
                    } // byte output
                    break;
            } // switch ifile
        } catch (Exception exc) {
            log.error(exc.getMessage());
            result = false;
        }
        return result;
    } // openStream

    /** Closes any open input and output files
     */
    public void closeAll() {
        try {
            if (charReader != null) {
                charReader.close();
            }
            if (charWriter != null) {
                charWriter.flush();
                charWriter.close();
            }
            if (byteReader != null) {
                byteReader.close();
            }
            if (byteWriter != null) {
                byteWriter.flush();
                byteWriter.close();
            }
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            exc.printStackTrace();
        }
    } // closeAll

    /** Closes open input and output files except for stdin and stdout.
     *  @param fileNames names of the input and output file, or null
     */
    public void closeAll(String[] fileNames) {
        try {
            if (fileNames[0] != null) {
                if (charReader != null) {
                    charReader.close();
                }
                if (byteReader != null) {
                    byteReader.close();
                }
            } // not stdin
            if (fileNames[1] != null) {
                if (charWriter != null) {
                    charWriter.close();
                }
                if (byteWriter != null) {
                    byteWriter.close();
                }
                // not stdout
            } else { // stdout
                if (charWriter != null) {
                    charWriter.flush();
                }
                if (byteWriter != null) {
                    byteWriter.flush();
                }
            }
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            exc.printStackTrace();
        }
    } // closeAll

    //--------------------------------------------------
    /** Sets the reader for character data.
     *  @param reader reader to be used to read character data
     */
    public void setCharReader(Reader reader) {
        charReader = reader;
    } // setCharReader

    /** Sets the reader for binary data.
     *  @param reader reader to be used to read binary data
     */
    public void setByteReader(InputStream reader) {
        byteReader = reader;
    } // setByteReader

    /** Sets the writer for character data.
     *  @param writer writer to be used to write character data
     */
    public void setCharWriter(PrintWriter writer) {
        charWriter = writer;
    } // setCharWriter

    /** Sets the writer for binary data.
     *  @param writer writer to be used to write binary data
     */
    public void setByteWriter(OutputStream writer) {
        byteWriter = writer;
    } // setByteWriter

    /** Gets the reader for character data.
     *  @return reader to be used to write character data
     */
    public Reader getCharReader() {
        return charReader;
    } // getCharReader

    /** Gets the reader for binary data.
     *  @return reader to be used to write binary data
     */
    public InputStream getByteReader() {
        return byteReader;
    } // getByteReader

    /** Gets the writer for character data.
     *  @return writer to be used to write character data
     */
    public PrintWriter getCharWriter() {
        return charWriter;
    } // getCharWriter

    /** Gets the writer for binary data.
     *  @return writer to be used to write binary data
     */
    public OutputStream getByteWriter() {
        return byteWriter;
    } // getByteWriter
    //--------------------------------------------------

    /** Tells whether the file format handled by this transformer
     *  uses byte files, or character files if false.
     *  @return true if the format uses byte files
     */
    public boolean isBinaryFormat() {
        return binary;
    } // isBinaryFormat

    /** Sets the binary format property
     *  @param binary true (false) if the format is (not) binary
     */
    public void setBinaryFormat(boolean binary) {
        this.binary = binary;
    } // setBinaryFormat

    /** Determines whether the head of the input file
     *  indicates that the file has a particular format
     *  @return true if the input file seems to be in this format
     */
    public boolean detect() {
        return false;
    } // detect

    /** Gets the list of applicable file format codes which are
     *  "understood" by this module
     *  @return list of (lowercase) file format codes
     *  separated by commata
     */
    public String getFormatCodes() {
        return formatCodeList;
    } // getFormatCodes

    /** Gets the first (main) code for the format
     *  @return file format code (lowercase)
     */
    public String getFirstFormatCode() {
        int pos = formatCodeList.indexOf(",");
        String result = formatCodeList;
        if (pos >= 0) {
            result = formatCodeList.substring(0, pos);
        }
        return result;
    } // getFirstFormatCode

    /** Sets the list of applicable file formats which are
     *  "understood" by this module
     *  @param list list of (lowercase) format codes
     *  separated by commata
     */
    protected void setFormatCodes(String list) {
        formatCodeList = list;
    } // setFormatCodes

    //--------------------------------
    // Description for user display
    //--------------------------------
    /** Gets the description for the format.
     *  @return text describing the format of this transformer
     */
    public String getDescription() {
        return description;
    } // getDescription

    /** Sets the description for the format.
     *  @param text text describing the format of this transformer
     */
    protected void setDescription(String text) {
        description = text;
    } // setDescription

    //--------------------------------
    // commandline options
    //--------------------------------
    /** Sets the value of a (String) option.
     *  Option names to be set may come from outside and therefore are forced to lower case.
     *  @param name name of the option
     *  @param value value to be associated with the name
     */
    protected void setOption(String name, String value) {
        options.setProperty(name.toLowerCase(), value);
        // log.info("setOption(\"" + name + "\", \"" + value + "\");");
    } // setOption

    /** Sets the value of an integer option.
     *  Option names to be set may come from outside and therefore are forced to lower case.
     *  @param name name of the option
     *  @param value value to be associated with the name
     */
    public void setIntOption(String name, int value) {
        options.setProperty(name.toLowerCase(), Integer.toString(value));
        // log.debug("setIntOption(\"" + name + "\", " + value + ");");
    } // setIntOption

    /** Parses a string for pairs of option names, possibly
     *  preceeded with "-", and values. Missing values default
     *  to "1".
     *  @param str string with pairs of option names and values,
     *  e.g. "-width 20 -group 2"
     */
    public void parseOptionString(String str) {
        // log.debug("parseOptionString(\"" + str + "\");");
        String [] parts = str.split("\\s+");
        boolean isName = false;
        String name = "";
        String value = "1";
        int ipart = 0;
        while (ipart < parts.length) {
            int sub = 0;
            if (parts[ipart].startsWith("-")) {
                isName = true;
                sub = 1;
            }
            if (isName) {
                if (name.length() > 0) {
                    setOption(name, value);
                }
                name = parts[ipart].substring(sub);
                value = "1";
                isName = false;
            } else {
                value = parts[ipart];
                isName = true;
            }
            ipart ++;
        } // while
        if (name.length() > 0) {
            setOption(name, value);
        }
    } // parseOptionString

    /** Gets the value of a (String) option, or the specified default.
     *  Option names are used by internal methods which always specify them in lower case.
     *  @param name name of the option
     *  @param def default value if option is not set
     *  @return option value
     */
    public String getOption(String name, String def) {
        String result = options.getProperty(name);
        return result != null ? result : def;
    } // getOption

    /** Gets the value of the numerical option, or the specified default.
     *  Option names are used by internal methods which always specify them in lower case.
     *  @param name name of the option
     *  @param def default value if option is not set or invalid
     *  @return option value
     */
    public int getIntOption(String name, int def) {
        int result = def;
        String temp = options.getProperty(name);
        if (temp != null && temp.length() > 0) {
            try {
                result = Integer.parseInt(temp);
            } catch (Exception exc) {
                // take default if error
            }
        }
        return result;
    } // getIntOption

    /** Sets properties for parsing:
     *  <ul>
     *  <li>sets a namespace</li>
     *  <li>sets encodings</li>
     *  </ul>
     *  from the <em>options</em>
     */
    protected void evaluateOptions() {
        lineNo   = -1;
        columnNo = -1;
        setSourceEncoding(getOption("enc1", "UTF-8")); // should be symmetrical for testing
        setResultEncoding(getOption("enc2", "UTF-8"));
        setNamespaceURI("http://org.teherba.xtrans/2006");
        String nsp = getOption("nsp" , "");
        if (! nsp.equals("")) {
            setNamespace(nsp, getNamespaceURI());
        }
        // log.debug("evaluateOptions();");
    } // evaluateOptions

    //--------------------------------
    // Conversion methods
    //--------------------------------
    /** Converts a character to an XML entity of the form "&amp;#x{hexstring};"
     *  @param st1 character for the entity
     *  @return XML entity, for example "&amp;#xa;"
     */
    public String string1ToEntity(String st1) {
        StringBuffer result = new StringBuffer(8);
        result.append("&#x");
        result.append(Integer.toHexString(st1.charAt(0)));
        result.append(';');
        return result.toString();
    } // string1ToEntity

    /** Converts an XML entity of the form "&amp;#x{hexstring};" or "&amp;#{decimaldigits};"
     *  to the corresponding character
     *  @param entity an XML entity, for example "&amp;#xa;"
     *  @return character for the entity
     */
    public String entityToString1(String entity) {
        String result = "°";
        try {
            if (entity.endsWith(";")) {
                entity = entity.substring(0, entity.length() - 1);
            }
            if (false) {
            } else if (entity.startsWith("&#x")) { // hex
                result = Character.toString((char) Integer.parseInt(entity.substring(3), 16));
            } else if (entity.startsWith("&#")) { // decimal
                result = Character.toString((char) Integer.parseInt(entity.substring(2)));
            } else {
                // leave degree - strange unique character
            }
        } catch (Exception exc) {
        }
        return result;
    } // entityToString1

    /** Replaces any entities (in fact "character references") by their
     *  corresponding character equivalents
     *  @param source a string containing XML entities, for example "&amp;#xa;"
     *  @return string with entities replaced
     */
    public static String replaceEntities(String source) {
        StringBuffer result = new StringBuffer(128);
        int start = 0;
        int ampos = source.indexOf('&', start);
        if (ampos < 0) {
            ampos = source.length();
        }
        result.append(source.substring(start, ampos));
        while (ampos < source.length()) {
            start = ampos + 1;
            int sempos = source.indexOf(';', start);
            if (sempos < 0) { // no semicolon found, do not evaluate
                result.append('&');
                sempos = ampos;
            } else { // evaluate source.substring(start = ampos+1, sempos)
                String name = source.substring(start, sempos);
                if (false) {
                } else if (name.startsWith("#x") || name.startsWith("#u")) { // &#x or &#u + hex character code
                    try {
                        result.append((char) Integer.parseInt(name.substring(2), 16));
                    } catch (Exception exc) { // invalid character in hex number
                        result.append(source.substring(ampos, sempos + 1)); // unchanged
                    }
                } else if (name.startsWith("#", start)) { // &# + decimal character code
                    try {
                        result.append((char) Integer.parseInt(name.substring(1), 10));
                    } catch (Exception exc) { // invalid character in decimal number
                        result.append(source.substring(ampos, sempos + 1)); // unchanged
                    }
                } else { // no '#'
                    if (false) {
                    } else if (name.equals("lt")) {
                        result.append('<');
                    } else if (name.equals("gt")) {
                        result.append('>');
                    } else if (name.equals("amp")) {
                        result.append('&');
                    } else if (name.equals("apos")) {
                        result.append('\'');
                    } else if (name.equals("quot")) {
                        result.append('"');
                    } else { // unknown entity, do not evaluate
                        result.append(source.substring(ampos, sempos + 1)); // unchanged
                    }
                } // no '#'
            } // sempos >= 0
            start = sempos + 1;
            ampos = source.indexOf('&', start);
            if (ampos < 0) {
                ampos = source.length();
            } // no '&' found
            result.append(source.substring(start, ampos)); // append rest of source string
        } // while ampos
        return result.toString();
    } // replaceEntities

    /** Converts from byte[] to Base64 encoding
     *  @param  bytes array of bytes to be encoded
     *  @param  len length of <em>bytes</em>
     *  @return the byte array encoded in Base64, that is a sequences
     *  of letters, digits, "+", "/" and "="
     */
    public String bytesToBase64(byte[] bytes, int len) {
        String result = "error";
        try {
            byte[] temp = new byte[len];
            System.arraycopy(bytes, 0, temp, 0, len);
            base64Node.putByteArray(BASE64_NODE, temp);
            result = base64Node.get(BASE64_NODE, "error2");
        } catch (Exception exc) {
            log.error("invalid encoding in conversion to Base64");
        }
        return result;
    } // bytesToBase64

    /** Converts from String to Base64 encoding
     *  @param str string of characters to be encoded
     *  @param encoding encoding encoding to be used for the generation
     *  of a byte array, e.g. "UTF-8" or "ISO-8859-1"
     *  @return the byte array encoded in Base64, that is a sequences
     *  of letters, digits, "+", "/" and "="
     */
    public String stringToBase64(String str, String encoding) {
        String result = "error";
        try {
            base64Node.putByteArray(BASE64_NODE, str.getBytes(encoding));
            result = base64Node.get(BASE64_NODE, "error2");
        } catch (Exception exc) {
            log.error("invalid encoding in conversion to Base64");
        }
        return result;
    } // stringToBase64

    /** Converts from Base64 encoding to byte[]
     *  @param base64 string of Base64 characters representing some byte array
     *  @return the decoded byte array
     */
    public byte[] base64ToBytes(String base64) {
        byte [] result = new byte[] {'e', 'r', 'r', 'o', 'r', '3'};
        try {
            base64Node.put(BASE64_NODE, base64.replaceAll("\\s", ""));
            result = base64Node.getByteArray(BASE64_NODE, new byte[] {'e', 'r', 'r', 'o', 'r', '4'});
        } catch (Exception exc) {
            log.error("invalid encoding in conversion from Base64");
        }
        return result;
    } // base64ToBytes

    /** Converts from Base64 encoding to String
     *  @param base64 string of Base64 characters representing some byte array
     *  @param encoding encoding encoding to be used for the interpretation
     *  of the byte array, e.g. "UTF-8" or "ISO-8859-1"
     *  @return the decoded byte array, encoded as a String
     */
    public String base64ToString(String base64, String encoding) {
        String result = "error1";
        try {
            base64Node.put(BASE64_NODE, base64.trim());
            result = new String(base64Node.getByteArray(BASE64_NODE
                , new byte[] {'e', 'r', 'r', 'o', 'r', '2'}), encoding);
        } catch (Exception exc) {
            log.error("invalid encoding in conversion from Base64");
        }
        return result;
    } // base64ToString

    //--------------------------------
    // Character mapping
    //--------------------------------
    /** Fills both tag maps with pairs of strings
     *  which are to be mapped.
     *  @param source tag to be mapped in a transformation to XML
     *  @param result tag to be mapped in a transformation from XML
     */
    protected void putTagMap(String source, String result) {
        sourceTagMap.put(source, result);
        resultTagMap.put(result, source);
    } // putTagMap

    /** Maps from a source tag to the corresponding XML tag
     *  during a transformation to XML
     *  @param tag key which is to be looked up
     *  @return resulting XML tag
     */
    protected String mapSourceTag(String tag) {
        String result = null;
        Object obj = sourceTagMap.get(tag);
        if (obj != null) {
            result = (String) obj;
        }
        return result;
    } // mapSourceTag

    /** Maps from a XML tag to the corresponding result tag
     *  during a transformation from XML
     *  @param tag key which is to be looked up
     *  @return resulting result tag
     */
    protected String mapResultTag(String tag) {
        String result = null;
        Object obj = resultTagMap.get(tag);
        if (obj != null) {
            result = (String) obj;
        }
        return result;
    } // mapResultTag

    /** Fills both replacement maps with pairs of strings
     *  which are to be replaced by each other.
     *  @param source source string for replacement in a transformation to XML, result string otherwise
     *  @param result result string for replacement in a transformation to XML, source string otherwise
     */
    protected void putReplacementMap(String source, String result) {
        sourceReplaceMap.put(source, result);
        resultReplaceMap.put(result, source);
    } // putReplacementMap

    /** Represents the entity escape character itself */
    private static final String AMP     = "&";
    /** Represents the escaped entity escape character */
    private static final String AMP_AMP = "&amp;";

    /** Fills both replacement maps with the pairs
     *  for common XML entities
     */
    protected void putEntityReplacements() {
        putReplacementMap("&"   , "&amp;"   );
        putReplacementMap("\""  , "&quot;"  );
        putReplacementMap("\'"  , "&apos;"  );
        putReplacementMap("<"   , "&lt;"    );
        putReplacementMap(">"   , "&gt;"    );
    } // putEntityReplacements

    /** Replaces all strings stored by <em>putReplacement</em>
     *  during a transformation to XML.
     *  "&amp;" must be replaced <em>before</em> all other strings if their replacement contains "&amp;".
     *  @param source string where to replace for transformation to XML
     *  @return resulting string after all replacements
     */
    public String replaceInSource(String source) {
        String result = source;
        String key;
        key = AMP;
        if (sourceReplaceMap.get(key) != null) {
            result = result.replaceAll("\\" + key, (String) sourceReplaceMap.get(key));
        }

        Iterator iter = sourceReplaceMap.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            if (! key.equals(AMP)) {
                result = result.replaceAll("\\" + key, (String) sourceReplaceMap.get(key));
            }
        } // while iter
        return result;
    } // replaceInSource

    /** Replaces nothing - allows for easy switching calls of this method
     *  to {@link #replaceInSource} and vice versa
     *  @param source string
     *  @return identical to source
     */
    public String replaceNoSource(String source) {
        return source;
    } // replaceNoSource

    /** Replaces nothing - allows for easy switching calls of this method
     *  to {@link #replaceInResult} and vice versa
     *  @param source string
     *  @return identical to source
     */
    public String replaceNoResult(String source) {
        return source;
    } // replaceNoResult

    /** Replaces all strings stored by <em>putReplacement</em>
     *  during a transformation from XML
     *  "&amp;" must be replaced <em>after</em> all other strings if their replacement contains "&amp;".
     *  @param source string where to replace for transformation from XML
     *  @return resulting string after all replacements
     */
    public String replaceInResult(String source) {
        String result = source;
        String key;
        Iterator iter = resultReplaceMap.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            if (! key.equals(AMP_AMP)) {
                result = result.replaceAll("\\" + key, (String) resultReplaceMap.get(key));
            }
        } // while iter

        key = AMP_AMP;
        if (resultReplaceMap.get(key) != null) {
            result = result.replaceAll("\\" + key, (String) resultReplaceMap.get(key));
        }
        return result;
    } // replaceInResult

    /** Determines which value is replaced for some key
     *  during a transformation to XML
     *  @param key which is to be replaced
     *  @return resulting replacement value
     */
    protected String getSourceReplacement(String key) {
        String result = null;
        Object obj = sourceReplaceMap.get(key);
        if (obj != null) {
            result = (String) obj;
        }
        return result;
    } // getSourceReplacement

    /** Determines which value is replaced for some key
     *  during a transformation from XML
     *  @param key which is to be replaced
     *  @return resulting replacement value
     */
    protected String getResultReplacement(String key) {
        String result = null;
        Object obj = resultReplaceMap.get(key);
        if (obj != null) {
            result = (String) obj;
        }
        return result;
    } // getResultReplacement

    ////////////////////////////////////////////////////////////////////
    // XMLReader - local variables
    ////////////////////////////////////////////////////////////////////

    /** standard SAX2 features of the XMLReader */
    private Map<String, Boolean> featureMap = new HashMap<String, Boolean>();

    /** standard SAX2 properties of the XMLReader */
    private Map<String, Object> propertyMap = new HashMap<String, Object>();

    /** standard SAX2 entity resolver */
    private EntityResolver entityResolver;

    /** standard SAX2 handler for the DTD */
    private DTDHandler dtdHandler;

    /** standard SAX2 lexical handler */
    private LexicalHandler lexicalHandler;

    /** standard SAX2 handler for document content */
    private ContentHandler contentHandler;

    /** standard SAX2 error handler */
    private ErrorHandler errorHandler;

    /** parent reader/filter */
    private XMLReader parent;

    // XMLReader.java - read an XML document.
    // http://www.saxproject.org
    // Written by David Megginson
    // NO WARRANTY!  This class is in the Public Domain.
    // Id: XMLReader.java,v 1.9 2004/04/26 17:34:34 dmegginson Exp
    /**
     * Interface for reading an XML document using callbacks.
     *
     * <blockquote>
     * <em>This module, both source code and documentation, is in the
     * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
     * See <a href='http://www.saxproject.org'>http://www.saxproject.org</a>
     * for further information.
     * </blockquote>
     *
     * <p><strong>Note:</strong> despite its name, this interface does
     * <em>not</em> extend the standard Java {@link java.io.Reader Reader}
     * interface, because reading XML is a fundamentally different activity
     * than reading character data.</p>
     *
     * <p>XMLReader is the interface that an XML parser's SAX2 driver must
     * implement.  This interface allows an application to set and
     * query features and properties in the parser, to register
     * event handlers for document processing, and to initiate
     * a document parse.</p>
     *
     * <p>All SAX interfaces are assumed to be synchronous: the
     * {@link #parse parse} methods must not return until parsing
     * is complete, and readers must wait for an event-handler callback
     * to return before reporting the next event.</p>
     *
     * <p>This interface replaces the (now deprecated) SAX 1.0 {@link
     * org.xml.sax.Parser Parser} interface.  The XMLReader interface
     * contains two important enhancements over the old Parser
     * interface (as well as some minor ones):</p>
     *
     * <ol>
     * <li>it adds a standard way to query and set features and
     *  properties; and</li>
     * <li>it adds Namespace support, which is required for many
     *  higher-level XML standards.</li>
     * </ol>
     *
     * <p>There are adapters available to convert a SAX1 Parser to
     * a SAX2 XMLReader and vice-versa.</p>
     *
     * @since SAX 2.0
     * @author David Megginson
     * @version 2.0.1+ (sax2r3pre1)
     * @see org.xml.sax.XMLFilter
     * @see org.xml.sax.helpers.ParserAdapter
     * @see org.xml.sax.helpers.XMLReaderAdapter
     */
    // public interface XMLReader

    ////////////////////////////////////////////////////////////////////
    // SAX Configuration - features and properties
    ////////////////////////////////////////////////////////////////////

    /**
     * Look up the value of a feature flag.
     *
     * <p>The feature name is any fully-qualified URI.  It is
     * possible for an XMLReader to recognize a feature name but
     * temporarily be unable to return its value.
     * Some feature values may be available only in specific
     * contexts, such as before, during, or after a parse.
     * Also, some feature values may not be programmatically accessible.
     * (In the case of an adapter for SAX1 Parser, there is no
     * implementation-independent way to expose whether the underlying
     * parser is performing validation, expanding external entities,
     * and so forth.) </p>
     *
     * <p>All XMLReaders are required to recognize the
     * http://xml.org/sax/features/namespaces and the
     * http://xml.org/sax/features/namespace-prefixes feature names.</p>
     *
     * <p>Typical usage is something like this:</p>
     *
     * <pre>
     * XMLReader r = new MySAXDriver();
     *
     *                         // try to activate validation
     * try {
     *   r.setFeature("http://xml.org/sax/features/validation", true);
     * } catch (SAXException e) {
     *   System.err.println("Cannot activate validation.");
     * }
     *
     *                         // register event handlers
     * r.setContentHandler(new MyContentHandler());
     * r.setErrorHandler(new MyErrorHandler());
     *
     *                         // parse the first document
     * try {
     *   r.parse("http://www.foo.com/mydoc.xml");
     * } catch (IOException e) {
     *   System.err.println("I/O exception reading XML document");
     * } catch (SAXException e) {
     *   System.err.println("XML exception reading document.");
     * }
     * </pre>
     *
     * <p>Implementors are free (and encouraged) to invent their own features,
     * using names built on their own URIs.</p>
     *
     * @param name The feature name, which is a fully-qualified URI.
     * @return The current value of the feature (true or false).
     * @exception org.xml.sax.SAXNotRecognizedException If the feature
     *            value can't be assigned or retrieved.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the feature name but
     *            cannot determine its value at this time.
     * @see #setFeature
     */
    public boolean getFeature(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        Boolean featureValue = this.featureMap.get(name);
        return (featureValue == null)
                ? false
                : featureValue.booleanValue();
    } // getFeature

    /**
     * Set the value of a feature flag.
     *
     * <p>The feature name is any fully-qualified URI.  It is
     * possible for an XMLReader to expose a feature value but
     * to be unable to change the current value.
     * Some feature values may be immutable or mutable only
     * in specific contexts, such as before, during, or after
     * a parse.</p>
     *
     * <p>All XMLReaders are required to support setting
     * http://xml.org/sax/features/namespaces to true and
     * http://xml.org/sax/features/namespace-prefixes to false.</p>
     *
     * @param name The feature name, which is a fully-qualified URI.
     * @param value The requested value of the feature (true or false).
     * @exception org.xml.sax.SAXNotRecognizedException If the feature
     *            value can't be assigned or retrieved.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the feature name but
     *            cannot set the requested value.
     * @see #getFeature
     */
    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        this.featureMap.put(name, value);
    }

    /**
     * Look up the value of a property.
     *
     * <p>The property name is any fully-qualified URI.  It is
     * possible for an XMLReader to recognize a property name but
     * temporarily be unable to return its value.
     * Some property values may be available only in specific
     * contexts, such as before, during, or after a parse.</p>
     *
     * <p>XMLReaders are not required to recognize any specific
     * property names, though an initial core set is documented for
     * SAX2.</p>
     *
     * <p>Implementors are free (and encouraged) to invent their own properties,
     * using names built on their own URIs.</p>
     *
     * @param name The property name, which is a fully-qualified URI.
     * @return The current value of the property.
     * @exception org.xml.sax.SAXNotRecognizedException If the property
     *            value can't be assigned or retrieved.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the property name but
     *            cannot determine its value at this time.
     * @see #setProperty
     */
    public Object getProperty(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.propertyMap.get(name);
    } // getProperty

    /**
     * Set the value of a property.
     *
     * <p>The property name is any fully-qualified URI.  It is
     * possible for an XMLReader to recognize a property name but
     * to be unable to change the current value.
     * Some property values may be immutable or mutable only
     * in specific contexts, such as before, during, or after
     * a parse.</p>
     *
     * <p>XMLReaders are not required to recognize setting
     * any specific property names, though a core set is defined by
     * SAX2.</p>
     *
     * <p>This method is also the standard mechanism for setting
     * extended handlers.</p>
     *
     * @param name The property name, which is a fully-qualified URI.
     * @param value The requested value for the property.
     * @exception org.xml.sax.SAXNotRecognizedException If the property
     *            value can't be assigned or retrieved.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the property name but
     *            cannot set the requested value.
     */
    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        this.propertyMap.put(name, value);
    } // setProperty

    ////////////////////////////////////////////////////////////////////
    // SAX Event handlers.
    ////////////////////////////////////////////////////////////////////

    /**
     * Allow an application to register an entity resolver.
     *
     * <p>If the application does not register an entity resolver,
     * the XMLReader will perform its own default resolution.</p>
     *
     * <p>Applications may register a new or different resolver in the
     * middle of a parse, and the SAX parser must begin using the new
     * resolver immediately.</p>
     *
     * @param resolver The entity resolver.
     * @see #getEntityResolver
     */
    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    } // setEntityResolver

    /**
     * Return the current entity resolver.
     *
     * @return The current entity resolver, or null if none
     *         has been registered.
     * @see #setEntityResolver
     */
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    } // getEntityResolver

    /** Allow an application to register a DTD event handler.
     *
     * <p>If the application does not register a DTD handler, all DTD
     * events reported by the SAX parser will be silently ignored.</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The DTD handler.
     * @see #getDTDHandler
     */
    public void setDTDHandler(DTDHandler handler) {
        this.dtdHandler = handler;
    } // setDTDHandler

    /** Return the current DTD handler.
     *
     * @return The current DTD handler, or null if none
     *         has been registered.
     * @see #setDTDHandler
     */
    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    } // getDTDHandler

    /** Allow an application to register a content event handler.
     *
     * <p>If the application does not register a content handler, all
     * content events reported by the SAX parser will be silently
     * ignored.</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The content handler.
     * @see #getContentHandler
     */
    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    } // setContentHandler

    /** Return the current content handler.
     *
     * @return The current content handler, or null if none has been registered.
     * @see #setContentHandler
     */
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    } // getContentHandler

    /** Allow an application to register a lexical handler.
     *
     * <p>If the application does not register a lexical handler, all
     * lexical events reported by the SAX parser will be silently
     * ignored.</p>
     *
     * @param handler The lexical handler.
     * @see #getLexicalHandler
     */
    public void setLexicalHandler(LexicalHandler handler) {
        this.lexicalHandler = handler;
    } // setLexicalHandler

    /** Return the current lexical handler.
     *
     * @return The current lexical handler, or null if none has been registered.
     * @see #setLexicalHandler
     */
    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    } // getLexicalHandler

    /**
     * Allow an application to register an error event handler.
     *
     * <p>If the application does not register an error handler, all
     * error events reported by the SAX parser will be silently
     * ignored; however, normal processing may not continue.  It is
     * highly recommended that all SAX applications implement an
     * error handler to avoid unexpected bugs.</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The error handler.
     * @see #getErrorHandler
     */
    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    } // setErrorHandler

    /**
     * Return the current error handler.
     *
     * @return The current error handler, or null if none
     *         has been registered.
     * @see #setErrorHandler
     */
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    } // getErrorHandler

    ////////////////////////////////////////////////////////////////////
    // Parsing.
    ////////////////////////////////////////////////////////////////////

    /**
     * Parse an XML document.
     *
     * <p>The application can use this method to instruct the XML
     * reader to begin parsing an XML document from any valid input
     * source (a character stream, a byte stream, or a URI).</p>
     *
     * <p>Applications may not invoke this method while a parse is in
     * progress (they should create a new XMLReader instead for each
     * nested XML document).  Once a parse is complete, an
     * application may reuse the same XMLReader object, possibly with a
     * different input source.
     * Configuration of the XMLReader object (such as handler bindings and
     * values established for feature flags and properties) is unchanged
     * by completion of a parse, unless the definition of that aspect of
     * the configuration explicitly specifies other behavior.
     * (For example, feature flags or properties exposing
     * characteristics of the document being parsed.)
     * </p>
     *
     * <p>During the parse, the XMLReader will provide information
     * about the XML document through the registered event
     * handlers.</p>
     *
     * <p>This method is synchronous: it will not return until parsing
     * has ended.  If a client application wants to terminate
     * parsing early, it should throw an exception.</p>
     *
     * @param input The input source for the top-level of the
     *        XML document.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see org.xml.sax.InputSource
     * @see #parse(java.lang.String)
     * @see #setEntityResolver
     * @see #setDTDHandler
     * @see #setContentHandler
     * @see #setErrorHandler
     */
    public void parse(InputSource input) throws IOException, SAXException {
        try {
            evaluateOptions();
            // convert the InputSource to our char and/or byte readers
            if (false) {
            } else if (input.getCharacterStream () != null) {
                charReader = new BufferedReader(input.getCharacterStream());
            } else if (input.getByteStream      () != null) {
                byteReader = input.getByteStream();
                charReader = new BufferedReader(new InputStreamReader(byteReader));
            } else if (input.getSystemId        () != null) {
                java.net.URL url = new URL(input.getSystemId( ));
                byteReader = url.openStream();
                charReader = new BufferedReader(new InputStreamReader(byteReader));
            } else {
                throw new SAXException("Invalid InputSource object");
            }
            generate();
            closeAll();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // parse

    /**
     * Parse an XML document from a system identifier (URI).
     *
     * <p>This method is a shortcut for the common case of reading a
     * document from a system identifier.  It is the exact
     * equivalent of the following:</p>
     *
     * <pre>
     * parse(new InputSource(systemId));
     * </pre>
     *
     * <p>If the system identifier is a URL, it must be fully resolved
     * by the application before it is passed to the parser.</p>
     *
     * @param systemId The system identifier (URI).
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see #parse(org.xml.sax.InputSource)
     */
    public void parse(String systemId) throws IOException, SAXException {
        try {
            evaluateOptions();
            openFile(0, systemId);
            generate();
            closeAll();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // parse

    /** Report a fatal XML parsing error.
     *  The default implementation throws a SAXParseException.
     *  Application writers may override this method in a subclass
     *  if they need to take specific actions for each fatal error
     *  (such as collecting all of the errors into a single report):
     *  in any case, the application must stop all regular processing
     *  when this method is invoked, since the document is no longer reliable,
     *  and the parser may no longer report parsing events.
     *  @param exc exception reproted by the parser
     */
    public void fatalError(SAXParseException exc) throws SAXException {
        log.error("BaseTransformer: " + exc.getMessage());
    } // fatalError

    //////////////////////////////////////////////////////////////////////////
    // Methods which turn an XMLReader in an XMLFilter
    //////////////////////////////////////////////////////////////////////////

    /** Gets the parent reader.
     *  This method allows the application to query the parent reader
     *  (which may be another filter). It is generally a bad idea
     *  to perform any operations on the parent reader directly:
     *  they should all pass through this filter.
     *  @return parent The parent filter, or null if none has been set.
     */
    public XMLReader getParent() {
        return parent;
    } // getParent

    /** Sets the parent reader.
     *  This method allows the application to link the filter to a
     *  parent reader (which may be another filter). The argument may not be null.
     *  @param parent The parent reader
     */
    public void setParent(XMLReader parent) {
        this.parent = parent;
    } // setParent

    //////////////////////////////////////////////////////////////////////////
    // Default implementations of Locator methods
    //////////////////////////////////////////////////////////////////////////

    /** system id for the locator */
    private String systemId;

    /** current line number in character file starting with 1, or -1 if not known */
    protected int lineNo;

    /** current column number in character file,
     *  byte position in byte file,
     *  always starting with 1
     *  or -1 if not known
     */
    protected int columnNo;

    /** Gets the publicId
     *  @return always null
     */
    public String getPublicId() {
        return null;
    } // getPublicId

    /** Gets the systemId
     *  @return always null
     */
    public String getSystemId() {
        return systemId;
    } // getSystemId

    /** Gets the line number
     *  @return line number in character file starting with 1, or -1 if not known
     */
    public int getLineNumber() {
        return lineNo;
    } // getLineNumber

    /** Gets the column number
     *  @return column number in character file or byte position in byte file,
     *  always starting with 1, or -1 if not known
     */
    public int getColumnNumber() {
        return columnNo;
    } // getColumnNumber

    //////////////////////////////////////////////////////////////////////////
    // Implementation of interface TransformerHandler
    //////////////////////////////////////////////////////////////////////////
    /** local handle for the result of this TransformerHandler */
    protected Result result;

    /** Set the Result associated with this TransformerHandler to be used for the transformation.
     *  @param result - A Result instance, should not be null.
     *  @throws IllegalArgumentException - if result is invalid for some reason.
     */
    public void setResult(Result result) throws IllegalArgumentException {
        this.result = result;
    } // setResult

    /** Get the Result associated with this TransformerHandler to be used for the transformation.
     *  @return A Result instance, should not be null.
     */
    public Result getResult() {
        return result;
    } // getResult

    /** Get the Transformer associated with this handler,
     *  which is needed in order to set parameters and output properties.
     *  @return Transformer associated with this TransformerHandler.
     */
    public Transformer getTransformer() {
        return null;
    } // getTransformer

    /** Set the base Id (URI or system Id) from where relative URLs will be resolved.
     *  @param systemId - Base URI for the source tree.
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    } // getSystemId

    //////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // Getters for XML tags.
    // The following methods are used here and in ./gramword.
    //--------------------------------

    /** Reconstructs an XML attribute list
     *  @param attrs attributes of the element
     *  @return a string of the form ' attr1="val1" attr2="val2"'
     */
    public String getAttrs(Attributes attrs) {
        StringBuffer result = new StringBuffer(128);
        if (attrs != null) {
            int nattr = attrs.getLength();
            int iattr = 0;
            while (iattr < nattr) {
                result.append(' ');
                result.append(attrs.getLocalName(iattr)); // QName
                result.append("=\"");
                String value = attrs.getValue(iattr);
                if (mustAmpEscape) {
                    value = value
                        .replaceAll("&", "&amp;")
                        .replaceAll("\\\"", "&quot;")
                        .replaceAll("\\\'", "&apos;")
                        .replaceAll("<", "&lt;")
                        .replaceAll(">", "&gt;")
                        .replaceAll("\\n", "&#xa;")
                        .replaceAll("\\t", "&#x9;")
                        ;
                } // mustAmpEscape
                result.append(value);
                result.append('"');
                iattr ++;
            } // while iattr
        } // attrs != null
        return result.toString();
    } // getAttrs

    /** Reconstructs an XML start tag
     *  @param qName name (tag) of the element
     *  @param attrs attributes of the element
     *  @return start tag with attributes
     */
    protected String getStartTag(String qName, Attributes attrs) {
        StringBuffer result = new StringBuffer(128);
        result.append('<');
        result.append(namespace);
        result.append(qName);
        result.append(getAttrs(attrs));
        result.append('>');
        return result.toString();
    } // getStartTag

    /** Reconstructs an XML empty tag without attributes
     *  @param qName name (tag) of the element
     *  @return empty tag
     */
    public String getEmptyTag(String qName) {
        StringBuffer result = new StringBuffer(128);
        result.append('<');
        result.append(namespace);
        result.append(qName);
        result.append(" />");
        return result.toString();
    } // getEmptyTag

    /** Reconstructs an XML empty tag with attributes
     *  @param qName name (tag) of the element
     *  @param attrs attributes of the element
     *  @return end tag
     */
    public String getEmptyTag(String qName, Attributes attrs) {
        StringBuffer result = new StringBuffer(128);
        result.append('<');
        result.append(namespace);
        result.append(qName);
        result.append(getAttrs(attrs));
        result.append(" />");
        return result.toString();
    } // getEmptyTag

    /** Reconstructs an XML end tag
     *  @param qName name (tag) of the element
     *  @return end tag
     */
    public String getEndTag(String qName) {
        StringBuffer result = new StringBuffer(128);
        result.append("</");
        result.append(namespace);
        result.append(qName);
        result.append('>');
        return result.toString();
    } // getEndTag

    /** Constructs an Attributes list from a single key-value pair
     *  @param key name of the attribute (without namespace)
     *  @param value value of the attribute
     *  @return Attributes2 list with a single attribute
     */
    public Attributes toAttribute(String key, String value) {
        AttributesImpl attrs = new AttributesImpl();
        if (value != null && value.length() > 0) {
            attrs.addAttribute("", key, key, "CDATA", value);
        }
        return attrs;
    } // toAttribute

    /** Constructs an Attributes list from key-value pairs
     *  @param keyValues keys and values in alternating sequence
     *  @return Attributes list with all attributes
     */
    public Attributes toAttributes(String[] keyValues) {
        AttributesImpl attrs = new AttributesImpl();
        int ipair = 0;
        while (ipair < keyValues.length) {
            if (keyValues[ipair] != null && keyValues[ipair].length() > 0) {
                attrs.addAttribute("", keyValues[ipair], keyValues[ipair], "CDATA", keyValues[ipair + 1]);
            }
            ipair += 2;
        } // while over pairs
        return attrs;
    } // toAttributes(String[])

    /** Constructs an Attributes list from key-value pairs
     *  @param keyValues keys and values in alternating sequence
     *  @return Attributes list with all attributes
     */
    public static Attributes attributesArray(String[] keyValues) {
        AttributesImpl attrs = new AttributesImpl();
        int ipair = 0;
        while (ipair < keyValues.length) {
            if (keyValues[ipair] != null && keyValues[ipair].length() > 0) {
                attrs.addAttribute("", keyValues[ipair], keyValues[ipair], "CDATA", keyValues[ipair + 1]);
            }
            ipair += 2;
        } // while over pairs
        return attrs;
    } // attributesArray(String[])

    /** Constructs an Attributes2 list from key-value pairs
     *  @param keyValues keys and values in alternating sequence
     *  @return Attributes2 list with all attribute
     */
    public Attributes toAttributes(ArrayList keyValues) {
        AttributesImpl attrs = new AttributesImpl();
        int ipair = 0;
        while (ipair < keyValues.size()) {
            attrs.addAttribute
                    ( ""
                    , (String) keyValues.get(ipair    )
                    , (String) keyValues.get(ipair    )
                    , "CDATA"
                    , (String) keyValues.get(ipair + 1)
                    );
            ipair += 2;
        } // while over pairs
        return attrs;
    } // toAttributes(ArrayList)

    /** Converts a leading string of whitespace characters to a
     *  description and returns the rest of the string starting with some
     *  non-whitespace character.
     *  @param line string with leading whitespace
     *  @return a string array {descr, rest} consisting of
     *  <ol>
     *  <li>a description to be attached to some XML element as a "w" attribute,
     *  consisting of numbers (count) and the letters "t" for tab, "s" for space,
     *  for example "4t2s" = 4 tabs followed by 2 spaces (a trailing "s" will be omitted)
     *  </li>
     *  <li>the rest of the string behind the whitespace</li>
     *  </ol>
     */
    public static String whitespaceToCode(String line) {
        StringBuffer code = new StringBuffer(32);
        int count = 0;
        int pos = 0;
        char coch = 's'; // assume space at the beginning
        if (true) {
            boolean busy = true;
            while (! busy && pos < line.length()) {
                char ch = line.charAt(pos ++);
                switch (ch) {
                    case ' ':
                        if (coch == 't') { // switch from spaces to tabs
                            code.append(Integer.toString(count));
                            code.append(coch);
                            count = 1;
                            coch = 's';
                        } else { // coch == 's'
                            count ++;
                        }
                        break;
                    case '\t':
                        if (coch == 's') { // switch from tabs to spaces
                            code.append(Integer.toString(count));
                            code.append(coch);
                            count = 1;
                            coch = 't';
                        } else { // coch == 's'
                            count ++;
                        }
                        break;
                    default: // first non-whitespace, break loop
                        busy = false;
                        pos --; // back on the non-whitespace
                        break;
                } // switch ch
            } // while busy && pos
            if (count > 0) { // last sequence of whitespace chars
                code.append(Integer.toString(count));
                if (coch != 's') { // omit trailing 's'
                    code.append(coch);
                }
            } // last sequence
        } // true
        return code.toString();
    } // whitespaceToCode

    /** Get a string of spaces and tabs from the "w" attribute.
     *  @param attrs attributes attached to some XML element, with a coded "w" attribute
     *  consisting of numbers (count) and the letters "t" for tab, "s" for space,
     *  for example "4t2s" = 4 tabs followed by 2 spaces (a trailing "s" may be omitted).
     *  @return string with space and/or tab characters, or the empty string
     */
    public static String attrToWhitespace(Attributes attrs) {
        return attrToWhitespace(attrs, "w");
    } // attrToWhitespace()

    /** Get a string of spaces and tabs from an attribute.
     *  @param attrs attributes attached to some XML element, with a coded attribute
     *  consisting of numbers (count) and the letters "t" for tab, "s" for space,
     *  for example "4t2s" = 4 tabs followed by 2 spaces (a trailing "s" may be omitted).
     *  @param attrName name of the whitespace attribute
     *  @return string with space and/or tab characters, or the empty string
     */
    public static String attrToWhitespace(Attributes attrs, String attrName) {
        StringBuffer result = new StringBuffer(32);
        String wsAttr = attrs.getValue("w");
        if (wsAttr == null) {
            wsAttr = attrs.getValue("s"); // for compatibility
        }
        if (wsAttr != null) { // with "w" attribute
            int count = 0;
            int pos = 0;
            while (pos < wsAttr.length()) {
                char ch = wsAttr.charAt(pos ++);
                switch (ch) {
                    case 's':
                        while (count > 0) {
                            result.append(' ');
                            count --;
                        }
                        break;
                    case 't':
                        while (count > 0) {
                            result.append('\t');
                            count --;
                        }
                        break;
                    default:
                        if (Character.isDigit(ch)) {
                            count = count * 10 + Character.digit(ch, 10);
                        }
                        break;
                } // switch
            } // while pos
            while (count > 0) { // no following letter => spaces
                result.append(' ');
                count --;
            }
        } // with "w" attribute
        return result.toString();
    } // attrToWhitespace(,)

    //------------------------------------------------
    // ContentHandler Methods
    //------------------------------------------------
    /** Handler for outgoing SAX events in a filter */
    protected ContentHandler filterHandler;

    /** Receive notification of the beginning of the document,
     *  and initialize the outgoing handler for a filter.
     *  @throws SAXException - any SAX exception,
     *  possibly wrapping another exception
     */
    public void startDocument()
            throws SAXException {
        SAXResult saxResult = (SAXResult) getResult();
        if (saxResult != null) {
            filterHandler = saxResult.getHandler();
        } else { // no parent set - assume XML serializer as default
            // System.err.println("BaseTransformer.startDocument: no parent set");
            XMLTransformer xmlTransformer = new XMLTransformer();
            xmlTransformer.setCharWriter(getCharWriter());
            filterHandler = (new SAXResult(xmlTransformer)).getHandler();
        } // default
    } // startDocument

    /** Receive notification of the end of the document.
     *  @throws SAXException - any SAX exception,
     *  possibly wrapping another exception
     */
    public void endDocument()
            throws SAXException {
    } // endDocument

    /** Receive notification of the start of an element.
     *  Looks for the element which contains raw lines.
     *  @param uri The Namespace URI, or the empty string if the element has no Namespace URI
     *  or if Namespace processing is not being performed.
     *  @param localName the local name (without prefix),
     *  or the empty string if namespace processing is not being performed.
     *  @param qName the qualified name (with prefix),
     *  or the empty string if qualified names are not available.
     *  @param attrs the attributes attached to the element.
     *  If there are no attributes, it shall be an empty Attributes object.
     *  @throws SAXException - any SAX exception,
     *  possibly wrapping another exception
     */
    public void startElement(String uri, String localName, String qName, Attributes attrs)
            throws SAXException {
    } // startElement

    /** Receive notification of the end of an element.
     *  Looks for the element which contains raw lines.
     *  Terminates the line.
     *  @param uri the Namespace URI, or the empty string if the element has no Namespace URI
     *  or if Namespace processing is not being performed.
     *  @param localName the local name (without prefix),
     *  or the empty string if Namespace processing is not being performed.
     *  @param qName the qualified name (with prefix),
     *  or the empty string if qualified names are not available.
     *  @throws SAXException - any SAX exception,
     *  possibly wrapping another exception
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
    } // endElement

    /** Receive notification of character data inside an element.
     *  @param ch the characters.
     *  @param start the start position in the character array.
     *  @param len the number of characters to use from the character array.
     *  @throws SAXException - any SAX exception,
     *  possibly wrapping another exception
     */
    public void characters(char[] ch, int start, int len)
            throws SAXException {
    } // characters

    //--------------------------------
    // fire* - main generator methods
    //--------------------------------
    /** Starts the XML document, writes the XML declaration
     *  with the proper encoding
     */
    public void fireStartDocument() {
        try {
            contentHandler.startDocument();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // fireStartDocument

    /** Ends the XML document
     */
    public void fireEndDocument() {
        try {
            contentHandler.endDocument();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // fireEndDocument

    /** Starts an XML element without namespace declaration
     *  @param tag name of the element
     *  @param attrs list of attributes
     */
    public void fireStartElement(String tag, Attributes attrs) {
        // log.debug("fireStartElement " + tag + (contentHandler == null ? " null" : " ch"));
        try {
            contentHandler.startElement("", tag, namespace + tag, attrs);
        } catch (Exception exc) {
            // System.out.println("namespacePrefix=" + namespacePrefix +  ", namespace=" + namespace
            //      + ", tag=" + tag + ", #attrs=" + attrs.getLength());
            log.error(exc.getMessage(), exc);
        }
    } // fireStartElement

    /** Ends an XML element
     *  @param tag name of the element
     */
    public void fireEndElement(String tag) {
        try {
            contentHandler.endElement("", tag, namespace + tag);
        } catch (Exception exc) {
            System.out.println("contentHandler=" + contentHandler
                    + ", tag=" + tag
                    + ", namespace=" + namespace
                    );
            log.error(exc.getMessage(), exc);
        }
    } // fireEndElement

    /** Starts and ends an entity
     *  @param name name of the entity
     */
    public void fireEntity(String name) {
        try {
           lexicalHandler.startEntity(name);
           lexicalHandler.endEntity  (name);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // fireEntity

    /** Writes a string as XML text (content of an element).
     *  Special XML characters like "&lt;", "&amp;" etc. must
     *  already be escaped as XML entities in <em>text</em>.
     *  @param text string to be written.
     */
    public void fireCharacters(String text) {
        try {
            contentHandler.characters(text.toCharArray(), 0, text.length());
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // fireCharacters

    /** Writes a new line in the XML output
     */
    public void fireLineBreak() {
        try {
            contentHandler.characters(newline.toCharArray(), 0, newline.length());
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // fireLineBreak

    /** Writes a string as XML comment.
     *  Special XML characters like "&lt;", "&amp;" etc. must
     *  already be encoded in <em>text</em>.
     *  @param text string to be written.
     */
    public void fireComment(String text) {
        try {
            lexicalHandler.comment(text.toCharArray(), 0, text.length());
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // fireComment

    /** Writes an XML processing instruction.
     *  @param target the processing instruction target
     *  @param data the processing instruction data, or null if none was supplied.
     *  The data does not include any whitespace separating it from the target
     */
    public void fireProcessingInstruction(String target, String data) {
        try {
            contentHandler.processingInstruction(target, data);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // fireProcessingInstruction

    //----------------------------------
    // fire* - generator helper methods
    //----------------------------------
    /** Starts a root XML element (with namespace declaration)
     *  @param tag name of the element
     */
    public void fireStartRoot(String tag) {
        if (namespaceURI.length() > 0) {
            String nsp = getNamespacePrefix();
            fireStartElement(tag, toAttribute("xmlns"
                    + (nsp.equals("") ? "" : (":" + nsp))
                    , getNamespaceURI())
                    );
        } else {
            fireStartElement(tag);
        }
    } // fireStartRoot

    /** Starts an XML element without attributes and namespace declaration
     *  @param tag name of the element
     */
    public void fireStartElement(String tag) {
        fireStartElement(tag, EMPTY_ATTRS);
    } // fireStartElement

    /** Writes an empty XML element
     *  @param tag name of the element
     *  @param attrs list of attributes
     */
    public void fireEmptyElement(String tag, Attributes attrs) {
        try {
            fireStartElement(tag, attrs);
            fireEndElement  (tag);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // fireEmptyElement

    /** Writes an empty XML element without attributes
     *  @param tag name of the element
     */
    public void fireEmptyElement(String tag) {
        fireEmptyElement(tag, EMPTY_ATTRS);
    } // fireEmptyElement

    /** Writes a simple XML element with no attributes, start tag, value, and end tag.
     *  The whole element is suppressed if the value is null or empty.
     *  @param tag name of the element
     *  @param value string for the content of the element
     */
    public void fireSimpleElement(String tag, String value) {
        try {
            if (value != null && ! value.equals("")) {
                fireStartElement(tag);
                // fireCharacters(replaceInSource(value));
                fireCharacters((value));
                fireEndElement(tag);
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // fireSimpleElement

    /** stack of element tags (for closing tags) */
    protected Stack<String> tagStack;

    /** Starts a nested XML element with attributes
     *  @param tag the element's tag
     *  @param attrs attributes for the element or empty string
     */
    protected void pushXML(String tag, Attributes attrs) {
        tagStack.push(tag);
        if (attrs != null) {
            fireStartElement(tag, attrs);
        } else {
            fireStartElement(tag);
        }
    } // pushXML

    /** Starts a nested XML element without attributes
     *  @param tag the element's tag
     */
    protected void pushXML(String tag) {
        tagStack.push(tag);
        fireStartElement(tag);
    } // pushXML

    /** Ends a nested XML element, if the specified element is on top of the stack.
     *  @param tag the element's tag which is expected on the stack
     *  @return whether the expected element was really on top of the stack
     */
    protected boolean popXML(String tag) {
        boolean result = false;
        if (! tagStack.isEmpty()) {
            String top = (String) tagStack.peek();
            if (top.equals(tag)) {
                tagStack.pop();
                fireEndElement(tag);
                result = true;
            } else if (true) {
                fireComment("popXML(\"" + tag + "\") found \"" + top + "\"");
            }
        } else {
            fireComment("stack underflow error");
        }
        return result;
    } // popXML

    /** Ends a nested XML element
     */
    protected void popXML() {
        if (! tagStack.isEmpty()) {
            String tag = (String) tagStack.pop();
            fireEndElement(tag);
        } else {
            fireComment("stack underflow error");
        }
    } // popXML

    /** Determines the tag in the stack's top element
     *  @return top element
     */
    protected String topXML() {
        String result = "";
        if (! tagStack.isEmpty()) {
            result = (String) tagStack.peek();
        }
        return result;
    } // topXML

    /** Writes a value as a sequence of LSB bytes.
     *  @param value string value of the attribute to be output
     *  @param len number of bytes to be written for the attribute
     *  @param hex whether the value is a string of hexadecimal digits
     */
    public void putLSB(String value, int len, boolean hex) {
        byte bytes[] = new byte[8];
        try {
            long number = Long.parseLong(value, hex ? 16 : 10);
            int ind = 0;
            while (ind < len) {
                bytes[ind] = (byte) (number & 0xff);
                number >>= 8;
                ind ++;
            } // while ind
            byteWriter.write(bytes, 0, len);
        } catch (Exception exc) {
            log.error(exc.getMessage() + "; invalid value " + value, exc);
        }
    } // putLSB

    //----------------------------------
    // generate and serialize
    //----------------------------------
    /** Transforms from the specified input file format to SAX events
     *  @return whether the transformation was successful
     */
    public boolean generate() {
        boolean result = true;
        // log.debug("BaseTransformer.generate - default implementation");
        return result;
    } // generate

    /** Transforms from SAX events to the specified output file format
     *  @return whether the transformation was successful
     */
/*
    private boolean serialize() {
        boolean result = true;
        // log.debug("BaseTransformer.serialize - default implementation");
        return result;
    } // serialize
*/

} // BaseTransformer
