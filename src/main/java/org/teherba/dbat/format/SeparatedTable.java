/*  Generator for a table with (comma, tab) separated values
    aöüÄÖÜß
    @(#) $Id$
    2011-11-11: writeComment(line, verbose)
    2011-08-24: writeGenericRow
    2011-02-02: writeRowCounter removed, writeComment prints a line
    2010-02-25: charWriter.write -> .print
    2007-01-12: copied from BaseTable
    2006-09-19: copied from numword.BaseSpeller
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

/** Generator for a table with (comma, tab) separated values.
 *  This format is by default implemented in {@link BaseTable}.
 *  @author Dr. Georg Fischer
 */
public class SeparatedTable extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** No-args Constructor
     */
    public SeparatedTable() {
        super();
        setFormatCodes("csv,tsv");
        setDescription("en", "Separated Values");
        setDescription("de", "Werte mit Trennzeichen");
        setDescription("fr", "Valeurs séparées");
    } // Constructor

    /** Writes a comment, but only if the "verbose" level is > 0.
     *  @param line string to be output as a comment
     *  @param verbose level of output detail
     */
    public void writeComment(String line, int verbose) {
        if (verbose > 0) {
            charWriter.println(line);
        }
    } // writeComment(2)

} // SeparatedTable
