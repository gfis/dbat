/*  Pseudo format which only echoes the SQL statement for the row select  
    @(#) $Id$
	2011-11-08: end comment better parseable
	2011-08-24: writeGenericRow
    2011-05-31: normal table serializer with single method 'writeComment'
	2010-06-16: copied from DefaultSpecTable
*/
/*
 * Copyright 2010 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  org.teherba.dbat.format.BaseTable;
import  java.util.ArrayList;

/** This class is only a tiny implementation of {@link org.teherba.dbat.format.BaseTable};
 *	it simply echoes the SQL which would have been executed 
 *  (instead of showing the results of the SQL execution).
 *  @author Dr. Georg Fischer
 */
public class EchoSQL extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** No-args Constructor
     */
    public EchoSQL() {
        super();
        setFormatCodes("echo");
		setDescription("en", "Echo SQL");
		// setDescription("de", "nur SQL-Ausgabe");
    } // Constructor

    /** Writes a comment line.
     *  @param line string to be output as a comment line
     */
    public void writeComment(String line) {
        try {
        	if (line != null && line.startsWith("SQL:")) {
        		if (line.endsWith(":SQL")) {
        			line = line.substring(0, line.length() - 4);
        		}
	            charWriter.println(line.replaceAll("\\A.*\n", ""));
	        }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeComment

    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *	Must be redefined here with no action, since the default implementation in {@link BaseTable#writeGenericRow} 
     *	would output the query results.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *	@param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList/*<1.5*/<TableColumn>/*1.5>*/ columnList) {
    	switch (rowType) {
    		case DATA:
    			break;
    	} // switch rowType
    } // writeGenericRow

} // EchoSQL
