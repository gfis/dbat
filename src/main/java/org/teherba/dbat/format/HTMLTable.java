/*  Generator for an HTML table
    @(#) $Id$
    2020-11-16: scrollArea="width,height"; endTable; <div> around the table
    2020-05-04: attribute target= in <col>
    2017-05-27: javadoc
    2016-12-09: avoid empty Javascrpt function calls
    2016-10-03: style could have been null
    2016-05-19: repair style on rows (table id)
    2015-04-22: repair title="..." attribute for SQL REPLACE with embedded HTML (highlighting)
    2014-11-10: s|getHrefValue -> s|getWrappedValue
    2012-12-10: sortable again and counter 0, language dependant
    2012-11-29: sorttable.js
    2012-05-15: pseudo column with style behind all real columns => Javascript sets style on row
    2012-01-05: no Javascript
    2011-11-16: describeConstraints with TreeMap
    2011-09-30: <col remark="...">
    2011-08-24: writeGenericRow
    2011-08-12: remove old methods write*
    2011-05-27: handle parameter substitution with JavaScript
    2011-03-29: (empty) writeProcessingInstruction
    2011-03-23: <meta name="robots" content="noindex, nofollow" />
    2011-02-16: <describe> with methods of SQLTable
    2011-01-21: xhtml and entities for request parameters
    2010-11-26: writeTrailer (previously in endDocument)
    2010-03-24: multiple parameters in 'link' attribute
    2010-02-25: charWriter.write -> .print
    2010-02-06: use targetEncoding
    2007-01-12: copied from BaseTable
    2006-09-19: copied from numword.BaseSpeller

    The small icons in web/img used for action fields were taken:
    - upd.png   from phpmyadmin
    - del.png   from phpmyadmin
    - dat.png   from http://commons.wikimedia.org/wiki/File:Button_multicol.png
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
import  org.teherba.dbat.format.SQLTable; // for <describe>
import  org.teherba.dbat.format.XMLTable;
import  org.teherba.dbat.web.Messages;
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  java.sql.DatabaseMetaData;
import  java.util.ArrayList;
import  java.util.HashMap;
import  java.util.TreeMap;

/** Generator for an XHTML table, maybe with links, styles on some columns.
 *  The output of the table itself is the same as with <code>XMLTable</code>,
 *  but HTML markup, forms and so on are also possible.
 *  @author Dr. Georg Fischer
 */
public class HTMLTable extends XMLTable {
    public final static String CVSID = "@(#) $Id$";

    /** Instance for DDL output */
    private SQLTable sqlTable;

    /** No-args Constructor
     */
    public HTMLTable() {
        this("html");
    } // Constructor

    /** Constructor with format
     *  @param format = "xml"
     */
    public HTMLTable(String format) {
        super();
        setFormatCodes(format);
        setDescription("en", "HTML");
    } // Constructor

    // method 'endTable'        is inherited from XMLTable
    // method 'writeComment'    is inherited from XMLTable

