/*  Generator for a MediaWiki table 
    @(#) $Id$
    2012-06-25: writeTrailer removed
    2011-08-24: writeGenericRow
	2011-08-12: remove old methods write*
	2011-07-27: copied from WikiTable
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

package org.teherba.dbat.format;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  java.util.ArrayList;
import  java.util.HashMap;

/** Generator for a MediaWiki table, maybe with links, styles on some columns.
 *  Several HTML tags like &lt;h3&gt;, &lt;br /&gt; are also translated to Wiki text.
 *  Example for a Wiki table: 
<pre>
===Vergleichbare Tools===
{| style="color:black; background-color:#ffffcc;" cellpadding="4" cellspacing="0" border="1"
!DB-System!!Werkzeug
|-
|DB2||[http://publib.boulder.ibm.com/infocenter/dzichelp/v2r2/topic/com.ibm.db29.doc.apsg/db2z_executesqlspufi.htm SPUFI] (z/OS), [http://publib.boulder.ibm.com/infocenter/db2luw/v9r5/topic/com.ibm.db2.luw.admin.cmd.doc/doc/r0010409.html db2] (Windows/Unix)
|-
|MySql||[http://dev.mysql.com/doc/refman/5.1/en/mysql.html mysql]
|-
|Oracle||[http://www.oracle.com/technology/docs/tech/sql_plus/index.html sqlplus]
|-
|SQLite3||[http://www.sqlite.org/sqlite.html sqlite3]
|-
|versch.||henplus, sqlline
|}
Eine &Uuml;bersicht vergleichbarer Werkzeuge findet man auch bei [http://java-source.net/open-source/sql-clients Java-Source.net].
</pre>
 *  @author Dr. Georg Fischer
 */
public class WikiTable extends BaseTable { 
    public final static String CVSID = "@(#) $Id$";
    
	/** Prefix for relative path in link, to make it absolute with "http://" */
	private String protocolContext;
	
