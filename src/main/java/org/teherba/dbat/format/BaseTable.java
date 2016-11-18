/*  Base class for file format representing table descriptions or results sets
 *  @(#) $Id$
 *  2016-10-13: less imports
 *  2016-08-26: getISOTimestamp()
 *  2016-05-08: close(); end of WW2 + 71 years
 *  2014-03-04: ignore pseudo columns
 *  2012-11-27: writeCommit
 *  2012-05-14: appendToParameters should not apply links
 *  2012-02-17: exception handling if stylesheet could not be compiled
 *  2011-12-13: ROW1
 *  2011-11-11: writeComment(line, verbose)
 *  2011-08-24: 3rd generation - writeGenericRow
 *  2011-08-12: remove old methods write*
 *  2011-06-10: isLetter -> isLetterOrDigit
 *  2011-03-29: writeProcessingInstruction
 *  2011-01-21: private targetEncoding, separator
 *  2010-11-26: writeTrailer (previously in endDocument)
 *  2010-02-25: charWriter.write -> .print
 *  2008-08-02: without result in decribeColumn; M+K->Berlin
 *  2008-02-08: completeColumn and describeColumn separated
 *  2006-11-17: adapted from xtrans.BaseTransformer
 *  2006-09-19: copied from numword.BaseSpeller
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
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  org.teherba.xtrans.BaseTransformer;
import  java.io.OutputStream;
import  java.io.PrintWriter;
import  java.sql.DatabaseMetaData;
import  java.sql.Types;
import  java.text.SimpleDateFormat;
import  java.util.ArrayList; // for 'columns'
import  java.util.Date;
import  java.util.HashMap;
import  java.util.Map;
import  java.util.Iterator;
import  java.util.TreeMap;
import  org.apache.log4j.Logger;

/** Base class defining common properties and methods
 *  for the external representation (formatting) of:
 *  <ul>
 *  <li>the description of  a relational table or view, or</li>
 *  <li>a result set obtained from an SQL SELECT statement.</li>
 *  <ul>
 *  Any specific format implementation like {@link HTMLTable}, {@link XMLTable}, {@link SeparatedTable} and so on
 *  extends this class.
 *  Apart from bean properties for all table formats, this class defines
 *  methods which represent various SAX-like events during the creation of the table,
 *  and which are called in the following sequence:
 *  <pre>
 *  writeStart [0:1] (not for Dbat, but for SpecificationHandler)
 *
 *    startDescription [0:1] (for -d)
 *      describeColumn [1:n]
 *      describeKey(... primary=true  ...) [1:1]
 *      describeKey(... primary=false ...) [0:n]
 *    endDescription
 *
 *  (and/or)
 *
 *    startTable
 *      for all rows retrieved from resultSet {
 *          if (isWithHeaders()) {
 *              writeGenericRow(HEADER, ...);
 *          }
 *          writeGenericRow(DATA, ...);
 *          writeGenericRow(DATA2, ...);
 *      }
 *    endTable
 *
 *  writeEnd (not for Dbat, but for SpecificationHandler)
 *  </pre>
 *  @author Dr. Georg Fischer
 */
