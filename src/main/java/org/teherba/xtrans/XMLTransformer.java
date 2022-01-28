/*  Transforms (generates and serializes) XML files; must be UTF-(: äöüÄÖÜß
    @(#) $Id$
    2018-02-14: ignore comment output in internal dbiv XML
 *  2017-05-27: javadoc 1.8
    2016-10-14: ignore external DTDs with DummyEntityResolver
    2007-03-29, Georg Fischer: copied from SwiftTransformer
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
import  org.teherba.xtrans.CharTransformer;
import  org.teherba.xtrans.DummyEntityResolver;
import  java.util.Properties;
import  org.xml.sax.Attributes;
import  org.xml.sax.ContentHandler;
import  org.xml.sax.InputSource;
import  org.xml.sax.SAXException;
import  org.xml.sax.XMLReader;
import  org.xml.sax.helpers.XMLReaderFactory;
import  org.apache.logging.log4j.Logger;
import  org.apache.logging.log4j.LogManager;
import  org.apache.xml.serializer.Serializer;
import  org.apache.xml.serializer.SerializerFactory;
import  org.apache.xml.serializer.OutputPropertiesFactory;

/** Transformer (generator and serializer) for XML files.
 *  The serializer uses the one of xml.apache.org.xalan.
 *  @author Dr. Georg Fischer
 */
public class XMLTransformer extends CharTransformer {
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;

    /** No-args constructor
     */
    public XMLTransformer() {
        super();
        setFormatCodes  ("xml");
        setDescription  ("XML (eXtensible Markup Language)");
        setMimeType     ("text/xml");
    } // Constructor()

    /** Initializes the (quasi-constant) global structures and variables.
     *  This method is called by the {@link XtransFactory} once for the
     *  selected generator and serializer.
     */
    public void initialize() {
        super.initialize();
        log = LogManager.getLogger(XMLTransformer.class.getName());
        serializer = null;
        handler    = null;
    //  mustAmpEscape = false; // true activates the Xalan-J serializer
    } // initialize

