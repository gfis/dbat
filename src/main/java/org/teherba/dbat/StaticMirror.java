/*  StaticMirror.java - Read all pages for a subdirectory and zip them together
 *  @(#) $Id$
 *  2017-06-17, Dr. Georg Fischer. Kein Feiertag mehr
 */
/*
 * Copyright 2017 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.common.URIReader;
import  org.apache.log4j.Logger;
import  java.io.File;
import  java.io.FileInputStream;
import  java.io.FileOutputStream;
import  java.io.InputStream;
import  java.io.OutputStream;
import  java.io.PrintWriter;
import  java.io.Serializable;
import  java.net.URI;
import  java.net.URL;
import  java.net.URLDecoder;
import  java.nio.channels.Channels;
import  java.nio.channels.WritableByteChannel;
import  java.text.SimpleDateFormat;
import  java.util.ArrayList;
import  java.util.Date;
import  java.util.HashMap;
import  java.util.Iterator;
import  java.util.HashMap;
import  java.util.LinkedList;
import  java.util.regex.Matcher;
import  java.util.regex.Pattern;
// for StAX - JSR 173
import  javax.xml.namespace.QName;
import  javax.xml.stream.XMLEventFactory;
import  javax.xml.stream.XMLEventReader;
import  javax.xml.stream.XMLEventWriter;
import  javax.xml.stream.XMLInputFactory;
import  javax.xml.stream.XMLOutputFactory;
import  javax.xml.stream.XMLStreamConstants;
import  javax.xml.stream.events.Attribute;
import  javax.xml.stream.events.Namespace;
import  javax.xml.stream.events.StartDocument;
import  javax.xml.stream.events.XMLEvent;

/** Generate a static set of HTML pages for all queries starting at the
 *  index page of some Dbat specification subdirectory. 
 *  The HTML pages can be stored in a subtree under the current directory,
 *  or in a ZIP file. 
 *  @author Dr. Georg Fischer
 */
public class StaticMirror implements Serializable {
    public final static String CVSID = "@(#) $Id$";
    /** log4j logger (category) */
    private Logger log;
    /** Debugging switch */
    private int debug = 1;
    /** path separator, works for Windows and Unix */
    private static final String SLASH = "/";
    /** encoding of files and URLs */
    private static final String ENCODING = "UTF-8";
    
    /** the pattern corresponding to the mirrorPrefix */
    private Pattern servletPattern;
    /** the String for the servletPattern */
    private String patternString;
    
    /** the String for the constant path to the Dbat servlet */
    private String servletPath;
    /** the String for the constant path to other files in /dbat */
    private String dbatPath;

    /** Queue of pairs (URL, newPath) which must still be processed */
    private LinkedList<String> pairQueue;
    
    /** maps a new file path (part of the mirrored set of HTML pages) to either:
     *  <ul>
     *  <li>"quest" if the file is already requested, or</li>
     *  <li>"ready" if it has been written, or</li>
     *  <li>"mkdir" if that subdirectory has alrady been created.</li>
     *  </ul>
     */
    private HashMap<String, String> newPathMap;

    /** No-args Constructor
     */
    public StaticMirror() {
        log        = Logger.getLogger(StaticMirror.class.getName());
        newPathMap = new HashMap<String, String>(512);
        pairQueue  = new LinkedList<String>();
        setStaticBase("mirror");
    } // Constructor

    /** the directory where to store the root of the static tree of HTML files */
    private String staticBase;
    /** Sets the root directory for HTML files
     *  @param dir path to be set as root
     */
    private void setStaticBase(String dir) {
        staticBase = dir + "/";
    } // setStaticBase

    /** The factory for the creation of new events */
    private XMLEventFactory factory = XMLEventFactory.newInstance();

 	/** ISO timestamp without milliseconds */
 	private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

    /** Modifies the attribute of some START_ELEMENT events
     *  @param attrName attribute to be modified
     *  @param qName qualified name of the element
     *  @param revent source event
     *  @return modified event
     */
    private XMLEvent modifyAttribute(String attrName, QName qName, XMLEvent revent) {
        // log.debug("qName: " + qName.getPrefix() + ":" + qName.getLocalPart());
        // modify <a>, <link>, <img> tags: href=, src= attributes
        Iterator niter  = revent.asStartElement().getNamespaces();
        Iterator aiter  = revent.asStartElement().getAttributes();
        ArrayList<Attribute> attrs = new ArrayList<Attribute>();
        while (aiter.hasNext()) {
            Attribute attr = (Attribute) aiter.next();
            QName qaName = attr.getName();
            // log.debug("qaName: " + qaName.getPrefix() + ":" + qaName.getLocalPart());
            String value = attr.getValue();
            
            attrs.add(factory.createAttribute(qaName, value));
        } // while attrIter
        return factory.createStartElement(qName, attrs.iterator(), niter);
    } // modifyASttribute
     
