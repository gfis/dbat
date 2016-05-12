/*  Generator for an XML table
    @(#) $Id$
    2014-03-04: ignore pseudo columns
    2012-07-09: null values mit isnull="yes" attribute
    2011-12-16: describeProcedureColumns; 12 Jahre oo
    2011-08-24: writeGenericRow
    2011-04-27: excape < & >
    2011-04-01: output of SpecHandler.DBAT_URI is suppressed with an attribute <dbat namespace="no" ...>
    2011-03-29: writeProcessingInstruction
    2011-03-23: <dbat xmlns=\"" + SpecHandler.DBAT_URI
    2010-02-25: charWriter.write -> .print
    2010-02-19: remark on column
    2008-02-08: with describeColumn
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
import  org.teherba.dbat.Configuration;         // for DBAT_URI
import  org.teherba.dbat.SpecificationHandler;  // for ROOT_TAG
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  java.sql.DatabaseMetaData;
import  java.sql.Types;
import  java.util.ArrayList;
import  java.util.HashMap;
import  java.util.Iterator;
import  java.util.TreeMap;

/** Generator for an XML table.
 *  The syntax is the same for an ordinary HTML table,
 *  only prefixed by the XML processing instruction.
 *  @author Dr. Georg Fischer
 */
public class XMLTable extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** whether an XML declaration was already output */
    protected boolean xmlDeclared;
    /** encoding for output */
    private String encoding;

    /** No-args Constructor
     */
    public XMLTable() {
    	this("xml");
    } // Constructor

    /** Constructor with format
     *  @param format = "xml"
     */
    public XMLTable(String format) {
        super();
        setFormatCodes(format);
        setDescription("en", "XML");
        xmlDeclared = false;
        encoding = "UTF-8";
    } // Constructor

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *  @param params array of 0 or more (name, value) strings which specify features in the file header.
     *  @param parameterMap map of request parameters to values
     *  The following names are interpreted:
     *  <ul>
     *  <li>encoding - encoding to be used for the output stream</li>
     *  </ul>
     */
    public void writeStart(String[] params,  HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        String encoding = getTargetEncoding();
        String nsp      = "";
        String xslt     = null;
        tableSeqNo = 0;
        try {
            int iparam = params.length;
            while (iparam > 0) {
                iparam -= 2;
                if (false) {
                } else if (params[iparam].equals("encoding")) {
                    encoding    = params[iparam + 1];
                } else if (params[iparam].equals("nsp")) {
                    nsp         = params[iparam + 1].toLowerCase();
                } else if (params[iparam].equals("xslt")) {
                    xslt        = params[iparam + 1];
                } else { // "dummy" - skip pair
                }
            } // while iparam
            charWriter.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
            if (true) {
                // do nothing
            } else if (xslt != null) { // an URL to a valid XSL transformation stylesheet
                writeProcessingInstruction("xml-stylesheet",  "type=\"text/xsl\" href=\"" + xslt + "\"");
            } else {
                charWriter.println("<!DOCTYPE dbat [");
                charWriter.print  (getEntitiesFromParameters(parameterMap));
                charWriter.println("]>");
            }
            charWriter.println("<" + SpecificationHandler.ROOT_TAG
                        +   ( nsp.length() == 0 ? ""
                            : " xmlns:" + nsp + "=\"" + Configuration.DBAT_URI + "\""
                            )
                        + ">");
            xmlDeclared = true;
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeStart

    /** Ends a file that may contain several table descriptions and/or a SELECT result sets
     */
    public void writeEnd() {
        try {
            charWriter.println("</" + SpecificationHandler.ROOT_TAG + ">");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeEnd

    /** Initializes a table
     *  @param tableName name of the table
     */
    public void startTable(String tableName) {
        super.startTable(tableName);
        try {
            if (! xmlDeclared) {
                xmlDeclared = true;
                charWriter.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\" ?>");
            }
            tableSeqNo ++;
            charWriter.println("<table id=\"tab" + String.valueOf(tableSeqNo) + "\" name=\"" + tableName + "\">");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startTable

    /** Terminates  a table
     */
    public void endTable() {
         try {
            charWriter.println("</table>");
         } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endTable

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
            charWriter.print("<create");
            if (schema != null) {
                charWriter.print(" schema=\"" + schema + "\"");
            }
            charWriter.print(" type=\"" + tableType + "\"");
            charWriter.print(" name=\"" + tableName + "\">");
            charWriter.println();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startDescription

    /** Ends the description of a table.
     */
    public void endDescription() {
        try {
            charWriter.println("</create>");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endDescription

    /** Returns the definition string for a table column in HTML format.
     *  @param colNo number of the column: 0, 1, 2
     *  @param column bean with the properties of the column
     */
    public void describeColumn(int colNo, TableColumn column) {
        StringBuffer buffer = new StringBuffer(128);
        try {
            buffer.append("\t<column name=\"");
            buffer.append(column.getName());
            buffer.append("\"");
            buffer.append(" type=\"");
            String typeName = column.getTypeName();
            int    dataType = column.getDataType();
            buffer.append(typeName);
            buffer.append("\"");

            int width = column.getWidth();
            if (dataType == Types.CHAR || dataType == Types.VARCHAR || dataType == Types.DECIMAL) {
                buffer.append(" width=\"");
                buffer.append(width);
                buffer.append("\"");
                if (dataType == Types.DECIMAL) {
                    int decimalDigits = column.getDecimal();
                    if (decimalDigits != 0) {
                        buffer.append(" decimal=\"");
                        buffer.append(String.valueOf(decimalDigits));
                        buffer.append("\"");
                    }
                } // decimal
            } // width

            if (! column.isNullable()) {
                buffer.append(" nullable=\"false\"");
            }
            String remark = column.getRemark();
            if (remark != null && remark.length() > 0) {
                buffer.append(" remark=\"");
                buffer.append(remark);
                buffer.append("\"");
            }
            buffer.append(" />");
            charWriter.println(buffer.toString());
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
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
        Iterator<String> citer = cstRows.keySet().iterator();
        String pkName = "";
        String pkColumnNames = "";
        String keySeq = "000"; // normalized to 3 digits
        HashMap<String, String> oldRow = new HashMap<String, String>();
        while (citer.hasNext()) { // process all cstRows
            String cstKey = citer.next();
            int tabPos = cstKey.indexOf("\t");
            String newName = cstKey.substring(0, tabPos); // ignore keySeq behind
            keySeq         = cstKey.substring(tabPos + 1);
            if (! newName.equals(pkName)) { // control change
                if (pkName == null || pkName.equals("PRIMARY")) {
                    pkName = "PK29"; // replace by some dummy constraint name
                }
                if (pkName.length() > 0) { // not first control change
                    charWriter.println("\t<constraint name=\"" + pkName + "\" type=\"primary\">\n"
                            + pkColumnNames
                            + "\t</constraint>\n");
                } // not first
                pkName = newName;
            } // controlChange
            oldRow = cstRows.get(cstKey);
            pkColumnNames += "\t\t\t<key name=\"" + oldRow.get("COLUMN_NAME") + "\" />\n";
        } // while all rows
    } // describePrimaryKey

    /** Writes the closing bracket (if any) behind the column descriptions.
     */
    public void describeColumnsEnd() {
    } // describeColumnsEnd

    /** Writes the description of all indexes
     *  @param tableName fully qualified name of the table
     *  @param cstRows array for the result set of <em>DatabaseMetaData.getIndexInfo</em>, properly sorted
     *  by INDEX_NAME and ORDINAL_POSITION,
     *  with an additional fictitious row for the last group change
     */
    public void describeIndexes(String tableName, TreeMap<String, HashMap<String, String>> cstRows) {
    /*
        Iterator<String> citer = cstRows.keySet().iterator();
        String ixName = "";
        String ixColumnNames = null;
        String keySeq = "000"; // normalized to 3 digits
        HashMap<String, String> oldRow = new HashMap<String, String>();
        while (citer.hasNext()) { // process all cstRows
            String cstKey   = citer.next();
            int tabPos      = cstKey.indexOf("\t");
            String newName  = cstKey.substring(0, tabPos); // ignore keySeq behind
            keySeq          = cstKey.substring(tabPos + 1);
            oldRow          = cstRows.get(cstKey);
            if (! newName.equals(ixName)) { // control change
                String unique = oldRow.get("NON_UNIQUE");
                if (ixName.length() > 0) { // not first control change
                    charWriter.println("CREATE "
                            + (unique == null ? "" : unique)
                            + "INDEX " + ixName + " ON " + tableName);
                    charWriter.println(ixColumnNames + "\t);");
                } // not first
                ixName = newName;
            } // controlChange
            ixColumnNames   = "\t\t<column>" + oldRow.get("COLUMN_NAME") + "</column>";
            ixColumnNames  += "\t" + oldRow.get("ASC_OR_DESC") + "\n"; // dir may be ""
        } // while all rows
    */
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
    /*
        Iterator<String> citer = cstRows.keySet().iterator();
        String fkName = "";
        String fkColumnNames = null;
        String pkColumnNames = null;
        String keySeq = "000"; // normalized to 3 digits
        HashMap<String, String> oldRow = new HashMap<String, String>();
        while (citer.hasNext()) { // process all cstRows
            String cstKey = citer.next();
            int tabPos = cstKey.indexOf("\t");
            String newName = cstKey.substring(0, tabPos); // ignore keySeq behind
            keySeq         = cstKey.substring(tabPos + 1);
            if (! newName.equals(fkName)) { // control change
                if (fkName.length() > 0) { // not first control change
                    charWriter.println("ALTER TABLE "    + tableName);
                    charWriter.println("\tADD CONSTRAINT " + fkName      + " FOREIGN KEY (" + fkColumnNames + ")");
                    charWriter.println("\t\tREFERENCES " + oldRow.get("PKTABLE_NAME") + "(" + pkColumnNames + ")");
                    charWriter.println("\t\tON UPDATE "  + oldRow.get("UPDATE_RULE")); // is already converted to String
                    charWriter.println("\t\tON DELETE "  + oldRow.get("DELETE_RULE")); // is already converted to String
                    charWriter.println("\t;");
                } // not first
                fkName = newName;
            } // controlChange
            oldRow = cstRows.get(cstKey);
            if (keySeq.equals("001")) {
                fkColumnNames  =        oldRow.get("FKCOLUMN_NAME");
                pkColumnNames  =        oldRow.get("PKCOLUMN_NAME");
            } else {
                fkColumnNames += ", " + oldRow.get("FKCOLUMN_NAME");
                pkColumnNames += ", " + oldRow.get("PKCOLUMN_NAME");
            }
        } // while all rows
    */
    } // describeConstraints

    /** Writes the description of a stored procedure
     *  @param schema             of the procedure
     *  @param procedureName name of the procedure
     *  @param procedureType type of the procedure as returned by <em>DatabaseMetaData.getProcedures</em>
     *  @param procSeparator string between CREATE PROCEDURE, BEGIN etc.
     *  @param cstRows array for the result set of <em>DatabaseMetaData.getProcedureColumns</em>, properly sorted
     *  by ORDINAL_POSITION, that is return value (if any), and parameters in call order
     *  with an additional fictitious row for the last group change
     */
    public void describeProcedureColumns(String schema, String procedureName
            , short  procedureType
            , String procSeparator
            , TreeMap<String, HashMap<String, String>> cstRows) {

        charWriter.print("<create");
        if (schema != null) {
            charWriter.print(" schema=\"" + schema + "\"");
        }
        charWriter.print(" type=\"PROCEDURE\"");
        charWriter.print(" name=\"" + procedureName + "\">");
        charWriter.println();

        Iterator<String> citer = cstRows.keySet().iterator();
        String keySeq = "000"; // ORDINAL_POSITION, normalized to 3 digits
        String value  = null;
        HashMap<String, String> oldRow = new HashMap<String, String>();
        while (citer.hasNext()) { // process all cstRows
            String cstKey = citer.next();
            oldRow = cstRows.get(cstKey);
            int tabPos = cstKey.indexOf("\t");
            String newName = cstKey.substring(0, tabPos); // ignore keySeq behind
            if (newName.equals(procedureName)) { // in first group
                StringBuffer colBuffer = new StringBuffer(64);
                colBuffer.append("\t<column name=\"");
                colBuffer.append(oldRow.get("COLUMN_NAME"));
                colBuffer.append("\"");

                colBuffer.append("\tdir=\"");
                value = oldRow.get("COLUMN_TYPE");
                if (value != null) {
                    colBuffer.append(value.toLowerCase());
                }
                colBuffer.append("\"");

                colBuffer.append("\ttype=\"");
                value = oldRow.get("TYPE_NAME");
                if (value != null) {
                    colBuffer.append(value);
                }
                colBuffer.append("\"");
                int    dataType = 0;
                int    precision= 0;
                short  scale    = 0;
                try {
                    dataType = Integer.parseInt(oldRow.get("DATA_TYPE"));
                    precision= Integer.parseInt(oldRow.get("PRECISION"));
                    scale    = Short.parseShort(oldRow.get("SCALE"));
                } catch (Exception exc) {
                }
                switch(dataType) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                        colBuffer.append(" width=\"");
                        colBuffer.append(String.valueOf(precision));
                        colBuffer.append("\"");
                        break;
                    case Types.DECIMAL:
                        colBuffer.append(" width=\"");
                        colBuffer.append(String.valueOf(precision));
                        colBuffer.append("\"");
                        if (scale != 0) {
                            colBuffer.append(" decimal=\"");
                            colBuffer.append(String.valueOf(scale));
                        colBuffer.append("\"");
                        }
                        break;
                } // dataType

                colBuffer.append("\tnullable=\"");
                colBuffer.append(oldRow.get("NULLABLE").startsWith("IS") ? "true" : "false");
                colBuffer.append("\"");

                String remark = oldRow.get("REMARK");
                if (remark != null && remark.length() > 0) {
                    colBuffer.append("\tremark=\"");
                    colBuffer.append(remark);
                    colBuffer.append("\"");
                } // with remark

                colBuffer.append(" />");
                charWriter.println(colBuffer.toString());
            } // in first group
        } // while all column descriptions
        charWriter.println("</create>");
    } // describeProcedureColumns

    /** Writes a comment line.
     *  @param line string to be output as a comment line
     */
    public void writeComment(String line) {
        try {
            charWriter.println("<!-- " + line + " -->");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeComment

    /** Tells, for the specific format, the rule to be applied for escaping.
     *  The result may optionally depend on the column's attributes and/or the cell value.
     *  @param colAttrs attributes of this column
     *  @param value can be tested for the occurrence of HTML tags
     *  @return the following escaping rules are currently observed:
     *  <ul>
     *  <li>0 = no escaping at all</li>
     *  <li>1 = "&amp;", "&lt;" and "&gt;" are escaped
     *  as "&amp;amp;", "&amp;lt;" and "&amp;gt;" respectively</li>
     *  <li>2 = "&apos;" is replaced by "&amp;apos"</li>
     *  <li>3 = combination of rule 1 and rule 2</li>
     *  <li>4 = like 1, but internally check column.expr for start of tag ('<')
     *  </ul>
     */
    /** Tells, for the specific format, the rule to be applied for escaping.
     */
    public int getEscapingRule() {
        return 4; // escape & < > if expr.matches(".*[<&>].*") ? 1 : 0;
    } // getEscapingRule

    /** Writes an XML processing instruction, or nothing for other formats.
     *  @param target the processing instruction target
     *  @param data the processing instruction data, or null if none was supplied.
     *  The data does not include any whitespace separating it from the target
     */
    public void writeProcessingInstruction(String target, String data) {
        try {
            charWriter.println("<?" + target + " " + data + "?>");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeProcessingInstruction

    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *  @param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList/*<1.5*/<TableColumn>/*1.5>*/ columnList) {
        TableColumn column = null;
        String pseudo = null;
        int ncol = columnList.size();
        int icol = 0;
        switch (rowType) {
            case HEADER:
                charWriter.print("<tr>");
                while (icol < ncol) {
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (true || pseudo == null) {
                        charWriter.println("<th>" + column.getLabel() + "</th>");
                    } // ! pseudo
                    icol ++;
                } // while icol
                charWriter.println("</tr>");
                break;
            case DATA:
                charWriter.print("<tr>");
                while (icol < ncol) {
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (true || pseudo == null) {
                       if (! column.getValue().equals("null")) {
                            charWriter.print("<td>"
                                    + column.getValue()
                                    + "</td>");
                        } else { // == null
                            charWriter.print("<td isnull=\"yes\"></td>");
                        }
                    } // ! pseudo
                    icol ++;
                } // while icol
                charWriter.println("</tr>");
                break;
        } // switch rowType
    } // writeGenericRow

} // XMLTable
