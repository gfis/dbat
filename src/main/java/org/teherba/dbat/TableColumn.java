/*  TableColumn - bean with properties of an abstract column
    @(#) $Id$
    2014-01-15: inactivate fragment logic
    2013-02-01: getHrefValue: move fragment to the end
    2012-05-08: {s|g}etDir(char) vs. {s|g}etDirection(String)
    2012-01-16: setDir, setWidth(String)
    2012-01-10: whitespace in URL makes no sense
    2011-11-15: defaultValue, autoIncrement
    2011-08-15: {set|get}HrefValue
    2010-09-12: {set|get}Value
    2010-03-13: completeColumn
    2010-03-09: setter for direction
    2010-02-19: remark on column
    2008-08-01: constructor with name
    2007-01-06, Georg Fischer: copied from xtrans
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
package org.teherba.dbat;
import  java.sql.ResultSet;
import  java.sql.Types;
import  org.apache.log4j.Logger;

/** Bean with properties of an column of an abstract (internal) table definition
 *  (or for the parameter of a Stored Procedure),
 *  which is used to map database (SQL) columns to presentation (HTML, XML) table columns.
 *  The position of the column in the table's rows is not neccessarily the same as
 *  the position in the corresponding SQL result set, nor is it the same as the
 *  column position in the resulting HTML table, because of:
 *  <ul>
 *  <li>pseudo columns used to set a style on the next HTML table
 *  =&gt; SQL result set has more columns than HTML</li>
 *  </ul>
 *  Initially, all properties are undefined. They are either filled from
 *  an XML specification, from the metadata of the SQL table, or they are
 *  deduced from other properties of the same column.
 *  For each output format,  only a subset of properties is needed and stored in
 *  <code>BaseTable.currentColumn</code>.
 *  @author Dr. Georg Fischer
 */
public class TableColumn implements Cloneable {
    public final static String CVSID = "@(#) $Id$";
    /** Debugging switch */
    private int debug = 0;

    /** undefined SQL type (check against java.sql.Types) */
    public final static int NO_TYPE = 0x77777777;

    //================
    // Construction
    //================

    /** No-argument constructor
     */
    public TableColumn() {
    /*
        align       = "";
        href        = null;
        index       = 0;
        key         = "";
        label       = "";
        link        = null;
        name        = "";
        pseudo      = "";
        remark      = "";
    */
        autoIncrement = -1; // unknown
        dataType    = NO_TYPE;
        decimal     = 0;
        defaultValue= null; // no default value
        setDir('o'); // assume "out" parameter
        expr        = "";
        hrefValue   = null;
        nullable    = true;
        style       = "";
        typeName    = "";
        value       = "";
        width       = 0; // indicates that it can still be overriden from dbMetaData
    } // no-args constructor

    /** Constructor with name
     *  @param name name of the new column
     */
    public TableColumn(String name) {
        this();
        setName(name);
    } // constructor(name)

    /** Constructor with index
     *  @param index sequential number of the column: 0, 1, 2
     */
    public TableColumn(int index) {
        this();
        this.index = index;
    //  setName("col" + index);
    } // Constructor(index)

    /** Creates a deep copy of the object
     *  @return a new column with all properties of <em>this</em> column
     */
    public TableColumn clone() {
        TableColumn result = new TableColumn();
        result.align        = this.align    ;
        result.autoIncrement= this.autoIncrement  ;
        result.dataType     = this.dataType ;
        result.decimal      = this.decimal  ;
        result.defaultValue = this.defaultValue   ;
        result.dir          = this.dir;
        result.expr         = this.expr     ;
        result.href         = this.href      ;
        result.hrefValue    = this.hrefValue;
        result.index        = this.index    ;
        result.key          = this.key      ;
        result.label        = this.label    ;
        result.link         = this.link     ;
        result.name         = this.name     ;
        result.nullable     = this.nullable ;
        result.pseudo       = this.pseudo   ;
        result.remark       = this.remark    ;
        result.style        = this.style    ;
        result.typeName     = this.typeName ;
        result.value        = this.value    ;
        result.width        = this.width    ;
        return result;
    } // clone

    //=================================
    // Properties, setters and getters
    //=================================

