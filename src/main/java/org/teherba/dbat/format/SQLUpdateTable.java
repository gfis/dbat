/*  Generator for UPDATE statements against an SQL table 
    @(#) $Id$
    2011-08-24: writeGenericRow
    2011-06-01: mode="update" instead of mode="sqlupdate"
    2011-05-04: rowCount incremented locally
	2011-04-15: copied from SQLTable
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
import  org.teherba.dbat.format.SQLTable;
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  java.util.ArrayList;

/** Generator for an SQL table writing
 *	DROP/CREATE statements of a table or INSERT statements for all rows of a result set.
 *	This class optionally emits JDBC escape sequences for date/time values.
 *  @author Dr. Georg Fischer
 */
public class SQLUpdateTable extends SQLTable {
    public final static String CVSID = "@(#) $Id$";

    /** local counter of rows, for insertion of COMMIT statements */
	protected int rowCount;

    /** No-args Constructor
     */
    public SQLUpdateTable() {
        this("update");
    } // Constructor()

    /** Constructor with format
     *	@param format either "sql" or "jdbc" (SQL with JDBC escape sequences for dates/times)
     */
    public SQLUpdateTable(String format) {
        super(format);
		rowCount = 0;
        setDescription("en", "SQL UPDATEs");
    } // Constructor(format)

    /** Initializes a table - with meta data, currently only implemented in SQLTable and its subclasses.
     *	For subclasses which do not override this method, the meta data are ignored.
     *  @param name name of the table
     *  @param tbMetaData meta data of the table
     */
    public void startTable(String name, TableMetaData tbMetaData) {
        super.startTable(name, tbMetaData);
        tableName = name;
        cellBuffer.setLength(0);
        lenCell = cellBuffer.length();
        rowCount = 0;
    } // startTable
    
    /** Terminates  a table
     */
    public void endTable() {
        try {
            charWriter.println("COMMIT;");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endTable

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
		        cellBuffer.setLength(0);
		        lenCell = 0;
		        appendCell("UPDATE "); 
		        appendCell(tableName);
    			while (icol < ncol) {
    				appendCell(icol > 0 ? "," : " SET ");
     				appendCell(columnList.get(icol).getLabel());
	    			appendCell("=");
     				appendCell(getContent(columnList.get(icol)));
    				icol ++;
    			} // while icol
    			charWriter.println(cellBuffer.toString());
    			break;
    		case DATA2:
		        cellBuffer.setLength(0);
		        lenCell = 0;
    			while (icol < ncol) {
    				appendCell(icol > 0 ? " AND " : "WHERE ");
     				appendCell(columnList.get(icol).getLabel());
	    			appendCell("=");
     				appendCell(getContent(columnList.get(icol)));
    				icol ++;
    			} // while icol
    			appendCell(";");
    			charWriter.println(cellBuffer.toString());
    			break;
    	} // switch rowType
    } // writeGenericRow

} // SQLUpdateTable
