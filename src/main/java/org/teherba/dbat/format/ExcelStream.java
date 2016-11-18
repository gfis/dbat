/*  Generator for an Excel 2003 XML table
 *  @(#) $Id$
 *  2016-10-13: less imports
 *  2016-05-17: formatting of header line
 *  2016-05-08: copied from ExcelTable
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
import  org.teherba.dbat.SQLAction; // DATE/TIME(STAMP)_FORMAT
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  org.teherba.dbat.format.BaseTable;
import  org.apache.poi.ss.usermodel.Cell;
import  org.apache.poi.ss.usermodel.CellStyle;
import  org.apache.poi.ss.usermodel.DataFormat;
import  org.apache.poi.ss.usermodel.Font;
import  org.apache.poi.ss.usermodel.Row;
import  org.apache.poi.ss.usermodel.Sheet;
import  org.apache.poi.ss.usermodel.Workbook;
import  org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.xssf.usermodel.XSSFWorkbook;
import  java.io.BufferedOutputStream;
import  java.sql.Types;
import  java.util.ArrayList;
import  java.util.HashMap;

/** Generator for binary Excel 97 *.xls (BIFF) and Excel 2007 (Office Open XML, OOXML) tables.
 *  The format is described in <a href="http://msdn.microsoft.com/en-us/library/aa140066%28office.10%29.aspx">http://msdn.microsoft.com/en-us/library/aa140066%28office.10%29.aspx</a>
 *  This is not to be confused with the <em>Microsoft Office XML formats</em> of Office 2003, c.f.
 *  <a href="http://en.wikipedia.org/wiki/Microsoft_Office_XML_formats">http://en.wikipedia.org/wiki/Microsoft_Office_XML_formats</a>.
 *  <p>
 *  In contrast to the CSV import into Excel, these formats avoid the unwanted date format
 *  interpretation of strings of the form 'mm-nn-pp'.
 *  <p>
 *  Depending on the mode ("xlsx" or "xls") the class decides which Excel format (OOXML or BIFF) 
 *  it should generate. This switch is made easy by the package <em>org.apache.poi.ss.usermodel</em>
 *  of Apache POI.
 *  @author Dr. Georg Fischer
 */
public class ExcelStream extends BaseTable {
    public final static String CVSID = "@(#) $Id$";

    /** encoding for output */
    private String encoding;
    /** sequential counter for {@link Row}s in a {@link Sheet} */
    private int rowNo;
    /** sequential counter for {@link Sheet}s in a {@link Workbook} */
    private int sheetNo;
    /** {@link Workbook} to be generated */
    private Workbook wbook;
    /** current {@link Sheet} to be filled with {@link Row}s and {@link Cell}s */
    private Sheet sheet;
    /** {@link DataFormat} for {@link #wbook} */
    private DataFormat wbDataFormat;

    /** No-args Constructor
     */
    public ExcelStream() {
        this("xlsx,xls");
    } // Constructor

    /** Constructor with format
     *  @param format = "xlsx,xls"
     */
    public ExcelStream(String format) {
        super();
        setBinaryFormat (true);
        setFormatCodes  (format);
        setDescription  ("en", "Excel");
        encoding        = "UTF-8";
        sheetNo         = 0;
        rowNo           = 0;
        wbDataFormat    = null;
    } // Constructor

