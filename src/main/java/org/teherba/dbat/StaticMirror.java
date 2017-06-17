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
package org.teherba.common;
import  org.teherba.common.URIReader;
import  java.io.File;
import  java.io.FileOutputStream;
import  java.io.PrintWriter;
import  java.io.Serializable;
import  java.nio.channels.Channels;
import  java.nio.channels.WritableByteChannel;
import  java.util.HashMap;
import  java.util.LinkedList;
import  java.util.regex.Matcher;
import  java.util.regex.Pattern;
import  org.apache.log4j.Logger;

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
    
    /** maps the URI of the Dbat specification to the static HTML pathname */
    private HashMap<String, String> uriMap;
    /** the URIs in this queue must still be processed */
    private LinkedList<String> uriQueue;

    /** No-args Constructor
     */
    public StaticMirror() {
        log      = Logger.getLogger(StaticMirror.class.getName());
        uriMap   = new HashMap<String, String>(512);
        uriQueue = new LinkedList<String>();
        setStaticRoot  ("mirror");
    } // Constructor

    /** the pattern corresponding to the {@link #mirrorPrefix} */
    private Pattern mirrorPattern;
    /** the String for mirrorPattern */
    private String patternString;
    
    /** Sets the query string prefix 
     *  @param prefix starts with the desired prefix for the specification subdirectory
     */
    private void setMirrorPrefix(String prefix) {
        if (debug > 0) {
            System.out.println("setMirrorPrefix(\"" + prefix + "\")");
        }
        int questPos = prefix.indexOf("?");
        String patternBuffer = "";
        patternBuffer += (prefix.substring(0, questPos));
        // patternBuffer.append("\\\\?(\\\\&(amp\\\\;)?)?");
        patternBuffer += ("\\?(\\&(amp\\;)?)?");
        int slashPos = prefix.indexOf("/", questPos);
        if (slashPos < 0) {
            slashPos = prefix.length();
        }
        patternBuffer += (prefix.substring(questPos + 1, slashPos));
        // patternBuffer.append("([\\\\/\\\\.]|\\\\%2\\\\w)");
        patternBuffer += ("(\\/|\\.|\\%2\\w)");
        patternString = patternBuffer;
        // patternBuffer.toString().replaceAll("\\=", "\\\\\\\\=");
        if (debug > 0) {
            System.out.println("  -> patternString=\"" + patternString + "\"");
        }
        mirrorPattern = Pattern.compile(patternString);
    } // setMirrorPrefix

    /** the directory where to store the root of the static tree of HTML files */
    private String staticRoot;
    /** Sets the root directory for HTML files
     *  @param dir path to be set as root
     */
    private void setStaticRoot(String dir) {
        staticRoot = dir + "/";
    } // setStaticRoot

    /** Derives a static pathname from the query string of a Dbat specification URI
     *  @param queryString the query string to be transformed
     *  @return derived HTML pathName
     */
    private String deriveStatic(String queryString) {
        String result = null;
    /*
        result = staticRoot + queryString
                .replaceAll(patternString, "/")
                .replaceAll("\\&amp\\;", SLASH)
                .replaceAll("\\=",       SLASH) // this is weak, the parameter separator could be "*", "," etc. also ???
                + ".html";
    */
        Matcher matcher = mirrorPattern.matcher(queryString);
        if (matcher.lookingAt()) {
            int prefixStart  = matcher.start();
            int prefixBehind = matcher.end  ();
            result = staticRoot 
                    // + queryString.substring(0, prefixStart);
                    + queryString.substring(prefixBehind)
                        .replaceAll("\\&amp\\;", SLASH)
                        .replaceAll("\\=",       SLASH) // this is weak, the parameter separator could be "*", "," etc. also ???
                    + ".html";
        } else {
            System.out.println("deriveStatic.assertion: \"" + queryString + "\" did not match \"" + patternString + "\"");  
        }
        if (debug > 0) {
            System.out.println("deriveStatic(queryString=\"" + queryString + "\") = \"" + result + "\"");
        } 
        return result;
    } // deriveStatic

    /** Queues an URI if is was not yet processed
     *  @param uri the URI to be queued
     *  @param staticName pathname of the static HTML page file
     *  @return whether the URI was not yet previously queued
     */
    private boolean enqueue(String uri, String staticName) {
    	uri = uri
    			.replaceAll("\\%2[EeFf]", "/")
    			.replaceAll("\\&amp\\;",  "&")
    			;
        if (debug > 0) {
            System.out.println("enqueue(uri=\"" + uri + "\", staticName=\"" + staticName + "\")");
        } 
        boolean result = true; // assume not yet stored
        if (uriMap  .get(uri) == null) {
            uriMap  .put(uri, staticName);
            uriQueue.add(uri);
        } else { // already stored
            result = false;
        } // already stored
        return result;
    } // enqueue

    /** Reads an HTML page from an URI and filters the contents of the HTML page
     *  for links to Dbat specifications which are replaced by 
     *  links to plain, static HTML files.
     *  The file is output via the parameter writer.
     *  @param uri URI to be read
     *  @param printer printer for the output file
     */
    public void filterPage(String uri, PrintWriter writer) {
        try {
            if (debug > 0) {
                System.out.println("filter(uri=\"" + uri + "\")");
            } 
            URIReader uriReader = new URIReader("http://localhost:8080/dbat/" + uri);
            String line = null;
            int lineCount = 0;
            while ((line = uriReader.readLine()) != null) {
                lineCount ++;
                if (lineCount == 2) {
                    writer.println("<!-- written by StaticMirror with patterString \"" + patternString + "\" -->");
                }
                /* do the filtering here */ 
                Matcher matcher = mirrorPattern.matcher(line);
                int oldStart = 0;
                while(matcher.find()) {
                    if (debug > 0) {
                        System.out.print("filterPage found: " + line);
                    }
                    int prefixStart  = matcher.start();
                    int prefixBehind = matcher.end  ();
                    int quotePos = line.indexOf("\"", prefixBehind);
                    if (quotePos >= 0) { // quote found
                        writer.print(line.substring(oldStart, prefixStart));
                        String staticName = deriveStatic(line.substring(prefixStart, quotePos));
                        writer.print(staticName);
                        oldStart = quotePos;
                        enqueue(line.substring(prefixStart, quotePos), staticName);
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
    } // filterPage
    
    /** Walks all pages starting at the specified URI, 
     *  and modifies the HTML pages read such that all links to 
     *  other specifications are replaced by static links.
     *  @param withZip whether to pack output pages into one ZIP file
     *  @param specURI start point for the mirroring
     */
    private void process(boolean withZip, String specURI) {
        try {
            int lastSlashPos = specURI.lastIndexOf(SLASH);
            setMirrorPrefix(specURI);
            // enqueue(specURI, deriveStatic(specURI));
            enqueue(specURI, staticRoot + "index.html");
            while (uriQueue.size() > 0) { // queue not yet exhausted
                String uri = uriQueue.poll();
                String staticName = uriMap.get(uri);
                if (staticName != null) { // new URI
                    lastSlashPos = staticName.lastIndexOf(SLASH);
                    String subDir = staticName.substring(0, lastSlashPos);
                    if (debug > 0) {
                        System.out.println("mkdirs " + subDir);
                    }
                    (new File(subDir)).mkdirs();
                    WritableByteChannel target = (new FileOutputStream (staticName)).getChannel(); // Channels.newChannel(System.out);
                    PrintWriter writer = new PrintWriter(Channels.newWriter(target, "UTF-8"));
                    filterPage(uri, writer);
                    writer.close();
                    uriMap.put(uri, staticName);
                    // new URI
                } else { // URI was already processed
                    // ignore
                } // URI was already processed         
            } // while queue not yet exhausted
            if (withZip) {
                /* 
                    nyi 
                */
            } else { // no zip 
                
            } // no zip
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // process

    //======================
    // Main method
    //======================

    /** Generate a ZIP file with a static mirror of all pages 
     *  in a specification subdirectory.
     *  @param args command line arguments: 
     *  <ul><li>(none) print a usage message</li>
     *  <li>-zip outfile pack all static pages into one zip file</li>
     *  <li>url address of the starting page of the specification subdirectry</li>
     *  </ul>
     */
    public static void main(String[] args) {
        Logger log = Logger.getLogger(StaticMirror.class.getName());
        StaticMirror mirror = new StaticMirror();
        boolean withZip = false;
        String specURI = "servlet?spec=migr/index";
        try {
            if (args.length == 0) { // usage
                System.out.println("usage: java -cp dist/dbat.jar org.teherba.dbat.StaticMirror"
                        + " [-zip] outpath specuri");
            } else { // mirror
                int iarg = 0;
                int iname = 0;
                while (iarg < args.length) {
                    if (false) {
                    } else if (args[iarg].equals("-zip")) {
                        withZip = true;
                    } else if (iname == 0) {
                        iname ++;
                        mirror.setStaticRoot(args[iarg]);
                    } else if (iname == 1) {
                        iname ++;
                        specURI = args[iarg];
                    }
                    iarg ++;
                } // while iarg
                mirror.process(withZip, specURI); 
            } // mirror
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // main

} // StaticMirror
