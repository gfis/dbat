/*  Generator for a default Dbiv XML specification file
    @(#) $Id$
    2016-08-09, Georg Fischer: copied from SpecDescription.java
*/
/*
 * Copyright 2016 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.dbat.SpecificationHandler;  // for DBIV_TAG
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.format.SpecDescription;
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
public class ViewDescription extends SpecDescription {
    public final static String CVSID = "@(#) $Id$";

    /** local copy of the table's schema */
    private String schema;
    /** local copy of the table's name */
    private String tableName;
    /** id for table */
    private String tableId = "a";

    /** No-args Constructor
     */
    public ViewDescription() {
        super();
        setFormatCodes("dbiv,view");
        setDescription("en", "Dbiv Spec. File");
        setDescription("de", "Dbiv-Spezifikation");
    } // Constructor

    /** XML Namespace prefix for HTML tags */
    private static final String HTML_PREFIX = "ht";
    /** XML Namespace prefix for DBAT tags */
    private static final String DBAT_PREFIX = "db";
    /** XML Namespace prefix for DBIV tags */
    private static final String DBIV_PREFIX = "iv";

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
            charWriter.println("    Dbiv Interactive View generated by ViewDescription"
                    + " at " + Messages.TIMESTAMP_FORMAT.format(new java.util.Date()));
            charWriter.println("    @(#) $" + "Id" + "$"); // git should never expand here
            charWriter.println("-->");
            charWriter.println("<" + SpecificationHandler.DBIV_TAG                  + "\n"
                    + "    xmlns   "             + "=\"" + Configuration.DBIV_URI   + "\"\n"
                    + "    xmlns:" + DBIV_PREFIX + "=\"" + Configuration.DBIV_URI   + "\"\n"
                    + "    xmlns:" + DBAT_PREFIX + "=\"" + Configuration.DBAT_URI   + "\"\n"
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
            charWriter.println("</" + SpecificationHandler.DBIV_TAG + ">");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeEnd

    // startDescription is inherited

    /** Ends the description of a SELECT statement.
     */
    public void endDescription() {
        try {
            charWriter.println("        <where></where>");
            charWriter.println("        <order by=\"\" />");
            charWriter.println("    </view>");
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
            if (colNo == 0) { // the tableName is known only now, not before startDescription
                charWriter.println("    <view name=\"" + tableName + "\">");
                charWriter.println("        <title>Interactive View " + getSchemaTable() + "</title>");
                charWriter.println("        <counter desc=\"Row,s\" />");
                charWriter.println("        <action name=\"ins\"        label=\"New Row\"  remark=\"insert\" position=\"middle\" />");
                charWriter.println("        <action name=\"upd\"        label=\"Mod.\"     remark=\"update\" position=\"result\" />");
                charWriter.println("        <action name=\"del\"        label=\"Del.\"     remark=\"delete\" position=\"result\" />");
                charWriter.println();
            }
            buffer.append("        <field");
            buffer.append(" name=\"");      buffer.append(column.getName());        buffer.append('"');
            buffer.append(" key=\"");                                               buffer.append('"');
            buffer.append(" label=\"");     buffer.append(column.getName());        buffer.append('"');
            String initValue = column.getDefault();
            if (initValue == null) {
            	initValue = "";
            }
            buffer.append(" init=\"");      buffer.append(initValue);               buffer.append('"');
            String typeName = column.getTypeName();
            if (false) {
            } else if (typeName.equals("VARCHAR")) { typeName = "CHAR";
            } 
            buffer.append(" type=\"");      buffer.append(typeName.toLowerCase());  buffer.append('"');
            buffer.append(" size=\"");      buffer.append(column.getWidth());       buffer.append('"');
            buffer.append(" valid=\"");                                             buffer.append('"');
            buffer.append(" remark=\"");    buffer.append(column.getRemark());      buffer.append('"');
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
                /*
                    charWriter.println("        <where> " + pkColumnNames + " like \'<parm name=\"prefix\" />%\'");
                    charWriter.println("        </where>");
                */
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

} // ViewDescription
