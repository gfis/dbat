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
import  org.apache.logging.log4j.Logger;
import  org.apache.logging.log4j.LogManager;
import  java.io.File;
import  java.io.FileOutputStream;
import  java.io.InputStream;
import  java.io.OutputStream;
import  java.io.Serializable;
import  java.net.URI;
import  java.net.URL;
import  java.text.SimpleDateFormat;
import  java.util.ArrayList;
import  java.util.Date;
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
    private int debug = 0;
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
        log        = LogManager.getLogger(StaticMirror.class.getName());
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

    /** Path in the mirror set of the file currently filtered */
    private String currentPath;

    /** The factory for the creation of new events */
    private XMLEventFactory factory = XMLEventFactory.newInstance();

    /** ISO timestamp without milliseconds */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

    /** Gets a relative path from the current source file that contains the link,
     *  to the new file. 
     *  First, the common starting path is determined.
     *  A number of "../" sequences must be used to ascend to that starting point,
     *  followed by the <em>newPath</em> minus the part above the starting point.
     *  @param ownPath link to current source file
     *  @param newPath link to file in the mirror set
     *  @return the relative path
     */
    private String getRelativePath(String ownPath, String newPath) {
        StringBuffer result = new StringBuffer(128);
        String[] oparts = ownPath.split(SLASH);
        String[] nparts = newPath.split(SLASH);
        int olen = oparts.length;
        int nlen = nparts.length;

        // determine starting point
        int 
        ipart = 0;
        boolean busy = true;
        while (busy && ipart < olen && ipart < nlen) { // are they the same?
            busy = oparts[ipart].equals(nparts[ipart]);
            ipart ++;
        } // while same
        int start = ipart - 1;
        //         0     1    2 3 4 5 6
        // new:    dummy/migr/a/b/c
        // own:    dummy/migr/d/e/f/g
        // start=2, nlen =5, olen=6    
        //         0     1    2 3 4 5 6
        // new:    dummy/migr/a/b/c
        // own:    dummy/migr/d/e
        // start=2, nlen =5, olen=4    

        // walk to all fathers of own
        ipart = olen;
        while (ipart - 1 > start) {
            result.append("../");
            ipart --;
        } // while fathers
        
        // walk down the rest of newPath
        ipart = start;
        while (ipart < nlen) {
            if (ipart > start) {
                result.append(SLASH);
            }
            result.append(nparts[ipart]);
            ipart ++;
        } // while rest
        if (debug >= 2) {
        	System.out.println("      getRelativePath"
        			+ ", ownPath=\"" + ownPath + "\", newPath=\"" + newPath + "\", start=" + start);
        	System.out.println("        ownPart=\"" + oparts[start] + "\", nparts=\"" + nparts[start] + "\"");
        }

        return result.toString();
    } // getRelativePath
       
    /** Modifies the attribute of some START_ELEMENT events
     *  @param attrName attribute to be modified
     *  @param qName qualified name of the element
     *  @param revent source event
     *  @return modified event
     */
    private XMLEvent modifyAttribute(String attrName, QName qName, XMLEvent revent) {
        Iterator niter  = revent.asStartElement().getNamespaces();
        Iterator aiter  = revent.asStartElement().getAttributes();
        ArrayList<Attribute> attrs = new ArrayList<Attribute>();
        while (aiter.hasNext()) {
            Attribute attr = (Attribute) aiter.next();
            QName qaName = attr.getName();
            String attr2Name = qaName.getLocalPart();
            if (attr2Name.equals(attrName)) { // this attribute is interesting
                String oldLink = attr.getValue();
                String newLink = enqueue(oldLink);
                String relLink = ""; // asume invalid
                if (newLink != null) {
                	relLink = getRelativePath(currentPath, newLink);
                    if (debug >= 2) {
                        System.err.println("  modifyAttribute(\"" + attrName + "\"): \"" 
                        		+ oldLink + "\" -> \"" + newLink + "\", relative \"" + relLink + "\"");
                    }
                } else {
                	// relLink = "";
                }
                attrs.add(factory.createAttribute(qaName, relLink));
            /*    
                } else {
                    attrs.add(attr);
                }
            */
                // interesting
            } else { // uninteresting
                attrs.add(attr);
            } // uninteresting
        } // while attrIter
        return factory.createStartElement(qName, attrs.iterator(), niter);
    } // modifyAttribute
     
    /** Opens the input and output (file) streams, copies them transparently,
     *  but modifies the linking attributes (href=, src=) of &gt;a&lt;, 
     *  &gt;link&lt;, &gt;script&lt; and &gt;img&lt; elements
     *  @param ownPath path if the file to be filtered; in the mirror set
     *  @param source stream for input  (file)
     *  @param target stream for output (file)
     *  This code was adopted from <em>xtool.XmlnsPrefix.java</em>
     */
    public void filterStax(String ownPath, InputStream source, OutputStream target) {
        XMLEvent revent = null;
        currentPath = ownPath;
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
                switch (revent.getEventType()) { // depending on event type

                    case XMLStreamConstants.START_ELEMENT: 
                        XMLEvent wevent = revent; // assume that unchanged
                        QName qName = revent.asStartElement().getName();
                        String localPart = qName.getLocalPart().toLowerCase();
                        if (debug >= 2) {
                            System.err.println("localPart=\"" + localPart + "\"");
                        }
                        if (false) {
                        } else if (localPart.equals("html"  )) { // insert a timestamp before
                            writer.add(factory.createCharacters("\n"));
                            writer.add(factory.createComment(" Modified by org.teherba.StaticMirror at " 
                                    +  TIMESTAMP_FORMAT.format(new java.util.Date()) + " "));
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

    /** Default for a request URI to the Dbat context */
    private URI dbatURI = null;
    
    /** Returns a complete Dbat URI
     *  @param specURL relative servlet or file path and maybe query
     *  @return a complete URI independant of the current location
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
    private URI completeURI(String specURL) {
        URI result = null;
        try {
            URI uri = new URI(specURL);
            String scheme   = uri.getScheme  (); if (scheme     == null) { scheme   = dbatURI.getScheme  (); }
            String userInfo = uri.getUserInfo(); if (userInfo   == null) { userInfo = dbatURI.getUserInfo(); }
            String host     = uri.getHost    (); if (host       == null) { host     = dbatURI.getHost    (); }
            int    port     = uri.getPort    (); if (port       <  0   ) { port     = dbatURI.getPort    (); }
            String path     = uri.getPath    (); 
            String query    = uri.getQuery   (); 
            String fragment = uri.getFragment();
            if (! path.startsWith("/dbat/")) {
                path = "/dbat/"  + path;
            }
            result = new URI(scheme, userInfo, host, port, path, query, fragment);
            if (debug >= 2) {
                System.out.println("    completeURI(\"" + specURL + "\") -> \"" + result.toString() + "\""
                		// + ", dbatURI=\"" + dbatURI.toASCIIString() + "\""
                		);
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result;
    } // completeURI

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
                    result = query.substring(pos)
                    //  .replaceAll("/"    , ".")
                        .replaceAll("&amp;", ".")
                        .replaceAll("&"    , ".")
                    //  .replaceAll("="    , ".")
                        + ".html";
                } else {
                    // view, more, help etc.
                    result = null;
                }
	            result = staticBase + result;
            } else { // normal file
            	if (path.startsWith("/dbat/spec")) {
            		result = staticBase + path.substring(11); // 10 + next slash
            	} else {
                	result = null;
                }
            }
        /*  not thought through / untested ???; beware of trailing extension tests!
            if (fragment != null) {
            	result += "#" + fragment;
            }
        */
            if (debug >= 1) {
                System.out.println("    deriveNewPath(\"" + uri.toString() + "\") => \"" + result + "\"");
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result;
    } // deriveNewPath
    
    /** Queues an URL and the corresponding <em>newPath</em> 
     *  if it was not yet processed. 
     *  The new path always starts with the {@link #staticBase}.
     *  @param specURL URL to be queued
     *  @return the new path/filename in the mirror set
     */
    private String enqueue(String specURL) {
        String result = null; // assume new URL, not yet stored
        try {
            URI fullURI    = completeURI(specURL);
            String newPath = deriveNewPath(fullURI);
            if (newPath != null) {
                String type    = newPath.endsWith(".html") ? "xhtml" : "other";
                if (newPathMap  .get(newPath) == null) { // new
                    newPathMap  .put(newPath, type);
                    pairQueue.add(fullURI.toString());
                    pairQueue.add(newPath);
                } // new
                result = newPath;
                if (debug >= 1) {
                    System.out.println("  enqueue(url=\"" + fullURI.toASCIIString() 
                    		+ "\") -> \"" + newPath + "\", size=" + (pairQueue.size() / 2));
                } 
                // != null
            } else {
            	// link outside the set to be mirrored - do not enqueue
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result;
    } // enqueue
    
    /** Walks all pages starting at the specified URL, 
     *  and modifies the HTML pages read such that all links to 
     *  other specifications are replaced by static links.
     *  @param withZip whether to pack output pages into one ZIP file
     *  @param specURL URL for a Dbat specification 
     *  which is the starting point for the mirroring
     *  @param outPath either the name of a subdirectory where to build the mirror,
     *  or the name of a ZIP file
     */
    private void process(boolean withZip, String specURL, String outPath) {
        try {
            dbatURI = new URI(specURL);
            setStaticBase(outPath); // these correspond
            enqueue(specURL);

            InputStream  source = null;
            OutputStream target = null;
            while (pairQueue.size() > 0) { // queue not yet exhausted
                String url       = pairQueue.poll();
                String newPath   = pairQueue.poll();
                if (debug >= 0) {
                    System.out.println("process polled url=\"" + url + "\", newPath=\"" + newPath + "\"");
                }
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
                        if (debug >= 2) {
                            System.out.println("copyOther, url=\"" + url + "\"");
                        }
                        byte[] buffer = new byte[4096];
                        int len = 0;
                        while ((len = source.read(buffer)) > 0) { // copy a chunk
                            target.write(buffer, 0, len);
                        } // while chunks
                    } else if (status.equals("xhtml")) { // filter and modify the links
                        if (debug >= 2) {
                            System.out.println("filterStax, url=\"" + url + "\"");
                        }
                        filterStax(newPath, source, target);
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
     *  <li>specURL complete URL (with scheme, host, port, path="/dbat/servlet/") of 
     *  a Dbat specification where to start the expansion</li>
     *  <li>target subdirectory or ZIP file which stores the static tree of mirrored pages</li>
     *  </ul>
     */
    public static void main(String[] args) {
        Logger log = LogManager.getLogger(StaticMirror.class.getName());
        StaticMirror mirror = new StaticMirror();
        boolean withZip = false;
        String specURL  = "http://localhost:8080/dbat/servlet?spec=migr/index";
        String target   = "dummy";
        try {
            if (args.length == 0) { // usage
                System.out.println("usage: java -cp dist/dbat.jar org.teherba.dbat.StaticMirror"
                        + " [-zip] specURL target");
            } else { // mirror
                int iarg = 0;
                int iname = 0;
                while (iarg < args.length) {
                    if (false) {
                    } else if (args[iarg].equals("-zip")) {
                        withZip = true;
                    } else if (iname == 0) {
                        iname ++;
                        specURL = args[iarg];
                    } else if (iname == 1) {
                        iname ++;
                        target  = args[iarg];
                    }
                    iarg ++;
                } // while iarg
                mirror.process(withZip, specURL, target); 
            } // mirror
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // main

} // StaticMirror