    /** Opens the input and output (file) streams, copies them transparently,
     *  but modifies the linking attributes (href=, src=) of &gt;a&lt;, 
     *  &gt;link&lt;, &gt;script&lt; and &gt;img&lt; elements
     *  @param source stream for input  (file)
     *  @param target stream for output (file)
     *  This code was adopted from <em>xtool.XmlnsPrefix.java</em>
     */
    public void filterStax(InputStream source, OutputStream target) {
        XMLEvent revent = null;
        try {
            // get reader and writer from factories
            XMLInputFactory  sourceFactory = XMLInputFactory .newInstance();
            XMLOutputFactory targetFactory = XMLOutputFactory.newInstance();
            sourceFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
            sourceFactory.setProperty(XMLInputFactory.IS_VALIDATING     , Boolean.FALSE);
            sourceFactory.setProperty(XMLInputFactory.SUPPORT_DTD       , Boolean.FALSE); // otherwise it takes very long
            XMLEventReader reader = sourceFactory.createXMLEventReader(source, ENCODING);
            XMLEventWriter writer = targetFactory.createXMLEventWriter(target, ENCODING);

            // loop over the input file
            while (reader.hasNext()) {
                revent = (XMLEvent) reader.next();
                if (debug >= 2) {
                    System.err.println("revent " + revent.getEventType());
                }
                switch (revent.getEventType()) { // depending on event type

                    case XMLStreamConstants.START_ELEMENT: 
                        XMLEvent wevent = revent; // assume that unchanged
                        QName qName = revent.asStartElement().getName();
                        String localPart = qName.getLocalPart().toLowerCase();
                        if (false) {
                        } else if (localPart.equals("html"  )) {
                            writer.add(factory.createCharacters("\n"));
                            writer.add(factory.createComment(" Modified by org.teherba.StaticMirror at " 
                            +  TIMESTAMP_FORMAT.format(new java.util.Date())));
                            writer.add(factory.createCharacters("\n"));
                        } else if (localPart.equals ("a"     )) {
                            wevent = modifyAttribute("href"  , qName, revent);
                        } else if (localPart.equals ("img"   )) {
                            wevent = modifyAttribute("src"   , qName, revent);
                        } else if (localPart.equals ("link")) {
                            wevent = modifyAttribute("href"  , qName, revent);
                        } else if (localPart.equals ("script")) {
                            wevent = modifyAttribute("src"   , qName, revent);
                        } else {
                            // wevent = revent;
                        }                           
                        writer.add(wevent);
                        break; // START_ELEMENT

                    default: 
                        // copy all other portions of XML unchanged
                        writer.add(revent);
                        break;
                } // switch event type
            } // while busy
            writer.flush();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } // try
    } // filterStax

