/*  Pseudo-abstract class for character file format transformers
    @(#) $Id$
 *  2017-05-27: javadoc 1.8
    2010-07-06: super.startDocument()
    2008-07-19: characters, error, FatalError, warning
    2006-09-20: copied from BaseTransformer
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
import  org.teherba.xtrans.CharRecord;
import  java.io.BufferedReader;
import  org.xml.sax.Attributes;
import  org.xml.sax.SAXException;
import  org.xml.sax.SAXParseException;
import  org.apache.log4j.Logger;

/** Base class for character file format transformers 
 *  defining common properties and methods.
 *  
 *  @author Dr. Georg Fischer
 */
public abstract class CharTransformer extends BaseTransformer {
    public final static String CVSID = "@(#) $Id$";
    
    /** log4j logger (category) */
    private Logger log;
    
    /** No-args Constructor
     */
    public CharTransformer() {
        super();
    } // Constructor
    
    /** Initializes the (quasi-constant) global structures and variables.
     *  This method is called by the {@link org.teherba.xtrans.XtransFactory} once for the
     *  selected generator and serializer.
     */
    public void initialize() {
        super.initialize();
        log = Logger.getLogger(CharTransformer.class.getName());
    } // initialize
    
    /** Tells that this specific format is a binary format
     *  @return false if character format, true for binary
     */
    public boolean isBinaryFormat() {
        setBinaryFormat(false);
        return false;
    } // isBinaryFormat

    /** Some formats are based on a character record */
    protected CharRecord record;
    
    /** Sets the underlying record
     *  @param subclassRecord character record for some subclass format
     */
    protected void setRecord(CharRecord subclassRecord) {
        this.record = subclassRecord;
    } // setRecord

    /** Fires several SAX events for the record (pseudo-abstract method)
     *  @param generator transformer with SAX event firing  methods
     */
    public void fireElements(BaseTransformer generator) {
    } // fireElements

    /*=============*/
    /* Generator   */
    /*=============*/
    
    /** Transforms from the specified format to XML
     *  @return whether the transformation was successful
     */
    public boolean generate() {
        boolean result = true;
        try {
            fireStartDocument();
            fireStartRoot(record.ROOT_TAG);
            fireLineBreak();
            while (record.read((BufferedReader) charReader)) {
                fireElements(this);
                fireLineBreak();
            } // while reading
            fireEndElement(record.ROOT_TAG);
            fireLineBreak();
            fireEndDocument();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return  result;
    } // generate

    /*=============*/
    /* SAX Handler */
    /*=============*/

    /** Some formats are based on a character record */
    protected CharRecord saxRecord;
    
    /** Sets the underlying record for SAX event processing
     *  @param subclassRecord character record for some subclass format
     */
    protected void setSAXRecord(CharRecord subclassRecord) {
        saxRecord = subclassRecord;
    } // setSAXRecord

    /** Receive notification of the beginning of the document,
     *  and initialize the outgoing handler for a filter.
     *  @throws SAXException - any SAX exception, 
     *  possibly wrapping another exception
     */
    public void startDocument() 
            throws SAXException {
        try {
            super.startDocument();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startDocument
    
    /** Receive notification of the start of an element.
     *  Pass processing to generated code in <em>AEB43RecordBase</em>.
     *  @param uri The Namespace URI, or the empty string if the element has no Namespace URI 
     *  or if Namespace processing is not being performed.
     *  @param localName the local name (without prefix), 
     *  or the empty string if Namespace processing is not being performed.
     *  @param qName the qualified name (with prefix), 
     *  or the empty string if qualified names are not available.
     *  @param attrs the attributes attached to the element. 
     *  If there are no attributes, it shall be an empty Attributes object.
     *  @throws SAXException for any SAX error
     */
    public void startElement(String uri, String localName, String qName, Attributes attrs) 
            throws SAXException {
        if (namespace.length() > 0 && qName.startsWith(namespace)) {
            qName = qName.substring(namespace.length());
        }
        try {
            saxRecord.startElement(uri, localName, qName, attrs);
        } catch (Exception exc) {
            throw new SAXException("error in startElement ", exc);
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
        if (false) {
        } else if (qName.equals(namespace + saxRecord.getRecordTag())) {
            saxRecord.write(charWriter);
            charWriter.println();
        } else {
        }
    } // endElement

    /** Receive notification of character data inside an element.
     *  @param ch the characters.
     *  @param start the start position in the character array.
     *  @param length the number of characters to use from the character array. 
     */
    public void characters(char[] ch, int start, int length) {
        charWriter.print(replaceInResult(new String(ch, start, length)));
    } // characters

    /** Receive notification of a parser error
     *  @param exc exception describing the error
     */
    public void error(SAXParseException exc) {
        charWriter.println("\n<!-- error: " + exc + "-->");
    } // error

    /** Receive notification of a fatal parser error
     *  @param exc exception describing the error
     */
    public void fatalError(SAXParseException exc) {
        charWriter.println("\n<!-- fatal error: " + exc + "-->");
    } // fatalError

    /** Receive notification of a parser warning
     *  @param exc exception describing the error
     */
    public void warning(SAXParseException exc) {
        charWriter.println("\n<!-- warning: " + exc + "-->");
    } // warning

} // CharTransformer
