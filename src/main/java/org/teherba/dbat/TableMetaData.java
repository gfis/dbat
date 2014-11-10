/*  TableMetaData - array list of {@link TableColumn}s and associated methods
    @(#) $Id$
    2012-01-25: fetchTarget = null | "parm"
    2011-09-30: if (column.getRemark() == null) { column.setRemark(stLabel);
    2011-08-25: addColumn(name, value)
    2011-08-16: toString() removed
    2011-05-04: without parameter rowCount
    2010-09-13: aggregate and group change features
    2010-03-16, Georg Fischer: copied from ColumnList
 */
/*
 * Copyright 2008 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.dbat.Configuration;
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.format.BaseTable;
import  java.sql.DatabaseMetaData;
import  java.sql.ResultSet;
import  java.sql.ResultSetMetaData;
import  java.util.ArrayList;
import  org.apache.log4j.Logger;

/** Bean for an array list of {@link TableColumn}s
 *  which is used to map database (SQL) columns to presentation (HTML, XML) table columns.
 *  The position of the column in the table's rows is not neccessarily the same as
 *  the position in the corresponding SQL result set, nor is it the same as the
 *  column position in the resulting HTML table, because of:
 *  <ul>
 *  <li>pseudo columns used to set a style on the next HTML table
 *  =&gt; SQL result set has more columns than HTML</li>
 *  <li>pseudo columns used to display a (delete, update) HTML button
 *  =&gt; SQL result set has less columns than HTML</li>
 *  </ul>
 *  {@link #columnList} is used to store the attributes of the column, and
 *  its string values, for the current row.
 *  {@link #oldValueList} stores the values of the previous row.
 *  There are methods for
 *  <ul>
 *  <li>handling the aggregation (concatenation) of the values in a specified column</li>
 *  <li>determination of a group change for a set of columns, and subsequent output of a header row</li>
 *  </ul>
 *  @author Dr. Georg Fischer
 */
public class TableMetaData {
    public final static String CVSID = "@(#) $Id$";
    /** Debugging switch */
    private int debug = 0;

    /** log4j logger (category), inherited by all subclasses */
    protected Logger log;

    /** Indicates the type of a control change */
    private static enum Change
            { NONE      // no control change
            , HEADING   // control change which causes a new heading
            , MINOR     // control change which causes no new heading, but still collapses the column values
            };
    /** Name of class in stylesheet which causes visible   values in control change columns */
    private static final String VISIBLE   = "visible";
    /** Name of class in stylesheet which causes invisible values in control change columns */
    private static final String INVISIBLE = "invisible";
    /** undefined table name */
    private static final String UNDEFINED_TABLE = "table_not_specified";
    /** Aggregation separator which indicates pivot matrix output */
    private static final String PIVOT    = "pivot";
    /** into="param" Attribute which indicates parameter filling */
    private static final String PARAM    = "param";

    /** Base array for columns' properties (of type {@link TableColumn}) */
    public ArrayList/*<1.5*/<TableColumn>/*1.5>*/ columnList;
    /** Array for column values of previous row, for group (control change) and aggregate features */
    public ArrayList/*<1.5*/<TableColumn>/*1.5>*/ oldValueList;
    /** State of the attributes; 0 = empty, 1 = partially filled, 2 = complete */
    private int fillState;
    /** Number of leading <em>groupColumn</em>s (if any) which cause a new header line, default = 1 */
    private int numHeadingColumns;

    //======================================
    // Bean properties, getters and setters
    //======================================

    /** Index of a column which should be aggregated */
    private int aggregateIndex;
    /** Values of {@link #aggregateIndex}:
     *  <ul>
     *  <li>&gt;= 0 : index of the aggregate column, no non-aggregate column change</li>
     *  <li>-1 : non-aggregate column change</li>
     *  <li>-2 : {@link #oldValueList} was not initialized, but feature is set - first row</li>
     *  <li>-3 : feature not set</li>
     *  <li>-4 : display 1 data row vertically with headers prefixed</li>
     *  <li>-5 : do not display any row, but append the values in the parameter map</li>
     *  </ul>
     */
    public  static final int AGGR_CHANGED  = -1;
    public  static final int AGGR_EMPTY    = -2;
    public  static final int AGGR_NOT_SET  = -3;
    public  static final int AGGR_VERTICAL = -4;
    public  static final int AGGR_PARAMS   = -5;

