/*  Pseudo format which only tests the SQL statement for the row select  
    @(#) $Id$
    2011-12-03: copied from ProbeSQL
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
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  org.teherba.dbat.SpecificationHandler;
import  org.teherba.dbat.format.BaseTable;
import  java.util.ArrayList;

/** This class is only a tiny implementation of {@link org.teherba.dbat.format.BaseTable};
 *  it supports the additional subselect construction inserted by {@link SpecificationHandler}.
 *  Normally, the <em>probe</em> output format retrieves no rows, but emits an error
 *  message for SQL syntax errors, missing tables or columns.
 *  @author Dr. Georg Fischer
 */
public class ProbeSQL extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** No-args Constructor
     */
    public ProbeSQL() {
        super();
        setFormatCodes("probe");
        setDescription("en", "Probe SQL");
        setDescription("de", "SQL-Syntaxtest");
    } // Constructor

    /** Writes a comment line.
     *  @param line string to be output as a comment line
     */
    public void writeComment(String line) {
    } // writeComment

    /** Writes a comment, but only if the "verbose" level is > 0.
     *  @param line string to be output as a comment
     *  @param verbose level of output detail
     */
    public void writeComment(String line, int verbose) {
         try {
            if (verbose > 0) {
                charWriter.println("-- " + line.replaceAll("\n", "\n--"));
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeComment(2)

    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *  Must be redefined here with no action, since the default implementation in {@link BaseTable#writeGenericRow} 
     *  would output the query results.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *  @param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList/*<1.5*/<TableColumn>/*1.5>*/ columnList) {
        int ncol = columnList.size();
        int icol = 0;
        switch (rowType) {
            case DATA:
                while (icol < ncol) {
                    charWriter.print(columnList.get(icol).getValue());
                    icol ++;
                } // while icol
                charWriter.println();
                break;
        } // switch rowType
   } // writeGenericRow

} // ProbeSQL
