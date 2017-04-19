/*  Generator for an XML table which is transformed by an XSLT stylesheet äöüÄÖÜß
    @(#) $Id$
    2014-11-10: s|getHrefValue -> s|getWrappedValue
    2011-09-10: copied from TransformedTable
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
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  java.sql.DatabaseMetaData;
import  java.sql.Types;
import  java.util.ArrayList;
import  java.util.HashMap;

/** Generator for an XML table which is additionally transformed by an XSLT stylesheet.
 *  The syntax is the same for an ordinary HTML table,
 *  only prefixed by the XML processing instruction.
 *  @author Dr. Georg Fischer
 */
public class TableGenerator extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** debugging switch */
    private int debug = 1;
    /** whether an XML declaration was already output */
    protected boolean xmlDeclared;
    /** encoding for output */
    private String encoding;

    /** No-args Constructor
     */
    public TableGenerator() {
        super();
        setFormatCodes("gen");
        setDescription("en", "generate SAX events");
        setDescription("de", "SAX-Event-Generator");
        setDescription("fr", "Générateur d'événements SAX");
        xmlDeclared = false;
        encoding = "UTF-8";
        setOutputFormat("gen");
    } // Constructor

    /** Constructor with format
     *  @param format = "gen"
     */
    public TableGenerator(String format) {
        super(format);
        xmlDeclared = false;
        encoding = "UTF-8";
        setOutputFormat("gen");
    } // Constructor

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *  @param params array of 0 or more (name, value) strings which specify features in the file header.
     *  @param parameterMap map of request parameters to values
     *  The following names are interpreted:
     *  <ul>
     *  <li>encoding - encoding to be used for the output stream</li>
     *  <li>namespace="no" - whether not to output an xmlns attribute with the root element</li>
     *  <li>xslt="filename.xsl" - whether to perform XSLT</li>
     *  </ul>
     */
    public void writeStart(String[] params,  HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        String encoding     = getTargetEncoding();
        String namespace    = null;
        try {
            int iparam = params.length;
            while (iparam > 0) {
                iparam -= 2;
                if (false) {
                } else if (params[iparam].equals("encoding")) {
                    encoding    = params[iparam + 1];
                } else if (params[iparam].equals("namespace")) {
                    namespace   = params[iparam + 1].toLowerCase();
                } else { // "dummy" - skip pair
                }
            } // while iparam
            generator.fireStartDocument();
            xmlDeclared = true;
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeStart

    /** Ends a file that may contain several table descriptions and/or a SELECT result sets
     */
    public void writeEnd() {
        try {
            generator.fireLineBreak();
            generator.fireEndDocument();
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
                generator.fireProcessingInstruction("xml", " version=\"1.0\" encoding=\"" + encoding + "\" ?>");
            }
            generator.fireStartElement("table", generator.toAttribute("name", tableName));
            generator.fireLineBreak();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startTable

    /** Terminates  a table
     */
    public void endTable() {
         try {
            generator.fireEndElement("table");
            generator.fireLineBreak();
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
            generator.fireStartElement("create", generator.toAttributes(new String[]
                    { (schema != null ? "schema" : ""), (schema != null ? schema : "")
                    , "type", tableType
                    , "name", tableName
                    } ));
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startDescription

    /** Ends the description of a table.
     */
    public void endDescription() {
        try {
            generator.fireEndElement("create");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endDescription

    /** Returns the definition string for a table column in HTML format.
     *  @param colNo number of the column: 0, 1, 2
     *  @param column bean with the properties of the column
     */
    public void describeColumn(int colNo, TableColumn column) {
        try {
            String typeName     = column.getTypeName();
            int    dataType     = column.getDataType();
            int    width        = column.getWidth();
            int decimalDigits   = column.getDecimal();
            String remark       = column.getRemark();
            String widthAttr    = (dataType == Types.CHAR || dataType == Types.VARCHAR || dataType == Types.DECIMAL) ? "width" : "";
            String decimalAttr  = (widthAttr.length() > 0 && decimalDigits != 0) ? "decimal" : "";
            generator.fireStartElement("column", generator.toAttributes(new String[]
                    { "name"        , column.getName()
                    , "type"        , typeName
                    , widthAttr     , String.valueOf(width)
                    , decimalAttr   , String.valueOf(decimalDigits)
                    , (! column.isNullable()                 ? "nullable" : ""), "false"
                    , (remark != null && remark.length() > 0 ? "remark"   : ""), remark
                    } ));
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // describeColumn

    /** Writes the description of a (primary) key of the table, for example (for SQL):
     *  <pre>
     *  , PRIMARY KEY (COL1, COL2) -- or
     *  , KEY KEY_NAME(COL1, COL2)
     *  </pre>
     *  @param tableName fully qualified name of the table
     *  @param primary whether to describe the primary key
     *  @param keyName name of the key ("" for primary)
     *  @param columnNames array of key column names, ordered by key sequence
     */
    public void describeKey(String tableName, boolean primary, String keyName, ArrayList/*<1.5*/<String>/*1.5>*/ columnNames) {
        try {
            if (columnNames.size() > 0) {
                if (primary) {
                    generator.fireStartElement("key", generator.toAttribute("type", "primary"));
                } else {
                    generator.fireStartElement("key", generator.toAttribute("name", keyName));
                }
                for (int ikey = 0; ikey < columnNames.size(); ikey ++) {
                    generator.fireEmptyElement("col", generator.toAttribute("name", (String) columnNames.get(ikey)));
                } // for ikey
                generator.fireEndElement("key");
            } // with column names
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // describeKey

    /** Writes the description of the table's primary key, for example:
     *  <pre>
     *  , PRIMARY KEY (FIRST_NAME, LAST_NAME)
     *  </pre>
     *  @param tableName fully qualified name of the table
     *  @param pkCount number of primary key columns
     *  @param primaryKeyFields array of primary key column names, ordered by key sequence
     */
    public void describePrimaryKey(String tableName, int pkCount, String[] primaryKeyFields) {
        try {
            if (pkCount > 0) {
                generator.fireStartElement("primary");
                for (int ipk = 1; ipk <= pkCount; ipk ++) {
                    generator.fireEmptyElement("key", generator.toAttribute("name", primaryKeyFields[ipk]));
                } // for ipk
                generator.fireEndElement("primary");
            } // with primay key
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // describePrimaryKey

    /** Writes a comment line.
     *  @param line string to be output as a comment line
     */
/*
    especially suppress the initial "SQL: ... " comment
    public void writeComment(String line) {
        try {
            generator.fireComment(line);
            generator.fireLineBreak();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeComment
*/
    /** Writes an XML processing instruction, or nothing for other formats.
     *  @param target the processing instruction target
     *  @param data the processing instruction data, or null if none was supplied.
     *  The data does not include any whitespace separating it from the target
     */
    public void writeProcessingInstruction(String target, String data) {
        try {
            generator.fireProcessingInstruction(target, data);
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
        nextStyle  = null;
        nextLobURL = null;
        TableColumn column = null;
        String pseudo = null;
        int ncol = columnList.size();
        int icol = 0;
        switch (rowType) {
            case HEADER:
                generator.fireStartElement("tr", generator.toAttribute("trans", "formed"));
                while (icol < ncol) {
                    column = columnList.get(icol);
                    String header = column.getLabel();
                    pseudo = column.getPseudo();
                    if (pseudo != null && pseudo.equals("style")) {
                        nextStyle = null;
                    } else {
                        if (header == null) {
                            header = "&nbsp;";
                        }
                        generator.fireSimpleElement("th", header);
                    }
                    icol ++;
                } // while icol
                generator.fireEndElement("tr");
                generator.fireLineBreak();
                break;
            case DATA:
                generator.fireStartElement("tr", generator.toAttribute("trans", "formed"));
                while (icol < ncol) {
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (pseudo != null) {
                        if (false) {
                        } else if (pseudo.equals("style")) {
                            nextStyle = column.getValue();
                        } else if (pseudo.equals("url")) {
                            nextLobURL = column.getValue();
                        } else {
                            // ignore if (pseudo.equals("delete") || pseudo.equals("update")) {
                        }
                    } else { // pseudo == null
                        generator.fireStartElement("td");
                /*
                        String align = column.getAlign();
                        if (align != null && ! align.equals("left")) {
                            result.append(" align=\"" + align + "\"");
                        }
                        if (false) {
                        } else if (nextStyle != null && nextStyle.length() > 0) {
                            result.append(" class=\"" + nextStyle + "\"");
                        } else if (style     != null && style    .length() > 0) {
                            result.append(" class=\"" + style + "\"");
                        }
                */
                        nextStyle = null;
                        String value     = column.getValue();
                        if (value == null) {
                            value = "null";
                        }
                        String wrappedValue = column.getWrappedValue();
                        if (wrappedValue != null) {
                            generator.fireStartElement("a", generator.toAttribute("href", wrappedValue
                                    // .replaceAll("&", "&amp;")
                                    ));
                        }
                        generator.fireCharacters(value);
                        if (wrappedValue != null) {
                            generator.fireEndElement("a");
                        }
                        generator.fireEndElement("td");
                } // pseudo == null
                    icol ++;
                } // while icol
                generator.fireEndElement("tr");
                generator.fireLineBreak();
                break;
        } // switch rowType
    } // writeGenericRow

} // TableGenerator
