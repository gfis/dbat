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
 *  it simply echoes the SQL which would have been executed 
 *  (instead of showing the results of the SQL execution).
 *  @author Dr. Georg Fischer
 */
public class EchoSQL extends BaseTable {
    public final static String CVSID = "@(#) $Id$";
    /** Debugging switch */
    private int debug = 0;

    /** No-args Constructor
     */
    public EchoSQL() {
        super();
        debug = 0;
        setFormatCodes("echo");
        setDescription("en", "Echo SQL");
        // setDescription("de", "nur SQL-Ausgabe");
    } // Constructor

    /** Writes the pure SQL instruction, with any placeholders replaced by constant values again.
     *  @param tbMetaData meta data for the table as far as they are already known
     *  @param sqlInstruction a SELECT, CALL, DELETE, INSERT or UPDATE statement
     *  @param action 0 = SELECT, 1 = CALL, 2 = DML instructions
     *  @param verbose 1 (0) = (not) verbose
     *  @param variables pairs of types and values for variables to be filled 
     *  into any placeholders ("?") in the prepared statement  
     */
    public void writeSQLInstruction(TableMetaData tbMetaData, String sqlInstruction
    		, int action, int verbose
    		, ArrayList/*<1.5*/<String>/*1.5>*/ variables) {
		sqlInstruction = sqlInstruction + " "; // because of trailing PARAMETER_MARKER
        String separator = ";";
        try {
            int len = sqlInstruction.length();
            StringBuffer buffer = new StringBuffer(len);
            int nvar = variables.size();
            if (nvar > 0) { // placeholders must be replaced
                int ivar = 0;
                int foundPos = 0; // position of the PARAMETER_MARKER's first char
                int startPos = 0; // starting position in buffer
                while (ivar < nvar && startPos < len) {
                    foundPos = sqlInstruction.indexOf(PARAMETER_MARKER, startPos);
                    if (foundPos < 0) { // error - not enough parameter markers
                        foundPos = len - 1;
                    }
                    buffer.append(sqlInstruction.substring(startPos, foundPos + 1)); // copy the left space also
                    startPos = foundPos + PARAMETER_MARKER.length() - 1; // copy the right space also
                    String varName  = variables.get(ivar + 0).toUpperCase(); 
                    String typeName = variables.get(ivar + 1).toUpperCase(); 
                    String value    = variables.get(ivar + 2); 
                    if (debug > 0) {
                        buffer.append('{');
                        buffer.append(String.valueOf(ivar));
                        buffer.append(',');
                        buffer.append(varName);
                        buffer.append('=');
                        buffer.append(typeName);
                        buffer.append(':');
                        buffer.append(value);
                        buffer.append('}');
                    } // debug
                    ivar += 3;
                    // keep this switch in synch with the code in SQLAction.setPlaceholder 
                    if (false) {
                    } else if (typeName.equals    ("DECIMAL"   )) {
                        buffer.append(value);
                    } else if (typeName.startsWith("INT"       )) {
                        buffer.append(value);
                    } else { // DATE, TIME, STRING, CHAR etc.
                        buffer.append('\'');
                        buffer.append(value);
                        buffer.append('\'');
                    } // switch typeName    
                } // while ivar
                foundPos = len;
                if (startPos < foundPos) { // copy the rest behind the last marker
                    buffer.append(sqlInstruction.substring(startPos, foundPos)); 
                }
            } else { // no placeholders
                buffer.append(sqlInstruction);
            }
            charWriter.print(buffer.toString().trim());
            charWriter.println(separator);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeSQLInstruction

    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *  Must be redefined here with no action, since the default implementation in {@link BaseTable#writeGenericRow} 
     *  would output the query results.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *  @param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList/*<1.5*/<TableColumn>/*1.5>*/ columnList) {
        switch (rowType) {
            case DATA:
                break;
        } // switch rowType
    } // writeGenericRow

} // EchoSQL