    /** Whether a table should be sortable via sorttable.js */
    private boolean isSortable;
    /** Whether table headers should be sticky */
    private boolean isSticky;
    /** The natural language for messages and internationalized text portions */
    private String language;

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *  @param attributes array of 0 or more pairs of strings (name1, value1, name2, value2 and so on) 
     *  which specify features in the header of the file to be generated.
     *  The possible attribute names are described in {@link BaseTable#writeStart}.
     *  @param parameterMap map of request parameters to values
     */
    public void writeStart(String[] attributes,  HashMap<String, String[]> parameterMap) {
        String contenttype  = getMimeType();
        String encoding     = getTargetEncoding();
        String javascript   = null;
        language            = "en"; // default
        isSortable          = false;
        String stylesheet   = "stylesheet.css";
        String target       = null;
        String title        = "dbat";
        String xslt         = null;
        tableSeqNo = 0;
        try {
            int iattr = attributes.length;
            while (iattr > 0) {
                iattr -= 2;
                if (false) {
                } else if (attributes[iattr].equals("contenttype"))    { contenttype   = attributes[iattr + 1];
                } else if (attributes[iattr].equals("encoding"))       { encoding      = attributes[iattr + 1];
                } else if (attributes[iattr].equals("javascript"))     {
                    javascript = attributes[iattr + 1];
                    isSortable = javascript.indexOf("sorttable") >= 0;
                } else if (attributes[iattr].equals("lang"))           { language      = attributes[iattr + 1];
                } else if (attributes[iattr].equals("stylesheet"))     { stylesheet    = attributes[iattr + 1];
                } else if (attributes[iattr].equals("target"))         { target        = attributes[iattr + 1];
                } else if (attributes[iattr].equals("title"))          { title         = attributes[iattr + 1];
                } else if (attributes[iattr].equals("xslt"))           { xslt          = attributes[iattr + 1];
                } else { // "dummy" - skip pair
                }
            } // while iattr

            charWriter.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
            charWriter.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""
                    + "\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\" [");
            charWriter.println("]>");
        /*
        */
        /*  handle parameter substitution with JavaScript in the future
            if (System.getProperty("file.separator").startsWith("/")) {
                charWriter.print  (getEntitiesFromParameters(parameterMap));
            } // else IE6: cannot handle Entity declarations in DTDs
        */
            if (xslt != null) { // an URL to a valid XSL transformation stylesheet
                charWriter.println("<?xml-stylesheet type=\"text/xsl\" href=\"" + xslt + "\" ?>");
            }
            charWriter.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            charWriter.println("<head>");
            charWriter.println("<meta http-equiv=\"Content-Type\" content=\"" + contenttype + "\" />");
            charWriter.println("<meta name=\"robots\" content=\"noindex, nofollow\" />");
            charWriter.println("<title>" + title + "</title>");
            if (stylesheet != null) { // always
                String[] stylesheets = stylesheet.split("\\s+");
                int icss = 0;
                while (icss < stylesheets.length) {
                    charWriter.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + stylesheets[icss] + "\" />");
                    icss ++;
                } // while icss
            } // stylesheet != null
            if (javascript != null) {
                String[] javascripts = javascript.split("\\s+");
                int ijs = 0;
                while (ijs < javascripts.length) {
                    charWriter.println("<script src=\"" + javascripts[ijs] + "\" type=\"text/javascript\"></script>");
                    ijs ++;
                } // while icss
            } // javascript != null
            charWriter.println("</head><body>");
            if (target != null) {
                charWriter.println("<base target=\"" + target + "\" />");
            }
        } catch (Exception exc) {
            if (true) {
                log.error(exc.getMessage(), exc);
                System.err.println("encoding="  + encoding
                        + ", contenttype="      + contenttype
                        + ", javascript="       + javascript
                        + ", stylesheet="       + stylesheet
                        + ", target="           + target
                        + ", title="            + title
                        );
            } else {
                System.out.println("HTMLTable#writeStart: charWriter == null");
            }
        }
    } // writeStart

    /** Ends   a file that may contain several table descriptions and/or a SELECT result sets
     */
    public void writeEnd() {
        try {
            charWriter.println("</body></html>");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeEnd

    /** Writes trailer information
     *  @param trailer trailing elements with links to spec file, Excel output, timestamp etc.
     */
    public void writeTrailer(String trailer) {
        try {
            if (trailer != null && trailer.length() > 0) {
                charWriter.println("<br />" + trailer);
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeTrailer

    /** Starts the description of a table with DROP table and CREATE TABLE
     *  @param dbMetaData database metadata
     *  as obtained with <code>con.getMetaData()</code>
     *  @param schema    name of the table's schema, or null if none
     *  @param tableName name of the table
     *  @param tableType type of the table: "TABLE", "VIEW", etc.
     */
    public void startDescription(DatabaseMetaData dbMetaData
            , String schema, String tableName, String tableType) {
        try {
            writeMarkup("\n<table><tr><td><pre>");
            sqlTable = new SQLTable();
            sqlTable.setOutputFormat("sql");
            sqlTable.setCharWriter(charWriter);
            sqlTable.setTargetEncoding(getTargetEncoding());
            sqlTable.startDescription(dbMetaData, schema, tableName, tableType);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startDescription

    /** Ends the description of a table.
     */
    public void endDescription() {
        try {
            sqlTable.endDescription();
            writeMarkup("</pre></td></tr></table>\n");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endDescription

    /** Returns the definition string for a table column in HTML format.
     *  @param colNo number of the column: 0, 1, 2
     *  @param column bean with the properties of the column
     */
    public void describeColumn(int colNo, TableColumn column) {
        sqlTable.describeColumn(colNo, column);
    } // describeColumn

    /** Writes the description of any primary key of the table, for example (for SQL):
     *  <pre>
     *  , PRIMARY KEY (COL1, COL2)
     *  );
     *  </pre>
     *  @param tableName name of the table on which the constraint is defined
     *  @param cstRows array for the result set of <em>DatabaseMetaData.getImportedKeys</em>, properly sorted
     *  by PKNAME and KEY_SEQ,
     *  with an additional fictitious row for the last group change
     */
    public void describePrimaryKey(String tableName, TreeMap<String, HashMap<String, String>> cstRows) {
        sqlTable.describePrimaryKey(tableName, cstRows);
    } // describePrimaryKey

    /** Writes the closing bracket (if any) behind the column descriptions, for example (for SQL):
     *  <pre>
     *  );
     *  </pre>
     *  The individual output format may skip this method, and may output the closing bracket
     *  behind the primary key, or even behind all constraints.
     */
    public void describeColumnsEnd() {
        sqlTable.describeColumnsEnd();
    } // describeColumnsEnd

    /** Writes the description of all indexes
     *  @param tableName fully qualified name of the table
     *  @param cstRows array for the result set of <em>DatabaseMetaData.getIndexInfo</em>, properly sorted
     *  by INDEX_NAME and ORDINAL_POSITION,
     *  with an additional fictitious row for the last group change
     */
    public void describeIndexes(String tableName, TreeMap<String, HashMap<String, String>> cstRows) {
        sqlTable.describeIndexes(tableName, cstRows);
    } // describeIndexes

    /** Writes the descriptions of all imported (foreign) key constraints as SQL of the form
     *  <pre>
     *  ALTER TABLE tabname
     *      ADD CONSTRAINT fkname1 REFERENCES pkTableName (fkColumnName1, ...)
     *          ON UPDATE ...
     *  </pre>
     *  @param tableName name of the table on which the constraint is defined
     *  @param cstRows array for the result set of <em>DatabaseMetaData.getImportedKeys</em>, properly sorted
     *  by FKNAME and KEY_SEQ,
     *  with an additional fictitious row for the last group change
     */
    public void describeConstraints(String tableName, TreeMap<String, HashMap<String, String>> cstRows) {
        sqlTable.describeConstraints(tableName, cstRows);
    } // describeConstraints

    /** local copy of the table's id */
    private String tableId;
    
    /** Initializes a table - with meta data, 
     *  @param tableName name of the table
     *  @param tbMetaData meta data of the table
     */
    public void startTable(String tableName, TableMetaData tbMetaData) {
        try {
            tableId = tableName;
            tableSeqNo ++;
            tableRowNo = 0;
            String divStyle = "";
            String scroll = tbMetaData.getScrollArea();
            if (scroll != null && ! scroll.equals("0,0")) { // do scroll and sticky headers
                isSticky = true;
                divStyle = " style=\"display: inline-block;";
                String[] area = scroll.split("\\,");
                String width  = "";
                String height = "";
                if (area.length == 1) { // height only
                    divStyle += " height: " + area[0] + ";";
                } else { // both "width,height" or ",height" or "width,"
                    if (area[1].length() > 0 && ! area[1].equals("0")) {
                        divStyle += " height: " + area[1] + ";";
                    }
                    if (area[0].length() > 0 && ! area[0].equals("0")) {
                        divStyle += " width: "  + area[0] + ";";
                    }
                } // both
                divStyle += " overflow: auto\"";
            } // do scroll
            charWriter.println("<div" + divStyle + ">");
            String tableClass = ""; // default: no classes
            if (isSortable) {
                tableClass += " class=\"sortable\"";
            }
            charWriter.println("<table id=\"" + tableId + "\"" + tableClass + ">");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startTable

    /** Terminates a table
     */
    public void endTable() {
        isSticky = false; // back to default
        try {
            charWriter.println("</table>");
            charWriter.println("</div>");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endTable

    /** Writes the number of selected rows, and a description of the object represented by the rows.
     *  A "+" is added behind the number if there would have been more rows, but the
     *  SELECT was terminated by FETCH_LIMIT.
     *  @param rowCount number of data rows which were output
     *  @param moreRows whether there would have been more rows in the resultset
     *  @param tbMetaData contains the descriptive text for the counter: 1 for "row" and 0 or &gt;= 2 for "rows"
     */
    public void writeTableFooter(int rowCount, boolean moreRows, TableMetaData tbMetaData) {
        String desc = tbMetaData.getCounterDesc(rowCount);
        if (desc != null) { // only if set
            if (isSortable) {
                charWriter.println("<tfoot>");
            }
            charWriter.print("<tr><td class=\"counter\" colspan=\"" + tbMetaData.getLastColumnCount() + "\">");
            if (rowCount == 0) {
                charWriter.print(desc);
            } else {
                charWriter.print(rowCount + (moreRows ? "+ " : " ") + desc);
            }
            charWriter.println("</td></tr>");
            if (isSortable) {
                charWriter.println("</tfoot>");
            }
        } // desc was set
    } // writeTableFooter

    /** Tells, for the specific format, the rule to be applied for escaping.
     *  The result may optionally depend on the column's attributes and/or the cell value.
     *  @return the following escaping rules are currently observed:
     *  <ul>
     *  <li>0 = no escaping at all</li>
     *  <li>1 = "&amp;", "&lt;" and "&gt;" are escaped
     *    as "&amp;amp;", "&amp;lt;" and "&amp;gt;" respectively</li>
     *  <li>2 = "&amp;apos;" is replaced by "&amp;amp;apos;"</li>
     *  <li>3 = combination of rule 1 and rule 2</li>
     *  <li>4 = like 1, but internally check column.expr for start of tag ('&lt;')</li>
     *  </ul>
     */
    public int getEscapingRule() {
        return 4; // escape & < > if expr.matches(".*[<&>].*") ? 1 : 0;
    } // getEscapingRule

    /** Writes HTML markup (for HTML), or nothing
     *  @param markup HTML element(s) to be written
     */
    public void writeMarkup(String markup) {
        try {
            charWriter.print(markup);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeMarkup

    /** Writes a Processing Instruction (for XML), or nothing for other formats.
     *  This empty method must be repeated here since {@link HTMLTable} is derived from {@link XMLTable}.
     *  @param instruction processing instruction to be written, without the SGML brackets
     */
    public void writeProcessingInstruction(String instruction) {
    } // writeProcessingInstruction

    /** Gets the string content of a header or data cell.
     *  The strings obtained by this method can be aggregated
     *  (with some separator) in order to form the contents of an aggregated column.
     *  Pivot tables and aggregation store the result back into the cell's value.
     *  Repeated calls of this method may not lengthen the resulting value.
     *  @param column attributes of this column, containing the value also
     *  @return string content of a cell
     */
    public String getFlatValue(TableColumn column) {
        StringBuffer result = new StringBuffer(128);
        String target       = column.getTarget();
        String value        = column.getValue();
        String wrap         = column.getWrap();
        String wrappedValue = column.getWrappedValue();
        // System.err.println("value=\"" + value + "\", wrappedValue=\"" + wrappedValue + "\"");
        if (false) {
        } else if (wrap != null) {
            if (false) {
            } else if (wrap.startsWith("javascript:") && wrappedValue != null) {
                result.append("<script type=\"text/javascript\">");
                result.append(wrappedValue);
                result.append("</script>");
            } else if (wrap.startsWith("verbatim:")) {
                result.append(value); // without entity replacement
            }
        } else {
            if (wrappedValue != null) {
                result.append("<a href=\"");
                result.append(wrappedValue.replaceAll("&", "&amp;"));
                if (target != null && target.length() > 0) {
                    result.append("\" target=\"");
                    result.append(target);
                }
                result.append("\">");
            }
            String pseudo = column.getPseudo();
            if (pseudo == null) { // this is a real cell, attach any accumulated strings from previous pseudo columns
                result.append(value);
            } else if (pseudo.equals("image")) { // special handling for pseudo="image"
                String srcAttr = value;
                if (value.matches("[\\w]+")) { // for compatibility: images for DBIV actions
                    srcAttr = "img/" + srcAttr + ".png";
                } else { // explicit, complete relative filename or URL
                    // done
                }
                result.append("<img src=\"");
                result.append(srcAttr);
                result.append("\" />");
            } else { // other pseudo attribute
                result.append(value); // because getFlatValue is called once more with this
            }
            if (wrappedValue != null) {
                result.append("</a>");
                column.setWrappedValue(null);
            }
        } // href
        return result.toString();
    } // getFlatValue

    /** Gets a string assignment suitable for the <em>style=</em> or <em>style=</em> attribute
     *  of an HTML element, or for Javascript.
     *  @param js whether to generate the assignment for Javascript
     *  @param styleOrClass either a single word which becomes the className,
     *  or a string containing a colon which becomes the CSS style definition(s)
     *  @return resulting attribute assignment as a string, for example
     *  <pre>
     *     class="red"
     *     style="color:white;background-color:red;"
     *
     *     ){className="red";}
     *     .style){color="white";backgroundColor="red";}
     *  </pre>
     */
    private String getStyleOrClass(boolean js, String styleOrClass) {
        StringBuffer result = new StringBuffer(64);
        int colonPos = styleOrClass.indexOf(':');
        if (js) {
            if (colonPos < 0) { // class
                result.append("){className=\"");
                result.append(styleOrClass);
                result.append("\";");
            } else { // style(s)
                result.append(".style){");
                String[] pairs = styleOrClass.split("\\;");
                int ipair = 0;
                while (ipair < pairs.length) {
                    String style[] = pairs[ipair].split("\\:");
                    if (style.length == 2) {
                        int minusPos = style[0].indexOf("-");
                        if (minusPos >= 0 && minusPos < style[0].length() - 2) {
                            result.append(style[0].substring(0, minusPos));
                            result.append(style[0].substring(minusPos + 1, minusPos + 2).toUpperCase());
                            result.append(style[0].substring(minusPos + 2));
                        } else {
                            result.append(style[0]);
                        }
                        result.append("=\"");
                        result.append(style[1]);
                        result.append("\";");
                    } else {
                        // silently ignore pairs with no or more than 2 colons
                    }
                    ipair ++;
                } // while isty
            } // style(s)
            result.append("}"); // for 'with' statement
            // Javascript
        } else { // normale style or class attribute
            result.append(colonPos < 0 ? " class" : " style");
            result.append("=\"");
            result.append(styleOrClass);
            result.append("\"");
        } // normal
        return result.toString();
    } // getStyleOrClass

    /** Name of class in stylesheet which causes visible   values in control change columns */
    private static final String VISIBLE   = "visible";
    /** Name of class in stylesheet which causes invisible values in control change columns */
    private static final String INVISIBLE = "invisible";

    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *  For group control changes, any {@link #INVISIBLE} style is reset to {@link #VISIBLE}.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *  @param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList<TableColumn> columnList) {
        nextStyle  = null;
        nextLobURL = null;
        TableColumn column = null;
        String pseudo = null;
        int ncol = columnList.size();
        int icol = 0;
        StringBuffer result = new StringBuffer(256);
        switch (rowType) {
            case HEADER:
                if (isSortable) {
                    charWriter.println("<thead>");
                }
                result.append("<tr>");
                while (icol < ncol) {
                    column = columnList.get(icol);
                    String header = column.getLabel();
                    String style  = column.getStyle();
                    if (style != null && style.endsWith(INVISIBLE)) {
                        column.setStyle(VISIBLE);
                    }
                    pseudo = column.getPseudo();
                    if (pseudo != null && pseudo.equals("style")) {
                        nextStyle = null;
                    } else { // non-pseudo
                        if (header == null) {
                            header = "&nbsp;";
                        }
                        result.append("<th");
                        if (isSticky) {
                            result.append(" class=\"sticky\"");
                        }
                        String remark = column.getRemark();
                        if (false) {
                        } else if (isSortable) {
                            result.append(" title=\"");
                            result.append(Messages.getSortTitle(language)); // "Click => Sort"
                            result.append("\"");
                        } else if (remark != null && remark.length() > 0) {
                            result.append(" title=\"");
                            result.append(remark); // should not contain quotes and apostrophes
                            result.append("\"");
                        } else {
                            String expr = column.getExpr();
                            if (expr != null && expr.length() > 0) {
                                result.append(" title=\"");
                                result.append(expr
                                        .replaceAll("\"", "&quot;")
                                //      .replaceAll("\'", "&apos;")
                                        );
                                result.append("\"");
                            }
                        }
                        result.append('>');
                        result.append(header);
                        result.append("</th>");
                    }
                    icol ++;
                } // while icol
                result.append("</tr>");
                charWriter.println(result.toString());
                if (isSortable) {
                    charWriter.println("</thead>");
                }
                tableRowNo ++;
                break;
            case DATA:
                result.append("<tr>");
                while (icol < ncol) {
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (false) {
                    } else if (pseudo == null || pseudo.equals("image")) {
                        result.append("<td");
                        String align = column.getAlign();
                        if (align != null && ! align.equals("left")) {
                            result.append(" align=\"" + align + "\"");
                        }
                        String style = column.getStyle();
                        if (style != null && style.endsWith(VISIBLE)) {
                            column.setStyle(INVISIBLE);
                        }
                        if (false) { // several classes separated by space?
                        } else if (nextStyle != null && nextStyle.length() > 0) {
                            result.append(getStyleOrClass(false, nextStyle));
                        } else if (style     != null && style    .length() > 0) {
                            result.append(getStyleOrClass(false,     style));
                        }
                        nextStyle = null;
                        result.append('>');
                        if (style != null && ! style.endsWith(INVISIBLE)) {
                            result.append(column.getValue());
                        }
                        result.append("</td>");
                    } else if (pseudo.equals("style")) {
                        nextStyle = column.getValue();
                /*
                    } else if (pseudo.equals("url")) {
                        nextLobURL = column.getValue();
                */
                    } // pseudo == null
                    icol ++;
                } // while icol
                if (nextStyle != null) { // pseudo column with style behind all real columns => Javascript sets style on row
                    result.append("\n<script type=\"text/javascript\">");
                    result.append("with(document.getElementById(\"");
                    result.append(tableId);
                    result.append("\").rows[");
                    result.append(String.valueOf(tableRowNo));
                    result.append("]");
                    result.append(getStyleOrClass(true, nextStyle));
                    result.append("</script>");
                } // behind all => apply it to entire <tr> element (row)
                result.append("</tr>");
                charWriter.println(result.toString());
                tableRowNo ++;
                break;
            case DATA2:
                // ignore
                break;
            case ROW1:
                while (icol < ncol) {
                    result.append("<tr>");
                    column = columnList.get(icol);
                    String header = column.getLabel();
                    result.append("<th align=\"left\" class=\"vert\">");
                    result.append(header);
                    result.append("</th>");

                    pseudo = column.getPseudo();
                    if (false) {
                    } else if (pseudo == null || pseudo.equals("image")) {
                        result.append("<td class=\"vert\"");
                        String style = column.getStyle();
                        if (style != null && style.endsWith(VISIBLE)) {
                            column.setStyle(INVISIBLE);
                        }
                        if (false) {
                        } else if (nextStyle != null && nextStyle.length() > 0) {
                            result.append(" class=\"" + nextStyle + "\"");
                        } else if (style     != null && style    .length() > 0) {
                            result.append(" class=\"" + style + "\"");
                        }
                        nextStyle = null;
                        result.append('>');
                        result.append(column.getValue());
                        result.append("</td>");
                    } else if (pseudo.equals("style")) {
                        nextStyle = column.getValue();
                /*
                    } else if (pseudo.equals("url")) {
                        nextLobURL = column.getValue();
                */
                    } // pseudo == null
                    result.append("</tr>");
                    icol ++;
                } // while icol
                charWriter.println(result.toString());
                break;
        } // switch rowType
    } // writeGenericRow

} // HTMLTable
