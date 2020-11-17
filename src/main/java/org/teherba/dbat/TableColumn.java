/*  TableColumn - bean with properties of an abstract column
    @(#) $Id$
    2020-05-04: attribute target= in <col>
    2019-07-12: output NULL for nullText != 0
    2017-10-02: no message for "Fragmented"
    2017-05-27: javadoc 1.8
    2016-12-09: avoid empty Javascrpt function calls
    2016-02-10: values may not be null if wrapped
    2014-11-11: wrap="verbatim"
    2014-11-10: s|getHrefValue -> s|getWrappedValue; SQLAction.separateURLfromValue now is this.separatedWrapperAndValue
    2014-11-06: additional property wrap
    2014-01-15: inactivate fragment logic
    2013-02-01: getWrappedValue: move fragment to the end
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
import  java.net.URLEncoder;
import  java.sql.ResultSet;
import  java.sql.Types;
import  java.util.regex.Pattern;
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
        name        = "";
        pseudo      = "";
        remark      = "";
        wrap        = null;
    */
        autoIncrement = -1; // unknown
        dataType    = NO_TYPE;
        decimal     = 0;
        defaultValue= null; // no default value
        setDir('o'); // assume "out" parameter
        expr        = "";
        wrappedValue  = null;
        nullable    = true;
        style       = "";
        target      = null; // no target attribute
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
        result.wrappedValue    = this.wrappedValue;
        result.index        = this.index    ;
        result.key          = this.key      ;
        result.label        = this.label    ;
        result.name         = this.name     ;
        result.nullable     = this.nullable ;
        result.pseudo       = this.pseudo   ;
        result.remark       = this.remark    ;
        result.style        = this.style    ;
        result.target       = this.target   ;
        result.typeName     = this.typeName ;
        result.value        = this.value    ;
        result.width        = this.width    ;
        result.wrap         = this.wrap     ;
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
    private String wrappedValue;
    /** Gets the value of the HTML link to another servlet, with filled parameters
     *  @return link with parameters
     */
    public String getWrappedValue() {
      return this.wrappedValue;
    } // getWrappedValue
    /** Sets the value of the HTML link to another servlet, with filled parameters
     *  @param wrappedValue URL with filled parameters for the current column
     */
    public void setWrappedValue(String wrappedValue) {
        String result = wrappedValue;
        if (wrappedValue != null) {
            int fragPos = wrappedValue.indexOf("%23"); // '#'
            if (fragPos < 0) {
                fragPos = wrappedValue.indexOf('#');
            }
            if (fragPos >= 0) { // with fragment
                // System.err.println("fragmented: " + wrappedValue);
                int ampPos = wrappedValue.indexOf('&', fragPos);
                if (false && ampPos >= 0) { // there are parameters behind '#': move fragment to the end
                    result = wrappedValue.substring(0, fragPos)
                           + wrappedValue.substring(ampPos)
                           + wrappedValue.substring(fragPos, ampPos).replaceAll("%23", "#");
                } // move fragment
            } // with '#'
        } // != null
        this.wrappedValue = result;
    } // setWrappedValue
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
    /** Target attribute for &lt;a href="   " target="   "&gt; links
     */
    private String target;
    /** Gets the target property of a column
     *  @return (for example) "_blank" or null for no target
     */
    public String getTarget() {
        return target;
    } // getTarget
    /** Sets the target attribute
     *  @param target attribute, for example "_blank" or null for no target
     */
    public void setTarget(String target) {
        this.target = target;
    } // setTarget
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
    //----------------
    /** specification of a Javascript function call */
    private String wrap;
    /** Gets the Javascript function call
     *  @return link with parameters
     */
    public String getWrap() {
        return wrap;
    } // getWrap
    /** Sets the Javascript function call
     *  @param wrap link with parameter
     */
    public void setWrap(String wrap) {
        this.wrap = wrap.replaceAll("\\s",""); // whitespace in URL makes no sense
    } // setWrap
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

    /** Takes a {@link TableColumn}'s attributes and an SQL SELECT column value which may be a
     *  concatenated list of parameters, and stores into the {@link TableColumn}:
     *  <ol>
     *  <li>an URL with the parameters derived from the SELECT value,</li>
     *  <li>the last partial value to be shown in the (HTML) cell.</li>
     *  </ol>
     *  @param values content of the data cell (from SQL SELECT), maybe a concatenation
     *  of parameters separated by some separator character.
     *  @param targetEncoding encoding to be used for output
     *  @param escapingRule from {@link org.teherba.dbat.format.BaseTable},
     *  how character entities should be handled
     *  @param nullText from {@link Configuration}, how null values should be output
     *
     *  A (relative) link to another specification with parameter(s) must be specified as
     *  <pre>
     *      spec=basename&amp;amp;name1[sep1&amp;amp;name2[sep2[&amp;amp;name3...]]]
     *  </pre>
     *  If there is more than one parameter, non-word separator strings <em>sepi</em> must be noted
     *  between the parameter names. A trailing "=" is ignored for compatibility reasons.
     *  The <em>values</em> string is split with the aid of the separator string(s)
     *  specified in the <em>link</em> string.
     *
     *  Only the last value is put into the output cell (if the column has no "pseudo" attribute),
     *  and any additional values before are inserted into the URL parameter list only.
     *  As a special case, the value <em>null</em> is passed through.
     */
    public void separateWrappedValue(String values, String targetEncoding, int escapingRule, int nullText) {
        if (values == null && nullText == 0) {
            values = "";
        }
        String displayValue = null;
        String href = this.getHref();
        String wrap = this.getWrap();
        String typeName = this.getTypeName();
        if (false) {

        } else if (wrap != null) { // e.g. wrap="javascript:," values="function,a,b,c" for comma separator
            if (false) {
            } else if (wrap.startsWith("javascript:")) {
                String separator = wrap.substring(wrap.indexOf(':') + 1); // ":" + sep were appended in SpecificationHandler
                String[] parts = values.split(Pattern.quote(separator));
                int ipart = 0;
                StringBuffer buffer = new StringBuffer(128);
                buffer.append(parts[ipart ++]); // function name
                buffer.append('(');
                while (ipart < parts.length) {
                    if (ipart > 1) {
                        buffer.append(',');
                    }
                    if (false) {
                    } else if (typeName.startsWith("INT") || typeName.startsWith("DEC")) { // numerical - without quotes in Javascript
                        buffer.append(parts[ipart]);
                    } else { // with quotes in Javascript
                        buffer.append('"');
                        buffer.append(parts[ipart]);
                        buffer.append('"');
                    }
                    displayValue = parts[ipart];
                    ipart ++;
                } // while ipart
                buffer.append(");");
                this.setWrappedValue(ipart >= 2 ? buffer.toString() : null);
                if (displayValue == null) {
                    displayValue = "";
                }
                // displayValue = "";
            } else if (wrap.startsWith("verbatim:")) { // HTML in the SQL table cell's value is passed through without modification
                escapingRule = 0;
                this.setWrappedValue(null);
                displayValue = values;
            } else { // unknown wrap - ignore
                this.setWrappedValue(null);
                displayValue = values;
            }
            // wrap != null

        } else if (href != null) { // for compatibility: split value and merge it with URL
            String oldURL = href; // cannot be null
            StringBuffer newURL = new StringBuffer(128);
            int lampos1 = oldURL.indexOf('&'); // leading ampersand introduces first parameter
            if (lampos1 < 0) { // no parameters
                newURL.append(oldURL); // copy whole oldURL to URL, remove whitespace
                displayValue = values;
            } else { // lampos1 >= 0: with parameters
                // keep the last partial value as displayValue, and move any additional parameters into the URL
                int vstart = 0;
                newURL.append(oldURL.substring(0, lampos1)); // copy initial "specname" or "servlet?spec=specname"
                while (lampos1 < oldURL.length()) { // process all parameters: "&name=val" (ignored),  or "&name[sep]", "&name="
                    int lampos2 = oldURL.indexOf('&', lampos1 + 1); // is there any second ampersand
                    if (lampos2 < 0) {
                        lampos2 = oldURL.length(); // behind entire string
                    }
                    String parm = oldURL.substring(lampos1 + 1, lampos2);
                    int eqpos = parm.indexOf('=');
                    int plast = parm.length() - 1;
                    if (plast <= 0) { // single '&' - ignore
                    } else if (eqpos >= 0 && eqpos < plast) {
                        // foreign "&name=value" pair - copy it unchanged
                        newURL.append('&');
                        newURL.append(parm);
                    } else {
                        while (plast >= 0 && ! Character.isLetterOrDigit(parm.charAt(plast))) {
                            // backspace over non-word characters
                            plast --;
                        } // while backspacing over separator
                        if (plast < 0) {
                        /* silently ignore this:
                            IllegalArgumentException exc =
                                    new IllegalArgumentException("empty parameter name at " + (lampos1 + 1));
                            log.error(exc.getMessage(), exc);
                        */
                            plast = parm.length();
                        } else { // plast >= 0 points to last character in name
                            String parmName = parm.substring(0, plast + 1);
                            String sep      = parm.substring(   plast + 1);
                            if (sep.length() == 0) {
                                sep = "=";
                            }
                            // now extract the corresponding portion from 'values'
                            if (values != null) {
                                int vsepos = values.indexOf(sep, vstart);
                                if (vsepos < 0) { // sep not found
                                    vsepos = values.length(); // ignore that problem, eat the rest of the string
                                    sep = ""; // because of " + sep.length()" below
                                }
                                displayValue = values.substring(vstart, vsepos)
                                        .trim()
                                        ;
                                vstart = vsepos + sep.length();
                                newURL.append('&');
                                newURL.append(parmName);
                                newURL.append('=');
                                try {
                                    newURL.append(URLEncoder.encode(displayValue, targetEncoding)
                                            .replaceAll("%C2%", "%") // this is a crude patch for Unicode problems
                                            );
                                } catch (Exception exc) {
                                    newURL.append(displayValue); // accept a bad URL
                                }
                            } else { // values == null
                                displayValue = null;
                            }
                        } // last >= 0
                    } // not foreign
                    lampos1 = lampos2;
                    lampos2 = oldURL.indexOf('&', lampos1 + 1);
                } // while lampos1
            } // with parameters
            this.setWrappedValue(newURL.toString());
            if (debug >= 2) System.err.println("newURL=\"" + newURL.toString() + "\", displayValue=\"" + displayValue + "\"");
            // href != null
        } else  {// plain value, we are done with it
            this.setWrappedValue(null);
            displayValue = values;
        }   // done with it

        if (displayValue != null) {
            switch (escapingRule) {
                case 0: // no escaping
                    break;
                default:
                case 1:
                    displayValue = displayValue
                            .replaceAll("&", "&amp;")
                            .replaceAll(">", "&gt;")
                            .replaceAll("<", "&lt;")
                            .replaceAll("&amp;amp", "&amp;")
                            ;
                    break;
                case 2:
                    displayValue = displayValue
                            .replaceAll("\'", "&apos;")
                            .replaceAll("&amp;amp", "&amp;")
                            ;
                    break;
                case 3:
                    displayValue = displayValue
                            .replaceAll("&", "&amp;")
                            .replaceAll(">", "&gt;")
                            .replaceAll("<", "&lt;")
                            .replaceAll("\'", "&apos;")
                            .replaceAll("&amp;amp", "&amp;")
                            ;
                    break;
                case 4:
                    if (this.getExpr().indexOf('<') < 0) { // then like 1
                    displayValue = displayValue
                            .replaceAll("&", "&amp;")
                            .replaceAll(">", "&gt;")
                            .replaceAll("<", "&lt;")
                            .replaceAll("&amp;amp", "&amp;")
                            ;
                    }
                    break;
            } // switch escapingRule
            // displayValue != null
        } else { // displayValue == null
            switch (nullText) {
                case 0:
                    displayValue = "";
                    break;
                case 1:
                    displayValue = "null";
                    break;
                default: // -1 = do nothing
                    break;
            } // switch nullText
        } // displayValue == null
        this.setValue(displayValue);

        if (debug >= 2) System.err.println("separateWrappedValue, wrappedValue=\"" + this.getWrappedValue() + "\", value=\"" + this.getValue() + "\"");
    } // separateWrappedValue

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
