/*  Selects the applicable transformer, and creates transformation pipelines
    @(#) $Id$
    2014-11-07: private TransformerHandlers -> public
    2010-12-07: -sqlpretty
    2010-07-28: config
    2010-06-14: -parse; -token
    2009-08-11: -seq
    2008-06-01: descMap with descriptions for subpackages
    2008-01-31: table in generated package.html
    2008-01-19: PL1, REXX, CSS, JavaScript, Pascal
    2007-12-06: commandline interface for package description
    2007-12-04: new package proglang with generalized scanner
    2007-09-17: RTF
    2007-08-31: JSON
    2007-07-03: NMEA-0183 (Sony CS1 GPS tracker)
    2007-06-22: GEDCOM
    2007-05-14: ExtraTransformer, YACCTransformer
    2007-03-15: w/o QueueTransformer
    2007-02-06: AEB43Transformer
    2007-01-03: CountingSerializer
    2006-12-28: DIFTransformer
    2006-10-13: copied from Transformer

    Usage:
        java -cp dist/xtrans.jar org.teherba.xtrans.XtransFactory
    Output files:
        src/main/java/org/teherba/xtrans//package.html
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
import  org.teherba.xtrans.BaseTransformer;
import  org.teherba.xtrans.XMLTransformer;
import  java.io.IOException; // thrown in createPipeline
import  java.util.Arrays; // asList
import  java.util.Iterator;
import  java.util.Properties;
import  java.util.StringTokenizer;
import  javax.xml.transform.Templates;
import  javax.xml.transform.TransformerFactory;
import  javax.xml.transform.sax.SAXResult;
import  javax.xml.transform.sax.SAXSource;
import  javax.xml.transform.sax.SAXTransformerFactory;
import  javax.xml.transform.sax.TransformerHandler;
import  javax.xml.transform.stream.StreamSource;
import  org.xml.sax.XMLReader;
import  org.apache.log4j.Logger;

/** Selects a specific transformer, and iterates over the descriptions
 *  of all transformers and their codes.
 *  Furthermore, it can create a transformation pipeline.
 *  @author Dr. Georg Fischer
 */
public class XtransFactory {
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;

    /** Names of input and output file, or null for STDIN/STDOUT. */
    private String[] fileNames;
    /** Input reader, generates SAX events */
    private BaseTransformer generator;
    /** Output writer, consumes SAX events */
    private BaseTransformer serializer;
    /** Factory for SAX XSLT transformers and translets */
    private static SAXTransformerFactory saxFactory;
    /** real path to web context */
    private String realPath;

    /** Set of transformers for different file formats
     */
    protected BaseTransformer[] allTransformers;

    /** No-args Constructor. Used for generation and serialization.
     *  Constructs all known transformers. Their constructors should
     *  not contain any heavy-weight initialization code, since they are
     *  all instantiated here, even if only two of them are really used.
     */
    public XtransFactory() {
        log = Logger.getLogger(XtransFactory.class.getName());
        saxFactory      = getSAXFactory();
        allTransformers = new BaseTransformer[] { null // since this allows for "," on next source line
                // the order here defines the order in documentation.jsp,
                // should be: "... group by package order by package, name"
                , new XMLTransformer            () // serializer for XML
                };
        realPath = "";
    } // Constructor

