/*  Selects the applicable table generator
    @(#) $Id$
    2012-06-16: GenerateSQLJ
    2012-05-22: JSONTable for Ajax 
    2011-09-10: TableGenerator, -m gen
    2011-07-27: WikiTable, -m wiki
    2011-07-15: TayloredTable, -m taylor
    2011-04-15: SQLUpdateTable, -m update
    2011-04-08: TransformedTable
    2010-07-24: ExcelTable, -m xls
    2010-06-16: EchoSQL, -m echo
    2010-02-26: default HTML; getTableSerializer in favour of getTableWriter (now deprecated)
    2010-02-23: DefaultSpecTable, -m spec
    2008-02-07: Sassnitz, Dorothea * 98 Jahre
    2007-01-12: copied from XtransFactory
*/
/*
 * Copyright 2007 Dr. Georg Fischer <punctum at punctum dot kom>
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
package org.teherba.dbat;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.DefaultSpecTable; // XML files suitable for processing by SpecificationHandler
import  org.teherba.dbat.format.EchoSQL;
import  org.teherba.dbat.format.ExcelTable;
import  org.teherba.dbat.format.FixedWidthTable;
import  org.teherba.dbat.format.GenerateSQLJ;
import  org.teherba.dbat.format.HTMLTable;
import  org.teherba.dbat.format.JDBCTable;
import  org.teherba.dbat.format.JSONTable;
import  org.teherba.dbat.format.ProbeSQL;
import  org.teherba.dbat.format.SeparatedTable;
import  org.teherba.dbat.format.SQLTable;
import  org.teherba.dbat.format.SQLUpdateTable;
import  org.teherba.dbat.format.TableGenerator;
import  org.teherba.dbat.format.TayloredTable;
import  org.teherba.dbat.format.TransformedTable;
import  org.teherba.dbat.format.WikiTable;
import  org.teherba.dbat.format.XMLTable;
import  java.util.Arrays; // asList
import  java.util.Iterator;
import  java.util.List;
import  java.util.StringTokenizer;
import  org.apache.log4j.Logger;

/** Selects a specific table output format, and iterates over the descriptions
 *  of all table formats and their codes.
 *  @author Dr. Georg Fischer
 */
public class TableFactory { 
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;
    
    /** Set of Tables for different file formats */
    private BaseTable[] allTables;
    
    /** No-args Constructor
     */
    public TableFactory() {
        log = Logger.getLogger(TableFactory.class.getName());
        allTables = new BaseTable[] { null
                // the following order is HTML first, then by pleasant descriptions 
                // this order defines the one in the menue of more.jsp 
                , new HTMLTable         ()     // [0] is the default
                , new ExcelTable        ()
                , new XMLTable          () 
                , new FixedWidthTable   () 
                , new SeparatedTable    () 
                , new SQLTable          () 
                , new SQLUpdateTable    () 
                , new JDBCTable         () 
                , new JSONTable         () 
                , new DefaultSpecTable  () 
                , new TayloredTable     () 
                , new TransformedTable  () 
                , new TableGenerator    ()
                , new WikiTable         ()
                , new EchoSQL           ()
                , new GenerateSQLJ      ()
                , new ProbeSQL          ()
                }; 
    } // Constructor

    /** Gets an iterator over all implemented Tables.
     *  @return list iterator over <em>allTables</em>
     */
    public Iterator /*<1.5*/<BaseTable>/*1.5>*/ getIterator() {
        List /*<1.5*/<BaseTable>/*1.5>*/ list = Arrays.asList(allTables);
        Iterator /*<1.5*/<BaseTable>/*1.5>*/ result = list.iterator();
        result.next(); // skip initial null element
        return result;
    } // getIterator
    
    /** Gets the number of available Tables
     *  @return number of formats which can be spelled
     */
    public int getCount() {
        return allTables.length - 1; // omit element [0] (== null)
    } // getCount
    
    /** Determines whether the format code denotes this 
     *  Table class.
     *  @param baseTable the table formatter to be tested
     *  @param format code for the desired format
     */
    public boolean isApplicable(BaseTable baseTable, String format) {
        boolean result = false;
        StringTokenizer tokenizer = new StringTokenizer(baseTable.getFormatCodes(), ",");              
        while (! result && tokenizer.hasMoreTokens()) { // try all tokens
            if (format.equals(tokenizer.nextToken())) { 
                result = true;
            }
        } // while all tokens
        return result;
    } // isApplicable

    /** Gets the applicable table serializer for a specified format code.
     *  @param format abbreviation for the format: xml, html, sql, jdbc, tsv, fix
     *  @return the table writer for that format, or <em>null</em> if the 
     *  format was not found
     */
    public BaseTable getTableSerializer(String format) {
        BaseTable baseTable = allTables[0];
        // determine the applicable BaseTable for 'format'
        int itab = 1;
        boolean found = false;
        while (itab < allTables.length) {
            if (isApplicable(allTables[itab], format)) { // found
                baseTable = allTables[itab];
                itab = allTables.length; // break loop
                found = true;
            } // if found
            itab ++;
        } //  while itab
        if (! found) {
            log.error("unknown format " + format);
            format = "html";
            baseTable = allTables[1]; // HTML = default
        } 
        baseTable.setOutputFormat(format);
        return baseTable;
    } // getTableSerializer

    /** Gets a plain text list of formatting codes and
     *  their descrioption, for help text output
     *  @param language ISO country code: "de", "en"
     *  @return plain text list with one line per format
     */
    public String getHelpList(String language) {
        if (language == null) { // safety brake
            language = "en";
        }
        StringBuffer result = new StringBuffer(256);
        final String SPACE2 = "  ";     
        Iterator /*<1.5*/<BaseTable>/*1.5>*/ titer = this.getIterator();
        while (titer.hasNext()) {
            BaseTable tableFormat = (BaseTable) titer.next();
            result.append(SPACE2);
            result.append(tableFormat.getFormatCodes());
            result.append("\t");
            result.append(tableFormat.getDescription(language));
            result.append("\n");
        } // titer
        return result.toString();
    } // getHelpList

} // TableFactory