    /** decimal points should be converted to this String (a comma) if it is != null */
    private String decimalSeparator;

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *  @param attributes array of 0 or more pairs of strings (name1, value1, name2, value2 and so on)
     *  which specify features in the header of the file to be generated.
     *  The possible attribute names are described in {@link BaseTable#writeStart}.
     *  @param parameterMap map of request parameters to values
     */
    public void writeStart(String[] attributes,  HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        try {
            decimalSeparator = null;
            String[] pdec = parameterMap.get("decimal");
            if (pdec != null && ! pdec[0].equals(".")) {
                decimalSeparator = pdec[0];
            }
            String encoding = getTargetEncoding();
            int iattr = attributes.length;
            while (iattr > 0) {
                iattr -= 2;
                if (false) {
                } else if (attributes[iattr].equals("encoding")) {
                    encoding = attributes[iattr + 1];
                }
            } // while iattr

            if (this.getOutputFormat().equals("xls")) {
                wbook = new HSSFWorkbook();
                setMimeType("application/vnd.ms-excel"); // BIFF file
            } else { // "xlsx
                wbook = new XSSFWorkbook();
                setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // OfficeOpenXML format
            }
            wbDataFormat = wbook.createDataFormat();
            sheetNo = 0;
            rowNo   = 0;
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeStart

    /** Ends a file that may contain several table descriptions and/or a SELECT result sets
     */
    public void writeEnd() {
        try {
            if (byteWriter == null) {
                System.err.println("ExcelStream#writeEnd: byteWriter == null");
                byteWriter = new BufferedOutputStream(System.out);
            }
            wbook.write(byteWriter);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeEnd

    /** Initializes a table
     *  @param tableName name of the table
     */
    public void startTable(String tableName) {
        sheet = wbook.createSheet(tableName);
        sheetNo ++;
        rowNo = 0;
    } // startTable

    /** Terminates  a table
     */
    public void endTable() {
    } // endTable

    /** Writes a comment line.
     *  @param line string to be output as a comment line
     */
    public void writeComment(String line) {
    } // writeComment

    /** Tells, for the specific format, the rule to be applied for escaping.
     *  The result may optionally depend on the column's attributes and/or the cell value.
     *  @return the following escaping rules are currently observed:
     *  <ul>
     *  <li>0 = no escaping at all</li>
     *  <li>1 = "&amp;", "&lt;" and "&gt;" are escaped
     *  as "&amp;amp;", "&amp;lt;" and "&amp;gt;" respectively</li>
     *  <li>2 = "&apos;" is replaced by "&amp;apos"</li>
     *  <li>3 = combination of rule 1 and rule 2</li>
     *  </ul>
     */
    public int getEscapingRule() {
        return 0;
    } // getEscapingRule

    /** Writes a complete header, data or alternate data row with all tags and cell contents.
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
        Row  row  = null;
        Cell cell = null;
        Font font       = wbook.createFont();
        CellStyle style = wbook.createCellStyle();
        style.setFont(font);
        switch (rowType) {
            case HEADER:
                row = sheet.createRow(rowNo ++);
                // row.setHeightInPoints(12.75f);
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                style.setAlignment(CellStyle.ALIGN_CENTER);
                while (icol < ncol) {
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (pseudo != null) {
                        if (false) {
                        } else if (pseudo.equals("style")) {
                            nextStyle = null;
                        }
                    } else { // pseudo == null
                        String header = column.getLabel();
                        if (header == null) {
                            header = "&nbsp;";
                        }
                        cell = row.createCell(icol);
                        cell.setCellValue(header);
                        cell.setCellStyle(style);
                    }
                    int width = column.getWidth();
                    switch (column.getDataType()) {
                        default: // all numeric types: INT, DECIMAL ...
                        case Types.INTEGER:
                        case Types.NUMERIC:
                        case Types.SMALLINT:
                        case Types.TINYINT:
                            break;
                        case Types.FLOAT:
                        case Types.DOUBLE:
                        case Types.REAL:
                        case Types.DECIMAL:
                            width ++; // for the point
                            break;
                        case Types.CHAR:
                        case Types.VARCHAR:
                            break;
                        case Types.DATE:
                            width = (width <= 10) ? 12 : width;
                            break;
                        case Types.TIME:
                            width = (width <=  8) ? 10 : width;
                            break;
                        case Types.TIMESTAMP:
                            width = (width <= 19) ? 21 : width;
                            break;
                    } // switch type
                    sheet.setColumnWidth(icol, 256 * width);
                    icol ++;
                } // while icol
                break;
            case DATA:
                row = sheet.createRow(rowNo ++);
                // row.setHeightInPoints(12.75f);
                font  = wbook.createFont();
                while (icol < ncol) {
                    style = wbook.createCellStyle();
                    style.setFont(font);
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (pseudo != null) {
                        if (false) {
                        } else if (pseudo.equals("style")) {
                            nextStyle = column.getValue();
                        }
                    } else { // pseudo == null
                        cell = row.createCell(icol);
                        String value = column.getValue();
                        if (value == null) {
                            value = "NULL";
                        } else {
                            try {
                                switch (column.getDataType()) {
                                default: // all numeric types: INT, DECIMAL ...
                                case Types.FLOAT:
                                case Types.DOUBLE:
                                case Types.INTEGER:
                                case Types.NUMERIC:
                                case Types.SMALLINT:
                                case Types.TINYINT:
                                case Types.REAL:
                                    style.setDataFormat(wbDataFormat.getFormat("0"));
                                    style.setAlignment(CellStyle.ALIGN_RIGHT);
                                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    cell.setCellValue(Double.valueOf(value));
                                    break;
                                case Types.DECIMAL:
                                    style.setAlignment(CellStyle.ALIGN_RIGHT);
                                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    cell.setCellValue(Double.valueOf(value));
                                    int decimalDigits = column.getDecimal(); // not yet filled properly, therefore:
                                    decimalDigits = 0;
                                    int dotPos = value.indexOf('.');
                                    if (dotPos >= 0) {
                                        decimalDigits = value.length() - 1 - dotPos;
                                    }
                                    String fraction = "00";
                                    if (false) {
                                    } else if (decimalDigits <= 0 || decimalDigits > 10) {
                                        style.setDataFormat(wbDataFormat.getFormat("0"));
                                    } else if (decimalDigits <= 10) {
                                        fraction = "0000000000".substring(0, decimalDigits);
                                        style.setDataFormat(wbDataFormat.getFormat("0." + fraction));
                                    }
                                    break;
                                case Types.CHAR:
                                case Types.VARCHAR:
                                    style.setAlignment(CellStyle.ALIGN_LEFT);
                                    cell.setCellType(Cell.CELL_TYPE_STRING);
                                    cell.setCellValue(value);
                                    break;
                                case Types.DATE:
                                    style.setAlignment(CellStyle.ALIGN_RIGHT);
                                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    cell.setCellValue(SQLAction.DATE_FORMAT.parse(value));
                                    style.setDataFormat(wbDataFormat.getFormat("yyyy-mm-dd"));
                                    break;
                                case Types.TIME:
                                    style.setAlignment(CellStyle.ALIGN_RIGHT);
                                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    cell.setCellValue(SQLAction.TIME_FORMAT.parse(value));
                                    style.setDataFormat(wbDataFormat.getFormat("hh:mm:ss"));
                                    break;
                                case Types.TIMESTAMP:
                                    style.setAlignment(CellStyle.ALIGN_RIGHT);
                                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    cell.setCellValue(SQLAction.TIMESTAMP_FORMAT.parse(value));
                                    style.setDataFormat(wbDataFormat.getFormat("yyyy-mm-dd hh:mm:ss"));
                                    break;
                                } // switch type
                            } catch (Exception exc) { // treat the value as text
                                    cell.setCellValue(value);
                            }
                            cell.setCellStyle(style);
                        } // not NULL
                    } // pseudo == null
                    icol ++;
                } // while icol
                break;
        } // switch rowType
    } // writeGenericRow

} // ExcelStream
