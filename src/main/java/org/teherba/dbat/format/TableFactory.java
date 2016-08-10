/*  Selects the applicable table generator
    @(#) $Id$
    2016-08-09: former name DefaultSpecTable -> SpecDescription, + ViewDescription; reordered
    2016-05-12: ExcelStream with Apache POI replaces ExcelTable
    2013-01-18: dynamic, with Class.forName
    2012-06-16: GenerateSQLJ
    2012-05-22: JSONTable for Ajax
    2011-09-10: TableGenerator, -m gen
    2011-07-27: WikiTable, -m wiki
    2011-07-15: TayloredTable, -m taylor
    2011-04-15: SQLUpdateTable, -m update
    2011-04-08: TransformedTable
    2010-07-24: ExcelTable, -m xls
    2010-06-16: EchoSQL, -m echo
    2010-02-26: default HTML; getTableSerializer in favour of getTableWriter
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
package org.teherba.dbat.format;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.HTMLTable; // this is the default
import  java.util.ArrayList;
import  java.util.Iterator;
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

    /** Array of serializers for different output formats */
    private ArrayList<BaseTable> serializers;

    /** Attempts to instantiate the serializer for some output format
     *  @param serializerName name of the class for the serializer
     */
    private boolean addSerializer(String serializerName) {
        boolean result = true; // assume that class is found
        try {
            BaseTable serializer = (BaseTable) Class.forName("org.teherba.dbat.format." + serializerName).newInstance();
            serializers.add(serializer);
        } catch (Exception exc) {
            // ignore any error almost silently - this output format will not be known
            result = false;
        }
        return result;
    } // addSerializer

    /** No-args Constructor
     */
    public TableFactory() {
        log = Logger.getLogger(TableFactory.class.getName());
        try {
            serializers = new ArrayList<BaseTable>(32);
            addSerializer("HTMLTable"         );
            addSerializer("XMLTable"          );
            addSerializer("ExcelStream"       ); // Apache POI HSSF = BIFF (<= 2003) or XSSF = OOXML (>= 2007)
            addSerializer("JSONTable"         );
            addSerializer("WikiTable"         );

            addSerializer("SeparatedTable"    );
            addSerializer("FixedWidthTable"   );
            addSerializer("TayloredTable"     );

            addSerializer("SQLTable"          );
            addSerializer("SQLUpdateTable"    );
            addSerializer("JDBCTable"         );

            addSerializer("TransformedTable"  );
            addSerializer("TableGenerator"    );

            addSerializer("SpecDescription"   );
            addSerializer("ViewDescription"   );
            addSerializer("EchoSQL"           );
            addSerializer("GenerateSQLJ"      );
            addSerializer("ProbeSQL"          );
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // Constructor(0)

    /** Gets an iterator over all implemented Tables.
     *  @return list iterator over <em>allTables</em>
     */
    public Iterator /*<1.5*/<BaseTable>/*1.5>*/ getIterator() {
        return serializers.iterator();
    } // getIterator

    /** Gets the number of available Tables (for example for HTML listboxes)
     *  @return number of formats which can be spelled
     */
    public int getCount() {
        return serializers.size(); // omit element [0] (= null)
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
        BaseTable result = null;
        // determine the applicable BaseTable for 'format'
        Iterator<BaseTable> siter = getIterator();
        boolean notFound = true;
        while (notFound && siter.hasNext()) {
            BaseTable serializer = siter.next();
            if (isApplicable(serializer, format)) { // found
                result = serializer;
                notFound = false;
            } // if found
        } //  while not found
        if (notFound) {
            log.error("unknown format " + format);
            result = new HTMLTable();
            format = "html";
        }
        result.setOutputFormat(format);
        return result;
    } // getTableSerializer

    /** Gets a plain text list of formatting codes and
     *  their description, for help text output
     *  @param language ISO country code: "de", "en"
     *  @return plain text list with one line per format
     */
    public String getHelpList(String language) {
        if (language == null || language.length() < 2) { // safety brake
            language = "en";
        }
        StringBuffer result = new StringBuffer(256);
        final String SPACES = "                "; // 16 x ' '
        Iterator <BaseTable> titer = getIterator();
        while (titer.hasNext()) {
            BaseTable tableFormat = (BaseTable) titer.next();
            result.append("  ");
            String code = tableFormat.getFormatCodes();
            result.append(code);
            result.append(SPACES.substring(0, 10 - code.length()));
            result.append(tableFormat.getDescription(language));
            result.append("\n");
        } // titer
        return result.toString();
    } // getHelpList

} // TableFactory
