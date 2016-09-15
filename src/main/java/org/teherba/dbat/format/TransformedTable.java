/*  Generator for an XML table which is transformed by an XSLT stylesheet
 *  @(#) $Id$
 *  2016-09-15: BasicFactory replaced by XtransFactory again
 *  2014-11-10: s|getHrefValue -> s|getWrappedValue
 *  2014-03-04: ignore pseudo columns
 *  2011-09-11: working with generator.fireEndDocument() after long trials
 *  2011-08-24: writeGenericRow
 *  2011-04-06: copied from XMLTable
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
import  org.teherba.dbat.SpecificationHandler; // for ROOT_TAG
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  org.teherba.xtrans.BaseTransformer;
import  org.teherba.xtrans.XtransFactory;
import  java.io.IOException;
import  java.sql.DatabaseMetaData;
import  java.sql.Types;
import  java.util.ArrayList;
import  java.util.HashMap;

/** Generator for an XML table which is additionally transformed by an XSLT stylesheet.
 *  The syntax is the same for an ordinary HTML table,
 *  only prefixed by the XML processing instruction.
 *  @author Dr. Georg Fischer
 */
public class TransformedTable extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** debugging switch */
    private int debug = 0;
    /** whether an XML declaration was already output */
    protected boolean xmlDeclared;
    /** encoding for output */
    private String encoding;
    /** XML  transformer factory */
    private XtransFactory xtransFactory;
    /** Input reader, generates SAX events */
    private BaseTransformer generator;
    /** Output writer, consumes SAX events */
    private BaseTransformer serializer;

    /** No-args Constructor
     */
    public TransformedTable() {
        super();
        setFormatCodes("trans");
        setDescription("en", "XML+XSLT");
        xmlDeclared = false;
        encoding = "UTF-8";
        xtransFactory = null;
        setOutputFormat("trans");
    } // Constructor

    /** Constructor with format
     *  @param format = "trans"
     */
    public TransformedTable(String format) {
        super(format);
        setDescription("en", "XSLT+XML");
        xmlDeclared = false;
        encoding = "UTF-8";
        xtransFactory = null;
        setOutputFormat("trans");
    } // Constructor

    /*-------------------------------------------------------
     *  Specific methods for SAX-like table generation events
     *-------------------------------------------------------*/

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *  @param params array of 0 or more (name, value) string which specify features in the file header.
     *  @param parameterMap map of request parameters to values
     *  The following names are interpreted:
     *  <ul>
     *  <li>encoding - encoding to be used for the output stream</li>
     *  <li>target - target of HTML base element, for example "_blank"</li>
     *  <li>title - title for the HTML head element, and the browser window</li>
     *  <li>xslt="subdir.filename" - whether to perform XSLT</li>
     *  </ul>
     */
    public void writeStart(String[] params,  HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        boolean success     = true; // assume successful creation of the pipeline
        String encoding     = getTargetEncoding();
        String namespace    = null;
        String xslt         = null; // this gets the complete real path + basic filename + ".xsl", or it remains null
        try {
            int iparam = params.length;
            while (iparam > 0) {
                iparam -= 2;
                if (false) {
                } else if (params[iparam].equals("encoding")) {
                    encoding    = params[iparam + 1];
                } else if (params[iparam].equals("namespace")) {
                    namespace   = params[iparam + 1].toLowerCase();
                } else if (params[iparam].equals("xslt") && xslt == null) { // request has precedence
                    xslt        = params[iparam + 1];
                    Object obj  = parameterMap.get("xslt");
                    if (obj != null) {
                        xslt = ((String[]) obj)[0]; // from the request with priority
                    }
                } else { // "dummy" - skip pair
                }
            } // while iparam

            xtransFactory = new XtransFactory(); // knows XML only
            // xtransFactory.setRealPath(params[iparm ++]);
            String[] pipeLine = new String[]
                    { "-xml", "-"       // STDIN (not used)
                    , (xslt != null ? "-xsl" : ""), (xslt != null ? xslt : "") // real path + filename.xsl
                //  , "-filter", "level"
                    , "-xml", "-"       // STDOUT (not used from here)
                    };
            log.info("xslt=" + xslt);
            xtransFactory.createPipeLine(pipeLine);
            generator  = xtransFactory.getGenerator();
            serializer = xtransFactory.getSerializer();
            serializer.setCharWriter(getCharWriter());
            generator.fireStartDocument();
            generator.fireStartRoot(SpecificationHandler.ROOT_TAG);
            if (debug >= 2) {
                // serializer.startElement("", SpecificationHandler.ROOT_TAG, SpecificationHandler.ROOT_TAG, null);
                StringBuffer buffer = new StringBuffer(128);
                int ipipe = 0;
                while (ipipe < pipeLine.length) {
                    buffer.append(ipipe == 0 ? '[' : ',');
                    buffer.append(pipeLine[ipipe]);
                    ipipe ++;
                } // while ipipe
                buffer.append(']');
                generator.fireComment("Test: TransformedTable.writeStart, xslt=" + xslt + ", pipeLine=" + buffer.toString()
                        + "\ngenerator="                + generator
                        + "\nserializer="               + serializer
                        + "\ngenerator.contentHandler=" + generator.getContentHandler()
                        + "\ngenerator.result="         + generator.getResult()
                        + "\nserializer.contentHandler=" + serializer.getContentHandler()
                        + "\nserializer.result="        + serializer.getResult()
                        + "\nwriter="                   + getCharWriter()
                        );
                generator.fireLineBreak();
            }
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
            if (debug >= 2) {
                generator.fireComment("Test: TransformedTable.writeEnd"
                        + "\ngenerator="                + generator
                        + "\nserializer="               + serializer
                        + "\nwriter="                   + getCharWriter()
                        );
                generator.fireLineBreak();
                // serializer.endElement("", SpecificationHandler.ROOT_TAG, SpecificationHandler.ROOT_TAG);
            }
            generator.fireEndElement(SpecificationHandler.ROOT_TAG);
            generator.fireLineBreak();
            generator.fireEndDocument();
            xtransFactory.closeFiles();
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
            // serializer.startElement("", "table", "table", serializer.toAttribute("name", tableName));
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
            // serializer.endElement("", "table", "table");
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
        StringBuffer buffer = new StringBuffer(128);
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
                generator.fireStartElement("tr"); // , generator.toAttribute("trans", "formed"));
                while (icol < ncol) {
                    column = columnList.get(icol);
                    String header = column.getLabel();
                    pseudo = column.getPseudo();
                    if (pseudo != null) {
                        if (false) {
                        } else if (pseudo.equals("style")) {
                            nextStyle = null;
                        }
                    } else { // pseudo == null
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
                generator.fireStartElement("tr"); // , generator.toAttribute("trans", "formed"));
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

} // TransformedTable