    public int getAggregateIndex() {
        return aggregateIndex;
    } // getAggregateIndex
    /** Sets the index of a column which should be aggregated
     *  @param aggregateIndex index >= 0
     *  or special negative values as described for {@link #getAggregateIndex}
     */
    public void setAggregateIndex(int aggregateIndex) {
        this.aggregateIndex = aggregateIndex;
    } // setAggregateIndex

    /** Name of column which should be aggregated, or null if none */
    private String aggregationName;
    /** Gets the aggregation column name
     *  @return name of a column to be aggregated, or null for none
     */
    public String getAggregationName() {
        return aggregationName;
    } // getAggregationName
    /** Sets the aggregation column name
     *  @param name name of a column to be aggregated, or null for none
     */
    public void setAggregationName(String name) {
        this.aggregationName = name;
    } // setAggregationName

    /** Separator string for column value aggregation, or "pivot" */
    private String aggregationSeparator;
    /** Gets the aggregation separator
     *  @return string which separates aggregated column values, or "pivot"
     */
    public String getAggregationSeparator() {
        return aggregationSeparator;
    } // getAggregationSeparator
    /** Sets the aggregation separator
     *  @param separator string which separates aggregated column values, or "pivot"
     */
    public void setAggregationSeparator(String separator) {
        this.aggregationSeparator = separator;
    } // setAggregationSeparator
    /** Tells whether we want pivot matrix output
     *  @return true if a pivot matrix is desired, false if not
     */
    public boolean isPivot() {
        return aggregationSeparator.equals(PIVOT);
    } // isPivot

    /** Sets the name of a column which should be aggregated,
     *  and a string (maybe empty) which separates the aggregated column values.
     *  @param aggregateName name of a columns which should be aggregated, or null if none is to be set
     *  @param aggregationSeparator string which separates the aggregated column values (default " "), or "pivot"
     *  @return index of the aggregate column (0 based)
     */
    public int setAggregateColumn(String aggregateName, String aggregationSeparator) {
        this.aggregationSeparator = (aggregationSeparator != null) ? aggregationSeparator : " ";
        // aggregateIndex = AGGR_NOT_SET; // assume name not found, feature not set
        if (aggregateName != null) { // feature is set
            int icol = 0;
            while (aggregateIndex < 0 && icol < columnList.size()) {
                TableColumn column = columnList.get(icol);
                String name   = column.getName ();
                String label  = column.getLabel();
                String pseudo = column.getPseudo();
                if  (pseudo == null
                    &&  (  (name  != null && name .equals(aggregateName))
                        || (label != null && label.equals(aggregateName))
                        )
                    ) {
                    aggregateIndex = icol;
                }
                if (debug >= 1) {
                    System.err.println("setAggregateColumn: " + columnList.get(icol).getName()
                            + " ? " + aggregateName + " => " + aggregateIndex);
                }
                icol ++;
            } // while icol
        } // feature is set
        return aggregateIndex;
    } // setAggregateColumn

    /** Sets the name of a column which should be aggregated,
     *  and a string (maybe empty) which separates the aggregated column values.
     *  Convenience method without parameters (were previously set).
     *  @return index of the aggregate column (0 based)
     */
    public int setAggregateColumn() {
        return setAggregateColumn(this.aggregationName, this.aggregationSeparator);
    } // setAggregateColumn

    /** descriptive text for count of rows: [0] is singular, [1] is plural (also for 0 rows)*/
    private String  counterDesc[]; // always 3 elements: 0 = singular, 1 = plural, 0 = zero rows

    /** Gets the descriptive text for the row counter.
     *  @param rowCount number of rows retrieved by the SELECT statement
     *  @return a word in the proper numerus
     */
    public String getCounterDesc(int rowCount) {
        int ix = 0;
        switch (rowCount) {
            case 1:
                ix = 0;
                break;
            case 0:
                ix = 2;
                break;
            default:
                ix = 1;
                break;
        } // switch rowCount
        return counterDesc[ix];
    } // getCounterDesc