    /** No-args Constructor
     */
    public WikiTable() {
    	super("wiki");
        setFormatCodes("wiki");
		setDescription("en", "MediaWiki Text");
		setDescription("de", "MediaWiki-Text");
    	protocolContext = "http://localhost:8080/dbat/";
    } // Constructor

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *	@param params array of 0 or more (name, value) string which specify features in the file header.
     *	@param parameterMap map of request parameters to values
     *	The following names are interpreted:
     *	<ul>
     *	<li>contentType - MIME type for the document content</li>
     *	<li>encoding - encoding to be used for the output stream</li>
     *	<li>javaScript - name of the file containing JavaScript functions</li>
     *	<li>styleSheet - name of the CSS file</li>
     *	<li>encoding - encoding to be used for the output stream</li>
     *	<li>target - target of HTML base element, for example "_blank"</li>
     *	<li>title - title for the HTML head element, and the browser window</li>
     *	</ul>
     */
    public void writeStart(String[] params,  HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
		String contenttype	= getMimeType();
		String encoding		= getTargetEncoding();
		String target		= null;
		String title		= "dbat";
		String xslt         = null;
        try {
			int iparam = params.length;
			while (iparam > 0) {
				iparam -= 2;
				if (false) {
				} else if (params[iparam].equals("encoding")) {
					encoding 	= params[iparam + 1];
				} else if (params[iparam].equals("title")) {
					title		= params[iparam + 1];
				} else { // "dummy" - skip pair
				}
			} // while iparam
			
       } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeStart

    /** Ends   a file that may contain several table descriptions and/or a SELECT result sets
     */
    public void writeEnd() {
        try {
    		// ignore: charWriter.println("</body></html>");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeEnd
    
    /** Initializes a table
     *  @param tableName name of the table
     */
    public void startTable(String tableName) {
        try {
            charWriter.println("\n{| border=\"1\"");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startTable

    /** Terminates  a table
     */
    public void endTable() {
         try {
            charWriter.println("|}");
         } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endTable

    /** Writes the number of selected rows, and a description of the object represented by the rows.
     *	A "+" is added behind the number if there would have been more rows, but the 
     * 	SELECT was terminated by FETCH_LIMIT.
     * 	@param rowCount number of data rows which were output
     *	@param moreRows whether there would have been more rows in the resultset
     *	@param tbMetaData contains the descriptive text for the counter: 1 for "row" and 0 or &gt;= 2 for "rows"
     */
    public void writeTableFooter(int rowCount, boolean moreRows, TableMetaData tbMetaData) {
    	String desc = tbMetaData.getCounterDesc(rowCount == 1 ? 0 : 1);
    	if (desc != null) { // only if set
       		charWriter.println("|" 
        			+ rowCount + (moreRows ? "+ " : " ")
        			+ desc
        			+ "\n|-"
        			);
        } // desc was set
    } // writeTableFooter

    /** Writes a comment line.
     *  @param line string to be output as a comment line
     */
    public void writeComment(String line) {
        try {
            charWriter.println("<!-- " + line + " -->");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeComment

	/** Tells, for the specific format, the rule to be applied for escaping.
	 *	The result may optionally depend on the column's attributes and/or the cell value.
	 *	@return the following escaping rules are currently observed:
	 *	<ul>
	 *	<li>0 = no escaping at all</li>
	 *	<li>1 = "&amp;", "&lt;" and "&gt;" are escaped
	 *  as "&amp;amp;", "&amp;lt;" and "&amp;gt;" respectively</li>
	 *	<li>2 = "&apos;" is replaced by "&amp;apos"</li>
	 *	<li>3 = combination of rule 1 and rule 2</li>
	 *	<li>4 = like 1, but internally check column.expr for start of tag ('<')
	 *	</ul>
	 */
    public int getEscapingRule() {
   		return 4; // escape & < > if expr.matches(".*[<&>].*") ? 1 : 0;
	} // getEscapingRule
    
    /** Writes HTML markup (for HTML), or nothing
     *  @param markup HTML element(s) to be written
     */
    public void writeMarkup(String markup) {
    	StringBuffer result = new StringBuffer(128);
		int digit = 0;
		int pos   = 0;
		int pos2  = 0;
		String attr = "";
        try {
        	if (false) {
        	} else if (markup.matches("\\<a.*")) { // <a ...> start of link
        		result.append("[");
        		pos  = markup.indexOf("href=\"");
        		pos2 = markup.indexOf("\"", pos + 6);
        		attr = markup.substring(pos + 6, pos2);
				result.append(! attr.startsWith("http") ? protocolContext : "");
				result.append(attr);
				result.append(" ");
        	} else if (markup.startsWith("</a>")) { // </a> end of link
        		result.append("]");
        	} else if (markup.startsWith("<br")) { // <br...> break
        		result.append("\n");
        	} else if (markup.startsWith("<form")) { // <form...> input form - comment it out
        		result.append("<!--");
        	} else if (markup.startsWith("</form")) { // </form> 
        		result.append("-->");
        	} else if (markup.matches("\\<\\/?h\\d\\>")) { // heading of level n
        		if (markup.startsWith("<h")) {
        			result.append("\n");
        			digit = Character.digit(markup.charAt(2), 10);
        			while (digit > 0) {
        				result.append("=");
        				digit --;
        			} 
        		} else { // must be "</hi>"
        			digit = Character.digit(markup.charAt(3), 10);
        			while (digit > 0) {
        				result.append("=");
        				digit --;
        			} 
        			result.append("\n");
				}
			} else {
				result.append(markup);
			}
           	charWriter.print(result.toString());
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        	System.err.println(markup);
        }
    } // writeMarkup
    
    /** Writes a Processing Instruction (for XML), or nothing for other formats.
	 *
	 *	Caution:
	 *	
     *  This empty method must be repeated here since {@link WikiTable} is derived from {@link XMLTable}.
     *  @param instruction processing instruction to be written, without the SGML brackets
     */
    public void writeProcessingInstruction(String instruction) {
    } // writeProcessingInstruction
        
    /** Gets the string content of a header or data cell.
     *	The strings obtained by this method can be aggregated 
     *	(with some separator) in order to form the contents of an aggregated column.
     *  @param column attributes of this column, containing the value also
     */
    public String getContent(TableColumn column) {
        StringBuffer result = new StringBuffer(128);
       	String value     = column.getValue(); 
    	String hrefValue = column.getHrefValue();
        if (hrefValue != null) {
	        result.append("[");
	        result.append(! hrefValue.matches("\\w+\\:.*") ? protocolContext : "");
    		result.append(hrefValue.replaceAll("&", "&amp;"));
        	result.append(" ");
        }
	    result.append(value);
		if (hrefValue != null) {
	        result.append("]");
        }
        return result.toString();
    } // getContent
        
    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *	@param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList/*<1.5*/<TableColumn>/*1.5>*/ columnList) {
		nextStyle  = null;
		nextLobURL = null;
		TableColumn column = null;
		String pseudo = null;
    	int ncol = columnList.size();
    	int icol = 0;
    	switch (rowType) {
    		case HEADER:
    			while (icol < ncol) {
					column = columnList.get(icol);
					String header = column.getLabel();
		            pseudo = column.getPseudo();
    		        if (pseudo != null && pseudo.equals("style")) {
        		        nextStyle = null;
            		} else {
                		if (header == null) {
                    		header = "&nbsp;";
	                	}
	            	}
	                charWriter.print((icol <= 0 ? "" : "|") + "|" + header);
    				icol ++;
    			} // while icol
    			charWriter.println("\n|-");
    			break;
    		case DATA:
				StringBuffer result = new StringBuffer(256);
    			while (icol < ncol) {
					column = columnList.get(icol);
		            pseudo = column.getPseudo();
			        if (pseudo != null) {
			            if (false) {
			            } else if (pseudo.equals("style")) {
			                nextStyle = column.getValue();
			            } else if (pseudo.equals("url")) {
			                nextLobURL = column.getValue();
			            } else { 
			            	// ignore if (pseudo.equals("delete") || pseudo.equals("update")) {
			            } 
			        } else { // pseudo == null
			            result.append((icol<= 0 ? "" : "|") + "|");
				        String align = column.getAlign();
				        if (align != null && ! align.equals("left")) {
				            result.append("align=\"" + align + "\"|");
				        }
					/*
					    String style = column.getStyle();
				        if (false) {
				        } else if (nextStyle != null && nextStyle.length() > 0) {
				            result.append(" class=\"" + nextStyle + "\"");
				        } else if (style 	 != null && style	 .length() > 0) {
				            result.append(" class=\"" + style + "\"");
						}
					*/
				        nextStyle = null;
				       	result.append(getContent(column));
    					// result.append("|");
    				} // pseudo == null
    				icol ++;
    			} // while icol
    			charWriter.println(result.toString() + "\n|-");
    			break;
    	} // switch rowType
    } // writeGenericRow

} // WikiTable
