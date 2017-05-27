/*  Pseudo filter which passes the SAX events and serializes them to an output file in addition
    @(#) $Id$
    2016-10-16, Georg Fischer: copied from TeeFilter.java
*/
/*
 * Copyright 2016 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.xtrans.CharTransformer;
import  java.io.FileOutputStream;
import  java.io.PrintWriter;
import  java.nio.channels.Channels;
import  java.nio.channels.WritableByteChannel;
import  org.xml.sax.Attributes;
import  org.xml.sax.SAXException;
import  org.apache.log4j.Logger;

/** Pseudo transformer which accepts a SAX stream of events,
 *  passes them to the following serialier, and 
 *  serializes them to an additional output file.
 *  @author Dr. Georg Fischer
 */
public class TeeFilter extends CharTransformer {
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;
    
    /** Writer for the secondary file */
    private PrintWriter duplWriter;
    
    /** Name of the secondary file */
    private String duplName;
    
    /** whether the class was initialized */
    private boolean isInitialized;

    /** No-args Constructor.
     */
    public TeeFilter() {
        this("tee.tmp");
    } // Constructor

    /** Constructor with fileName.
     *  @param fileName path and name of the output file to which the SAX events are serialized
     */
    public TeeFilter(String fileName) {
        super();
        setFormatCodes("tee");
        setDescription("duplicate the output");
        setFileExtensions("xml");
        log = Logger.getLogger(TeeFilter.class.getName());
        duplName = fileName;
        isInitialized = false;
    } // Constructor

    /** Initializes the (quasi-constant) global structures and variables.
     *  This method is called by the {@link org.teherba.xtrans.XtransFactory} once for the
     *  selected generator and serializer.
     */
    public void initialize() {
        super.initialize();
        isInitialized = true;
    } // initialize

    /** Transforms from the specified format to XML
     *  @return whether the transformation was successful
     */
    public boolean generate() {
        log.warn("xtrans.TeeFilter cannot parse input");
        boolean result = false;
        return  result;
    } // generate

    /*===========================*/
    /* SAX handler for XML input */
    /*===========================*/

    /** Receive notification of the beginning of the document.
     *  Start with level 0.
     */
    public void startDocument() {
        if (! isInitialized) {
            initialize();
        }
        try {
            WritableByteChannel channel = (new FileOutputStream(duplName, append)).getChannel();
            duplWriter = new PrintWriter(Channels.newWriter(channel, getResultEncoding()));
            super.startDocument();
            filterHandler.startDocument();
            duplWriter.println("<?xml version=\"1.0\" encoding=\"" + getResultEncoding() + "\"?>");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startDocument

    /** Receive notification of the end of the document.
     *  For a wellformed XML document, the level must be 0 again.
     */
    public void endDocument() {
        try {
            filterHandler.endDocument();
            duplWriter.println(); // trailing newline
            duplWriter.close();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endDocument

    /** Receive notification of the start of an element.
     *  Increase the level, and attach the attribute.
     *  @param uri The Namespace URI, or the empty string if the element has no Namespace URI
     *  or if Namespace processing is not being performed.
     *  @param localName the local name (without prefix),
     *  or the empty string if Namespace processing is not being performed.
     *  @param qName the qualified name (with prefix),
     *  or the empty string if qualified names are not available.
     *  @param attrs the attributes attached to the element.
     *  If there are no attributes, it shall be an empty Attributes object.
     */
    public void startElement(String uri, String localName, String qName, Attributes attrs) {
        try {
            filterHandler.startElement(uri, localName, qName, attrs);
            String attrStr = getAttrs(attrs);
            duplWriter.print("<" + qName + attrStr + ">");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startElement

    /** Receive notification of the end of an element.
     *  Decrease the level.
     *  @param uri the Namespace URI, or the empty string if the element has no Namespace URI
     *  or if Namespace processing is not being performed.
     *  @param localName the local name (without prefix),
     *  or the empty string if Namespace processing is not being performed.
     *  @param qName the qualified name (with prefix),
     *  or the empty string if qualified names are not available.
     */
    public void endElement(String uri, String localName, String qName) {
        try {
            filterHandler.endElement(uri, localName, qName);
            duplWriter.print("</" + qName + ">");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endElement

    /** Receive notification of character data inside an element.
     *  @param ch the characters.
     *  @param start the start position in the character array.
     *  @param len the number of characters to use from the character array.
     */
    public void characters(char[] ch, int start, int len) {
        try {
            filterHandler.characters(ch, start, len);
            if (mustAmpEscape) {
                duplWriter.print(new String(ch, start, len)
                    .replaceAll("&", "&amp;")
                    .replaceAll("\\\"", "&quot;")
                    .replaceAll("\\\'", "&apos;")
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    );
            } else {
                duplWriter.print(new String(ch, start, len));
            } // escaping
       } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // characters

    /** Receive notification of an XML comment.
     *  @param ch the characters of the comment.
     *  @param start the start position in the character array.
     *  @param len the number of characters to use from the character array.
     */
    public void comment(char[] ch, int start, int len) {
        try {
            // ??? filterHandler.getLexicalHandler().comment(ch, start, len);
            duplWriter.print("<!--" + new String(ch, start, len) + "-->");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // comment

    /** Receive notification of a processing instruction.
     *  By default, do nothing. Application writers may override this method
     *  in a subclass to take specific actions for each processing instruction,
     *  such as setting status variables or invoking other methods.
     *  @param target The processing instruction target.
     *  @param data The processing instruction data, or null if none is supplied.
     *  @throws SAXException Any SAX exception, possibly wrapping another exception.
     */
    public void processingInstruction(String target, String data) throws SAXException {
        try {
            filterHandler.processingInstruction(target, data);
            duplWriter.print("<?" + target + " " + data + "?>");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // processingInstruction

} // TeeFilter