    /** Sets the descriptive text for the row counter, a noun in singular or plural
     *  @param partList text to be shown for 0, 1 or more rows,
     *  empty string for default ("rows"),
     *  null if no counter should be shown under the table.
     *  The parameter consists of up to 3 word particles, where
     *  <ul>
     *  <li>the 1st is a noun (in singular form) which is used for a result of 1 row,</li>
     *  <li>the optional 2nd particle is used for more than 1 row in the result set,
     *  or for zero rows if there is no 3rd word; the particĺe is appended to the first
     *  noun if its length is less than that of the noun,</li>
     *  <li>the optional 3rd word is used for a result of 0 rows, or - if the word is empty -
     *  the counter is suppressed.</li>
     *  </ul>
     */
    public void setCounterDesc(String partList) {
        if (partList == null) {
            counterDesc = new String[] { null, null, null };
        } else { // non-null string - split it on commas
            int cpos = partList.indexOf(','); // position of 1st comma
            if (cpos < 0) { // no "," - only 1 variant
                counterDesc = new String[] { partList, partList, "0 " + partList };
            } else { // "row,s," or "Zeile,n," or "Mann,Männer"
                int cpos2 = partList.indexOf(',', cpos + 1); // position of 2nd comma
                if (cpos2 < 0) { // only 1 comma
                    counterDesc = new String[]
                            { partList.substring(0, cpos)
                            , partList.substring(cpos + 1)
                            , null
                            };
                    if (counterDesc[1].length() < counterDesc[0].length()) { // plural shorter => append to singular
                        counterDesc[1] = counterDesc[0] + counterDesc[1];
                    }
                    counterDesc[2] = "0 " + counterDesc[1]; // "0 Männer"
                } else { // there is a 2nd comma
                    counterDesc = new String[]
                            { partList.substring(0, cpos)
                            , partList.substring(cpos + 1, cpos2)
                            , cpos2 < partList.length() - 1 ? partList.substring(cpos2 + 1) : null
                            };
                    if (counterDesc[1].length() < counterDesc[0].length()) { // plural shorter => append to singular
                        counterDesc[1] = counterDesc[0] + counterDesc[1];
                    }
                } // 2nd comma
            } // with ","
        } // partList nonempty
    } // setCounterDesc

    /** Comma separated list (without spaces!) of column names which participate in control change determination */
    private String groupColumns;
    /** Gets the column names which determine a control change and the
     *  corresponding output of a heading line with column labels
     *  @return comma separated list of column names, or null if none was set
     */
    public String getGroupColumns() {
        return groupColumns;
    } // getGroupColumns
    /** Sets the column names which determine a control change and the
     *  corresponding output of a heading line with column labels
     *  @param groupColumns comma separated list of column names, or null if none is to be set
     */
    public void setGroupColumns(String groupColumns) {
        this.groupColumns = "," + (groupColumns == null ? "" : groupColumns) + ",";
    } // setGroupColumns

    /** Unique identification of the table in multi-select activations */
    private String identifier;
    /** Gets the unique identifier of the table.
     *  @return an identifier unique over one activation (command or XML specification)
     */
    public String getIdentifier() {
        return identifier;
    } // getIdentifier
    /** Sets the unique identifier of the table.
     *  @param id identifier, must be unique over one activation (command or XML specification)
     *  or null if this feature is not desired
     */
    public void setIdentifier(String id) {
        this.identifier = id;
    } // setIdentifier

    /** Attribute which tells where to store the fetch results: null (normal output table), or "parm" = map of parameters */
    private String fetchTarget;
    /** Tells where to store the fetch results
     *  @return null (normal output table), or "parm" = map of parameters
     */
    public String getFetchTarget() {
        return fetchTarget;
    } // getFetchTarget
    /** Sets the property which tells where to store the fetch results
     *  @param fetchTarget null (normal output table), or "parm" = map of parameters
     */
    public void setFetchTarget(String fetchTarget) {
        this.fetchTarget = fetchTarget;
    } // setFetchTarget

    /** Schema (user, qualifier) part of the table's name */
    private String schema;
    /** Base name of the table (without schema) */
    private String tableBaseName;
    /** Fully qualified (with schema/user) name of the table */
    private String tableName;

    /** Gets the schema (user) of the underlying table
     *  @return schema
     */
    public String getSchema() {
        return schema;
    } // getSchema