    /** HTML alignment for column values */
    private String align;
    /** Gets the alignment
     *  @return left, right, center
     */
    public String getAlign() {
        return align;
    } // getAlign
    /** Sets the alignment
     *  @param align : left, right or center
     */
    public void setAlign(String align) {
        this.align = align;
    } // setAlign
    //----------------
    /** code for auto increment property of the column */
    private short autoIncrement;
    /** Gets the aut increment property
     *  @return -1: unknown, 0: no auto increment, 1: auto increment
     */
    public short getAutoIncrement() {
        return autoIncrement;
    } // getAutoIncremtent
    /** Sets the auto increment property of the column
     *  @param autoIncrement = -1: unknown, 0: no auto increment, 1: auto increment
     */
    public void setAutoIncrement(short autoIncrement) {
        this.autoIncrement = autoIncrement;
    } // setAutoIncrement
    //----------------
    /** SQL data type codes for the column */
    private int dataType;
    /** Gets the SQL data type of the column
     *  @return one of the field constants in java.sql.Types
     */
    public int getDataType() {
        return dataType;
    } // getDataType
    /** Sets the SQL data type of the column
     *  @param dataType one of the field constants in java.sql.Types
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    } // setDataType
    //----------------
    /** number of decimal places for numeric fields */
    private int decimal;
    /** Gets the number of decimal places
     *  @return 0, 2 ...
     */
    public int getDecimal() {
        return decimal;
    } // getDecimal
    /** Sets the number of decimal places
     *  @param decimal 0, 2 ...
     */
    public void setDecimal(int decimal) {
        this.decimal = decimal;
    } // setDecimal

