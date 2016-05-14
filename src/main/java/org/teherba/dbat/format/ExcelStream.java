/*  Generator for an Excel 2003 XML table
    @(#) $Id$
    2016-05-ß8: copied from ExcelTable
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
import  org.teherba.dbat.format.BaseTable;
import  org.apache.poi.ss.usermodel.Cell;
import  org.apache.poi.ss.usermodel.Row;
import  org.apache.poi.ss.usermodel.Sheet;
import  org.apache.poi.ss.usermodel.Workbook;
import  org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.xssf.usermodel.XSSFWorkbook;
import  java.io.BufferedOutputStream;
import  java.sql.Types;
import  java.util.ArrayList;
import  java.util.HashMap;

/** Generator for binary Excel 97 *.xls and Excel 2007 OOXMLXML table.
 *  The format is described in <a href="http://msdn.microsoft.com/en-us/library/aa140066%28office.10%29.aspx">http://msdn.microsoft.com/en-us/library/aa140066%28office.10%29.aspx</a>
 *  This is not to be confused with the <em>Office Open XML</em> of Office 2007 and later, c.f.
 *  <a href="http://en.wikipedia.org/wiki/Microsoft_Office_XML_formats">http://en.wikipedia.org/wiki/Microsoft_Office_XML_formats</a>.
 *  <p>
 *  In contrast to the CSV import into Excel, this format avoids unwanted date format
 *  interpretation of strings of the form 'mm-nn-pp'.
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
    /** {@link workbook} to be generated */
    private Workbook wbook;
    /** current {@link Sheet} to be filled with {@link Row}s and {@link Cell}s */
    private Sheet sheet;

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
    } // Constructor

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *  @param attributes array of 0 or more pairs of strings (name1, value1, name2, value2 and so on) 
     *  which specify features in the header of the file to be generated.
     *  The possible attribute names are described in {@link BaseTable#writeStart}.
     *  @param parameterMap map of request parameters to values
     */
    public void writeStart(String[] attributes,  HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        try {
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
        switch (rowType) {
            case HEADER:
            /*           
                headerRow.setHeightInPoints(12.75f);
                for (int i = 0; i < titles.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(titles[i]);
                    cell.setCellStyle(styles.get("header"));
                }
            */
                Row row = sheet.createRow(rowNo ++);
                row.setHeightInPoints(12.75f);
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
                        Cell cell = row.createCell(icol);
                        cell.setCellValue(header);
                    }
                    icol ++;
                } // while icol
                break;
            case DATA:
            /*
                charWriter.print("<Row>");
                StringBuffer result = new StringBuffer(256);
                while (icol < ncol) {
                    result.setLength(0);
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (pseudo != null) {
                        if (false) {
                        } else if (pseudo.equals("style")) {
                            nextStyle = column.getValue();
                        }
                    } else { // pseudo == null
                        result.append("<Cell");
                        if (column.getWrappedValue() != null) {
                            result.append(" ss:HRef=\"");
                            result.append(column.getWrappedValue());
                            result.append("\"");
                        }
                        result.append("><Data ss:Type=\"");
                        int    dataType = column.getDataType();
                        if (false) {
                        } else if (dataType == Types.DECIMAL                               ) {
                            result.append("Number");
                        } else if (dataType == Types.DATE       || dataType == Types.TIMESTAMP) {
                            result.append("DateTime");
                        } else { // if (dataType == Types.CHAR  || dataType == Types.VARCHAR  ) {
                            result.append("String");
                        }
                        result.append("\">");
                        result.append(column.getValue());
                        nextStyle = null;
                        result.append("</Data></Cell>");
                        result.append(newline);
                        charWriter.print(result.toString());
                    } // pseudo == null
                    icol ++;
                } // while icol
                charWriter.print("</Row>" + newline);
        */
                row = sheet.createRow(rowNo ++);
                row.setHeightInPoints(12.75f);
                StringBuffer result = new StringBuffer(256);
                while (icol < ncol) {
                    result.setLength(0);
                    column = columnList.get(icol);
                    pseudo = column.getPseudo();
                    if (pseudo != null) {
                        if (false) {
                        } else if (pseudo.equals("style")) {
                            nextStyle = column.getValue();
                        }
                    } else { // pseudo == null
                    /*
                        result.append("<Cell");
                        if (column.getWrappedValue() != null) {
                            result.append(" ss:HRef=\"");
                            result.append(column.getWrappedValue());
                            result.append("\"");
                        }
                        result.append("><Data ss:Type=\"");
                        int    dataType = column.getDataType();
                        if (false) {
                        } else if (dataType == Types.DECIMAL                               ) {
                            result.append("Number");
                        } else if (dataType == Types.DATE       || dataType == Types.TIMESTAMP) {
                            result.append("DateTime");
                        } else { // if (dataType == Types.CHAR  || dataType == Types.VARCHAR  ) {
                            result.append("String");
                        }
                        result.append("\">");
                        result.append(column.getValue());
                        nextStyle = null;
                        result.append("</Data></Cell>");
                        result.append(newline);
                        charWriter.print(result.toString());
                    */
                        Cell cell = row.createCell(icol);
	        			String value = column.getValue();
	        			if (value == null) {
	        			    result.append("NULL");
	        			} else {
	        			    switch (column.getDataType()) {
	        			        case Types.DECIMAL:
	        			        	break;
	        			        default: // all numeric types: INT, DECIMAL
	        			        case Types.CHAR:
	        			        case Types.VARCHAR:
	        			        	break;
	        			        case Types.DATE:
	        			        case Types.TIME:
	        			        case Types.TIMESTAMP:
	        			            break;
	        			    } // switch type
	        			} // not NULL
                        cell.setCellValue(value);
                    } // pseudo == null
                    icol ++;
                } // while icol
                break;
        } // switch rowType
    } // writeGenericRow

} // ExcelStream