public abstract class BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category), inherited by all subclasses */
    protected Logger log;
    /** whether to write debugging output */
    private final static boolean DEBUG = false;
    /** system dependant newline separator */
    protected String newline;
    /** Sequential Id for tables: "table1" etc. */
    protected int tableSeqNo;
    /** Sequential number of table row: 0, 1 etc. */
    protected int tableRowNo;
    /** the string which indicates a placeholder for a host variable in an SQL statement to be prepared */
    public static final String PARAMETER_MARKER = " ? ";
    /** ISO timestamp without milliseconds */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    /** Get an ISO timestamp
     *  @return String of the form "yyyy-MM-dd' 'HH:mm:ss"
     */
    public static String getISOTimestamp() {
        return TIMESTAMP_FORMAT.format(new java.util.Date());
    } // getISOTimestamp

    /** Indicates the type of a row to be output by {@link #writeGenericRow} */
    public static enum RowType
            { HEADER    // header row
            , DATA      // primary data row
            , DATA2     // secondary data row
            , ROW1      // 1 vertical row with headers prefixed
            };

    //======================================
    // Bean properties, getters and setters
    //======================================

    /** human readable description of the format in different languages, key is 2-letter ISO (country) code */
    protected HashMap<String, String> descriptionMap;
    /** Gets the human readable description of the format.
     *  @param language ISO country code for the language: "en", "de"
     *  @return text describing the format of this table
     */
    public String getDescription(String language) {
        return descriptionMap.get(language);
    } // getDescription

    /** Sets the human readable description of the format.
     *  All other languages default to the English version,
     *  therefore "en" must also be set first.
     *  @param language 2-letter ISO (country) code for the language: "en", "de"
     *  @param description text describing the format of this table
     */
    public void setDescription(String language, String description) {
        descriptionMap.put(language, description);
        if (language.equals("en")) {
            descriptionMap.put("de", description);
        } // default
    } // setDescription

    /** comma separated list of usual file extensions for this format */
    private   String fileExtensionList;
    /** Gets the first file extension
     *  @return file type (extension) without dot, normally 3 lowercase letters
     */
    public String getFileExtension() {
        int comma = fileExtensionList.indexOf(',');
        if (comma < 0) {
            comma = fileExtensionList.length();
        }
        return (comma > 0) ? fileExtensionList.substring(0, comma) : "dat";
    } // getFileExtension
    /** Sets the file extensions
     *  @param extensionList comma separated list of usual file extensions
     *  for this format
     */
    protected void setFileExtensions(String extensionList) {
        this.fileExtensionList = extensionList;
    } // setFileExtensions

    /** comma separated list of applicable file formats */
    private   String formatCodeList;
    /** Gets the list of applicable file format codes which are
     *  "understood" by this module.
     *  @return list of (lowercase) file format codes
     *  separated by commata
     */
    public String getFormatCodes() {
        return formatCodeList;
    } // getFormatCodes
    /** Gets the first code for the format
     *  @return file format code (lowercase)
     */
    public String getFirstFormatCode() {
        int pos = formatCodeList.indexOf(",");
        String result = formatCodeList;
        if (pos >= 0) {
            result = formatCodeList.substring(0, pos);
        }
        return result;
    } // getFirstFormatCode
    /** Sets the list of applicable file formats which are
     *  "understood" by this module
     *  @param list list of (lowercase) format codes
     *  separated by commata
     */
    protected void setFormatCodes(String list) {
        formatCodeList = list;
        setFileExtensions(list);
    } // setFormatCodes

    /** generator for SAX events */
    protected BaseTransformer generator;
    /** Gets the generator
     *  @return applicable Xtrans transformer
     */
    public BaseTransformer getGenerator() {
        return this.generator;
    } // getGenerator
    /** Sets the generator
     *  @param generator set the generator transformer to this Xtrans transformer
     */
    public void setGenerator(BaseTransformer generator) {
        this.generator = generator;
    } // setGenerator

    /** URI for additional input file */
    private   String inputURI;
    /** Gets the URI of an additional input file (-u option)
     *  @return URI with http:, file: or data: schema or relative path
     */
    public String getInputURI() {
        return inputURI;
    } // getInputURI
    /** Sets the URI of an additional input file (-u option)
     *  @param uri URI with http:, file: or data: schema or relative path
     */
    public void setInputURI(String uri) {
        inputURI = uri;
    } // setInputURI

    /** MIME type for display/store of the foreign format */
    private   String mimeType;
    /** Gets the MIME type
     *  @return mime type in the form "major/minor", for example "text/xml"
     */
    public String getMimeType() {
        return this.mimeType;
    } // getMimeType
    /** Sets the MIME type
     *  @param mimeType type in the form "major/minor", for example "text/xml"
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    } // setMimeType

    /** Output format: html, tsv */
    private String outputFormat;
    /** Gets the outputFormat
     *  @return format, one of "html", "tsv" (for Excel), "xml", "spec" ...
     */
    public String getOutputFormat() {
        return outputFormat;
    } // getOutputFormat
    /** Sets the outputFormat
     *  @param outputFormat one of "html", "tsv" (for Excel), "xml", "spec" ...
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat; // default, also if unknown format
    } // setOutputFormat

    /** separator string between column values */
    private   String separator;
    /** Gets the separator for csv format and raw input.
     *  @return separator string
     */
    public String getSeparator() {
        return separator;
    } // getSeparator
    /** Sets the separator for csv format and raw input.
     *  Any surrounding quotes are removed.
     *  @param sep separator string
     */
    public void setSeparator(String sep) {
        if (sep.startsWith("\"") && sep.endsWith("\"")) { // remove surrounding quotes
            separator = sep.substring(1, sep.length() - 1);
        } else {
            separator = sep;
        }
    } // setSeparator

    /** Gets the output file encoding
     *  @return encoding name of the target encoding (UTF-8, ISO-8859-1),
     *  or empty for binary data
     */

    /** output file encoding */
    private   String targetEncoding;
    /** Gets the output file encoding
     *  @return encoding name of the target encoding (UTF-8, ISO-8859-1),
     *  or empty for binary data
     */
    public String getTargetEncoding() {
        return targetEncoding;
    } // getTargetEncoding
    /** Sets the output file encoding
     *  @param encoding name of the encoding (UTF-8, ISO-8859-1),
     *  or empty for binary data
     */
    public void setTargetEncoding(String encoding) {
        targetEncoding = encoding;
    } // setTargetEncoding
    //--------------------------------------------------
    /** writer for binary files */
    protected PrintWriter  charWriter;
    /** writer for binary files */
    protected OutputStream byteWriter;

    /** Sets the writer for character data.
     *  @param writer writer to be used to write character data
     */
    public void setCharWriter(PrintWriter writer) {
        this.charWriter = writer;
    } // setCharWriter

    /** Sets the writer for binary data.
     *  @param writer writer to be used to write binary data
     */
    public void setByteWriter(OutputStream writer) {
        this.byteWriter = writer;
    } // setByteWriter

    /** Gets the writer for character data.
     *  @return writer to be used to write character data
     */
    public PrintWriter getCharWriter() {
        return this.charWriter;
    } // getCharWriter

    /** Gets the writer for binary data.
     *  @return writer to be used to write binary data
     */
    public OutputStream getByteWriter() {
        return this.byteWriter;
    } // getByteWriter
    //--------------------------------------------------
    /** whether the file format is binary */
    protected boolean binary;

    /** Tells whether the file format handled by this transformer
     *  uses byte files, or character files if false.
     *  @return true if the format uses byte files
     */
    public boolean isBinaryFormat() {
        return binary;
    } // isBinaryFormat

    /** Sets the binary format property
     *  @param binary true (false) if the format is (not) binary
     */
    public void setBinaryFormat(boolean binary) {
        this.binary = binary;
    } // setBinaryFormat

    //==============
    // Constructors
    //==============

    /** No-args Constructor
     */
    public BaseTable() {
        this("tsv");
        tableSeqNo = 0;
        singleCell = new TableColumn();
        singleCell.setWidth(16);
    } // Constructor

    /** Constructor with format
     *  @param format code for desired output format
     */
    public BaseTable(String format) {
        log = Logger.getLogger(BaseTable.class.getName());
        descriptionMap      = new HashMap<String, String>(4);
        setSeparator        ("\t");
        setTargetEncoding   ("UTF-8"); // default for XML
        setByteWriter       (null);
        setCharWriter       (null);
        setFileExtensions   (format);
        setFormatCodes      (format);
        setMimeType         ("text/plain");
        setDescription      ("en", format.toUpperCase());
        newline             = System.getProperty("line.separator");
        pseudoAttributes    = new StringBuffer(64);
    } // Constructor(format)

    //===================
    //  Utility methods
    //===================

    /** Converts a character to an XML entity of the form "&#x{hexstring};"
     *  @param ch character for the entity
     *  @return XML entity, for example "&#xa;"
     */
    public String charToEntity(char ch) {
        StringBuffer result = new StringBuffer(8);
        result.append("&#x");
        result.append(Integer.toHexString(ch));
        result.append(';');
        return result.toString();
    } // charToEntity

    /** Converts an XML entity of the form "&#x{hexstring};" or "&#{decimaldigits};"
     *  to the corresponding character
     *  @param entity an XML entity, for example "&#xa;"
     *  @return character for the entity
     */
    public char entityToChar(String entity) {
        char result = 0;
        try {
            if (entity.endsWith(";")) {
                entity = entity.substring(0, entity.length() - 1);
            }
            if (entity.startsWith("&#x")) { // hex
                result = (char) Integer.parseInt(entity.substring(3), 16);
            } else if (entity.startsWith("&#")) { // decimal
                result = (char) Integer.parseInt(entity.substring(2));
            } else {
            }
        } catch (Exception exc) {
        }
        return result;
    } // entityToChar

    /** a bunch of spaces */
    protected final static String SPACES = // 80 blanks, do   n e v e r   replace them by tabs!
            "                                                                                ";
    /** Create a string of spaces of a specified length
     *  @param spaceCount number of spaces to be created
     *  @return string of spaces
     */
    public String spaces(int spaceCount) {
        StringBuffer result = new StringBuffer(256);
        while (spaceCount > 0) { // advance - insert so many spaces
            int portion = spaceCount;
            if (portion > SPACES.length()) {
                portion = SPACES.length();
            }
            result.append(SPACES.substring(0, portion));
            spaceCount -= portion;
        } // while advancing
        return result.toString();
    } // spaces

    //-----------------------------------------------------------------------
    // The following properties and methods are common to all output formats
    // (they could be abstract).
    //-----------------------------------------------------------------------

    /** Row for table descriptions */
    protected TableMetaData metaRow;
    //---------------------------------
    /** URL for next real  LOB column, from a column with the <code>pseudo="url"</code> attribute,
     *  used to store the  LOB value under that URL
     *  and to replace the LOB value by that URL.
     */
    protected String nextLobURL;
    /** Gets the next LOB URL
     *  @return URL to be remembered
     */
    protected String getNextLobURL() {
        return nextLobURL;
    } // getNextLobURL
    /** Sets the next LOB URL
     *  @param url URL to be remembered, or null = reset
     */
    protected void setNextLobURL(String url) {
        nextLobURL = url;
    } // setNextLobURL

    /** Stores a CLOB value under the {@link #nextLobURL},
     *  and replaces the CLOB value by that URL
     *  @param value a very long string from a CLOB
     *  @return the URL which replaces the value
     */
    protected String replaceClobByURL(String value) {
        String result = nextLobURL;
        return result;
    } // replaceClobByURL
    //---------------------------------
    /** Attributes for the next real cell, from an SQL column with <code>pseudo="attr"</code> attribute,
     *  used to accumulate style, title and other attributes and to apply them on the next real cell
     */
    private StringBuffer pseudoAttributes;
    /** Gets the remembered attributes string
     *  @return concatenated attributes in the form <pre> attr1="val1" attr2="val2"</pre>
     */
    protected String getPseudoAttrs() {
        return pseudoAttributes.toString();
    } // getPseudoAttrs
    /** Append an attribute for the next real cell
     *  @param name attribute name, taken from the col element's name
     *  @param value attribute value
     */
    protected void appendPseudoAttr(String name, String value) {
        pseudoAttributes.append(" ");
        pseudoAttributes.append(name);
        pseudoAttributes.append("=\"");
        pseudoAttributes.append(value);
        pseudoAttributes.append("\"");
    } // appendPseudoAttr
    /** Clears the remembered attributes string
     */
    protected void clearPseudoAttrs() {
        pseudoAttributes.setLength(0);
    } // clearPseudoAttrs
    //---------------------------------
    /** Parameters for the next real cell, from an SQL column with <code>pseudo="parm"</code> attribute,
     *  used as the HTTP parameters in the query string of a link on the next real cell
     */
    private StringBuffer pseudoParameters;
    /** Gets the remembered parameters string
     *  @return concatenated parameters in the form <pre>&parm1=val1&parm2=val2</pre>
     */
    protected String getPseudoParms() {
        return pseudoParameters.toString();
    } // getPseudoParms
    /** Append a parameter for the next real cell
     *  @param name parameter name, taken from the col element's name
     *  @param value parameter value
     */
    protected void appendPseudoParm(String name, String value) {
        pseudoParameters.append("&amp;");
        pseudoParameters.append(name);
        pseudoParameters.append("=");
        pseudoParameters.append(value);
    } // appendPseudoParm
    /** Clears the remembered parameters string
     */
    protected void clearPseudoParms() {
        pseudoParameters.setLength(0);
    } // clearPseudoParms
    //---------------------------------
    /** Style for next real column, from a column with <code>pseudo="style"</code> attribute,
     *  used to apply font styles and colors on cell contents
     */
    protected String nextStyle;
    /** Gets the next style
     *  @return style to be remembered, or null = reset
     */
    protected String getNextStyle(String style) {
        return nextStyle;
    } // getNextStyle
    /** Sets the next style
     *  @param style style to be remembered, or null = reset
     */
    protected void setNextStyle(String style) {
        nextStyle = style;
    } // setNextStyle
    //---------------------------------
    /** Returns a block of DTD ENTITIY definition lines, one for each request parameter
     *  @param parameterMap map of request parameters to values
     *  @return a block of lines of ENTITY definitions terminated by newlines
     */
    public String getEntitiesFromParameters(Map<String, String[]> parameterMap) {
        StringBuffer result = new StringBuffer(256);
        Iterator<String> parmIter = parameterMap.keySet().iterator();
        while (parmIter.hasNext()) {
            String name = parmIter.next();
            String[] values = (String[]) (parameterMap.get(name));
            int ival = 0;
            if (ival < values.length) { // take first value only, if any
                result.append("<!ENTITY "); // or "ENTITY % " ???
                result.append(name);
                result.append(" \"");
                result.append(values[ival]
                        .replaceAll("&", "&amp;")
                        .replaceAll(">", "&gt;")
                        .replaceAll("<", "&lt;")
                        );
                result.append("\">\n");
                ival ++;
            } // while ival
        } // while parmIter
        return result.toString();
    } // getEntitiesFromParameters

    /** Maps parameter names to arrays of (String) parameter values */
    private HashMap<String, String[]> parameterMap;

    /** Sets the parameter map to be uses in the Http request
     *  @param parameterMap map of request parameters to lists pf values
     */
    public void setParameterMap(HashMap<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
    } // setParameterMap

    /** Sets the value(s) of a (new) parameter
     *  @param name   name of the parameter to be set
     *  @param values list of values to be set
     */
    public void setParameter(String name, String[] values) {
        this.parameterMap.put(name, values);
    } // setParameter

    /** Appends a row from an SQL result set to the parameter map.
     *  This method is generic for all formats, and cannot be overridden.
     *  @param rowIndex number of SQL result set row: 0 = first, 1, 2 and so on
     *  @param tbMetaData meta data for the table
     *  @param columnList contains the row to be written
     */
    public final void appendToParameters(int rowIndex, TableMetaData tbMetaData, ArrayList<TableColumn> columnList) {
        int ncol = columnList.size();
        int icol = 0;
        while (icol < ncol) {
            TableColumn column = columnList.get(icol);
            String name = column.getName();
            String newValue = columnList.get(icol).getValue();
            Object obj = parameterMap.get(name);
            if (obj == null || rowIndex == 0) { // parameter undefined so far, or first row
                parameterMap.put(name, new String[] { newValue });
            } else { // parameter already defined - must copy into length + 1 and append
                String[] oldValues = (String[]) obj;
                int len = oldValues.length;
                String[] newValues = new String[len + 1];
                System.arraycopy(oldValues, 0, newValues, 0, len);
                newValues[len] = newValue;
                parameterMap.put(name, newValues);
            }
            icol ++;
        } // while icol
    } // appendToParameters

    /*-------------------------------------------------------
     *  Specific methods for SAX-like table generation events
     *-------------------------------------------------------*/

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *  @param attributes array of 0 or more pairs of strings (name1, value1, name2, value2 and so on)
     *  which specify features in the header of the file to be generated.
     *  @param parameterMap map of request parameters to values
     *  The following "attribute" names are interpreted:
     *  <ul>
     *  <li>contenttype - MIME type for the document content</li>
     *  <li>encoding - encoding to be used for the output stream</li>
     *  <li>javascript - names of the files containing JavaScript functions (multiple, separated by whitespace)</li>
     *  <li>lang - code for the language the specification is written for: en, de </li>
     *  <li>namespace="no" - whether not to output an <code>xmlns</code> attribute on the <code>&lt;dbat&gt;</code> root element</li>
     *  <li>specname - name of the specification file, relative to <em>urlpath</em></li>
     *  <li>stylesheet - names of the CSS files (multiple, separated by whitespace)</li>
     *  <li>target - target of HTML base element, for example <code>_blank</code></li>
     *  <li>title - title for the HTML head element, and the browser window</li>
     *  <li>urlpath - URL of the specification subdirectory, for example <code>http://.../dbat/spec/</code></li>
     *  <li>xslt="subdir.filename" - whether to perform XSLT</li>
     *  </ul>
     */
    public void writeStart(String[] attributes,  HashMap<String, String[]> parameterMap) {
    } // writeStart

    /** Ends a file that may contain several table descriptions and/or a SELECT result sets
     */
    public void writeEnd() {
    } // writeEnd

    /** Flushes and closes the output printer or stream writer
     */
    public void close() {
        try {
            if (isBinaryFormat()) {
                byteWriter.flush();
                byteWriter.close();
            } else {
                charWriter.flush();
                charWriter.close();
            }
        } catch(Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // close

    /** Writes trailer information
     *  @param trailer trailing elements with links to spec file, Excel output, timestamp etc.
     */
    public void writeTrailer(String trailer) {
        writeComment(trailer);
    } // writeTrailer

    /** Starts the description of a table with DROP table and CREATE TABLE
     *  @param dbMetaData result of <code>con.getDatabaseMetaData()</code>.
     *  @param schema    name of the table's schema, or null if none
     *  @param tableName name of the table
     *  @param tableType type of the table: "TABLE", "VIEW" etc.
     */
    public void startDescription(DatabaseMetaData dbMetaData, String schema, String tableName, String tableType) {
        startTable(tableName);
        metaRow = new TableMetaData();
        int mcol = 0;
        TableColumn column;
        column = metaRow.addColumn(mcol ++); column.setWidth(16); column.setLabel("name");
        column = metaRow.addColumn(mcol ++); column.setWidth(12); column.setLabel("type");
        column = metaRow.addColumn(mcol ++); column.setWidth( 8); column.setLabel("width");
        column = metaRow.addColumn(mcol ++); column.setWidth( 8); column.setLabel("nullable");
        column = metaRow.addColumn(mcol ++); column.setWidth(32); column.setLabel("remark");
    } // startDescription

    /** Ends the description of a table.
     */
    public void endDescription() {
        endTable();
    } // endDescription

    /** Simple cell which only has <code>href=</code and <code>value=</code> attributes */
    private TableColumn singleCell;
    /** Gets the simple cell
     *  @return a column
     */
    public TableColumn getSingleCell() {
        if (singleCell == null) {
            singleCell = new TableColumn();
        }
        return singleCell;
    } // getSingleCell

    /** Writes the value of the simple table cell
     */
    public void writeSingleCell() {
        charWriter.print(getFlatValue(singleCell));
    } // writeSingleCell

    //======================================
    // Table description (DDL)
    //======================================

    /** Writes the definition string for a table column.
     *  @param colNo number of the column: 0, 1, 2
     *  @param column bean with the properties of the column
     */
    public void describeColumn(int colNo, TableColumn column) {
        try {
            if (colNo == 0) { // first column to be described
                writeGenericRow(RowType.HEADER , metaRow, metaRow.columnList);
            }
            int mcol = 0;
            metaRow.columnList.get(mcol ++).setValue(column.getName());
            metaRow.columnList.get(mcol ++).setValue(column.getTypeName());
            int width    = column.getWidth();
            int dataType = column.getDataType();
            StringBuffer buffer = new StringBuffer(16);
            if (dataType == Types.CHAR || dataType == Types.VARCHAR || dataType == Types.DECIMAL) {
                buffer.append(String.valueOf(width));
                if (dataType == Types.DECIMAL) {
                    int decimalDigits = column.getDecimal();
                    if (decimalDigits != 0) {
                        buffer.append(",");
                        buffer.append(String.valueOf(decimalDigits));
                    }
                } // decimal
            } // width
            metaRow.columnList.get(mcol ++).setValue(buffer.toString());
            metaRow.columnList.get(mcol ++).setValue(String.valueOf(column.isNullable()));
            String remark = column.getRemark();
            if (remark == null) {
                remark = "";
            }
            metaRow.columnList.get(mcol ++).setValue(remark);
            writeGenericRow(RowType.DATA , metaRow, metaRow.columnList);
        } catch (Exception exc) {
            // log.error(exc.getMessage(), exc);
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
        writeComment("BaseTable.describeColumnsEnd");
    } // describeColumnsEnd

    /** Writes the descriptions of any primary key constraint as SQL of the form
     *  <pre>
     *  , PRIMARY KEY (COL1, COL2)
     *  </pre>
     *  @param tableName name of the table on which the constraint is defined
     *  @param cstRows array for the result set of <em>DatabaseMetaData.getImportedKeys</em>, properly sorted
     *  by PKNAME and KEY_SEQ,
     *  with an additional fictitious row for the last group change
     */
    public void describePrimaryKey(String tableName, TreeMap<String, HashMap<String, String>> cstRows) {
        writeComment("BaseTable.describePrimaryKey");
    } // describePrimaryKey

    /** Writes the description of all indexes
     *  @param tableName fully qualified name of the table
     *  @param cstRows array for the result set of <em>DatabaseMetaData.getIndexInfo</em>, properly sorted
     *  by INDEX_NAME and ORDINAL_POSITION,
     *  with an additional fictitious row for the last group change
     */
    public void describeIndexes(String tableName, TreeMap<String, HashMap<String, String>> cstRows) {
        writeComment("BaseTable.describeIndexes");
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
        writeComment("BaseTable.describeConstraints");
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
            , short procedureType
            , String procSeparator
            , TreeMap<String, HashMap<String, String>> cstRows) {
        writeComment("BaseTable.describeProcedureColumns");
    } // describeProcedureColumns

    //==========================================
    // Table elements generated for a SELECT
    //==========================================

    /** Initializes a table
     *  @param tableName name of the table
     */
    public void startTable(String tableName) {
    } // startTable

    /** Initializes a table - with meta data, currently only implemented in SQLTable and its subclasses.
     *  For subclasses which do not override this method, the meta data are ignored.
     *  @param tableName name of the table
     *  @param tbMetaData meta data of the table
     */
    public void startTable(String tableName, TableMetaData tbMetaData) {
        startTable(tableName);
    } // startTable

    /** Terminates  a table
     */
    public void endTable() {
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
            if (rowCount == 0) {
                writeComment(desc);
            } else {
                writeComment(rowCount + (moreRows ? "+ " : " ") + desc);
            }
        } // if set
    } // writeTableFooter

    /** Writes a comment.
     *  @param line string to be output as a comment
     */
    public void writeComment(String line) {
    } // writeComment(1)

    /** Writes a comment, but only if the "verbose" level is > 0.
     *  @param line string to be output as a comment
     *  @param verbose level of output detail
     */
    public void writeComment(String line, int verbose) {
        writeComment(line);
    } // writeComment(2)

    /** Writes a COMMIT statement.
     *  @param rowCount number of INSERT/UPDATE statements already generated
     */
    public void writeCommit(int rowCount) {
        // no-op for most formats, only -sql/jdbc and -update do implement this method
    } // writeCommit

    /** Writes the pure SQL instruction, with any placeholders replaced by constant values again.
     *  @param tbMetaData meta data for the table as far as they are already known
     *  @param sqlInstruction a SELECT, CALL, DELETE, INSERT or UPDATE statement
     *  @param action 0 = SELECT, 1 = CALL, 2 = DML instructions
     *  @param verbose 1 (0) = (not) verbose
     *  @param variables pairs of types and values for variables to be filled
     *  into any placeholders ("?") in the prepared statement
     */
    public void writeSQLInstruction(TableMetaData tbMetaData, String sqlInstruction
            , int action, int verbose
            , ArrayList<String> variables) {
        String separator = ";";
        writeComment("SQL:\n" + sqlInstruction.trim() + separator + "\n:SQL", verbose);
    } // writeSQLInstruction

    /** Tells, for the specific format, the rule to be applied for escaping.
     *  The result may optionally depend on the column's attributes and/or the cell value.
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
    public int getEscapingRule() {
        return 0; // default: no escaping
    } // getEscapingRule

    /** Writes HTML markup (for HTML), or nothing for other formats
     *  @param markup HTML element(s) to be written
     */
    public void writeMarkup(String markup) {
        // default: ignore markup
    } // writeMarkup

    /** Writes an XML processing instruction, or nothing for other formats.
     *  @param target the processing instruction target
     *  @param data the processing instruction data, or null if none was supplied.
     *  The data does not include any whitespace separating it from the target
     */
    public void writeProcessingInstruction(String target, String data) {
        // default: ignore processing instructions
    } // writeProcessingInstruction

    //=======================================
    // 3rd generation methods
    //=======================================

    /** Gets the string content of a header or data cell.
     *  The strings obtained by this method can be aggregated
     *  (with some separator) in order to form the contents of an aggregated column.
     *  Pivot tables and aggregation store the result back into the cell's value.
     *  Repeated calls of this method may not lengthen the resulting value.
     *  @param column attributes of this column, containing the value also
     *  @return string content of a cell
     */
    public String getFlatValue(TableColumn column) {
        String value = column.getValue();
        return value; // for many formats it is simply the column's value (not enclosed in a link)
    } // getFlatValue

    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *  @param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList<TableColumn> columnList) {
        TableColumn column = null;
        String pseudo = null;
        int ncol = columnList.size();
        int icol = 0;
        boolean first = true;
        String separator = getSeparator();
        switch (rowType) {
            case HEADER:
                first = true;
                while (icol < ncol) {
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (pseudo == null) {
                        if (first) {
                            first = false;
                        } else {
                            charWriter.print(separator);
                        }
                        charWriter.print(columnList.get(icol).getLabel());
                    } // ! pseudo
                    icol ++;
                } // while icol
                charWriter.println();
                break;
            case DATA:
                first = true;
                while (icol < ncol) {
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (pseudo == null) {
                        if (first) {
                            first = false;
                        } else {
                            charWriter.print(separator);
                        }
                        charWriter.print(columnList.get(icol).getValue());
                    } // ! pseudo
                    icol ++;
                } // while icol
                charWriter.println();
                break;
            case DATA2:
                break;
            default:
                break; // ignore all unimplemented row types
        } // switch rowType
    } // writeGenericRow

} // BaseTable
