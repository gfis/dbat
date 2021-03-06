/*  Generator for an SQL table (DDL or INSERT statements)
    @(#) $Id$
 *  2017-05-27: javadoc
    2016-10-13: less imports
    2016-08-26: with getISOTimestamp()
    2016-07-29: describeProcdureCOlumns: IS (NOT) NULL not recognized by MySQL
    2016-05-11: describe width of DATE/TIME/TIMESTAMP columns as comment
    2014-03-04: ignore pseudo columns
    2013-01-04: comments before COMMIT with trailing ";" for DB2 commandline processor
    2012-11-27: writeCommit
    2012-04-17: NON_UNIQUE INDEX; error in INSERT header (getLabel -> getName)
    2011-12-06: CONSTRAINT PRIMARY KEY
    2011-12-03: writeComment(2)
    2011-08-24: writeGenericRow
    2011-08-02: spaces behind escape codes {d, {t, {ts
    2011-07-20: KEY (name) is probably nonstandard, MySQL
    2011-05-04: rowCount incremented locally
    2011-04-14: writeStart, writeEnd
    2011-02-14: writeComment with "--" after all newlines
    2010-02-25: charWriter.write -> .print
    2010-02-19: remark on column
    2009-08-14: care for indexName == null in describeIndexes
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
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  java.sql.DatabaseMetaData;
import  java.sql.Types;
import  java.util.ArrayList;
import  java.util.HashMap;
import  java.util.Iterator;
import  java.util.TreeMap;

/** Generator for SQL INSERT ... INTO ... VALUES statements for the rows of a result set.
 *  In the <em>describe</em> mode it generates DROP/CREATE statements for a table.
 *  Optionally, this class emits JDBC escape sequences for date/time values (when it is
 *  subclassed from {@link JDBCTable}).
 *  @author Dr. Georg Fischer
 */
