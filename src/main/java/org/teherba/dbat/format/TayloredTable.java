/*  File Tayloring with the result set of a query
    @(#) $Id$
	2011-08-24: writeGenericRow
	2011-08-12: revised, print the template in end row
    2011-07-19, Dr. Georg Fischer: copied from SeparatedTable
    
    Uses URIReader for the template file behind -u, and test cases U*
*/
/*
 * Copyright 2011 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.common.URIReader;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  java.util.ArrayList;
import  java.util.regex.Matcher;
import  java.util.regex.Pattern;
import  org.apache.log4j.Logger;

/** This output format first reads a sequential "template" file (a block of text)  
 *	which is specified after the <code>-u</code> option in the commandline interface.
 *	Reading may happen from STDIN, from a local file, an URL or a <code>data:</code> URI.
 *  <p  />
 *	The template contains variable names, which correspond to the label
 *	attributes of the columns. In the template, they are prefixed or enclosed by the character(s)
 *	given in the <em>separator</em> property. 
 *	These separators are internally quoted for regex replacement operations.
 *  <p  />
 *	Later, every row of the query's result set is splitted into values
 *  which replace the variables in the template. The template is output as many times as
 *	there are rows in the result set. 
 *	Column values which do not correspond to template variables are ignored.
 *  @author Dr. Georg Fischer
 */
public class TayloredTable extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;
	
    /** Separator which escapes variable names in template text;
     *	up to 2 characters, one before and an optional one behind the variable name
     */
    private String separator;
    /** Template text */
    private String templateText;
    /** Text which get/has variables replaced by query values */
    private String tayloredText;

    /** No-args Constructor
     */
    public TayloredTable() {
        super();
        log = Logger.getLogger(TayloredTable.class.getName());
        setFormatCodes("taylor");
		setDescription("en", "File Tayloring");
		setDescription("de", "Variablenersetzung");
    } // Constructor
	
    /** Initializes a table.
     *	Reads a pattern file from an URI (-u option, via {@link #getInputURI}), 
     *	and remembers it in {@link #templateText}.
     *  @param tableName name of the table
     */
    public void startTable(String tableName) {
		separator = getSeparator();
		try {
			URIReader reader = new URIReader(getInputURI());
			StringBuffer content = new StringBuffer(4096);
			String line = null;
			while ((line = reader.readLine()) != null) { 
				content.append(line); // append all lines from the URI 
				content.append(newline); // and terminate them
			} // while reading from URI
			reader.close();
			templateText = content.toString();
		} catch (Exception exc) {
            log.error(exc.getMessage(), exc);
		}
    } // startTable
  
	/** Gets the escaped variable name from a table column.
	 *  The variable is prefixed by the 1st character in <em>separator</em>,
	 *	and it can have a suffix from the 2nd character in <em>separator</em>.
	 *	The surrounding characters are itself escaped if they are valid
	 *	metacharacters of regular expressions.
	 *	@param column extract the variable from this column's header
	 *	@return resulting variable surrounded by escape characters,
	 *	suitable for the first parameter of <em>String.replaceAll</em>
	 */
	private String getEnclosedVariable(TableColumn column) {
		String head = separator;
		String tail = "";
		if (head.length() >= 2) {
			tail = head.substring(1, 2);
			head = head.substring(0, 1);
		}
        return Pattern.quote(head + column.getName() + tail);
	} // getEnclosedVariable	 

    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *	@param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList/*<1.5*/<TableColumn>/*1.5>*/ columnList) {
    	int ncol = columnList.size();
    	int icol = 0;
    	switch (rowType) {
    		case DATA:
				tayloredText = templateText;
    			while (icol < ncol) {
					TableColumn column = columnList.get(icol);
			   		tayloredText = tayloredText.replaceAll(getEnclosedVariable(column)
	   						, Matcher.quoteReplacement(column.getValue()));
    				icol ++;
    			} // while icol
    			charWriter.print(tayloredText);
    			break;
    	} // switch rowType
    } // writeGenericRow

} // TayloredTable
