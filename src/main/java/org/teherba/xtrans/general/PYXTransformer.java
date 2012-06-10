/*  Transforms PYX, a line-oriented representation of XML
    @(#) $Id$
    2008-07-19, Dr. Georg Fischer: copied from LineTransformer
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

package org.teherba.xtrans.general;
import  org.teherba.xtrans.CharTransformer;
import  java.io.BufferedReader;
import  java.util.ArrayList;
import  org.xml.sax.Attributes;
import  org.xml.sax.SAXException;
import  org.apache.log4j.Logger;

/** PYX is a line-oriented representation of XML which is especially
 *  suitable for processing with Unix utilities like grep, sed, wc, diff etc.
 *  The format is described at <a href="http://www.ibm.com/developerworks/xml/library/x-matters17.html">IBM</a>
 *  and <a href="http://www.xml.com/pub/a/2000/03/15/feature/index.html">xml.com</a>.
 *  <p>
 *  The XML features are indicated by the first character of a line as follows:
 *  <ul>
 *  <li>( start tag</li>
 *  <li>) end tag</li>
 *  <li>A attribute and value (separated by space, without quotes</li>
 *  <li>- character data, \n, \t and \\</li>
 *  <li>? processing instruction</li>
 *  <li>C comment (optional)</li>
 *  </ul>
 *  Example:
<pre>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;root&gt;
  &lt;?target data="processing-instruction"?&gt;
  &lt;element name="elem1" id="17"&gt;
    &lt;!--this is a
     multiline comment--&gt;
  &lt;empty-element&gt;&lt;/empty-element&gt;
        character content with   tab and     tab
  &lt;/element&gt;
  mixed character content
&lt;/root&gt;</pre>
PYX representation:
<pre>
(root
-\n
?target data="processing-instruction"
-\n
(element
Aname elem1
Aid 17
-\n
Cthis is a \n     multiline comment
-\n
(empty-element
)empty-element
-\n\tcharacter content with \t tab and \t tab
-\n
)element
-\n  mixed character content
-\n
)root
</pre>
 *  @author Dr. Georg Fischer
 */
public class PYXTransformer extends CharTransformer {
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;

    /** No-args Constructor.
     */
    public PYXTransformer() {
        super();
        setFormatCodes("pyx");
        setDescription("line oriented representation of XML");
        setFileExtensions("pyx");
    } // Constructor()

	/** Initializes the (quasi-constant) global structures and variables.
	 *  This method is called by the {@link org.teherba.xtrans.XtransFactory} once for the
	 *  selected generator and serializer.
	 */
	public void initialize() {
		super.initialize();
        log = Logger.getLogger(PYXTransformer.class.getName());
        putEntityReplacements();
	} // initialize

    /** Start element "tag" (indicator in column 1) */
    private static final char START_CHAR   = '(';
    /** End element tag */
    private static final char END_CHAR     = ')';
    /** Attribute tag */
    private static final char ATTR_CHAR    = 'A';
    /** Character content tag */
    private static final char CONTENT_CHAR = '-';
    /** Processing Instruction tag */
    private static final char PI_CHAR      = '?';
    /** Comment tag */
    private static final char COMMENT_CHAR = 'C';

    /** State of the finite automaton */
    private int state;
    /** Parser states enumeration */
    private static final int IN_COLUMN1                 = 0;
    private static final int IN_START_TAG               = 1;
    private static final int IN_END_TAG                 = 2;
    private static final int IN_ATTRS                   = 3;
    private static final int IN_PI                      = 4;
    private static final int IN_COMMENT                 = 5;
    private static final int IN_CHARACTERS              = 6;
    private static final int IN_BACKSLASH               = 7;