public class SQLTable extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** maximum number of INSERT statements before a COMMIT */
    public final static int MAX_COMMIT = 250;
    /** local copy of the table's name */
    protected String tableName;
    /** primary   buffer for the assembly of the INSERT statement and its value list */
    protected StringBuffer cellBuffer;
    /** maximum length of assembled output line */
    protected int maxLen;
    /** current length of assembled output line */
    protected int lenCell;
    /** whether dates and timestamps should be written as JDBC escapes */
    protected boolean isJDBC;
    /** local counter of rows, for insertion of COMMIT statements */
    protected int rowCount;

    /** No-args Constructor
     */
    public SQLTable() {
        this("sql");
    } // Constructor()

    /** Constructor with format
     *  @param format either "sql" or "jdbc" (SQL with JDBC escape sequences for dates/times)
     */
    public SQLTable(String format) {
        super(format);
        isJDBC = format.equals("jdbc");
        maxLen = 72;
        cellBuffer = new StringBuffer(256); lenCell = 0;
        rowCount = 0;
        setDescription("en", "SQL INSERTs");
    } // Constructor(format)

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *  @param params array of 0 or more (name, value) string which specify features in the file header.
     *  @param parameterMap map of request parameters to values
     *  The following names are interpreted:
     *  <ul>
     *  <li>contentType - MIME type for the document content</li>
     *  <li>encoding - encoding to be used for the output stream</li>
     *  <li>javaScript - name of the file containing JavaScript functions</li>
     *  <li>styleSheet - name of the CSS file</li>
     *  <li>encoding - encoding to be used for the output stream</li>
     *  <li>target - target of HTML base element, for example "_blank"</li>
     *  <li>title - title for the HTML head element, and the browser window</li>
     *  </ul>
     */
    public void writeStart(String[] params,  HashMap<String, String[]> parameterMap) {
        String encoding     = getTargetEncoding();
        String title        = "dbat";
        try {
            int iparam = params.length;
            while (iparam > 0) {
                iparam -= 2;
                if (false) {
                } else if (params[iparam].equals("encoding")) {
                    encoding    = params[iparam + 1];
                } else if (params[iparam].equals("title")) {
                    title       = params[iparam + 1];
                } else { // "dummy" - skip pair
                }
            } // while iparam

            writeComment("SQL generated by Dbat on " + BaseTable.getISOTimestamp());
            // writeComment("encoding: " + encoding + ", title: " + title);
       } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            System.err.println("encoding="  + encoding
                    + ", title="            + title
                    );
        }
    } // writeStart

    /** Ends   a file that may contain several table descriptions and/or a SELECT result sets
     */
    public void writeEnd() {
    /*
        try {
            writeComment("finished on " + BaseTable.getISOTimestamp());
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    */
    } // writeEnd

    /** Writes a comment line.
     *  @param line string to be output as a comment line
     */
    public void writeComment(String line) {
        try {
            charWriter.println("-- " + line.replaceAll("\n", "\n--")); //  + " ;");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeComment

    /** Writes a comment, but only if the "verbose" level is &gt; 0.
     *  @param line string to be output as a comment
     *  @param verbose level of output detail
     */
    public void writeComment(String line, int verbose) {
        if (verbose > 0) {
            writeComment(line);
        }
    } // writeComment(2)

    /** Writes a COMMIT statement.
     *  @param rowCount number of INSERT/UPDATE statements already generated
     */
    public void writeCommit(int rowCount) {
        charWriter.println("COMMIT;");
        charWriter.println("-- " + rowCount + " ;");
    } // writeCommit

    //======================================
    // Table description (DDL)
    //======================================

    /** Starts the description of a TABLE (or VIEW) with DROP TABLE and CREATE TABLE
     *  @param dbMetaData database metadata
     *  as obtained with <code>con.getMetaData()</code>
     *  @param schema    name of the table's schema, or null if none
     *  @param tableName name of the table
     *  @param tableType type of the table: "TABLE", "VIEW", etc.
     */
    public void startDescription(DatabaseMetaData dbMetaData, String schema, String tableName, String tableType) {
        try {
            writeComment(dbMetaData.getDatabaseProductName() // + " " + dbMetaData.getDatabaseProductVersion()
                    + " with " +  dbMetaData.getDriverName() // + " " + dbMetaData.getDriverVersion()
                    );
            charWriter.print("DROP   " + tableType + " ");
            if (schema != null && ! schema.equals("")) {
                charWriter.print(schema + ".");
            }
            charWriter.println(tableName + ";");
            charWriter.print("CREATE " + tableType + " ");
            if (schema != null && ! schema.equals("")) {
                charWriter.print(schema + ".");
            }
            charWriter.println(tableName      );
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startDescription

    /** Ends the description of a table.
     */
    public void endDescription() {
        try {
            charWriter.println("COMMIT;");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endDescription

    /** Returns the definition string for a table column in HTML format.
     *  @param colNo number of the column: 0, 1, 2
     *  @param column bean with the properties of the column
     */
    public void describeColumn(int colNo, TableColumn column) {
        cellBuffer.setLength(0); // currently no restrictions on line length implemented
        try {
            cellBuffer.append(colNo == 0 ? "\t( " : "\t, ");
            cellBuffer.append(column.getName());
            cellBuffer.append("\t");
            String typeName = column.getTypeName();
            int    dataType = column.getDataType();
            cellBuffer.append(typeName);
            int width = column.getWidth();
            if (false) {
            } else if (dataType == Types.CHAR   ) {
                cellBuffer.append("(" + width + ")");
            } else if (dataType == Types.VARCHAR) {
                cellBuffer.append("(" + width + ")");
            } else if (dataType == Types.DECIMAL) {
                cellBuffer.append("(" + width);
                int decimalDigits = column.getDecimal();
                if (decimalDigits != 0) {
                    cellBuffer.append("," + String.valueOf(decimalDigits));
                }
                cellBuffer.append(")");
            } else if (dataType == Types.DATE
                    || dataType == Types.TIME
                    || dataType == Types.TIMESTAMP
                    ) {
                cellBuffer.append(" -- (" + width + ")");
            }
            if (column.isNullable()) {
                cellBuffer.append(" NOT NULL");
            }
            String remark = column.getRemark();
            String label  = column.getLabel();
            if (label != null && label.length() > 0) {
                cellBuffer.append(" -- ");
                cellBuffer.append(label);
            }
            else
            if (remark != null && remark.length() > 0) {
                cellBuffer.append(" -- ");
                cellBuffer.append(remark);
            }
            charWriter.println(cellBuffer.toString());
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // describeColumn

    /** Writes the closing bracket (if any) behind the column descriptions, for example (for SQL):
     *  <pre>
     *  );
     *  </pre>
     *  The individual output format may skip this method, and may output the closing bracket
     *  behind the primary key, or even behind all constraints.
     */
    public void describeColumnsEnd() {
        try {
            if (false) { // is done below in describePrimaryKey
                charWriter.println("\t);"); // closes CREATE TABLE
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // describeColumnsEnd

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
        String pkColumnNames = null;
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
                    charWriter.println("\t, CONSTRAINT " + pkName      + " PRIMARY KEY (" + pkColumnNames + ")");
                } // not first
                pkName = newName;
            } // controlChange
            oldRow = cstRows.get(cstKey);
            if (keySeq.equals("001")) {
                pkColumnNames  =        oldRow.get("COLUMN_NAME");
            } else {
                pkColumnNames += ", " + oldRow.get("COLUMN_NAME");
            }
        } // while all rows
        charWriter.println("\t);"); // closes CREATE TABLE
    } // describePrimaryKey

    /** Writes the description of all indexes
     *  @param tableName fully qualified name of the table
     *  @param cstRows array for the result set of <em>DatabaseMetaData.getIndexInfo</em>, properly sorted
     *  by INDEX_NAME and ORDINAL_POSITION,
     *  with an additional fictitious row for the last group change
     */
    public void describeIndexes(String tableName, TreeMap<String, HashMap<String, String>> cstRows) {
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
            if (keySeq.equals("001")) {
                ixColumnNames   = "\t( " + oldRow.get("COLUMN_NAME");
            } else {
                ixColumnNames  += "\t, " + oldRow.get("COLUMN_NAME");
            }
            ixColumnNames += "\t" + oldRow.get("ASC_OR_DESC") + "\n"; // dir may be ""
        } // while all rows
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
        charWriter.println("DROP   PROCEDURE " + procedureName + " " + procSeparator);
        charWriter.println("CREATE PROCEDURE " + procedureName                           );

        Iterator<String> citer = cstRows.keySet().iterator();
        int icol = 0;
        String keySeq = "000"; // ORDINAL_POSITION, normalized to 3 digits
        String value  = null;
        String field  = null;
        HashMap<String, String> oldRow = new HashMap<String, String>();
        while (citer.hasNext()) { // process all cstRows
            String cstKey = citer.next();
            oldRow = cstRows.get(cstKey);
            int tabPos = cstKey.indexOf("\t");
            String newName = cstKey.substring(0, tabPos); // ignore keySeq behind
            if (newName.equals(procedureName)) { // in first group
                StringBuffer colBuffer = new StringBuffer(64);
                if (icol <= 0) {
                    colBuffer.append("\t( ");
                } else {
                    colBuffer.append("\t, ");
                };
                value = oldRow.get("COLUMN_TYPE");
                if (value != null) {
                    colBuffer.append(value);
                    colBuffer.append("\t");
                }
                colBuffer.append(oldRow.get("COLUMN_NAME"));
                colBuffer.append("\t");

                value = oldRow.get("TYPE_NAME");
                if (value != null) {
                    colBuffer.append(value);
                }

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
                        colBuffer.append("(");
                        colBuffer.append(String.valueOf(precision));
                        colBuffer.append(")");
                        break;
                    case Types.DECIMAL:
                        colBuffer.append("(");
                        colBuffer.append(String.valueOf(precision));
                        if (scale != 0) {
                            colBuffer.append(",");
                            colBuffer.append(String.valueOf(scale));
                        }
                        colBuffer.append(")");
                        break;
                } // dataType
            /*  will not be recognized by MySQL
                colBuffer.append("\t");
                colBuffer.append(oldRow.get("NULLABLE"));
            */
                String remark = oldRow.get("REMARK");
                if (remark != null && remark.length() > 0) {
                    colBuffer.append("\t-- ");
                    colBuffer.append(remark);
                }

                charWriter.println(colBuffer.toString());
                icol ++;
            } // in first group
        } // while all column descriptions
        charWriter.println("\t)");
        charWriter.println("\tBEGIN");
        charWriter.println("\tEND;");
        charWriter.println("\t" + procSeparator);
    } // describeProcedureColumns

    //==========================================
    // Table elements generated for a SELECT
    //==========================================

    /** Appends a string to {@link #cellBuffer}, but inserts a newline before
     *  if the maximum line length would be exceeded
     *  @param value string to be appended
     */
    protected void appendCell(String value) {
        try {
            int len = value.length();
            if (lenCell + len >= maxLen) {
                charWriter.println(cellBuffer.toString());
                cellBuffer.setLength(0);
                lenCell = 0;
            }
            cellBuffer.append(value);
            lenCell += len;
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // appendCell

    /** Appends a column's value to {@link #cellBuffer}, but inserts a newline before
     *  if the maximum line length would be exceeded
     *  @param column the value of which is to be appended
     */
    protected void appendValue(TableColumn column) {
        StringBuffer result = new StringBuffer(256);
        String value = column.getValue();
        if (value == null) {
            result.append("NULL");
        } else {
            switch (column.getDataType()) {
                case Types.CHAR:
                    // fall thru
                case Types.VARCHAR:
                    result.append("\'");
                    result.append(value.replaceAll("\'", "\'\'")); // duplicate inner apostrophes
                    result.append("\'");
                    break;
                case Types.DATE:
                    result.append(getJDBCescape("{d", value));
                    break;
                case Types.TIME:
                    result.append(getJDBCescape("{t", value));
                    break;
                case Types.TIMESTAMP:
                    result.append(getJDBCescape("{ts", value));
                    break;
                default: // all numeric types: INT, DECIMAL
                    result.append(value);
                    break;
            } // switch type
        } // not NULL
        appendCell(result.toString());
    } // appendValue

    /** Initializes a table - with meta data, currently only implemented in SQLTable and its subclasses.
     *  For subclasses which do not override this method, the meta data are ignored.
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

    /** Gets an JDBC escaped time value
     *  @param escapeTag string to be used to construct the escape
     *  @param value time value
     *  @return escaped string value, for example "{dt '2017-05-27'}"
     */
    protected String getJDBCescape(String escapeTag, String value) {
        return ((isJDBC ? escapeTag : "") + "\'" + value + "\'"+ (isJDBC ? "}" : ""));
    } // getJDBCescape

    /** data type of current column */
    protected int currentDataType;

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
        boolean first = true;
        switch (rowType) {
            case DATA:
                cellBuffer.setLength(0);
                lenCell = 0;
                appendCell("INSERT INTO ");
                appendCell(tableName);
                appendCell(" ");
                first = true;
                while (icol < ncol) {
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (pseudo == null) {
                        appendCell(first ? "(" : ",");
                        first = false;
                        appendCell(column.getName());
                    } // ! pseudo
                    icol ++;
                } // while icol
                appendCell(")");
                charWriter.println(cellBuffer.toString());
                break;
            case DATA2:
                cellBuffer.setLength(0);
                lenCell = 0;
                appendCell("VALUES ");
                first = true;
                while (icol < ncol) {
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (pseudo == null) {
                        appendCell(first ? "(" : ",");
                        first = false;
                        appendValue(column);
                    } // ! pseudo
                    icol ++;
                } // while icol
                appendCell(");");
                charWriter.println(cellBuffer.toString());
                break;
        } // switch rowType
    } // writeGenericRow

} // SQLTable