    /** Derives a new pathname from an URI
     *  @param uri completed URI from href/src/link attribute
     *  @return string with the following replacements:
     *  <ul>
     *  <li>parameter separators ("&amp;") replaced by "/" </li>
     *  <li>"=" replaced by "."</li>
     *  <li>spaces or "+" replaced by "_"</li>
     *  </ul>
     */
    private String deriveNewPath(URI uri) {
        String result = null;
        try {
            String path     = uri.getPath    (); 
            String query    = uri.getQuery   (); 
            String fragment = uri.getFragment();
            if (query != null) { // Servlet request, skip over "&amp;spec="
                int pos = 0;
                if (query.startsWith("&amp;", pos)) { 
                    pos += 5; 
                }
                if (query.startsWith("&"    , pos)) { 
                    pos += 1; 
                }
                if (query.startsWith("spec=", pos)) { 
                    pos += 5; 
                }
                result = query.substring(pos);
                if (result.matches("\\w+\\.")) {
                    pos = result.indexOf(".");
                    result = result.substring(0, pos) + "/" + result.substring(pos + 1); // "." -> "/"
                }
                result = result
                        .replaceAll("&amp;", "/")
                        .replaceAll("="    , ".")
                        + ".html";
            } else { // normal file
                result = path;
            }
            result = staticBase + result;
            if (debug >= 1) {
                System.out.println("deriveNewPath(\"" + uri.toString() + "\") => \"" + result + "\"");
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result;
    } // deriveNewPath
       
    /** Default for a request URI to the Dbat context */
    private URI dbatURI = null;
    
    /** Returns a complete Dbat URI
     *  @param partialURL relative servlet or file path and maybe query
     *  In general, the partialURL takes the following forms:
     *  <pre>
     *  http://domainname:8080/dbat/servlet?&amp;spec=migr/index&amp;parm1=val1&amp;parm2=val2
     *  xxxxxxxxxxxxxxxxxxxxxx               xxxx         .
     *  yyyyyyyyyyyyyyyyyyyyyyyyyyyy        yyyyy
     
     *  http://domainname:8080/dbat/path/normal.file.and.extension
     *  xxxxxxxxxxxxxxxxxxxxxx               xxxx         .
     *  yyyyyyyyyyyyyyyyyyyyyyyyyyyy        yyyyy
     *  </pre>
     *  where the parts underlined with x's or y's are optional,
     *  and the sub-specification separator may be a "/" or a ".".
     */
    private URI completeURI(String dbatURL) {
        URI result = null;
        try {
            URI uri = new URI(dbatURL);
            String scheme   = uri.getScheme  (); if (scheme     == null) { scheme   = dbatURI.getScheme  (); }
            String userInfo = uri.getUserInfo(); if (userInfo   == null) { userInfo = dbatURI.getUserInfo(); }
            String host     = uri.getHost    (); if (host       == null) { host     = dbatURI.getHost    (); }
            int    port     = uri.getPort    (); if (port       == 0   ) { port     = dbatURI.getPort    (); }
            String path     = uri.getPath    (); 
            if (! path.startsWith("/dbat/")) {
                path = "/dbat/"  + path;
            }
            String query    = uri.getQuery   (); 
            String fragment = uri.getFragment();
            result = new URI(scheme, userInfo, host, port, path, query, fragment);
            if (debug >= 1) {
                System.out.println("completeURI(\"" + dbatURL + "\") -> \"" + result.toString() + "\"");
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result;
    } // completeURI

    /** Queues an URL and the corresponding <em>newPath</em> if it was not yet processed
     *  @param dbatURL URL to be queued
     *  @return whether the URL respectively its <em>newPath</em> was new (not yet previously queued)
     */
    private boolean enqueue(String dbatURL) {
        boolean result = true; // assume new URL, not yet stored
        try {
            URI fullURI    = completeURI(dbatURL);
            String newPath = deriveNewPath(fullURI);
            String type    = newPath.endsWith(".html") ? "xhtml" : "other";
            if (newPathMap  .get(newPath) == null) { // new
                newPathMap  .put(newPath, type);
                pairQueue.add(fullURI.toString());
                pairQueue.add(newPath);
                if (debug >= 1) {
                    System.out.println("enqueue(url=\"" + fullURI.toString() + "\", newPath=\"" + newPath + "\")");
                } 
            } else { // already stored
                result = false;
            } // already stored
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result;
    } // enqueue
    
    /** Walks all pages starting at the specified URL, 
     *  and modifies the HTML pages read such that all links to 
     *  other specifications are replaced by static links.
     *  @param withZip whether to pack output pages into one ZIP file
     *  @param dbatURL URL for a Dbat specification 
     *  which is the starting point for the mirroring
     *  @param outPath either the name of a subdirectory where to build the mirror,
     *  or the name of a ZIP file
     */
    private void process(boolean withZip, String dbatURL, String outPath) {
        try {
            dbatURI = new URI(dbatURL);
            setStaticBase(outPath); // these correspond
            enqueue(dbatURL);

            InputStream  source = null;
            OutputStream target = null;
            while (pairQueue.size() > 0) { // queue not yet exhausted
                String url       = pairQueue.poll();
                String newPath   = pairQueue.poll();
                int lastSlashPos = newPath.lastIndexOf(SLASH);
                String subDir    = newPath.substring(0, lastSlashPos);
                String status    = newPathMap.get(subDir);
                if (status == null) { // subDir also new
                    if (debug >= 1) {
                        System.out.println("mkdirs " + subDir);
                    }
                    (new File(subDir)).mkdirs();
                    newPathMap.put(subDir, "mkdir");
                } // subDir also new
                status = newPathMap.get(newPath);
                if (false) {
                } else if (status == null) {
                    System.err.println("StaticMirror.process, assertion: status=null");
                } else if (status.equals("ready")) {
                    // file was already copied, ignore
                } else { // process now
                    source = new URIReader(url, "byte");
                    target = new FileOutputStream(newPath);
                    if (false) {
                    } else if (status.equals("other")) { // copy transparently
                        byte[] buffer = new byte[4096];
                        int len = 0;
                        while ((len = source.read(buffer)) > 0) { // copy a chunk
                            target.write(buffer, 0, len);
                        } // while chunks
                    } else if (status.equals("xhtml")) { // filter and modify the links
                        if (true) {
                            filterStax(source, target);
                        } else {
                            filterHTML(new URIReader(url), new PrintWriter(target));
                        }
                    } else {
                        System.err.println("StaticMirror.process, assertion: wrong status=\"" + status + "\"");
                    }
                    source.close();
                    target.close();
                    newPathMap.put(newPath, "ready"); 
                } // process now
            } // while queue not yet exhausted

            /*
                if (withZip) {
                    // nyi 
                } else { // no zip 
                    
                } // no zip
            */
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // process

    //======================
    // Main method
    //======================

    /** Generate subdirectory (or a ZIP file, nyi) 
     *  with a static mirror of all pages 
     *  in a Dbat specification subdirectory.
     *  @param args command line arguments: [-zip] spec subdir
     *  <ul><li>(none) print a usage message</li>
     *  <li>-zip outfile pack all static pages into one zip file</li>
     *  <li>dbatURL complete URL (with scheme, host, port, path="/dbat/servlet/") of 
     *  a Dbat specification where to start the expansion</li>
     *  <li>target subdirectory or ZIP file which stores the static tree of mirrored pages</li>
     *  </ul>
     */
    public static void main(String[] args) {
        Logger log = Logger.getLogger(StaticMirror.class.getName());
        StaticMirror mirror = new StaticMirror();
        boolean withZip = false;
        String dbatURL  = "http://localhost:8080/dbat/servlet?spec=migr/index";
        String target   = "dummy";
        try {
            if (args.length == 0) { // usage
                System.out.println("usage: java -cp dist/dbat.jar org.teherba.dbat.StaticMirror"
                        + " [-zip] dbatURL target");
            } else { // mirror
                int iarg = 0;
                int iname = 0;
                while (iarg < args.length) {
                    if (false) {
                    } else if (args[iarg].equals("-zip")) {
                        withZip = true;
                    } else if (iname == 0) {
                        iname ++;
                        dbatURL = args[iarg];
                    } else if (iname == 1) {
                        iname ++;
                        target  = args[iarg];
                    }
                    iarg ++;
                } // while iarg
                mirror.process(withZip, dbatURL, target); 
            } // mirror
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // main

//===============================================================
// unused stuff

    /** Initializes the various URL/path/prefix and pattern Strings,
     *  and queues the starting page
     *  @param withZip whether to pack output pages into one ZIP file
     *  @param dbatURL URL for a Dbat specification 
     *  which is the starting point for the mirroring
     *  @param target either the name of a subdirectory where to build the mirror,
     *  or the name of a ZIP file
     *  In general, the dbatURL takes the following forms:
     *  <pre>
     *  http://domainname:8080/dbat/servlet?&amp;spec=migr/index&amp;parm1=val1&amp;parm2=val2
     *  xxxxxxxxxxxxxxxxxxxxxx               xxxx         .
     *  yyyyyyyyyyyyyyyyyyyyyyyyyyyy        yyyyy
     
     *  http://domainname:8080/dbat/path/normal.file.and.extension
     *  xxxxxxxxxxxxxxxxxxxxxx               xxxx         .
     *  yyyyyyyyyyyyyyyyyyyyyyyyyyyy        yyyyy
     *  </pre>
     *  where the parts underlined with x's or y's are optional,
     *  and the sub-specification separator may be a "/" or a ".".
     */
    private void initialize(boolean withZip, String dbatURL, String target) {
        if (debug >= 1) {
            System.out.println("initialize(\"" + dbatURL + "\")");
        }
        try {
        /* 
            dbatURI = new URI(dbatURL);
            setStaticBase(target); // these correspond
            enqueue(dbatURL);

            int questPos = dbatURL.indexOf("?");
            if (questPos < 0) {
                System.err.println("StaticMirror.initialize, assertion: no question mark in dbatURL=\"" + dbatURL + "\"");
            }
            String url       = pairQueue.poll();
            String newPath   = pairQueue.poll();
            pairQueue.add(url    );
            pairQueue.add(newPath); // queue restored

            int questPos = prefix.indexOf("?");
            String patternString = "";
            patternString += (prefix.substring(0, questPos));
            patternString += ("\\?(\\&(amp\\;)?)?");
            int slashPos = prefix.indexOf("/", questPos);
            if (slashPos < 0) {
                slashPos = prefix.length();
            }
            patternString += (prefix.substring(questPos + 1, slashPos));
            patternString += ("(\\/|\\.|\\%2\\w)");
            if (debug >= 1) {
                System.out.println("  -> patternString=\"" + patternString + "\"");
            }
            servletPattern = Pattern.compile(patternString);
            int lastSlashPos = dbatURL.lastIndexOf(SLASH);
        */    
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // initialize

   
    /** Derives a static pathname from the query string of a Dbat specification URL
     *  @param queryString the query string to be transformed
     *  @return derived HTML pathName
     */
    private String deriveStatic(String queryString) {
        String result = null;
        Matcher matcher = servletPattern.matcher(queryString);
        if (matcher.lookingAt()) {
            int prefixStart  = matcher.start();
            int prefixBehind = matcher.end  ();
            result = staticBase 
                    // + queryString.substring(0, prefixStart);
                    + queryString.substring(prefixBehind)
                        .replaceAll("\\&amp\\;", SLASH)
                        .replaceAll("\\=",       ".") // this is weak, the parameter separator could be "*", "," etc. also ???
                    + ".html";
        } else {
            System.out.println("deriveStatic.assertion: \"" + queryString + "\" did not match \"" + patternString + "\"");  
        }
        if (debug >= 2) {
            System.out.println("deriveStatic(queryString=\"" + queryString + "\") = \"" + result + "\"");
        } 
        return result;
    } // deriveStatic

    /** Queues an URL if is was not yet processed
     *  @param url the URL to be queued
     *  @param newPath pathname of the static HTML page file
     *  @return whether the URL was not yet previously queued
     */
    private boolean enqueue99(String url, String newPath) {
        url = url
                .replaceAll("\\%2[EeFf]", "/")
                .replaceAll("\\&amp\\;",  "&")
                ;
        boolean result = true; // assume not yet stored
        if (newPathMap  .get(url) == null) {
            newPathMap  .put(url, newPath);
            pairQueue.add(url);
            if (debug >= 1) {
                System.out.println("enqueue(url=\"" + url + "\", newPath=\"" + newPath + "\")");
            } 
        } else { // already stored
            result = false;
        } // already stored
        return result;
    } // enqueue99

    
    /** Sets the query string prefix 
     *  @param prefix starts with the desired prefix for the specification subdirectory
     */
    private void setMirrorPrefix(String prefix) {
        if (debug >= 1) {
            System.out.println("setMirrorPrefix(\"" + prefix + "\")");
        }
        int questPos = prefix.indexOf("?");
        String patternString = "";
        patternString += (prefix.substring(0, questPos));
        patternString += ("\\?(\\&(amp\\;)?)?");
        int slashPos = prefix.indexOf("/", questPos);
        if (slashPos < 0) {
            slashPos = prefix.length();
        }
        patternString += (prefix.substring(questPos + 1, slashPos));
        patternString += ("(\\/|\\.|\\%2\\w)");
        if (debug >= 1) {
            System.out.println("  -> patternString=\"" + patternString + "\"");
        }
        servletPattern = Pattern.compile(patternString);
    } // setMirrorPrefix

    private static Pattern hrefPattern = Pattern.compile("href=");

    /** Reads an HTML page from an URL and filters the contents of the HTML page
     *  for links to Dbat specifications which are replaced by 
     *  links to plain, static HTML files.
     *  @param uriReader for the URL to be read
     *  @param writer printer for the output file
     */
    public void filterHTML(URIReader uriReader, PrintWriter writer) {
        try {
            String line = null;
            int lineCount = 0;
            while ((line = uriReader.readLine()) != null) {
                lineCount ++;
                if (lineCount == 2) {
                    writer.println("<!-- written by StaticMirror with patterString \"" + patternString + "\" -->");
                }
                /* do the filtering here */ 
                Matcher matcher = hrefPattern.matcher(line);
                int oldStart = 0;
                while(matcher.find()) {
                    if (debug >= 2) {
                        System.out.print("filterHTML found: " + line);
                    }
                    int prefixStart  = matcher.start();
                    int prefixBehind = matcher.end  () + 1;
                    int quotePos = line.indexOf("\"", prefixBehind);
                    if (quotePos >= 0) { // quote found
                        writer.print(line.substring(oldStart, prefixBehind));
                        String url = line.substring(prefixBehind, quotePos);
                        String newPath = deriveNewPath(new URI(url));
                        writer.print(newPath);
                        oldStart = quotePos;
                        enqueue(url);
                        // quoteFound
                    } else { // quote not found
                        // assertion ???
                    }
                } // while busy
                writer.println(line.substring(oldStart));
            } // while line
            uriReader.close();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // filterHTML

} // StaticMirror