    /** Parses a table name into schema and basename
     *  @param defaultSchema default for the schema if the table's name contains none
     *  @param tableName fully qualified (with schema/user) name of the table
     */
    public void parseTableName(String defaultSchema, String tableName) {
        try {
            this.tableName      = tableName;
            this.tableBaseName  = tableName; // .toUpperCase(); // no! u
            // schema = "";
            int dotPos = tableBaseName.indexOf('.');
            if (dotPos >= 0) { // with explicit schema in tableName
                schema        = tableBaseName.substring(0, dotPos);
                tableBaseName = tableBaseName.substring(dotPos + 1);
            } else {
                if (defaultSchema != null && defaultSchema.length() > 0) {
                    schema = defaultSchema;
                }
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // parseTableName

    /** Gets the table's base name (without schema/user)
     *  @return base name without qualifier
     */
    public String getTableBaseName() {
        return tableBaseName;
    } // getTableBaseName

    /** Gets the table's fully qualified name (with schema/user)
     *  @return full name with qualifier
     */
    public String getTableName() {
        return tableName;
    } // getTableName

    /** Sets the table's name, with optional schema/user.
     *  If the schema is not specificied, it is taken from
     *  the default in the configuration.
     *  @param tableName name with optional qualifier
     */
    public void setTableName(String defaultSchema, String tableName) {
        parseTableName(defaultSchema, tableName);
    } // setTableName

    /** whether to print header and trailer */
    private boolean withHeaders;
    /** Tells whether to output table header rows
     *  @return false if no table header rows should be written (default: true)
     */
    public boolean isWithHeaders() {
        return withHeaders;
    } // isWithHeaders
    /** Defines whether to output table header rows
     *  @param withHeaders false if no table header rows should be written  (default: true)
     */
    public void setWithHeaders(boolean withHeaders) {
        this.withHeaders = withHeaders;
    } // setWithHeaders

    //=============================
    // Constructors
    //=============================

    /** No-args Constructor
     */
    public TableMetaData() {
        log = Logger.getLogger(TableMetaData.class.getName());
        columnList              = new ArrayList/*<1.5*/<TableColumn>/*1.5>*/(16); // empty so far
        schema                  = "";
        tableBaseName           = UNDEFINED_TABLE;
        tableName               = tableBaseName;
        setAggregationName      (null);
        setAggregationSeparator (",");
        setAggregateIndex       (this.AGGR_NOT_SET);
        setCounterDesc          (null); // default = not set
        setIdentifier           (null);
        setFillState            (0); // empty
        setAggregateIndex       (AGGR_NOT_SET);   // feature not set
        groupColumns            = null; // feature not set
        numHeadingColumns       = 1;
        oldValueList            = null; // only needed for group and aggregate features
    } // no-args Constructor

    /** Constructor from configuration. Some of their properties
     *  are taken as defaults, but can be overwritten for this table serialization.
     *  @param config overall configuration of a session
     */
    public TableMetaData(Configuration config) {
        this();
        setWithHeaders(config.isWithHeaders());
    } // Constructor

    //==========================
    // Meta data completion
    //==========================

    /** Fill all column descriptions from database metadata
     *  @param dbMetaData metadata for the database connection
     *  @param schema schema if the table's name contained one
     *  @param tableBaseName fully qualified (with schema/user) name of the table
     */
    public void fillColumns(DatabaseMetaData dbMetaData, String schema, String tableBaseName) {
        try {
            ResultSet results = dbMetaData.getColumns(null, schema, tableBaseName, "%"); // all columns
            int icol = 0;
            while (results.next()) { // get all columns
                // TableColumn column = this.addColumn(icol);
                TableColumn column = icol < columnList.size()
                ? columnList.get(icol)
                : this.addColumn(icol);
                ;
                column.completeColumn(results);
                icol ++;
            } // while all columns
            results.close();
            setFillState(2);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // fillColumns

    /** Remove all columns - not used
     */
    private void clear() {
        try {
            columnList.clear();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // clear

    /** Gets the number of created column descriptions
     *  @return size of {@link #columnList}, 0, 1, 2 ...
     */
    public int getColumnCount() {
        lastColumnCount = columnList.size();
        return columnList.size();
    } // getColumnCount

    /** Sets the number of created column descriptions: 1, 2, ...
     *  @param rsSize new size of {@link #columnList};
     *  the new size must be smaller than the current size of {@link #columnList}.
     */
    public void setColumnCount(int rsSize) {
        int ncol = columnList.size();
        while (rsSize < ncol) {
            columnList.remove(-- ncol);
            lastColumnCount = ncol;
        } // while shrinking
    } // setColumnCount

    /** Number of columns which were serialized in the last call of {@link #writePreviousRow} */
    private int lastColumnCount;
    /** Gets the number of columns which were serialized in the last call of {@link #writePreviousRow}
     *  @return number of columns including pivot columns
     */
    public int getLastColumnCount() {
        return lastColumnCount;
    } // getColumnCount

    //========================
    // modification methods
    //========================

    /** Adds a new column description with the index only.
     *  @param index sequential number of the column: 0, 1, 2
     *  @return the new element just created
     */
    public TableColumn addColumn(int index) {
        TableColumn column = new TableColumn(index);
        columnList.add(column);
        return column;
    } // addColumn(index)

    /** Adds a column description.
     *  @param column existing column description
     *  @return the same column
     */
    public TableColumn addColumn(TableColumn column) {
        columnList.add(column);
        return column;
    } // add(column)

    /** Adds a new column description with a name and a value.
     *  @param name name of the column
     *  @param value value of the column
     */
    public TableColumn addColumn(String name, String value) {
        TableColumn column = new TableColumn(columnList.size());
        column.setName(name);
        column.setValue(value);
        column.setWidth(16);
        columnList.add(column);
        return column;
    } // addColumn(name, value)

    /** Gets an element of the column array list
     *  @param index index of the desired column: 0,1,...
     *  @return a {@link TableColumn}
     */
    public TableColumn getColumn(int index) {
        return columnList.get(index);
    } // getColumn

    /** Gets the fill state of column attributes:
     *  <ul>
     *  <li>0 = empty</li>
     *  <li>1 = partial</li>
     *  <li>2 = complete</li>
     *  </ul>
     *  @return one of the fill state values 0, 1, 2
     */
    public int getFillState() {
        return this.fillState;
    } // getFillState

    /** Sets the fill state of column attributes:
     *  <ul>
     *  <li>0 = empty</li>
     *  <li>1 = partial</li>
     *  <li>2 = complete</li>
     *  </ul>
     *  @param fillState one of the fill state values 0, 1, 2
     */
    public void setFillState(int fillState) {
        this.fillState = fillState;
    } // setFillState

    /** Inserts the missing attributes from the result set's metadata
     *  into the list of all columns.
     *  @param stResults some result set (from a SELECT) for the applicable table;
     *  as opposed to the constructor, the metadata are not taken from dbMetaData.getColumns!
     */
    public void putAttributes(ResultSet stResults) {
        try {
            ResultSetMetaData rsMetaData = stResults.getMetaData();
            int rsCount = rsMetaData.getColumnCount();
            int
            icol = columnList.size();
            while (icol < rsCount) { // append enough new elements
                columnList.add(new TableColumn(icol)); // index is always set
                icol ++;
            } // while not complete
            // this.setColumnCount(rsCount); // shrink it to 1 for -m probe, and preferrable in general?
            icol = 0;
            while (icol < rsCount) {
                TableColumn column = columnList.get(icol);
                if (true) { // ! describe ???
                    if (column.getName() == null) {
                        String name = rsMetaData.getColumnLabel(icol + 1);
                        if (! name.matches("\\w+")) { // no identifier
                            name = "col" + Integer.toString(icol);
                        } // no identfier
                        column.setName(name);
                    } // name not set
                    String stLabel = rsMetaData.getColumnLabel(icol + 1).replaceAll("\\s+", " ");
                        // CASE WHEN statements had multiple lines (bad for Excel column header)
                /*
                    if (label.startsWith(tableName + ".")) {
                        label = label.substring(tableName.length() + 1);
                    }
                */
                    String label = column.getLabel();
                    if (label == null || label.length() == 0) {
                        column.setLabel(stLabel);
                /*
                    } else if (column.getRemark() == null) {
                        column.setRemark(stLabel);
                */
                    }
                    if (column.getDataType() == TableColumn.NO_TYPE) {
                        column.setDataType(rsMetaData.getColumnType(icol + 1));
                    }
                    if (column.getWidth() == 0) { // can still be overwritten
                        column.setWidth(rsMetaData.getColumnDisplaySize(icol + 1));
                    }
                    // column.setNullable(! stResults.getString("IS_NULLABLE").equalsIgnoreCase("NO"));
                } // if ! describe
                icol ++;
            } // while icol
            setFillState(1);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // putAttributes(stResults)

    /** Fill all column descriptions from a comma separated list of integer field widths.
     *  Expand the column list as neccessary.
     *  @param widthList comma separated list of integers
     */
    public void fillColumnWidths(String widthList) {
        String widths[] = widthList.split ("\\D+"); // one or more non-digit characters
        int icol = 0;
        while (icol < widths.length) {
            TableColumn column = icol < columnList.size()
                ? columnList.get(icol)
                : this.addColumn(icol);
                ;
            int width = 8; // some reasonable default width
            try {
                width = Integer.parseInt(widths[icol]);
            } catch (Exception exc) {
                // ignore, take default width
            }
            column.setWidth(width);
            icol ++;
        } // while icol
    } // fillColumnWidths

    //===============================================
    // Grouping, Aggregation and Pivot tables
    //===============================================

    /** Initializes the {@link #oldValueList}.
     */
    private void initOldValues() {
        if (oldValueList == null) { // not filled so far
            int icol = 0;
            oldValueList = new ArrayList/*<1.5*/<TableColumn>/*1.5>*/(16); // empty so far
            while (icol < columnList.size()) {
                oldValueList.add(new TableColumn(icol));
                icol ++;
            } // while icol
        } // not filled so far
    } // initOldValues
    //-----------------------------------
    /** Copies the properties of all columns to the {@link #oldValueList}.
     *  @param tbSerializer the desired output formatter
     */
    public void rememberRow(BaseTable tbSerializer) {
        if (groupColumns != null || aggregateIndex >= 0) { // feature is active
            if (oldValueList == null) { // not filled so far
                initOldValues();
            } // not filled so far
            int icol = 0;
            while (icol < columnList.size()) { // copy
                while (oldValueList.size() <= icol) { // columnList may contain additional pivot columns in the meantime
                    oldValueList.add(new TableColumn(icol));
                } // while icol
                TableColumn column = columnList.get(icol);
                column.setValue(tbSerializer.getFlatValue(column));
                oldValueList.set(icol, column.clone());
                icol ++;
            } // while copying
        } // only if feature is active
    } // rememberRow(1)
    //-----------------------------------
    /** Aggregates the specified column by appending the
     *  aggregation separator and the current column to the aggregation column's previous value.
     *  The caller must ensure that the feature is set (aggregateIndex >= 0).
     *  @param tbSerializer the desired output formatter
     */
    public void aggregateColumn(BaseTable tbSerializer) {
        if (oldValueList == null) {
            initOldValues();
        }
        TableColumn column = columnList.get(aggregateIndex);
        column.setValue
                ( oldValueList.get(aggregateIndex).getValue()
                + this.aggregationSeparator
                + tbSerializer.getFlatValue(column)
                );
    } // aggregateColumn(1)
    //-----------------------------------
    /** Appends another pivot table column, with the label from the {@link #aggregateIndex} column,
     *  and the value (and all other attributes) from the next column.
     *  The caller must ensure that the feature is set (aggregateIndex >= 0).
     *  @param tbSerializer the desired output formatter
     */
    public void addPivotColumn(BaseTable tbSerializer) {
        TableColumn column = columnList.get(aggregateIndex + 1).clone();
        column.setLabel(tbSerializer.getFlatValue(column));
        columnList.add(column);
    } // addPivotColumn
    //-----------------------------------
    /** Determines whether there is a change in a certain column.
     *  @param index index of the column to be investigated, 0 based.
     *  The method must be called only if oldValueList != null
     *  @return true if the old string value of column[colNo] differs
     *  from the current value, false otherwise
     */
    private boolean hasColumnChange(int index) {
        boolean result = false; // assume there is no group change
        String oldValue = oldValueList.get(index).getValue();
        String newValue = columnList  .get(index).getValue();
        // System.err.println(oldValue + " <> " + newValue);
        if (oldValue != null) {
            if (newValue != null) {
                result = ! oldValue.equals(newValue);
            } else {
                result = true;
            }
        } else { // oldValue == null
            result = newValue != null;
        } // oldValue == null
        return result;
    } // hasColumnChange

    /** Determines whether there is a change in any of the group column values
     *  @return whether there is a group control change between the old and the current column list
     *  Internally, the following decisions are made:
     *  <ul>
     *  <li>{@link TableMetaData.Change#HEADING} or</li>
     *  <li>{@link TableMetaData.Change#MINOR}   if any of the old group column values differs from    the current value,</li>
     *  <li>{@link TableMetaData.Change#NONE}    if all        old group column values are the same as the current values</li>
     *  </ul>
     */
    public boolean hasGroupChange() {
        Change result = Change.NONE; // assume there is no group change
        if (groupColumns != null) {
            if (oldValueList == null) {
                initOldValues();
            } else { // was already filled
                int icol  = 0;
                int ihead = 0;
                while (icol < columnList.size()) {
                    TableColumn column = columnList.get(icol);
                    String style  = column.getStyle();
                    String name   = column.getName ();
                    String label  = column.getLabel();
                    String pseudo = column.getPseudo();
                //  || --> improve performance
                    if (pseudo == null
                            &&  (   (name  != null && groupColumns.indexOf("," + name   + ",") >= 0)
                                ||  (label != null && groupColumns.indexOf("," + label  + ",") >= 0)
                                )
                        ) { // participates in group change
                        if (hasColumnChange(icol)) {
                            if (false) {
                            } else if (ihead < numHeadingColumns) {
                                result = Change.HEADING;
                            } else if (result == Change.NONE) {
                                result = Change.MINOR;
                            }
                            if (result != Change.NONE) { // all following control change columns are visible again
                                if (style != null && style.endsWith(INVISIBLE)) {
                                    column.setStyle(VISIBLE);
                                }
                            }
                        } // there was a control change
                        ihead ++;
                    } // name or label participates
                    icol ++;
                } // while icol
            } // was already filled
        } // groupColumns != null
        return result == Change.HEADING;
    } // hasGroupChange

    /** Determines whether there is a change in any of the non-aggregate column values
     *  @return
     *  <ul>
     *  <li>-1 if any of the old non-aggregate column values differs from the current value,</li>
     *  <li>-2 if the oldValueList was not yet filled,</li>
     *  <li>-3 if the aggreate feature is not set,</li>
     *  <li>the index of the aggregate or pivot column
     *    if all old non-aggregate values are the same as the current values
     *  </li>
     *  </ul>
     */
    public int getAggregateChange() {
        int result = aggregateIndex; // assume there is no aggregate change
        if (aggregateIndex >= 0) { // feature is set
            if (oldValueList == null) {
                initOldValues();
                result = AGGR_EMPTY;
            } else { // was already filled
                int icol = 0;
                while (result >= 0 && icol < columnList.size()) {
                    if (isPivot() ? icol < aggregateIndex : icol != aggregateIndex) {
                        if (hasColumnChange(icol)) {
                            result = AGGR_CHANGED;
                        }
                    }
                    icol ++;
                } // while icol
            } // was already filled
        } // feature is set
        return result;
    } // getAggregateChange

    /** Writes the previous row in the specified output format.
     *  @param tbSerializer one of HTMLTable, SQLTable, XMLTable etc.
     *  @param withHeaders whether a header line should be printed before the row
     *  @param rowCount number of rows already printed so far, -1 and 0 =&gt; first
     *  @param columnCount regular length of a row from the SQL result set
     */
    public void writePreviousRow(BaseTable tbSerializer, boolean withHeaders, int rowCount, int columnCount) {
        if (isPivot()) { // cut out the 2 original columns for pivot row data
            oldValueList.remove(aggregateIndex + 1);
            oldValueList.remove(aggregateIndex); // in this order, since remove shifts columns
        }
        lastColumnCount = oldValueList.size();
        if (withHeaders && rowCount <= 0) {
            tbSerializer.writeGenericRow(BaseTable.RowType.HEADER, this, this.oldValueList);
        }
        if (! isPivot() || rowCount > 0) {
            tbSerializer.writeGenericRow(BaseTable.RowType.DATA  , this, this.oldValueList);
            tbSerializer.writeGenericRow(BaseTable.RowType.DATA2 , this, this.oldValueList);
        }
        if (isPivot()) {
            int icol = columnList.size();
            while (icol > columnCount) { // truncate to result set size
                columnList.remove(-- icol);
            } // while icol
            addPivotColumn(tbSerializer);
        }
    } // writePreviousRow

} // TableMetaData
