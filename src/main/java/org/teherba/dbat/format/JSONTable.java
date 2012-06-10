/*  Generator for an XML table 
    @(#) $Id$
    2012-05-22, Georg Fischer: copied from XMLTable
*/
/*
 * Copyright 2012 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.dbat.Configuration; 		// for DBAT_URI
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  java.sql.DatabaseMetaData;
import  java.sql.Types;
import  java.util.ArrayList;
import  java.util.HashMap;
import  java.util.Iterator;
import  java.util.TreeMap;

/** Generator for a JavaScript Object Notation (JSON) table.
 *  The main structure of the output object is an array of tables
 *  with properties "name", "thead" and "tbody". The latter is
 *	an array of rows which are arrays of cells.
 *	<pre>
{ "tables": 
[ { "table":
  { "name": "table_not_specified"
  , "thead": ["Name","University","Birthdate"]
  , "tbody": 
    [ ["Martha","Freiburg","1909-11-17"]
    , ["Maria","Hermsdorf","1914-09-17"]
    ]
  }
, "table":
  { "name": "table_not_specified"
  , "thead": ["Name","University","Birthdate"]
  , "tbody": 
    [ ["Lucie","Lübars","1887-07-09"]
    , ["Maria","Hermsdorf","1914-09-17"]
    ]
  }
, "table":
  { "name": "table_not_specified"
  , "thead": ["Name","University","Birthdate"]
  , "tbody": 
    [ ["Dorothea","Lübars","1910-02-07"]
    , ["Johannes","Schramberg","1911-06-03"]
    ]
  }
  }
]
}
 *	</pre>
 *  @author Dr. Georg Fischer
 */
public class JSONTable extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

	/** encoding for output */
	private String encoding;
	
    /** No-args Constructor
     */
    public JSONTable() {
        super();
        setFormatCodes("json");
		setDescription("en", "JSON");
    	encoding = "UTF-8";
    } // Constructor

    /** Constructor with format
     *	@param format = "xml"
     */
    public JSONTable(String format) {
        super(format);
    	encoding = "UTF-8";
    } // Constructor

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets.
     *	Starts the JSON object.
     *	@param params - array of 0 or more (name, value) strings which specify features in the file header.
     *	@param parameterMap map of request parameters to values
     */
    public void writeStart(String[] params,  HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        try {
            charWriter.println("{ \"tables\": ");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeStart

    /** Ends a file that may contain several table descriptions and/or a SELECT result sets.
     *	Close the JSON object.
     */
    public void writeEnd() {
        try {
            charWriter.println("  }");
            charWriter.println("]");
            charWriter.println("}");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeEnd
    
    /** Initializes a table.
     *  Start or continue an array of table objects.
     *  @param tableName name of the table
     */
    public void startTable(String tableName) {
        super.startTable(tableName);
        try {
        	tableSeqNo ++;
            charWriter.println((tableSeqNo <= 1 ? "[ { " : ", ") + "\"table\":");
            charWriter.println("  { \"name\": \"" + tableName + "\"");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startTable
    
    /** Terminates  a table
     */
    public void endTable() {
         try {
         	if (irow == 0) {
            	charWriter.println("    , \"thead\": [ ]");
            	charWriter.println("    , \"tbody\": [");
         	}
            charWriter.println("    ]");
            charWriter.println("  }");
         } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endTable

    /** Writes the closing bracket (if any) behind the column descriptions.
     */
    public void describeColumnsEnd() {
	} // describeColumnsEnd
	   
	/** Tells, for the specific format, the rule to be applied for escaping.
	 *	The result may optionally depend on the column's attributes and/or the cell value.
     *  @param colAttrs attributes of this column
	 *	@param value can be tested for the occurrence of HTML tags
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
	/** Tells, for the specific format, the rule to be applied for escaping.
	 */
    public int getEscapingRule() {
   		return 0; 
	} // getEscapingRule
    
    /** Gets the string content of a header or data cell.
     *	The strings obtained by this method can be aggregated 
     *	(with some separator) in order to form the contents of an aggregated column.
     *  @param column attributes of this column, containing the value also
     */
    public String getContent(TableColumn column) {
       	String value = column.getValue(); // for many formats it is simply the column's value
        StringBuffer result = new StringBuffer(128);
        if (value == null) {
            result.append("null");
        } else {
            switch (column.getDataType()) {
                case Types.CHAR: // fall thru
                case Types.VARCHAR:
                    result.append("\""); 
                    result.append(value.replaceAll("\"", "\\\"").replaceAll("\'", "\\\'")); // escape quotes and apostrophes
                    result.append("\"");
                    break;
                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                    result.append("\""); 
                    result.append(value);
                    result.append("\"");
                    break;
                default: // all numeric types: INT, DECIMAL, and BOOLEAN
                    result.append(value);
                    break;
            } // switch type
        } // not NULL
        return result.toString();
    } // getContent
    
    /** current row number */
    private int irow;
    
    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *	@param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList/*<1.5*/<TableColumn>/*1.5>*/ columnList) {
    	int ncol = columnList.size();
    	int icol = 0;
    	switch (rowType) {
    		case HEADER:
    			charWriter.print  ("  , \"thead\": ");
    			while (icol < ncol) {
    				charWriter.print((icol > 0 ? "," : "[") + "\"" + columnList.get(icol).getLabel() + "\"");
    				icol ++;
    			} // while icol
    			charWriter.println("]");
    			charWriter.println("  , \"tbody\": ");
    			irow = 0;
    			break;
    		case DATA:
    			charWriter.print((irow > 0 ? "    , " : "    [ "));
    			irow ++;
    			while (icol < ncol) {
    				charWriter.print((icol > 0 ? "," : "[") + getContent(columnList.get(icol)));
    				icol ++;
    			} // while icol
    			charWriter.println("]");
    			break;
    	} // switch rowType
    } // writeGenericRow

} // JSONTable
