/*  Generator for a table with fixed width columns
    @(#) $Id$
	2011-08-24: writeGenericRow
	2011-05-24: avoid empty header line
	2011-02-14: without headers and comments
	2010-03-17: MAX_WIDTH=512
    2010-02-25: charWriter.write -> .print
    2007-01-17: copied from BaseTable
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

/** Generator for a table with fixed width columns.
 *	For &lt;describe&gt;, this format generates an IBM DB2 LOAD specification,
 *  Example (from http://www.clever-sys.de/fua8.htm):
 *  <pre>
//DSNUPROC.SYSIN DD *
LOAD DATA REPLACE LOG YES ENFORCE CONSTRAINTS
DISCARDS 0 CONTINUEIF(1:1)=X'FF'
INTO TABLE PSTDEMO.ORDERS WHEN (1:2) = X'0003'
(ORDER_ID POSITION (3:5) DECIMAL,
CUST_ID POSITION (6:10) CHAR,
ORDER_DATE POSITION (11:20) DATE EXTERNAL(10),
ORDER_TIME POSITION (21:28) TIME EXTERNAL(8),
FREIGHT_CHARGES POSITION (31:33) DECIMAL
NULLIF (29:29) = X'FF',
ORDER_SALESMAN POSITION (36:41) CHAR
NULLIF (34:34) = X'FF',
ORDER_POSTED_DATE POSITION (42:67) TIMESTAMP EXTERNAL(26),
ORDER_SHIP_DATE POSITION (68:75) CHAR)
 * </pre>
 *  @author Dr. Georg Fischer
 */
public class FixedWidthTable extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** No-args Constructor
     */
    public FixedWidthTable() {
        super();
        setFormatCodes("fix");
		setDescription("en", "Fixed width columns");
		setDescription("de", "Spalten fester Breite");
    } // Constructor

    /** Gets the string content of a header or data cell.
     *	The strings obtained by this method can be aggregated 
     *	(with some separator) in order to form the contents of an aggregated column.
     *  @param column attributes of this column, containing the value also
     */
    public String getContent(TableColumn column) {
       	String value = column.getValue(); // for many formats it is simply the column's value
		int width = column.getWidth();
        if (value == null) {
            value = "";
        }
        if (value.length() < width) { // padding necessary
        	String align = column.getAlign();
        	if (align == null) {
        		align = ""; // default: left
        	}
    		int pad = width - value.length();
        	if (false) {
        	} else if (align.equals("right")) {
		        value = spaces(pad) + value;
        	} else if (align.equals("center")) {
		        value = spaces(pad >> 1) + value + spaces(pad - (pad >> 1));
        	} else { // default, if (align.equals("left")) {
		        value = value + spaces(pad);
        	}
	    } else if (value.length() > width) { // truncation, always on the right
	    	value = value.substring(0, width); 
	    }
        return value;
    } // getContent
        
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
    			while (icol < ncol) {
    				charWriter.print(getContent(columnList.get(icol)));
    				icol ++;
    			} // while icol
				charWriter.println();
    			break;
    	} // switch rowType
    } // writeGenericRow

    /** Writes a comment, but only if the "verbose" level is > 0.
     *  @param line string to be output as a comment
     *	@param verbose level of output detail
     */
    public void writeComment(String line, int verbose) {
    	if (verbose > 0) {
    		charWriter.println(line);
    	}
    } // writeComment(2)

} // FixedWidthTable
