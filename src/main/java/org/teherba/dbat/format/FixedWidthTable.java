/*  Generator for a table with fixed width columns
    @(#) $Id$
    2014-03-04: ignore pseudo columns
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
 *  For &lt;describe&gt;, this format should generate an IBM DB2 LOAD specification,
 *  at some time in the future.
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

    /** Reads one row from an URI
     *  @param tbMetaData the target table's metadata
     *  @return an array of String values extracted from one input line or row, 
     *  or null if there was no more row which could be read
     */
    public String[] loadNextRow(TableMetaData tbMetaData) {
        String[] loadValues = null;
        int columnCount = tbMetaData.getColumnCount();
        try {
            String line = loadReader.readLine();
            if (line != null) {
                loadValues = new String[columnCount];
                int spos = 0; // starting position
                int fixCount = 0;
                while (fixCount < columnCount && spos < line.length()) {
                    TableColumn column = tbMetaData.getColumn(fixCount);
                    String pseudo = column.getPseudo();
                    if (pseudo == null) {
                        int epos = spos + column.getWidth();
                        if (epos >= line.length()) {
                            epos = line.length();
                        }
                        switch (loadTrimSides) {
                            case 0: // notrim
                                loadValues[fixCount] = (      line.substring(spos, epos))                    ;
                                break;
                            case 1: // rtrim
                                loadValues[fixCount] = ("x" + line.substring(spos, epos)).trim().substring(1);
                                break;
                            default:
                            case 2:
                                loadValues[fixCount] = (      line.substring(spos, epos)).trim()             ;
                                break;
                        } // switch trimSides
                        spos = epos;
                    } // not pseudo
                    fixCount ++;
                } // while fixCount
                while (fixCount < columnCount) { // not filled
                    loadValues[fixCount ++] = "";
                } // while not filled
            } // line != null
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return loadValues;
    } // loadNextRow

    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *  @param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList<TableColumn> columnList) {
        TableColumn column = null;
        String pseudo = null;
        int ncol = columnList.size();
        int icol = 0;
        StringBuffer result = new StringBuffer(256);
        switch (rowType) {
            case DATA:
                while (icol < ncol) {
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (pseudo == null) {
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
                                result.append(spaces(pad));
                                result.append(value);
                            } else if (align.equals("center")) {
                                result.append(spaces(pad >> 1));
                                result.append(value);
                                result.append(spaces(pad - (pad >> 1)));
                            } else { // default, if (align.equals("left")) {
                                result.append(value);
                                result.append(spaces(pad));
                            }
                        } else if (value.length() > width) { // truncation, always on the right
                            result.append(value.substring(0, width));
                        } else { // value.length() == width
                            result.append(value);
                        }
                    } // pseudo == null
                    icol ++;
                } // while icol
                charWriter.println(result.toString());
                break;
            default:
                break;
        } // switch rowType
    } // writeGenericRow

    /** Writes a comment, but only if the "verbose" level is > 0.
     *  @param line string to be output as a comment
     *  @param verbose level of output detail
     */
    public void writeComment(String line, int verbose) {
        if (verbose > 0) {
            charWriter.println(line);
        }
    } // writeComment(2)

} // FixedWidthTable