    /** Transforms from the specified format to XML
     *  @return whether the transformation was successful
     */
    public boolean generate() {
        boolean result = true;
        try {
            putEntityReplacements();
            fireStartDocument();

            StringBuffer content = new StringBuffer(296); // Buffer for character content, tag, attribute etc.
            String tag = ""; // current start or end tag
            boolean startPending = false; // whether a start element is pending
            char ch; // current character read
            BufferedReader buffReader = new BufferedReader(charReader);
            ArrayList/*<1.5*/<String>/*1.5>*/ keyValues = new ArrayList/*<1.5*/<String>/*1.5>*/(16);
            int oldState = 0; // previous value of 'state'
            state = IN_COLUMN1;
            int chint = 0;

            while ((chint = buffReader.read()) >= 0) {
                ch = (char) chint;
                // System.out.println("State " + state + ", char " + ch);
                switch (state) {

                    case IN_COLUMN1:
                        content.setLength(0);
                        if (startPending && ch != ATTR_CHAR) {
                            fireStartElement(tag, toAttributes(keyValues));
                            keyValues = new ArrayList/*<1.5*/<String>/*1.5>*/(16);
                            startPending = false;
                        } // startPending
                        switch (ch) {
                            case START_CHAR:
                                startPending = true;
                                state = IN_START_TAG;
                                break;
                            case END_CHAR:
                                state = IN_END_TAG;
                                break;
                            case ATTR_CHAR:
                                startPending = true;
                                state = IN_ATTRS;
                                break;
                            case CONTENT_CHAR:
                                state = IN_CHARACTERS;
                                break;
                            case PI_CHAR:
                                state = IN_PI; // processing instruction
                                break;
                            case COMMENT_CHAR:
                                state = IN_COMMENT;
                                break;
                            default: // invalid character in column 1
                                state = IN_CHARACTERS;
                                break;
                        } // switch ch
                        break;

                    case IN_START_TAG:
                        switch (ch) {
                            case ' ':
                            case '\t':
                            case '\r':
                                // ignore whitespace
                                break;
                            case '\n':
                                tag = content.toString();
                                content.setLength(0);
                                state = IN_COLUMN1;
                                break;
                            default:
                                content.append(ch);
                                break;
                        } // switch ch
                        break; // IN_START_TAG

                    case IN_END_TAG:
                        switch (ch) {
                            case ' ':
                            case '\t':
                            case '\r':
                                // ignore whitespace
                                break;
                            case '\n':
                                tag = content.toString();
                                content.setLength(0);
                                fireEndElement(tag);
                                state = IN_COLUMN1;
                                break;
                            default:
                                content.append(ch);
                                break;
                        } // switch ch
                        break; // IN_END_TAG

                    case IN_ATTRS:
                        switch (ch) {
                            case ' ':
                            case '\t':
                                keyValues.add(content.toString());
                                content.setLength(0);
                                break;
                            case '\r':
                                // ignore
                                break;
                            case '\n':
                                keyValues.add(content.toString());
                                content.setLength(0);
                                state = IN_COLUMN1;
                                break;
                            default:
                                content.append(ch);
                                break;
                        } // switch ch
                        break; // IN_ATTRS

                    case IN_CHARACTERS:
                        switch (ch) {
                            case '\\':
                                oldState = state;
                                state = IN_BACKSLASH;
                                break;
                            case '\r':
                                // ignore whitespace
                                break;
                            case '\n':
                                fireCharacters(content.toString());
                                content.setLength(0);
                                state = IN_COLUMN1;
                                break;
                            default:
                                content.append(ch);
                                break;
                        } // switch ch
                        break; // IN_CHARACTERS

                    case IN_COMMENT:
                        switch (ch) {
                            case '\\':
                                oldState = state;
                                state = IN_BACKSLASH;
                                break;
                            case '\r':
                                // ignore
                                break;
                            case '\n':
                                fireComment(content.toString());
                                content.setLength(0);
                                state = IN_COLUMN1;
                                break;
                            default:
                                content.append(ch);
                                break;
                        } // switch ch
                        break; // IN_COMMENT

                    case IN_BACKSLASH:
                        switch (ch) {
                            case '\\':
                                content.append(ch);
                                break;
                            case 'b':
                                content.append('\b');
                                break;
                            case 'f':
                                content.append('\f');
                                break;
                            case 'n':
                                content.append('\n');
                                break;
                            case 'r':
                                content.append('\r');
                                break;
                            case 't':
                                content.append('\t');
                                break;
                            default:
                                content.append(ch);
                                break;
                        } // switch ch
                        state = oldState;
                        break; // IN_BACKSLASH

                    case IN_PI:
                        switch (ch) {
                            case ' ':
                                tag = content.toString();
                                content.setLength(0);
                                break;
                            case '\r':
                                // ignore
                                break;
                            case '\n':
                                fireProcessingInstruction(tag, content.toString());
                                content.setLength(0);
                                state = IN_COLUMN1;
                                break;
                            default:
                                content.append(ch);
                                break;
                        } // switch ch
                        break; // IN_CHARACTERS

                    default:
                        log.error("invalid state " + state);
                        break; // default state

                } // switch state
            } // while not EOF
            buffReader.close();
            fireLineBreak();
            fireEndDocument();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return  result;
    } // generate

    /*===========================*/
    /* SAX handler for XML input */
    /*===========================*/

    /** Receive notification of the beginning of the document.
     */
    public void startDocument() {
    } // startDocument

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
        charWriter.println(START_CHAR + qName);
        if (attrs != null) {
            int nattr = attrs.getLength();
            for (int ind = 0; ind < nattr; ind ++) {
                charWriter.println(ATTR_CHAR + attrs.getQName(ind) + " " + attrs.getValue(ind));
            } // for ind
        } // attrs
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
        charWriter.println(END_CHAR + qName);
    } // endElement

    /** Receive notification of character data inside an element.
     *  @param ch the characters.
     *  @param start the start position in the character array.
     *  @param len the number of characters to use from the character array.
     */
    public void characters(char[] ch, int start, int len) {
        charWriter.print(CONTENT_CHAR);
        int ind = 0;
        while (ind < len) {
            char chi = ch[start + ind];
            switch (chi) {
                case '\r':
                    // ignore
                    break;
                case '\n':
                    charWriter.print("\\n");
                    break;
                case '\t':
                    charWriter.print("\\t");
                    break;
                default:
                    charWriter.print(chi);
                    break;
            } // switch chi
            ind ++;
        } // while ind
        charWriter.println();
    } // characters

    /** Receive notification of an XML comment.
     *  @param ch the characters of the comment.
     *  @param start the start position in the character array.
     *  @param len the number of characters to use from the character array.
     */
    public void comment(char[] ch, int start, int len) {
        charWriter.print(COMMENT_CHAR);
        int ind = 0;
        while (ind < len) {
            char chi = ch[start + ind];
            switch (chi) {
                case '\r':
                    // ignore
                    break;
                case '\n':
                    charWriter.print("\\n");
                    break;
                case '\t':
                    charWriter.print("\\t");
                    break;
                default:
                    charWriter.print(chi);
                    break;
            } // switch chi
            ind ++;
        } // while ind
        charWriter.println();
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
        charWriter.println(PI_CHAR + target + " " + data);
    } // processingInstruction

} // PYXTransformer