    /** default value for a column */
    private String defaultValue;
    /** Gets the default value
     *  @return some string value
     */
    public String getDefault() {
        return defaultValue;
    } // getValue
    /** Sets the default value
     *  @param defaultValue some string value
     */
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    } // setDefault
    //----------------
    /** Direction for parameters of Stored Procedures: "in" ('i'), "out" ('o') or "inout" ('b') */
    private char dir;
    /** Gets the direction for parameters of Stored Procedures
     *  @return dir, one of 'i' (in), 'b' (both, inout) or 'o' (out)
     */
    public char getDir() {
        return dir;
    } // getDir
    /** Sets the direction for parameters of Stored Procedures
     *  @param dir one of 'i' (in), 'b' (both, inout) or 'o' (out)
     */
    public void setDir(char dir) {
        this.dir = dir;
    } // setDir
    /** Gets the direction for parameters of Stored Procedures
     *  @return one of "in", "out" or "inout"
     */
    public String getDirection() {
        String result = "in";
        switch (dir) {
            default:
            case 'i': result = "in";    break;
            case 'o': result = "out";   break;
            case 'b': result = "inout"; break;
        } // switch
        return result;
    } // getDirection
    /** Sets the direction for parameters of Stored Procedures
     *  @param direction one of "in", "inout", "out"
     */
    public void setDirection(String direction) {
        if (direction != null && direction.length() > 0) {
            if (false) {
            } else if (direction.equals("in"   )) {
                dir = 'i';
            } else if (direction.equals("inout")) {
                dir = 'b';
            } else if (direction.equals("out"  )) {
                dir = 'o';
            } else { // ignore wrong direction code?
                dir = 'i';
            }
        } // direction is filled
    } // setDirection
    //----------------
    /** expression for SQL SELECT */
    private String expr;
    /** Gets the SQL expression
     *  @return string to be used to select the contents of this column
     */
    public String getExpr() {
        return expr;
    } // getExpr
    /** Sets the SQL expression
     *  @param expr string to be used to select the contents of this column
     */
    public void setExpr(String expr) {
        this.expr = expr;
    } // setExpr
    //----------------
    /** specification of an HTML link to another servlet */
    private String href;
    /** Gets the HTML link to another servlet
     *  @return link with parameters
     */
    public String getHref() {
        return href;
    } // getHref
    /** Sets the HTML link to another servlet
     *  @param href link with parameter name for current column
     */
    public void setHref(String href) {
        this.href = href.replaceAll("\\s",""); // whitespace in URL makes no sense
    } // setHref
    //----------------
    /** the value of an HTML link to another servlet, with filled parameters */
    private String hrefValue;
    /** Gets the value of the HTML link to another servlet, with filled parameters
     *  @return link with parameters
     */
    public String getHrefValue() {
      return this.hrefValue;
    } // getHrefValue
    /** Sets the value of the HTML link to another servlet, with filled parameters
     *  @param hrefValue URL with filled parameters for the current column
     */
    public void setHrefValue(String hrefValue) {
        String result = hrefValue;
        if (hrefValue != null) {
            int fragPos = hrefValue.indexOf("%23"); // '#'
            if (fragPos < 0) {
                fragPos = hrefValue.indexOf('#');
            }
            if (fragPos >= 0) { // with fragment
                System.err.println("fragmented: " + hrefValue);
                int ampPos = hrefValue.indexOf('&', fragPos);
                if (false && ampPos >= 0) { // there are parameters behind '#': move fragment to the end
                    result = hrefValue.substring(0, fragPos)
                           + hrefValue.substring(ampPos)
                           + hrefValue.substring(fragPos, ampPos).replaceAll("%23", "#");
                } // move fragment
            } // with '#'
        } // != null
        this.hrefValue = result;
    } // setHrefValue
    //----------------
    /** index in an output (HTML) table: 0, 1, 2 */
    private int index;
    /** Gets the index in the HTML table
     *  @return index &gt;= 0
     */
    public int getIndex() {
        return index;
    } // getIndex
    /** Sets the index in the HTML table
     *  @param index &gt;= 0
     */
    public void setIndex(int index) {
        this.index = index;
    } // setIndex
    //----------------
    /** key for update/delete actions */
    private String key;
    /** Gets the key
     *  @return comma separated string of key column names
     */
    public String getKey() {
        return key;
    } // getKey
    /** Sets the key
     *  @param key comma separated string of key column names
     */
    public void setKey(String key) {
        this.key = key;
    } // setKey
    //----------------
    /** Label for table header */
    private String label;
    /** Gets the label
     *  @return string to be displayed in table header
     */
    public String getLabel() {
        return label;
    } // getLabel
    /** Sets the label
     *  @param label string to be displayed in table header
     */
    public void setLabel(String label) {
        this.label = label;
    } // setLabel
    //----------------
    /** HTML link to another specification */
    private String link;
    /** Gets the HTML link to another specification
     *  @return link with parameter name
     */
    public String getLink() {
        return link;
    } // getLink
    /** Sets the HTML link to another specification
     *  @param link link with parameter name for current column
     */
    public void setLink(String link) {
        this.link = link.replaceAll("\\s",""); // whitespace in URL makes no sense
    } // setLink
    //----------------
    /** name of the column */
    private String name;
    /** Gets the name of the column
     *  @return name of the column
     */
    public String getName() {
        return name;
    } // getName
    /** Sets the name of the column
     *  @param name name of the column
     */
    public void setName(String name) {
        this.name = name;
    } // setName
    //----------------
    /** whether the (SQL) column is nullable */
    private boolean nullable;
    /** Determines whether the column is nullable
     *  @return true if the (SQL) column may contain NULL
     */
    public boolean isNullable() {
        return nullable;
    } // isNullable
    /** Sets the nullable property
     *  @param nullable whether the (SQL) column may contain NULL
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    } // setNullable
    //----------------
    /** whether the current SQL column needs some special processing, and which one
     */
    private String pseudo;
    /** Gets the pseudo property of a column
     *  @return (for example) "style", "image"
     */
    public String getPseudo() {
        return pseudo;
    } // getPseudo
    /** Sets the pseudo property of a column
     *  @param pseudo attribute, for example "style", "image"
     */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    } // setPseudo
    //----------------
    /** Remark / comment for the column */
    private String remark;
    /** Gets the remark
     *  @return comment for the column
     */
    public String getRemark() {
        return remark;
    } // getRemark
    /** Sets the remark.
     *  @param remark comment for the column
     */
    public void setRemark(String remark) {
        this.remark = remark;
    } // setRemark
    //----------------
    /** CSS style or class for HTML data cell value */
    private String style;
    /** Gets the CSS style or class from CSS file
     *  @return (for example) "visible", "border:0"
     *  A class is assumed when the parameter contains a ":".
     */
    public String getStyle() {
        return style;
    } // getStyle
    /** Sets the CSS style or class
     *  @param style or class from CSS file (for example) "visible", "border:0".
     *  A class is assumed when the parameter contains a ":".
     */
    public void setStyle(String style) {
        this.style = style;
    } // setStyle
    //----------------
    /** SQL data type names for the column */
    private String typeName;
    /** Gets the SQL data type name of the column
     *  @return a string like "VARCHAR", "TIMESTAMP" etc.
     */
    public String getTypeName() {
        return typeName;
    } // getTypeName
    /** Sets the SQL data type of the column
     *  @param typeName a string like "VARCHAR", "TIMESTAMP" etc.
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    } // setTypeName
    //----------------
    /** String value of the column */
    private String value;
    /** Gets the value
     *  @return string value of the column
     */
    public String getValue() {
        return value;
    } // getValue
    /** Sets the value
     *  @param value string value of the column
     */
    public void setValue(String value) {
        this.value = value;
    } // setValue
    //----------------
    /** width of the column (in characters), or 0 if not defined */
    private int width;
    /** Gets the width of the column (in characters)
     *  @return 0-n
     */
    public int getWidth() {
        return width;
    } // getWidth
    /** Sets the width of the column (in characters)
     *  @param width 0-n
     */
    public void setWidth(int width) {
        this.width = width;
    } // setWidth
    /** Sets the width of the column (in characters)
     *  @param width 0-n as a string
     */
    public void setWidth(String width) {
        if (width != null && width.length() > 0) {
            try {
                this.width = Integer.parseInt(width);
            } catch (Exception exc) {
                // ignore any wrong value
            }
        }
    } // setWidth
    //===============================================
    /** Fills missing properties of a column from the
     *  database metadata in the result set.
     *  @param dbResults column metadata result set with current row for this column
     *  as obtained with <code>dbMetaData.getColumns</code>
     */
    public void completeColumn(ResultSet dbResults) {
        try {
            // normalize column type
            String typeName = dbResults.getString("TYPE_NAME").toUpperCase();
            int    dataType = dbResults.getInt   ("DATA_TYPE");
            if (false) {
            } else if (typeName.equals    ("DATE")) {
                dataType = Types.DATE;
            } else if (typeName.startsWith("VARCHAR")) {
                typeName = "VARCHAR"; // normalize Oracle's "VARCHAR2"
                dataType = Types.VARCHAR;
            } else if (typeName.startsWith("TIMESTAMP")) {
                typeName = "TIMESTAMP"; // remove Oracle's "(6)"
                dataType = Types.TIMESTAMP;
            } else if (typeName.startsWith("NUMBER")) {
                typeName = "DECIMAL"; // normalize if Oracle
                dataType = Types.DECIMAL;
            }
            this.setTypeName(typeName);
            this.setDataType(dataType);
            if (dataType == Types.DECIMAL) {
                int decimalDigits = dbResults.getInt("DECIMAL_DIGITS");
                if (decimalDigits != 0) {
                    if (this.getDecimal() == 0) {
                        this.setDecimal(decimalDigits);
                    }
                }
            } // DECIMAL

            int width = dbResults.getInt("COLUMN_SIZE");
            if (this.getWidth() == 0) {
                this.setWidth(width);
            } // width

            String align = this.getAlign();
            if (align == null) { // determine alignment
                align = "left";
                if (false) {
                } else if (typeName.indexOf("DATE") >= 0) {
                } else if (typeName.indexOf("TIME") >= 0) {
                } else if (typeName.indexOf("CHAR") >= 0) {
                } else if (typeName.indexOf("LOB" ) >= 0) { // LOB, BLOB, CLOB
                } else { // numerical types
                    align = "right";
                }
                this.setAlign(align);
            } // determine alignment

            String name = this.getName();
            if (name == null) {
                name = dbResults.getString("COLUMN_NAME");
                this.setName(name);
            } // name

            this.setNullable(dbResults.getString("IS_NULLABLE").equalsIgnoreCase("NO"));
            this.setDefault (dbResults.getString("COLUMN_DEF"));

            String remark = this.getRemark();
            if (remark == null) {
                remark = dbResults.getString("REMARKS");
                this.setRemark(remark);
            } // name

            if (debug >= 2) {
                System.err.println("TableColumn.completeColumn: " + toString());
            }
        } catch (Exception exc) {
            Logger.getLogger(TableColumn.class.getName()).error(exc.getMessage(), exc);
        }
    } // completeColumn

    /** Gets a textual representation of the column attributes
     */
    public String toString() {
        StringBuffer result = new StringBuffer(128);
        result.append("<column name=\""     + getName()
                + "\" index=\""             + getIndex()
                + "\" typeName=\""          + getTypeName()
                + "\" dataType=\""          + getDataType()
                + "\" width=\""             + getWidth()
                + "\" />");
        return result.toString();
    } // toString

} // TableColumn
