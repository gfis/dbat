/*  Selects the applicable transformer, and creates transformation pipelines
    @(#) $Id$
 *  2017-05-27: javadoc 1.8
    2016-10-16: less imports; TeeFilter
    2016-09-17: dynamic ArrayList of transformers; MutiFormatFactory removed
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
import  org.teherba.xtrans.DummyEntityResolver;
import  org.teherba.xtrans.TeeFilter;
import  org.teherba.xtrans.XMLTransformer;
import  java.io.File;
import  java.io.PrintWriter;
import  java.util.ArrayList;
import  java.util.HashMap;
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
    /** ArrayList of transformers for different formats */
    protected ArrayList<BaseTransformer> transformers;

    /** Attempts to instantiate the class for some transformer = format
     *  @param transformerName name of the class for the transformer,
     *  without the prefix "org.teherba.xtrans.".
     */
    private void addClass(String transformerName) {
        try {
            BaseTransformer transformer = (BaseTransformer) Class.forName("org.teherba.xtrans."
                    + transformerName).newInstance();
            if (transformer != null) {
                // transformer.initialize();
                transformers.add(transformer);
            } // != null
        } catch (Exception exc) {
            log.debug(exc.getMessage(), exc);
            // ignore any error silently - this format will not be known
        }
    } // addClass

    /** No-args Constructor. Used for generation and serialization.
     *  Constructs all enabled transformers. Their constructors should
     *  not contain any heavy-weight initialization code, since they are
     *  all instantiated here, even if only two of them are really used.
     */
    public XtransFactory() {
        log        = Logger.getLogger(XtransFactory.class.getName());
        realPath   = "";
        saxFactory = getSAXFactory();
        try {
            transformers = new ArrayList<BaseTransformer>(64);
            transformers.add(new XMLTransformer());
            // the order here defines the order in documentation.jsp,
            // should be: "... group by package order by package, name"
        //  this.addClass("XMLTransformer");
            this.addClass("config.IniTransformer");
            this.addClass("config.MakefileTransformer");
            this.addClass("config.ManifestTransformer");
            this.addClass("config.PropertiesTransformer");
            this.addClass("database.DatabaseTransformer");
            this.addClass("edi.EdifactTransformer");
            this.addClass("edi.X12Transformer");
            this.addClass("finance.AEB43Transformer");
            this.addClass("finance.DATEVTransformer");
            this.addClass("finance.DTATransformer");
            this.addClass("finance.DTA2Transformer");
            this.addClass("finance.MT103Transformer");
            this.addClass("finance.MT940Transformer");
            this.addClass("finance.SWIFTTransformer");
            this.addClass("general.Col1Transformer");
            this.addClass("general.ColumnTransformer");
            this.addClass("general.HexDumpTransformer");
            this.addClass("general.JSONTransformer");
            this.addClass("general.LineTransformer");
            this.addClass("general.PYXTransformer");
            this.addClass("general.SeparatedTransformer");
            this.addClass("general.SiXMLTransformer"); // not yet
            this.addClass("geo.NMEATransformer");
            this.addClass("grammar.ExtraTransformer");
            this.addClass("grammar.YACCTransformer");
            this.addClass("image.raster.ExifGenerator");
            this.addClass("image.vector.WMFTransformer");
            this.addClass("misc.GEDCOMTransformer");
            this.addClass("misc.MorseCodeTransformer");
            this.addClass("net.Base64Transformer");
            this.addClass("net.LDIFTransformer");
            this.addClass("net.QuotedPrintableTransformer");
            this.addClass("net.URITransformer");
            this.addClass("office.data.DBaseTransformer");
            this.addClass("office.data.DIFTransformer");
            this.addClass("office.text.HitTransformer");
            this.addClass("office.text.RichTextTransformer");
            this.addClass("office.text.TeXTransformer");
            this.addClass("organizer.ICalendarTransformer");
            this.addClass("organizer.VCardTransformer");
            this.addClass("parse.ParseFilter");
            this.addClass("proglang.CTransformer");
            this.addClass("proglang.CobolTransformer");
            this.addClass("proglang.CppTransformer");
            this.addClass("proglang.CSSTransformer");
            this.addClass("proglang.FortranTransformer");
            this.addClass("proglang.JavaTransformer");
            this.addClass("proglang.JavaScriptTransformer");
            this.addClass("proglang.JCLTransformer");
            this.addClass("proglang.PascalTransformer");
            this.addClass("proglang.PL1Transformer");
            this.addClass("proglang.PostScriptTransformer");
            this.addClass("proglang.ProgramSerializer");
            this.addClass("proglang.REXXTransformer");
            this.addClass("proglang.RubyTransformer");
            this.addClass("proglang.SQLTransformer");
            this.addClass("proglang.SQLPrettyFilter");
            this.addClass("proglang.TokenTransformer");
            this.addClass("proglang.VisualBasicTransformer");
            this.addClass("pseudo.CountingSerializer");
            this.addClass("pseudo.FileTreeGenerator");
            this.addClass("pseudo.JavaImportChecker");
            this.addClass("pseudo.LevelFilter");
        //  this.addClass("pseudo.MailSerializer");
            this.addClass("pseudo.SequenceGenerator");
            this.addClass("pseudo.SystemGenerator");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // Constructor

    /** Determines whether the format code denotes this
     *  transformer class.
     *  @param transformer the transformer to be tested
     *  @param format code for the desired format
     *  @return whether to class can handle this format
     */
    private boolean isApplicable(BaseTransformer transformer, String format) {
        boolean result = false;
        // log.debug("tokenizer:" + transformer.getFormatCodes());
        StringTokenizer tokenizer = new StringTokenizer(transformer.getFormatCodes(), ",");
        while (! result && tokenizer.hasMoreTokens()) { // try all tokens
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
        Iterator<BaseTransformer> titer = getIterator();
        boolean busy = true;
        while (busy && titer.hasNext()) {
            transformer = titer.next();
            if (isApplicable(transformer, format)) { // found this format
                transformer.initialize();
                busy = false; // break loop
                // log.info("getTransformer(\"" + format + "\") = " + transformer);
                // found this format
            } else {
                transformer = null;
            }
        } // while busy
        return transformer;
    } // getTransformer

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
     */
    public TransformerHandler getFilterHandler(String format) {
        TransformerHandler handler = null;
        try {
            // log.debug("filter-name=" + format);
            handler = (new XtransFactory      ()).getTransformer(format);
                 // an XtransFactory instance always returns the same object
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return handler;
    } // getFilterHandler
    
    /** Gets the "tee" (duplicating) transformer.
     *  @param duplName name of the "T" output file 
     *  @return the duplicating transformer writing to that output file
     */
    public TransformerHandler getTeeFilterHandler(String duplName) {
        TransformerHandler handler = null;
        try {
            handler = new TeeFilter(duplName);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return handler;
    } // getTeeFilterHandler

    /** Gets a handler from a translet (precompiled stylesheet).
     *  @param transletClassName name of the translet class
     *  @param saxFactory the SAXTransformerFactory to be used
     *  @return a transformer handler which performs the XSLT,
     *  or null if a handler could not be created
     */
    public TransformerHandler getTransletHandler(String transletClassName, SAXTransformerFactory saxFactory) {
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
        }
        return handler;
    } // getTransletHandler

    /** Gets a handler from a stylesheet source file.
     *  @param fileName path and name of the stylesheet file
     *  @return a transformer handler which performs the XSLT,
     *  or null if a handler could not be created
     */
    public TransformerHandler getXSLHandler(String fileName) {
        TransformerHandler handler = null;
        try {
            // log.warn("xsl-name = " + fileName);
            handler = saxFactory.newTransformerHandler(new StreamSource(fileName));
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
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
     */
    public void createPipeLine(String[] args) {
        if (true) { // try {
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
                } else if (arg.startsWith("-tee" ))  { // -tee
                    if (iarg >= args.length) {
                        log.error(arg + " must be followed by an output filename");
                        return;
                    }
                    handlers[ihand ++] = getTeeFilterHandler(realPath + args[iarg ++]);
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
                            log.error("option \"" + arg + "\" must be followed by a value");
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
            generator.setEntityResolver(new DummyEntityResolver());

            // insert the stylesheet or filter transformation handler(s) into the double linked chain
            int nhand = ihand; // number of XSLT transformation handlers
            if (nhand > 0) {
                bases[0].setContentHandler(handlers[0]); // feed the generator's SAX events into the first handler
                try {
                    bases[0].setProperty("http://xml.org/sax/properties/lexical-handler", handlers[0]);
                    bases[0].setFeature("http://xml.org/sax/features/external-general-entities", false);
                } catch (Exception exc) {
                    // ignore
                }
                handlers[nhand - 1].setResult(new SAXResult(bases[1])); // feed result of last handler into serializer
            } // nhand > 0
            ihand = nhand - 1;
            while (ihand > 0) {
                handlers[ihand - 1].setResult(new SAXResult(handlers[ihand])); // feed one handler's SAX events into the next handler
                ihand --;
            } // while ihand
    /*
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw exc;
    */
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
        openFiles ();
        generator .generate(); // call all piped processors
        closeFiles();
    } // process

    /** Gets an Iterator over all implemented transformers.
     *  @return list iterator over {@link #transformers}
     */
    public Iterator<BaseTransformer> getIterator() {
        return transformers.iterator();
    } // getIterator

    /** Gets the number of available transformers
     *  @return number of formats which can be spelled
     */
    public int size() {
        return transformers.size();
    } // size

    /** Returns a list of available transformers
     */
    public String toString() {
        StringBuffer result = new StringBuffer(1024);
        Iterator<BaseTransformer> iter = this.getIterator();
        while (iter.hasNext()) {
            BaseTransformer trans = iter.next();
            String name = trans.getClass().getName();
            result.append(name);
            result.append(' ');
            result.append(trans.getFormatCodes());
            result.append("\n");
        } // while hasNext
        return result.toString();
    } // toString

    /** Maps subpackage names to their descriptions */
    private static HashMap<String, String> descMap;

    /** Stores the descriptions of all subpackages.
     */
    private static void storeSubPackages() {
        descMap = new HashMap<String, String>();
        descMap.put("config"        , "configuration file formats");
        descMap.put("edi"           , "electronic data interchange (business) formats");
        descMap.put("finance"       , "financial data formats (SWIFT et al.)");
        descMap.put("general"       , "general purpose file formats");
        descMap.put("geo"           , "geopositioning data formats");
        descMap.put("grammar"       , "grammar/syntax description languages");
        descMap.put("image"         , "graphics and image file formats");
        descMap.put("image.raster"  , "raster image file formats");
        descMap.put("image.vector"  , "vector image file formats");
        descMap.put("misc"          , "miscellaneous file formats");
        descMap.put("net"           , "Internet standard (RFC) file formats");
        descMap.put("office"        , "file formats for office applications");
        descMap.put("office.data"   , "office table and spreadsheet applications");
        descMap.put("office.text"   , "office text processing applications");
        descMap.put("organizer"     , "organizer (PIM) file formats");
        descMap.put("parse"         , "transforming parser");
        descMap.put("proglang"      , "programming languages");
        descMap.put("pseudo"        , "pseudo files and filters");
    } // storeSubPackages

    /** Main program, writes package descriptions for all subpackages.
     *  The code is taken from <em>web/documentation.jsp</em>.
     *  @param args commandline arguments (none)
     */
    public static void main(String args[]) {
        XtransFactory factory = new XtransFactory();
        Iterator iter = factory.getIterator();
        String appName = "xtrans";
        String oldPackage = "";
        String packageName = "";
        try {
            storeSubPackages();
            PrintWriter out = null;
            iter.next(); // skip over element [0] which is null
            while (iter.hasNext()) {
                BaseTransformer trans = (BaseTransformer) iter.next();
                String name = trans.getClass().getName();
                int pos = name.indexOf(appName + ".");
                name = name.substring(pos + appName.length() + 1);
                pos = name.lastIndexOf(".");
                packageName = name.substring(0, pos);
                name = name.substring(pos + 1);
                String superName = trans.getClass().getSuperclass().getName();
                pos = superName.indexOf(appName + ".");
                superName = superName.substring(pos + appName.length() + 1, pos + appName.length() + 1 + 4);
                if (! packageName.equals(oldPackage)) {
                    if (out != null) {
                        out.println("</table></blockquote></body>");
                        out.println("</html>");
                        out.close();
                    }
                    out = new PrintWriter(new File("src/main/java/org/teherba/"
                            + appName + "/"
                            + packageName.replaceAll("\\.", "/")
                            + "/package.html"));
                    oldPackage = packageName;
                    String desc = descMap.get(packageName);
                    if (desc == null) {
                        desc = "different " + packageName + " file formats";
                    }
                    out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
                    out.println("  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
                    out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
                    out.println("<head><!-- generated by XtransFormatFactory.main --></head><body>");
                    out.println("<p>Converters between XML and " + desc + ".\n");
                    out.println("<blockquote><table cellspacing=\"1\" cellpadding=\"0\" summary=\"Description of the various formats\">");
                } // new packageName
                out.print("<tr><td><strong>-" + trans.getFirstFormatCode() + "</strong></td><td>");
                out.print(trans.getDescription());
                out.println("</td></tr>");
            } // while iter.hasNext()
            if (out != null) {
                out.println("</table></blockquote></body>");
                out.println("</html>");
                out.close();
            }
        } catch (Exception exc) {
            System.err.println("package name " + packageName);
            System.err.println(exc.getMessage());
            exc.printStackTrace();
        } // try
    } // main

} // XtransFactory
