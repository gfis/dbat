/*  Generator for a default Dbat XML specification file
    @(#) $Id$
    2016-08-09: repaired with FROM, no constraint; Vroni = 35
    2010-05-21: with HTML namespace prefix
    2010-02-25: charWriter.write -> .print
    2010-02-23: copied from SQLTable
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
import  org.teherba.dbat.Configuration;         // for DBAT_URI, HTML_URI
import  org.teherba.dbat.Messages;              // for TIMESTAMP_FORMAT
import  org.teherba.dbat.SpecificationHandler;  // for ROOT_TAG
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.format.XMLTable;
import  java.sql.DatabaseMetaData;
import  java.sql.Types;
import  java.util.Date;
import  java.util.HashMap;
import  java.util.Iterator;
import  java.util.TreeMap;

/** Generates a default Dbat XML specification file
 *  which describes a hypothetic "SELECT *" for the table.
 *  This specification can be fed into {@link SpecificationHandler}.
 *  This class can only be used for the description of a table, not for the
 *  representation of a result set. The output is a starting point for
 *  an application specific modification of a query.
 *  <p>
 *  Typical activation:
 *  <pre>
 *  java -jar dbat.jar -c conn -m spec -d tablename
 *  </pre>
 *  @author Dr. Georg Fischer
 */
public class SpecDescription extends XMLTable {
    public final static String CVSID = "@(#) $Id$";

    /** local copy of the table's schema */
    private String schema;
    /** local copy of the table's name */
    private String tableName;
    /** id for table */
    private String tableId = "a";

    /** No-args Constructor
     */
    public SpecDescription() {
        super();
        setFormatCodes("spec");
        setDescription("en", "Dbat Spec. File");
        setDescription("de", "Dbat-Spezifikation");
    } // Constructor

    /** XML Namespace prefix for HTML tags */
    private static final String HTML_PREFIX = "ht";

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *  @param params array of 0 or more (name, value) strings which specify features in the file header.
     *  @param parameterMap map of request parameters to values
     *  The following names are interpreted:
     *  <ul>
     *  <li>encoding - encoding to be used for the output stream</li>
     *  </ul>
     */
    public void writeStart(String[] params,  HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        try {
            String encoding = getTargetEncoding();
            String conn     = "worddb";
            int iparam = params.length;
            while (iparam > 0) {
                iparam -= 2;
                if (false) {
                } else if (params[iparam].equals("conn"    )) {
                    conn     = params[iparam + 1];
                } else if (params[iparam].equals("encoding")) {
                    encoding = params[iparam + 1];
                }
            } // while iparam
            charWriter.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
            charWriter.println("<!--");
            charWriter.println("    Dbat Specification generated by SpecDescription"
                    + " at " + Messages.TIMESTAMP_FORMAT.format(new java.util.Date()));
            charWriter.println("    @(#) $" + "Id" + "$"); // git should never expand here
            charWriter.println("-->");
            charWriter.println("<" + SpecificationHandler.ROOT_TAG                  + "\n"
                    + "    xmlns   "             + "=\"" + Configuration.DBAT_URI   + "\"\n"
                    + "    xmlns:" + HTML_PREFIX + "=\"" + Configuration.HTML_URI   + "\"\n"
                    + "    encoding=\""     + encoding                              + "\"\n"
                    + "    conn=\""         + conn                                  + "\"\n"
                    + "    lang=\""         + "en"                                  + "\"\n"
                    + "    title=\""        + getSchemaTable()                      + "\"\n"
                    + "    >");
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


    /** Gets the table's name with an optional schema prefix
     *  @return "[schema.]tablename"
     */
    protected String getSchemaTable() {
        StringBuffer buffer = new StringBuffer(32);
        if (schema != null && ! schema.equals("")) {
            buffer.append(schema);
            buffer.append(".");
        }
        buffer.append(tableName);
        return buffer.toString();
    } // getSchemaTable
    
    /** Whether a <em>from</em> element was already output */
    private boolean wroteFrom;
    
    /** writes the <em>from</em> element */
    private void writeFrom() {
        if (! wroteFrom) {
            charWriter.println("        <from> " + getSchemaTable() + " " + tableId);
            charWriter.println("        </from>");
            wroteFrom = true;
        } 
    } // writeFrom
        
    /** Remembers the table (and its schema).
     *  @param dbMetaData database metadata
     *  as obtained with <code>con.getMetaData()</code>
     *  @param schema    name of the table's schema, or null if none
     *  @param tableName name of the table
     *  @param tableType type of the table: "TABLE", "VIEW", etc.
     */
    public void startDescription(DatabaseMetaData dbMetaData, String schema, String tableName, String tableType) {
        try {
            this.schema     = schema;
            this.tableName  = tableName;
            wroteFrom = false; // <from> not yet written
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startDescription

    /** Ends the description of a SELECT statement.
     */
    public void endDescription() {
        try {
            writeFrom();
            charWriter.println("        <counter desc=\"Row,s\" />");
            charWriter.println("    </select>");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endDescription

    /** Returns the definition string for a table column in XML format.
     *  @param colNo number of the column: 0, 1, 2
     *  @param column bean with the properties of the column
     */
    public void describeColumn(int colNo, TableColumn column) {
        StringBuffer buffer = new StringBuffer(128);
        try {
            if (colNo == 0) {
                charWriter.println("    <" + HTML_PREFIX + ":h2>Table " + getSchemaTable() + "</" + HTML_PREFIX + ":h2>");
                charWriter.println("    <" + HTML_PREFIX + ":form method=\"get\">");
                charWriter.println("        <" + HTML_PREFIX + ":input label=\"Prefix\" name=\"prefix\" maxsize=\"20\" size=\"10\" />");
                charWriter.println("        <" + HTML_PREFIX + ":input type=\"submit\" value=\"Submit\" />");
                charWriter.println("    </" + HTML_PREFIX + ":form>");
                charWriter.println();
                charWriter.println("    <select>");
            }
            buffer.append("        <col label=\"");
            buffer.append(column.getName());
            int    dataType = column.getDataType();
            if (false) {
            } else if (dataType == Types.CHAR   ) {
            } else if (dataType == Types.VARCHAR) {
            } else if (dataType == Types.DECIMAL || dataType == Types.INTEGER) {
                buffer.append("\" align=\"right");
            }
            buffer.append("\">" + tableId + ".");
            buffer.append(column.getName());
            buffer.append("</col>");
            String label = column.getLabel();
            if (label != null && label.length() > 0) {
                buffer.append(" <!-- ");
                buffer.append(label);
                buffer.append(" -->");
            }
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
                    writeFrom();
                    charWriter.println("        <where> " + pkColumnNames + " like \'<parm name=\"prefix\" />%\'");
                    charWriter.println("        </where>");
                } // not first
                pkName = newName;
            } // controlChange
            oldRow = cstRows.get(cstKey);
            if (pkColumnNames.length() > 0) {
                pkColumnNames += " || ";
            }
            pkColumnNames += oldRow.get("COLUMN_NAME");
        } // while all rows
    } // describePrimaryKey

} // SpecDescription
