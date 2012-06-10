/*  Pseudo transformer which adds a level attribute to all elements, and outputs XML
    @(#) $Id$
	2010-07-06: super.startDocument(), and acceptable for -filter in a pipe
    2008-05-30, Dr. Georg Fischer: copied from 'CountingSerializer'
*/
/*
 * Copyright 2008 Dr. Georg Fischer <punctum at punctum dot kom>
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

package org.teherba.xtrans.pseudo;
import  org.teherba.xtrans.CharTransformer;
import  org.xml.sax.Attributes;
import  org.xml.sax.helpers.AttributesImpl;
import  org.apache.log4j.Logger;

/** Pseudo transformer which accepts a SAX stream of events,  
 *  adds a level attribute to all elements, and outputs XML.
 *	The name of the level attribute may be specified behind the "-attr" option; 
 *  it defaults to "level". Repetitively "-1" strings are appended to 
 *	any existing attribute name.
 *  <p>
 *  This class is more a testing device for repeated XML filtering with
 *	chained SAX handlers than a meaningful transformer.
 *  @author Dr. Georg Fischer
 */
public class LevelFilter extends CharTransformer { 
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;
    
    /** Constructor.
     */
    public LevelFilter() {
        super();
        setFormatCodes("level");
        setDescription("add level attribute");
        setFileExtensions("xml");
    } // Constructor
    
	/** Initializes the (quasi-constant) global structures and variables.
	 *  This method is called by the {@link org.teherba.xtrans.XtransFactory} once for the
	 *  selected generator and serializer.
	 */
	public void initialize() {
		super.initialize();
        log = Logger.getLogger(LevelFilter.class.getName());
	} // initialize

    /** Transforms from the specified format to XML
     *  @return whether the transformation was successful
     */
    public boolean generate() {
        log.warn("xtrans.LevelFilter can serialize to XML only");
        boolean result = false;
        return  result;
    } // generate

    /*===========================*/
    /* SAX handler for XML input */
    /*===========================*/

    /** current nesting level of elements */
    private int level;
    
    /** name of the attribute to be added */
    private String attrName;
    
    /** Receive notification of the beginning of the document.
     *  Start with level 0.
     */
    public void startDocument() {
        try {
			super.startDocument();
	        level = 0;
    	    attrName = getOption("attr", "level"); // get the level attribute's name from option "-attr"; default: "level"
			filterHandler.startDocument();
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
    	level ++;
        try {
			String aname = attrName;
  			// System.err.println("attrName=" + attrName + ", level=" + String.valueOf(level));
        	while (attrs.getValue(aname) != null) {
        		aname += "-1";
        	} // make unique
        	AttributesImpl newAttrs = new AttributesImpl(attrs);
            newAttrs.addAttribute("", aname, aname, "CDATA", Integer.toString(level));
			filterHandler.startElement(uri, localName, qName, newAttrs);
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
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        level --;
    } // endElement
    
    /** Receive notification of character data inside an element.
     *  @param ch the characters.
     *  @param start the start position in the character array.
     *  @param length the number of characters to use from the character array. 
     */
    public void characters(char[] ch, int start, int length) {
        try {
			filterHandler.characters(ch, start, length);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // characters

} // LevelFilter