    /** Transforms from the specified format to XML
     *  @return whether the transformation was successful
     */
    public boolean generate() {
        try {
            // log.error("XMLTransformer cannot be used as generator");
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(getContentHandler());
            parser.setErrorHandler  (getErrorHandler());
            parser.setEntityResolver(new DummyEntityResolver());
            this.setLexicalHandler(this);
            parser.setFeature       ("http://xml.org/sax/features/validation", false);
            parser.setFeature       ("http://xml.org/sax/features/namespaces", true);
            parser.setFeature       ("http://xml.org/sax/features/namespace-prefixes", true);
            parser.setFeature       ("http://xml.org/sax/features/external-general-entities", false);
            parser.setErrorHandler  (getErrorHandler());
            parser.setProperty      ("http://xml.org/sax/properties/lexical-handler", getLexicalHandler());
            parser.parse(new InputSource(charReader));
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return true;
    } // generate

    /*=======================*/
    /* XML SAX Handler       */
    /*=======================*/

    /** Serializes to XML with more detailed features */
    private Serializer serializer;
    /** Handles XML content */
    private ContentHandler handler;

    /** Opens some input or output file, and in the case of XML output,
     *  configure Apache's serializer and set it as content handler.
     *  If <em>handler</em> is null, some simple default serializing is done.
     *  @param ifile 0 for source file, 1 for result file
     *  @param fileName name of the file to be opened, or null for stdin/stdout
     *  @return whether the operation was successful
     */
    public boolean openFile(int ifile, String fileName) {
        boolean result = true;
        try {
            if (ifile == 1) { // output
                result = super.openFile(ifile, fileName);
                // log.warn("openFile(" + ifile + ", " + fileName + "), mustAmpEscape = " + mustAmpEscape);
                if (! mustAmpEscape) {
                    Properties props = OutputPropertiesFactory.getDefaultMethodProperties("xml");
                    props.setProperty("encoding", getResultEncoding());
                    props.setProperty("indent", "yes");
                    props.setProperty("standalone", "yes");
                    props.setProperty("{http\u003a//xml.apache.org/xalan}indent-amount", "2");
                    props.setProperty("{http\u003a//xml.apache.org/xalan}entities", "org/apache/xml/serializer/XMLEntities");
                //  props.setProperty("S_KEY_CONTENT_HANDLER", "org.apache.xml.serializer.Serializer");
                    serializer = SerializerFactory.getSerializer(props);
                    serializer.setWriter(getCharWriter());
                    handler = serializer.asContentHandler();
                } // if Apache serializer
            } else { // parser, input
                result = super.openFile(ifile, fileName);
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result;
    } // openFile

    /** Receive notification of the beginning of the document.
     */
    public void startDocument() {
        try {
            if (handler != null) {
                handler.startDocument();
            } else {
                charWriter.println("<?xml version=\"1.0\" encoding=\"" + getResultEncoding() + "\"?>");
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startDocument

    /** Receive notification of the end of the document.
     */
    public void endDocument() {
        try {
            if (handler != null) {
                handler.endDocument();
            } else {
                charWriter.println(); // trailing newline
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endDocument

    /** Receive notification of the start of an element.
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
            if (handler != null) {
                handler.startElement(uri, localName, qName, attrs);
            } else {
                String attrStr = getAttrs(attrs);
                charWriter.print("<" + qName
                //      + " was=\"here\" "
                        + attrStr + ">");
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startElement

    /** Receive notification of the end of an element.
     *  @param uri the Namespace URI, or the empty string if the element has no Namespace URI
     *  or if Namespace processing is not being performed.
     *  @param localName the local name (without prefix),
     *  or the empty string if Namespace processing is not being performed.
     *  @param qName the qualified name (with prefix),
     *  or the empty string if qualified names are not available.
     */
    public void endElement(String uri, String localName, String qName) {
        try {
            if (handler != null) {
                handler.endElement(uri, localName, qName);
            } else {
                charWriter.print("</" + qName + ">");
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endElement

    /** Receive notification of the end of an entity
     *  @param name The name of the entity. If it is a parameter entity,
     *  the name will begin with '%', and if it is the external DTD subset, it will be "[dtd]".
     */
    public void endEntity(String name) {
        try {
        /*
            getLexicalHandler().startEntity("&" + name + ";");
            getLexicalHandler().endEntity  ("&" + name + ";");
        */
            String ent = "";
            if (false) {
            } else if (name.equals("ouml")) { ent = "ö";
            } else if (name.equals("uuml")) { ent = "ü";
            } else if (name.equals("nbsp")) { ent = "&#xa0;";
            }
            if (handler != null) {
                getLexicalHandler().endEntity(name);
            } else if (charWriter != null) {
                charWriter.print(ent + "{{" + name + "}}");
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endEntity

    /** Receive notification of an XML comment.
     *  @param ch the characters of the comment.
     *  @param start the start position in the character array.
     *  @param len the number of characters to use from the character array.
     */
    public void comment(char[] ch, int start, int len) {
        try {
            if (handler != null) {
                getLexicalHandler().comment(ch, start, len);
            } else {
                if (charWriter == null) {
                     // log.error("null charWriter in comment: " + new String(ch, start, len));
                     // ignore, it would be for th einternal XML in dbiv expansion
                } else {
                    charWriter.print("<!--" + new String(ch, start, len) + "-->");
                }
            }
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
            if (handler != null) {
                handler.processingInstruction(target, data);
            } else {
                charWriter.print("<?" + target + " " + data + "?>");
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // processingInstruction

    /** Receive notification of character data inside an element.
     *  @param ch the characters.
     *  @param start the start position in the character array.
     *  @param len the number of characters to use from the character array.
     */
    public void characters(char[] ch, int start, int len) {
        try {
            if (handler != null) {
                handler.characters(ch, start, len);
            } else { // external serializer
                if (mustAmpEscape) {
                    charWriter.print(new String(ch, start, len)
                        .replaceAll("&", "&amp;")
                        .replaceAll("\\\"", "&quot;")
                        .replaceAll("\\\'", "&apos;")
                        .replaceAll("<", "&lt;")
                        .replaceAll(">", "&gt;")
                        );
                } else {
                    charWriter.print(new String(ch, start, len));
                } // escaping
            } // no external serializer
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // characters

} // XMLTransformer