    /** Gets a SAX transformer factory
     *  @return properly configured SAXTransformerFactory
     */
    private SAXTransformerFactory getSAXFactory() {
        Properties props = System.getProperties();
        props.put("javax.xml.transform.TransformerFactory"
            //  , "org.apache.xalan.xsltc.trax.TransformerFactoryImpl");
                , "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        // System.setProperties(props);
        TransformerFactory tempFactory = TransformerFactory.newInstance();
        // TransformerFactory must support SAXSource and SAXResult
        return (tempFactory.getFeature(SAXSource.FEATURE) && tempFactory.getFeature(SAXResult.FEATURE))
                ? ((SAXTransformerFactory) tempFactory)
                : null;
    } // getSAXFactory

    /** maximum number of files / formats / encodings */
    private static final int MAX_FILE = 2;

    /** Sets the real path to the context of the web application
     *  @param path path to be set
     */
    public void setRealPath(String path) {
        realPath = path;
        if (path.length() > 0) {
            realPath += "/";
        }
    } // setRealPath

    /** Gets a handler for a filtering transformer.
     *  @param format name of the filter
     *  @return the filtering transformer for that format,
     *  or null if a handler could not be created
     *  @throws some exeption
     */
    public TransformerHandler getFilterHandler(String format) throws Exception {
        TransformerHandler handler = null;
        try {
            // log.debug("filter-name=" + format);
            handler = (new BasicFactory      ()).getTransformer(format); // an XtransFactory instance always returns the same object
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw exc;
        }
        return handler;
    } // getFilterHandler

    /** Gets a handler from a translet (precompiled stylesheet).
     *  @param transletClassName name of the translet class
     *  @param saxFactory the SAXTransformerFactory to be used
     *  @return a transformer handler which performs the XSLT,
     *  or null if a handler could not be created
     *  @throws some exeption
     */
    public TransformerHandler getTransletHandler(String transletClassName, SAXTransformerFactory saxFactory) throws Exception {
        TransformerHandler handler = null;
        try {
            saxFactory.setAttribute("use-classpath", "true");
            String transletName = "org.teherba.xtrans.translets." + transletClassName;
            // log.debug("translet-name=" + transletName);
            saxFactory.setAttribute("translet-name", transletName);
            Templates translet = saxFactory.newTemplates(new StreamSource());
            handler = saxFactory.newTransformerHandler(translet);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw exc;
        }
        return handler;
    } // getTransletHandler

    /** Gets a handler from a stylesheet source file.
     *  @param fileName path and name of the stylesheet file
     *  @return a transformer handler which performs the XSLT,
     *  or null if a handler could not be created
     *  @throws some exeption
     */
    public TransformerHandler getXSLHandler(String fileName) throws Exception {
        TransformerHandler handler = null;
        try {
            // log.warn("xsl-name = " + fileName);
            handler = saxFactory.newTransformerHandler(new StreamSource(fileName));
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw exc;
        }
        return handler;
    } // getXSLHandler

    /** Configures a SAX event generator, a series of XSLT transformations
     *  (either with a stylesheet or a translet) or filters, and a SAX event serializer.
     *  @param args commandline arguments:
     *  <pre>
     *  -format1 [-opt val]* infile [-xsl[t] stylesheet | -trans[let] transletname | -filter filterformat]* -format2 [-opt val]* outfile
     *  </pre>
     *  In the commandline, the position of the options, formats and files is irrelevant,
     *  only the left-to-right sequence matters for the sequence of operations
     *  to be performed.
     *  <p>
     *  A single "-" denotes STDIN as input file. The result is written to STDOUT if
     *  the output filename is missing. There may be 0, 1 or 2 filenames.
     *  <p>
     *  The default format is "-xml" for input and output.
     *  @throws some exeption, for example if the stylesheet could not be compiled
     */
    public void createPipeLine(String[] args) throws Exception {
        try {
            generator   = null;
            serializer  = null;
            int MAX_BASE = 2;
            BaseTransformer[] bases = new BaseTransformer[MAX_BASE];
            int ibase = 0; // number of xtrans transformers created so far
            fileNames = new String[MAX_BASE];
            int ifile = 0; // number of in/output filenames encountered so far
            TransformerHandler[] handlers // these extend ContentHandler + LexicalHandler
                    = new TransformerHandler[args.length]; // way too many
            int ihand = 0; // number of transformations requested so far (-xsl and -trans options)
            String options = ""; // all options and their values separated by spaces

            int iarg = 0;
            while (iarg < args.length) { // evaluate all commandline arguments
                String arg = args[iarg ++];
                if (arg.length() == 0) {
                    // ignore empty string array elements
                } else if (arg.startsWith("-filt" )) { // -filter
                    if (iarg >= args.length) {
                        log.error(arg + " must be followed by filter format");
                        return;
                    }
                    handlers[ihand ++] = getFilterHandler(args[iarg ++]);
                } else if (arg.startsWith("-trans")) { // -translet
                    if (iarg >= args.length) {
                        log.error(arg + " must be followed by a translet name");
                        return;
                    }
                    handlers[ihand ++] = getTransletHandler(args[iarg ++], saxFactory);
                } else if (arg.startsWith("-xsl")  ) { // -xslt
                    if (iarg >= args.length) {
                        log.error(arg + " must be followed by a stylesheet name");
                        return;
                    }
                    handlers[ihand ++] = getXSLHandler(realPath + args[iarg ++]);
                } else if (arg.equals("-")         ) { // single hyphen -> read from STDIN
                    if (ifile >= MAX_BASE) {
                        log.error("more than " + MAX_BASE + " filenames: " + arg);
                        return;
                    }
                    fileNames[ifile ++] = null; // STDIN or STDOUT
                } else if (arg.startsWith("-")     ) { // format or option
                    BaseTransformer base = this.getTransformer(arg.substring(1));
                    if (base != null) { // was a valid format code
                        bases[ibase ++] = base;
                    } else { // must be an option
                        if (iarg >= args.length) {
                            log.error("option " + arg + " must be followed by a value");
                            return;
                        }
                        options += arg + " " + args[iarg ++] + " ";
                    } // option
                    if (ibase > MAX_BASE) {
                        log.error("more than " + MAX_BASE + " format codes: " + arg);
                        return;
                    }
                } else { // filename
                    if (ifile >= MAX_BASE) {
                        log.error("more than " + MAX_BASE + " filenames: " + arg);
                        return;
                    }
                    fileNames[ifile ++] = arg; // STDIN or STDOUT
                } // which type of argument
            } // while arguments

            // set the default formats
            while (ibase < MAX_BASE) {
                bases[ibase ++] = this.getTransformer("xml");
            } // default formats
            // set the default file specifications (null -> STDIN or STDOUT)
            while (ifile < MAX_BASE) {
                fileNames[ifile ++] = null;
            } // default files

            // configure the xtrans converters and open the files
            ifile = 0;
            while (ifile < MAX_BASE) {
                bases[ifile].parseOptionString(options); // options apply to input and output format
                // bases[ifile].openFile(ifile, fileNames[ifile]);
                ifile ++;
            } // while configure
            generator  = bases[0];
            serializer = bases[1];
            generator.setContentHandler(serializer);
            generator.setLexicalHandler(serializer);

            // insert the stylesheet or filter transformation handler(s) into the double linked chain
            int nhand = ihand; // number of XSLT transformation handlers
            if (nhand > 0) {
                bases[0].setContentHandler(handlers[0]); // feed the generator's SAX events into the first handler
            //  bases[0].setLexicalHandler(handlers[0]); // feed the generator's SAX events into the first handler
                bases[0].setProperty("http://xml.org/sax/properties/lexical-handler", handlers[0]);
                handlers[nhand - 1].setResult(new SAXResult(bases[1])); // feed result of last handler into serializer
            }
            ihand = nhand - 1;
            while (ihand > 0) {
                handlers[ihand - 1].setResult(new SAXResult(handlers[ihand])); // feed one handler's SAX events into the next handler
                ihand --;
            } // while ihand
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw exc;
        }
    } // createPipeLine

    /** Gets the generator for further configuration,
     *  for example in XtransServlet for the redirection of the input reader to an uploaded file
     *  @return producer of XML
     */
    public BaseTransformer getGenerator() {
        return generator;
    } // getGenerator

    /** Gets the serializer for further configuration,
     *  for example in XtransServlet for the redirection of the output reader to the servlet's response
     *  @return consumer of XML
     */
    public BaseTransformer getSerializer() {
        return serializer;
    } // getSerializer

    /** Opens both files
     */
    public void openFiles() {
        generator .openFile(0, fileNames[0]);
        serializer.openFile(1, fileNames[1]);
        if (fileNames[1] == null) { // stdout
            serializer.setAppend(true);
        }
    } // openFiles

    /** Close all remaining open files, especially STDOUT
     */
    public void closeFiles() {
        generator .closeAll(fileNames);
        serializer.closeAll(fileNames);
    } // closeFiles

    /** Runs an XML generator, a series of XSLT transformations
     *  (either with a stylesheet or a translet) and an XML serializer.
     */
    public void process() {
        openFiles();
        generator .generate(); // call all piped processors
        closeFiles();
    } // process

    /** Gets an iterator over all implemented transformers.
     *  @return list iterator over <em>allTransformers</em>
     */
    public Iterator getIterator() {
        Iterator result = (Arrays.asList(allTransformers)).iterator();
        result.next(); // skip initial null element
        return result;
    } // getIterator

    /** Gets the number of available transformers
     *  @return number of formats which can be spelled
     */
    public int getCount() {
        return allTransformers.length - 1; // minus [0] (== null)
    } // getCount

    /** Determines whether the format code denotes this
     *  transformer class.
     *  @param transformer the transformer to be tested
     *  @param format code for the desired format
     */
    private boolean isApplicable(BaseTransformer transformer, String format) {
        boolean result = false;
        // log.debug("tokenizer:" + transformer.getFormatCodes());
        StringTokenizer tokenizer = new StringTokenizer(transformer.getFormatCodes(), ",");
        while (! result && tokenizer.hasMoreTokens()) {
            // try all tokens
            if (format.equals(tokenizer.nextToken())) {
                result = true;
            }
        } // while all tokens
        return result;
    } // isApplicable

    /** Gets the applicable transformer for a specified format code.
     *  @param format abbreviation for the format according to ISO 639
     *  @return the transformer for that format, or <em>null</em> if the
     *  format was not found
     */
    public BaseTransformer getTransformer(String format) {
        BaseTransformer transformer = null;
        // determine the applicable transformer for 'format'
        int itrans = 1;
        while (itrans < allTransformers.length) {
            if (isApplicable(allTransformers[itrans], format)) { // found
                transformer = allTransformers[itrans];
                transformer.initialize();
                itrans = allTransformers.length; // break loop
            } // found
            itrans ++;
        } // while itrans
        // log.info("getTransformer(\"" + format + "\") = " + transformer);
        return transformer;
    } // getTransformer

} // XtransFactory
