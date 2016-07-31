/*  SQLAction.java - Properties and methods specific for one elementary sequence of SQL instructions
    @(#) $Id$
    2014-11-10: separateURLFromValue moved to TableColumn.separateWrappedValue
    2014-03-10: insertFromURI, no Date/Time/Timestamp escape sequences
    2012-11-27: COMMIT if max_commit % 250 = 0
    2012-06-27: without references to com.ibm.db2.jcc.*; printSQLError commented out
    2012-06-13: prepare PreparedStatements
    2012-05-21: improved setting of update_count and sql_state (for CALL a.o.)
    2012-05-16: describe table is uppercased for DB2
    2012-04-26: TIMESTAMP_FORMAT with ' ' instead of 'T'
    2012-04-17: NON_UNIQUE INDEX
    2012-03-02: execSQLStatement returns manipulatedSum => update_count
    2012-01-25: tableMetaData.getFetchTarget = null | "parm"
    2012-01-14: #510, if (value == null || value.equals("PRIMARY")) { // patch for MySQL defect
    2011-08-24: writeGenericRow
    2011-08-06: CLOBs <-> Berlin
    2011-08-02: TIME and DECIMAL
    2011-07-21: cope with timestamps in CALL statement
    2011-05-04, Dr. Georg Fischer: extracted from Dbat.java
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
import  org.teherba.common.CommandTokenizer;
import  org.teherba.common.URIReader;
import  org.teherba.dbat.Configuration;
import  org.teherba.dbat.Placeholder;
import  org.teherba.dbat.PlaceholderList;
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.TableMetaData;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.SQLTable;
import  java.io.Serializable;
import  java.io.ByteArrayOutputStream;
import  java.io.InputStream;
import  java.io.OutputStream;
import  java.io.Reader;
import  java.io.StringWriter;
import  java.io.Writer;
import  java.math.BigDecimal;
import  java.sql.Blob;
import  java.sql.CallableStatement;
import  java.sql.Clob;
import  java.sql.Connection;
import  java.sql.DatabaseMetaData;
import  java.sql.PreparedStatement;
import  java.sql.ResultSet;
import  java.sql.SQLException;
import  java.sql.Statement;
import  java.sql.Types;
import  java.text.SimpleDateFormat;
import  java.util.ArrayList;
import  java.util.HashMap;
import  java.util.TreeMap;
// import  com.ibm.db2.jcc.DB2Diagnosable;
// import  com.ibm.db2.jcc.DB2Sqlca;
import  org.apache.log4j.Logger;

/** This class contains properties and methods closely related to the
 *  JDBC database access layer. Preparation and execution of SQL statements
 *  and the retrieval of result sets is done here, together with
 *  description generation from metadata.
 *  @author Dr. Georg Fischer
 */
public class SQLAction implements Serializable {
    public final static String CVSID = "@(#) $Id$";
    /** log4j logger (category) */
    private Logger log;
    /** Debugging switch */
    private int debug = 0;

    //========================
    // (almost) Constants
    //========================

    /** ISO calendar date format */
    public  static final SimpleDateFormat DATE_FORMAT       = new SimpleDateFormat("yyyy-MM-dd");
    /** ISO time format */
    public  static final SimpleDateFormat TIME_FORMAT       = new SimpleDateFormat("HH:mm:ss");
    /** ISO timestamp with milliseconds */
    public  static final SimpleDateFormat TIMESTAMP_FORMAT  = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS");

    /** whether to use batch INSERTs */
    private boolean batchInsert;

    //==========================================
    // Bean properties, getters and setters
    // Some are repeated from Configuration.java,
    // with specific values for this SQL action
    //===========================================

    /** whether the last SQL instruction was a COMMIT */
    private boolean committed;
    /** Determines whether the last SQL action was committed
     *  @return true if no additional COMMIT is neccessary
     */
    public boolean isCommitted() {
        return committed;
    } // isCommitted
    /** Sets the committing state
     *  @param state whether no additional COMMIT is neccessary
     */
    public void setCommitted(boolean state) {
        committed = state;
    } // setCommitted

    /** JDBC configuration with connection, user defineable properties and serializer */
    private Configuration config;
    /** Sets the configuration.
     *  The configuration contains the database connection, user defineable properties and the table serializer.
     *  @param config configuration
     */
    public void setConfiguration(Configuration config) {
        this.config = config;
        setMaxCommit       (config.getMaxCommit       ());
        setDecimalSeparator(config.getDecimalSeparator());
        setNullText        (config.getNullText        ());
        setTrimSides       (config.getTrimSides       ());
    } // setConfiguration
    //--------
    /** Character for the decimal point or comma */
    private String  decimalSeparator;
    /** Gets the decimal separator
     *  @return a point or comma
     */
    public String getDecimalSeparator() {
        return this.decimalSeparator;
    } // getDecimalSeparator
    /** Sets the decimal separator
     *  @param separator a point or comma
     */
    public void setDecimalSeparator(String separator) {
        this.decimalSeparator = separator;
    } // setDecimalSeparator
    //--------
    /** Rule how to escape a value, from {@link BaseTable}.
     *  The following escaping rules are currently observed:
     *  <ul>
     *  <li>0 = no escaping at all</li>
     *  <li>1 = "&amp;", "&lt;" and "&gt;" are escaped as "&amp;amp;", "&amp;lt;" and "&amp;gt;" respectively</li>
     *  <li>2 = "&apos;" is replaced by "&amp;apos"</li>
     *  <li>3 = combination of rule 1 and rule 2</li>
     *  <li>4 = like 1, but only if the SQL expression of the column does not contain a '&lt;' </li>
     *  </ul>
     */
    private   int escapingRule;
    //--------
    /** max. number of rows to be fetched; default "infinite" */
    private int fetchLimit;
    /** Gets the maximum number of rows to be fetched
     *  @return  maximum number of rows to be fetched
     */
    public int getFetchLimit() {
        return this.fetchLimit;
    } // getFetchLimit
    /** Sets the maximum number of rows to be fetched
     *  @param fetchLimit maximum number of rows to be fetched
     */
    public void setFetchLimit(int fetchLimit) {
        this.fetchLimit = fetchLimit;
    } // setFetchLimit
    //--------
    /** number of SQL instructions executed in this action */
    private int instructionSum;
    /** Gets the number of instructions executed in this action
     *  @return number of SQL instructions
     */
    public int getInstructionSum() {
        return  this.instructionSum;
    } // getInstructionSum
    /** Sets the number of instructions executed in this action
     *  @param instructionSum number of SQL instructions
    */
    private void setInstructionSum(int instructionSum) {
        this.instructionSum = instructionSum;
    } // getInstructionSum
    //--------
    /** number of rows affected by a DML statement (UPDATE/INSERT/DELETE) */
    private int manipulatedSum;
    /** Gets the number of rows affected by this action
     *  @return number of rows
     */
    public int getManipulatedSum() {
        return  this.manipulatedSum;
    } // getManipulatedSum
    /** Sets the number of rows affected by this action
     *  @param manipulatedSum number of rows
    */
    private void setManipulatedSum(int manipulatedSum) {
        this.manipulatedSum = manipulatedSum;
    } // setManipulatedSum
    //--------
    /** insert a COMMIT statement after this number of rows in a result set (modes -sql/jdbc, -update) */
    private int maxCommit;
    /** Gets the commit limit
     *  @return number of rows after which a COMMIT is written
     */
    public int getMaxCommit() {
        return  this.maxCommit;
    } // getMaxCommit
    /** Sets the commit limit
     *  @param maxCommit number of rows after which a COMMIT is written
     */
    public void setMaxCommit(int maxCommit) {
        this.maxCommit = maxCommit;
    } // setMaxCommit
    //--------
    /** whether to write the value <em>null</em> in text formats: 0 = omit, 1 = write "null" */
    private int nullText;
    /** Tells whether the value <em>null</em> should be written in text formats
     *  @return 0 = omit, 1 = write "null"
     */
    public int getNullText() {
        return nullText;
    } // getNullText
    /** Tells whether the value <em>null</em> should be written in text formats
     *  @param nullText  0 = omit, 1 = write "null"
     */
    public void setNullText(int nullText) {
        this.nullText = nullText;
    } // setNullText
    //--------
    /** encoding of URLs, from {@link BaseTable} */
    private   String targetEncoding;
    //--------
    /** how to trim CHAR and VARCHAR column values */
    private int trimSides;
    /** Tells how CHARs and VARCHARs should be trimmed by INSERT and SELECT
     *  @return how to trim CHARs and VARCHARs: 0 = never, 1 = right, 2 = both sides
     */
    public int getTrimSides() {
        return trimSides;
    } // getTrimSides
    /** Sets the trimming behaviour
     *  @param trimSides how to trim CHARs and VARCHARs: 0 = never, 1 = right, 2 = both sides
     */
    public void setTrimSides(int trimSides) {
        this.trimSides = trimSides;
    } // setTrimSides
    //--------
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

    //===================================================
    // Constructor, class initialization and finalization
    //===================================================

    /** No-args Constructor
     */
    public SQLAction() {
        log = Logger.getLogger(SQLAction.class.getName());
        batchInsert         = false;        // -b
        setMaxCommit(250);
    } // Constructor

    /** Constructor from configuration
     *  @param config overall configuration of a session
     */
    public SQLAction(Configuration config) {
        this();
        setConfiguration(config);
        setFetchLimit (config.getFetchLimit());
        setWithHeaders(config.isWithHeaders());
    } // Constructor

    /** Terminates the processing of SQL statements,
     *  clean-up for the next invocation
     */
    public void terminate() {
        if (! isCommitted()) {
            execCommitStatement();
        }
        config.closeConnection();
    } // terminate

    //========================================
    // Auxiliary methods, for LOBs and other
    //========================================

    /** Fetches the character content of a Clob (SQL Character Large OBject, LONG VARCHAR) column
     *  and writes it somewhere with a Writer.
     *  @param writer where to write the character content
     *  @param clob CLBO to be fetched
     */
    public void fetchClob(Writer writer, Clob clob) {
        int chunkLen = 8192;
        char[] chunk = new char[chunkLen];
        int len = 0;
        try {
            Reader reader = clob.getCharacterStream();
            while ((len = reader.read(chunk, 0, chunkLen)) > 0) {
                writer.write(chunk, 0, len);
            } // while substrings
            clob.free();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
        }
    } // fetchClob

    /** Reads character content with a Reader
     *  and store it into a Clob (SQL Character Large OBject, LONG VARCHAR) column
     *  @param reader from where to read the character content
     *  @param clob store into this Clob
     */
    public void storeClob(Reader reader, Clob clob) {
        int chunkLen = 8192;
        long pos = 0;
        char[] buffer = new char[chunkLen];
        int len = 0;
        try {
            while ((len = reader.read(buffer)) > 0) { // read a chunk
                clob.setString(pos, new String(buffer, 0, len));
                pos += len;
            } // while reading chunks
            clob.free();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
        }
    } // storeClob

    /** Fetches the byte content of a Blob (SQL Binary Large OBject) column,
     *  and writes it to an OutputStream
     *  @param outputStream where to write the byte content
     *  @param blob fetch the content of this column
     */
    public void fetchBlob(OutputStream outputStream, Blob blob) {
        int chunkLen = 8192;
        byte[] buffer = new byte[chunkLen];
        int len = 0;
        try {
            InputStream blobStream  = blob.getBinaryStream();
            while ((len = blobStream.read(buffer)) > 0) { // read a chunk
                outputStream.write(buffer, 0, len );
            } // while reading chunks
            blobStream.close();
            blob.free();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
        }
    } // fetchBlob

    /** Reads character content with a Reader
     *  and store it into a Blob (SQL Binary Large OBject, LONG VARBINARY) column
     *  @param inputStream from where to read the byte content
     *  @param blob store the content into this column
     */
    public void storeBlob(InputStream inputStream, Blob blob) {
        int chunkLen = 8192;
        byte[] buffer = new byte[chunkLen];
        int len = 0;
        try {
            OutputStream blobStream  = blob.setBinaryStream(0L);
            while ((len = inputStream.read(buffer)) > 0) { // read a chunk
                blobStream.write(buffer, 0, len );
            } // while reading chunks
            blobStream.close();
            blob.free();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
        }
    } // storeBlob

    /** Escapes single quotes (duplicates them) in a value read from the
     *  input file or table column
     *  @param value raw value to be escaped
     *  @return printable SQL string
     */
    private String escapeSQLValue(String value) {
        String result = null;
        if (value.compareToIgnoreCase("null") != 0) {
            StringBuffer buffer = new StringBuffer(256);
            int qpos1 = 0;
            int qpos2 = value.indexOf('\'', qpos1); // position of single quote (apostrophe)
            while (qpos2 >= 0) {
                buffer.append(value.substring(qpos1, qpos2 + 1)); // including the quote
                buffer.append('\''); // the 2nd quote
                qpos1 = qpos2 + 1; // behind the current quote
                qpos2 = value.indexOf('\'', qpos1);
            } // while single quotes
            buffer.append(value.substring(qpos1));
            result = buffer.toString();
        } // != "null"
        return result;
    } // escapeSQLValue

    /** Converts the codes for UPDATE_RULE and DELETE_RULE to strings
     *  @param ruleCode = results.getShort("UPDATE_RULE"    );
     *  @return SQL keyword(s) for "ON UPDATE|DELETE"
     */
    private String getRuleString(short ruleCode) {
        String result = "";
        switch (ruleCode) {
            case DatabaseMetaData.importedKeyCascade:
                result = "CASCADE";
                break;
            case DatabaseMetaData.importedKeyNoAction:
                result = "NO ACTION";
                break;
            default:
            case DatabaseMetaData.importedKeyRestrict:
                result = "RESTRICT";
                break;
            case DatabaseMetaData.importedKeySetDefault:
                result = "SET DEFAULT";
                break;
            case DatabaseMetaData.importedKeySetNull:
                result = "SET NULL";
                break;
        } // switch
        return result;
    } // getRuleString

    /** Processes a DB2 specific SQLException
     *  @param sqle SQLException to be processed
     */
    public void printSQLError(Exception sqle) {
    /*
        while (sqle != null && (sqle instanceof SQLException)) { // Check whether there are more SQLExceptions to process
            if (sqle instanceof DB2Diagnosable) { // Check if DB2-only information exists
                DB2Diagnosable diagnosable = (DB2Diagnosable) sqle;
                // diagnosable.printTrace(printWriter, "");
                Throwable throwable = diagnosable.getThrowable();
                if (throwable != null) {
                    // Extract java.lang.Throwable information
                    // such as message or stack trace.
                } // process Throwable
                DB2Sqlca sqlca = diagnosable.getSqlca();
                try  {
                    if (sqlca != null) {
                        int sqlCode         = sqlca.getSqlCode();
                        String sqlErrmc     = sqlca.getSqlErrmc();
                        String[] sqlErrmcTokens = sqlca.getSqlErrmcTokens();
                        String sqlErrp      = sqlca.getSqlErrp();
                        int[] sqlErrd       = sqlca.getSqlErrd();
                        char[] sqlWarn      = sqlca.getSqlWarn();
                        String sqlState     = sqlca.getSqlState();
                        String errMessage   = sqlca.getMessage();
                        System.err.println ("Server error message: " + errMessage);
                        System.err.println ("--------------- SQLCA ---------------");
                        System.err.println ("Error code: " + sqlCode);
                        System.err.println ("SQLERRMC: " + sqlErrmc);
                        for (int i = 0; i < sqlErrmcTokens.length; i ++) {
                            System.err.println ("  token " + i + ": " + sqlErrmcTokens[i]);
                        }

                        System.err.println ( "SQLERRP: " + sqlErrp );
                        System.err.println (
                          "SQLERRD(1): " + sqlErrd[0] + "\n" +
                          "SQLERRD(2): " + sqlErrd[1] + "\n" +
                          "SQLERRD(3): " + sqlErrd[2] + "\n" +
                          "SQLERRD(4): " + sqlErrd[3] + "\n" +
                          "SQLERRD(5): " + sqlErrd[4] + "\n" +
                          "SQLERRD(6): " + sqlErrd[5] );
                        System.err.println (
                          "SQLWARN1: " + sqlWarn[0] + "\n" +
                          "SQLWARN2: " + sqlWarn[1] + "\n" +
                          "SQLWARN3: " + sqlWarn[2] + "\n" +
                          "SQLWARN4: " + sqlWarn[3] + "\n" +
                          "SQLWARN5: " + sqlWarn[4] + "\n" +
                          "SQLWARN6: " + sqlWarn[5] + "\n" +
                          "SQLWARN7: " + sqlWarn[6] + "\n" +
                          "SQLWARN8: " + sqlWarn[7] + "\n" +
                          "SQLWARN9: " + sqlWarn[8] + "\n" +
                          "SQLWARNA: " + sqlWarn[9] );
                        System.err.println ("SQLSTATE: " + sqlState);
                        // portion of SQLException
                    } // sqlca != null
                } catch (Exception exc) {
                    // bad luck, error in error processing
                    log.error(exc.getMessage(), exc);
                }
            } // sqle instanceof DB2Diagnosable
            sqle=((SQLException) sqle).getNextException();     // Retrieve next SQLException
        } // while (sqle != null)
    */
    } // printSQLError

    //===================
    // Workhorse Actions
    //===================
    /** Generate SQL DROP/CREATE statements for one procedure.
     *  @param dbMetaData metadata for the database connection
     *  @param schema     name of the table's schema, or null if none
     *  @param procedureName  name of the procedure
     *  @param procedureType  type of the table as retrieved from <em>DatabaseMetaData.getProcedures</em>
     */
    private void describeProcedure(DatabaseMetaData dbMetaData, String schema, String procedureName, short procedureType) {
        BaseTable     tbSerializer = config.getTableSerializer();
        ResultSet results = null;
        try {
            TreeMap<String, HashMap<String, String>> cstRows = new TreeMap<String, HashMap<String, String>>();
            String cstKey = null;
            String value  = null;
            String field  = null;
            short sval = 0;
            int   ival = 0;
            int   icol = 0;
            results = dbMetaData.getProcedureColumns(null, schema, procedureName, "%");
            while (results.next()) { // get all index info rows
                field = "COLUMN_NAME";
                value = results.getString(field);
                cstKey = procedureName;
                if (true) {
                    HashMap<String, String> cstRow = new HashMap<String, String>();
                    cstRow.put(field, value);

                    field = "COLUMN_TYPE";
                    sval  = results.getShort(field);
                    switch (sval) {
                        case DatabaseMetaData.procedureColumnIn:
                            cstRow.put(field, "IN");
                            break;
                        case DatabaseMetaData.procedureColumnInOut:
                            cstRow.put(field, "INOUT");
                            break;
                        case DatabaseMetaData.procedureColumnOut:
                            cstRow.put(field, "OUT");
                            break;
                        default:
                            break;
                    } // switch

                    field = "TYPE_NAME";
                    cstRow.put(field, results.getString(field));

                    field = "DATA_TYPE";
                    value = String.valueOf(results.getInt(field));
                    cstRow.put(field, value);

                    field = "PRECISION";
                    value = String.valueOf(results.getInt(field));
                    cstRow.put(field, value);

                    field = "SCALE";
                    value = String.valueOf(results.getShort(field));
                    cstRow.put(field, value);

                    field = "NULLABLE";
                    sval  = results.getShort(field);
                    switch (sval) {
                        case DatabaseMetaData.procedureNoNulls:
                            cstRow.put(field, "NOT NULL");
                            break;
                        case DatabaseMetaData.procedureNullable:
                            cstRow.put(field, "IS NULL");
                            break;
                        default:
                        case DatabaseMetaData.procedureNullableUnknown:
                            break;
                    } // switch
                    field = "REMARKS";
                    value = results.getString(field);
                    if (value != null) {
                        cstRow.put(field, value);
                    }

                /* MySQL does not know these:
                    field = "COLUMN_DEF";
                    value = results.getString(field);
                    if (value != null) {
                        cstRow.put(field, value);
                    }
                    field = "ORDINAL_POSITION";
                */
                    cstKey += "\t" + String.valueOf(1000 + icol).substring(1);
                    icol ++;
                    cstRows.put(cstKey, cstRow);
                } // if valid column
            } // while results
            results.close();
            HashMap<String, String> cstRow = new HashMap<String, String>();
            cstKey = "{}\t000"; // higher than all real PK_NAMEs
            cstRows.put(cstKey, cstRow); // add a fictitious row for the last group change
            String procSeparator = config.getProcSeparator();
            if (procSeparator == null) {
                procSeparator = "$";
            }
            tbSerializer.describeProcedureColumns(schema, procedureName, procedureType, procSeparator, cstRows);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
            // setCommitted(true); // avoid a final COMMIT
        }
    } // describeProcedure

    /** Generate SQL DROP/CREATE statements for one table.
     *  @param dbMetaData metadata for the database connection
     *  @param schema     name of the table's schema, or null if none
     *  @param tableBaseName  name of the table
     *  @param tableType  type of the table: "TABLE", "VIEW", etc.
     */
    private void describeTable(DatabaseMetaData dbMetaData, String schema, String tableBaseName, String tableType) {
        int verbose = config.getVerbose();
        BaseTable     tbSerializer = config.getTableSerializer();
        TableMetaData tbMetaData   = new TableMetaData(); // purely local
        ResultSet results = null;
        try {
            tbMetaData.setTableName(schema, tableBaseName);
            tbMetaData.fillColumns(dbMetaData, schema, tableBaseName);
            tbSerializer.startDescription(dbMetaData, schema, tableBaseName, tableType);
            int icol = 0;
            int columnCount = tbMetaData.getColumnCount();
            while (icol < columnCount) { // get all columns
                // System.err.println("icol=" + icol + ", name=" + tbMetaData.getColumn(icol).getName());
                tbSerializer.describeColumn(icol, tbMetaData.getColumn(icol));
                icol ++;
            } // while all columns
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
            // setCommitted(true); // avoid a final COMMIT
        }

        try { // any primary key constraints
            TreeMap<String, HashMap<String, String>> cstRows = new TreeMap<String, HashMap<String, String>>();
            String cstKey = null;
            String value  = null;
            String field  = null;
            results = dbMetaData.getPrimaryKeys(null, schema, tableBaseName);
            while (results.next()) { // get all imported keys
                field = "PK_NAME";
                value = results.getString(field);
                if (value == null || value.equals("PRIMARY")) { // patch for MySQL defect
                    value = "PK29";
                } // MySQL
                if (true) {
                    HashMap<String, String> cstRow = new HashMap<String, String>();
                    cstKey = value;
                    cstRow.put(field, value);
                    field = "COLUMN_NAME";
                    cstRow.put(field, results.getString(field));
                    field = "KEY_SEQ";
                    value = String.valueOf(1000 + results.getShort(field)).substring(1);
                    cstKey += "\t" + value;
                    cstRow.put(field, value);
                    cstRows.put(cstKey, cstRow);
                }
            } // while results
            results.close();
            HashMap<String, String> cstRow = new HashMap<String, String>();
            cstKey = "{}\t000"; // higher than all real PK_NAMEs
            cstRows.put(cstKey, cstRow); // add a fictitious row for the last group change
            tbSerializer.describePrimaryKey(tbMetaData.getTableName(), cstRows);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
            // setCommitted(true); // avoid a final COMMIT
        }

        try { // any indexes
            TreeMap<String, HashMap<String, String>> cstRows = new TreeMap<String, HashMap<String, String>>();
            String cstKey = null;
            String value  = null;
            String field  = null;
            results = dbMetaData.getIndexInfo(null, schema, tableBaseName, false, true);
            while (results.next()) { // get all index info rows
                field = "INDEX_NAME";
                value = results.getString(field);
                if (results.getShort("TYPE") != DatabaseMetaData.tableIndexStatistic && ! value.equals("PRIMARY")) {
                    HashMap<String, String> cstRow = new HashMap<String, String>();
                    field = "INDEX_NAME";
                    value = results.getString(field);
                    cstKey = value;
                    cstRow.put(field, value);

                    field = "COLUMN_NAME";
                    cstRow.put(field, results.getString(field));

                    field = "ORDINAL_POSITION";
                    value = String.valueOf(1000 + results.getShort(field)).substring(1);
                    cstKey += "\t" + value;
                    cstRow.put(field, value);

                    field = "ASC_OR_DESC";
                    value = results.getString(field);
                    if (value == null) {
                        value = "";
                    } else if (value.equals("A")) {
                        value = "ASC";
                    } else if (value.equals("D")) {
                        value = "DESC";
                    }
                    cstRow.put(field, value);

                    field = "NON_UNIQUE";
                    value = results.getBoolean(field) ? "" : "UNIQUE ";
                    cstRow.put(field, value);

                    cstRows.put(cstKey, cstRow);
                } // no statistic, not PRIMARY
            } // while results
            results.close();
            HashMap<String, String> cstRow = new HashMap<String, String>();
            cstKey = "{}\t000"; // higher than all real PK_NAMEs
            cstRows.put(cstKey, cstRow); // add a fictitious row for the last group change
            tbSerializer.describeIndexes(tbMetaData.getTableName(), cstRows);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
            // setCommitted(true); // avoid a final COMMIT
        }

        try { // the foreign key constraints
            TreeMap<String, HashMap<String, String>> cstRows = new TreeMap<String, HashMap<String, String>>();
            String cstKey = null;
            String value  = null;
            String field  = null;
            results = dbMetaData.getImportedKeys(null, schema, tableBaseName);
            while (results.next()) { // get all imported keys
                field = "FK_NAME";
                value = results.getString(field);
                if (true) {
                    HashMap<String, String> cstRow = new HashMap<String, String>();
                    cstKey = value;
                    cstRow.put(field, value);
                    field = "FKCOLUMN_NAME";
                    cstRow.put(field, results.getString(field));
                    field = "PKCOLUMN_NAME";
                    cstRow.put(field, results.getString(field));
                    field = "PKTABLE_NAME";
                    cstRow.put(field, results.getString(field));
                    field = "KEY_SEQ";
                    value = String.valueOf(1000 + results.getShort(field)).substring(1);
                    cstKey += "\t" + value;
                    cstRow.put(field, value);
                    field = "UPDATE_RULE";
                    cstRow.put(field, getRuleString(results.getShort(field)));
                    field = "DELETE_RULE";
                    cstRow.put(field, getRuleString(results.getShort(field)));
                    // field = "DEFERRABILITY"; cstRow.put(field, String.valueOf(1000 + results.getShort(field)).substring(1));
                    cstRows.put(cstKey, cstRow);
                }
            } // while results
            results.close();
            HashMap<String, String> cstRow = new HashMap<String, String>();
            cstKey = "{}\t000"; // higher than all real FK_NAMEs
            cstRows.put(cstKey, cstRow); // add a fictitious row for the last group change
            tbSerializer.describeConstraints(tbMetaData.getTableName(), cstRows);

            if (verbose > 0) {
                results = dbMetaData.getIndexInfo(null, schema, tableBaseName, false, true);
                while (results.next()) { // get all indexes = keys
                    if (results.getShort  ("TYPE"               ) != DatabaseMetaData.tableIndexStatistic) {
                        tbSerializer.writeComment("indexInfo"
                            + ": NON_UNIQUE="       + results.getBoolean("NON_UNIQUE"       )
                            + ", INDEX_NAME="       + results.getString ("INDEX_NAME"       )
                        //  + ", TYPE="             + results.getShort  ("TYPE"             )
                            + ", ORDINAL_POSITION=" + results.getShort  ("ORDINAL_POSITION" )
                            + ", COLUMN_NAME="      + results.getString ("COLUMN_NAME"      )
                            + ", ASC_OR_DESC="      + results.getString ("ASC_OR_DESC"      )
                        //  + ", CARDINALITY="      + results.getInt    ("CARDINALITY"      )
                        //  + ", PAGES="            + results.getInt    ("PAGES"            )
                        //  + ", FILTER_CONDITION=" + results.getString ("FILTER_CONDITION" )
                            , verbose);
                    } // != statistics
                } // while results

                results = dbMetaData.getExportedKeys(null, schema, tableBaseName);
                while (results.next()) { // get all indexes = keys
                    String pkTable      = results.getString("PKTABLE_NAME"  );
                    String pkColName    = results.getString("PKCOLUMN_NAME" );
                    String fkTable      = results.getString("FKTABLE_NAME"  );
                    String fkColName    = results.getString("FKCOLUMN_NAME" );
                    short keySeq        = results.getShort("KEY_SEQ"        );
                    short updateRule2   = results.getShort("UPDATE_RULE"    );
                    short deleteRule2   = results.getShort("DELETE_RULE"    );
                    String pkName       = results.getString("PK_NAME"       );
                    String fkName2      = results.getString("FK_NAME"       );
                    short deferrability2    = results.getShort("DEFERRABILITY"  );
                    tbSerializer.writeComment("exportedKeys"
                            + ": PKTABLE_NAME="     + pkTable
                            + ", PKCOLUMN_NAME="    + pkColName
                            + ", FKTABLE_NAME="     + fkTable
                            + ", FKCOLUMN_NAME="    + fkColName
                            + ", KEY_SEQ="          + keySeq
                            + ", UPDATE_RULE="      + updateRule2
                            + ", DELETE_RULE="      + deleteRule2
                            + ", PK_NAME="          + pkName
                            + ", FK_NAME="          + fkName2
                            + ", DEFERRABILITY="    + deferrability2
                            , verbose);
                } // while results

                results = dbMetaData.getImportedKeys(null, schema, tableBaseName);
                while (results.next()) { // get all indexes = keys
                    String pkTable      = results.getString("PKTABLE_NAME"  );
                    String pkColName    = results.getString("PKCOLUMN_NAME" );
                    String fkTable      = results.getString("FKTABLE_NAME"  );
                    String fkColName    = results.getString("FKCOLUMN_NAME" );
                    short keySeq        = results.getShort("KEY_SEQ"        );
                    short updateRule2   = results.getShort("UPDATE_RULE"    );
                    short deleteRule2   = results.getShort("DELETE_RULE"    );
                    String pkName       = results.getString("PK_NAME"       );
                    String fkName2      = results.getString("FK_NAME"       );
                    short deferrability2    = results.getShort("DEFERRABILITY"  );
                    tbSerializer.writeComment("importedKeys"
                            + ": PKTABLE_NAME="     + pkTable
                            + ", PKCOLUMN_NAME="    + pkColName
                            + ", FKTABLE_NAME="     + fkTable
                            + ", FKCOLUMN_NAME="    + fkColName
                            + ", KEY_SEQ="          + keySeq
                            + ", UPDATE_RULE="      + updateRule2
                            + ", DELETE_RULE="      + deleteRule2
                            + ", PK_NAME="          + pkName
                            + ", FK_NAME="          + fkName2
                            + ", DEFERRABILITY="    + deferrability2
                            , verbose);
                } // while imported keys

                results = dbMetaData.getPrimaryKeys(null, schema, tableBaseName);
                while (results.next()) { // get all indexes = keys
                    String pkTable      = results.getString("TABLE_NAME"    );
                    String pkColName    = results.getString("COLUMN_NAME"   );
                    short  keySeq       = results.getShort("KEY_SEQ"        );
                    String pkName       = results.getString("PK_NAME"       );
                    tbSerializer.writeComment("primaryKeys"
                            + ": PKTABLE_NAME="     + pkTable
                            + ", PKCOLUMN_NAME="    + pkColName
                            + ", KEY_SEQ="          + keySeq
                            + ", PK_NAME="          + pkName
                            , verbose);
                } // while primary keys
                results.close();
            } // if verbose

            tbSerializer.endDescription();
            instructionSum ++;
            manipulatedSum ++;
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
            // setCommitted(true); // avoid a final COMMIT
        }
    } // describeTable

    /** Generate SQL DROP/CREATE statements for one or more table(s)
     *  @param defaultSchema the default schema if none was specified with the table pattern
     *  @param tablePattern pattern for table(s) to be described,
     *  either in SQL notation (with optional '%', '_')
     *  or as an OS wildcard (with '*', '?')
     */
    protected void describeTables(String defaultSchema, String tablePattern) {
        try {
            Connection con = config.getOpenConnection();
            DatabaseMetaData dbMetaData = con.getMetaData();
            TableMetaData tbMetaData = new TableMetaData();
            switch (config.getRdbmsId()) {
                case Configuration.DB2:
                    tbMetaData.parseTableName(defaultSchema.toUpperCase(), tablePattern.toUpperCase());
                    break;
                default:
                    tbMetaData.parseTableName(defaultSchema, tablePattern);
                    break;
            } // switch Rdbms

            boolean found = true;
            if (! found) { // print a comment header for schemata first
                ResultSet results = dbMetaData.getSchemas();
                int ischema = 0;
                while (results.next()) { // get all schemas
                    String schema    = results.getString("TABLE_SCHEM");
                    String catalog   = results.getString("TABLE_CATALOG");
                    System.out.println("-- Schema=" + schema + ", Catalog=" + catalog);
                    ischema ++;
                } // while results
                results.close();
            } // schemas
            // System.err.println("describeTables, defaultSchema=" + defaultSchema + ", tablePattern=" + tablePattern);

            found = false;
            if (! found) {
                ResultSet results = dbMetaData.getTables
                        ( null // catalogPattern
                        , tbMetaData.getSchema()        .replaceAll("\\*", "%").replaceAll("\\?", "_") // schema
                        , tbMetaData.getTableBaseName() .replaceAll("\\*", "%").replaceAll("\\?", "_")
                        , null // returns all types, otherwise: new String[] {"TABLE", "VIEW"}
                        );
                while (results.next()) { // get all tables
                    String schema           = results.getString("TABLE_SCHEM");
                    if (schema != null && schema.equals(config.getDefaultSchema())) {
                        schema = null; // omit it
                    }
                    String tableBaseName    = results.getString("TABLE_NAME");
                    String tableType        = results.getString("TABLE_TYPE");
                    describeTable(dbMetaData, schema, tableBaseName, tableType);
                    found = true;
                } // while results
                results.close();
            } // tables

            if (! found) { // no table found, try procedures
                ResultSet results = dbMetaData.getProcedures
                        ( null // catalogPattern
                        , null // tbMetaData.getSchema()        .replaceAll("\\*", "%").replaceAll("\\?", "_") // schema
                        , tablePattern  .replaceAll("\\*", "%").replaceAll("\\?", "_")
                        );
                while (results.next()) { // get all procedures
                    String schema           = results.getString("PROCEDURE_SCHEM");
                    if (schema != null && schema.equals(config.getDefaultSchema())) {
                        schema = null; // omit it
                    }
                    String procedureName    = results.getString("PROCEDURE_NAME");
                    short  procedureType    = results.getShort ("PROCEDURE_TYPE");
                    describeProcedure(dbMetaData, schema, procedureName, procedureType);
                    found = true;
                } // while results
            } // procedures
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
            setCommitted(true); // avoid a final COMMIT
        } finally {
            config.closeConnection();
        }
    } // describeTables

    /** Call a Stored Procedure
     *  @param con open database connection
     *  @param tbMetaData meta data for the table as far as they are already known
     *  @param karg index of name in argument list, followed by in/inout/out options and parameter type/values
     *  @param args rest of argument list containing name and parameters
     *  @return 1 for success, 0 for failure
     */
    public int callStoredProcedure(Connection con, TableMetaData tbMetaData, int karg, String[] args) {
        int result = 1; // assume success
        StringBuffer callSql = new StringBuffer();
        String value = null;
        int ivalue = 0;
        int parameterIndex = 0;
        int carg = karg;
        char sep = '(';
        BaseTable tbSerializer = config.getTableSerializer();
        TableColumn column = null;
        String procedureName = args[karg];
        try {
            callSql.append("{call ");
            if (karg < args.length) {
                callSql.append(args[karg ++]); // name of Stored Procedure
            }
            parameterIndex = 0;
            carg = karg;
            while (carg < args.length) {
                String opt = args[carg ++];
                if (tbMetaData.getFillState() > 0) {
                    column = tbMetaData.getColumn(parameterIndex);
                } else {
                    column = new TableColumn(opt.substring(1) + String.valueOf(parameterIndex + 1)); // in1, out2, out3 ...
                    column.setIndex(parameterIndex);
                    column.setLabel(opt.substring(1) + String.valueOf(parameterIndex + 1)); // column.getLabel());
                    tbMetaData.addColumn(column);
                }
                parameterIndex ++;
                if (false) {
                } else if (opt.startsWith("-inout")) { // -in or -inout
                    column.setDir('b');
                    carg ++; // skip over in/inout parameter value
                } else if (opt.startsWith("-i"    )) { // -in or -inout
                    column.setDir('i');
                    carg ++; // skip over in/inout parameter value
                } else if (opt.startsWith("-o"    )) { // -out
                    column.setDir('o');
                }
                callSql.append(sep);
                sep = ',';
                callSql.append('?'); // append placeholder
            } // while counting parameters
            callSql.append(")}");
            int columnCount = parameterIndex;

            if (con == null) { // not yet set
                con = config.openConnection();
            }
            CallableStatement cstmt = con.prepareCall(callSql.toString());
            parameterIndex = 0;
            carg = karg;
            while (carg < args.length) { // insert input values and/or register output datatypes
                String opt = args[carg ++].toLowerCase();
                column = tbMetaData.getColumn(parameterIndex);
                parameterIndex ++;
                String typeName = "VARCHAR";
                int cpos = opt.indexOf(':');
                if (cpos >= 0) {
                    typeName = opt.substring(cpos + 1).toUpperCase(); // rest behind colon
                }
                if (opt.startsWith("-in")) { // valid for "-inout", too
                    value = args[carg ++];
                    if (false) {
                    } else if (typeName.equals    ("CLOB"      )) {
                        // not yet
                    } else if (typeName.equals    ("DATE"      )) {
                        cstmt.setDate(parameterIndex, java.sql.Date.valueOf(value));
                    } else if (typeName.equals    ("DECIMAL"   )) {
                        cstmt.setBigDecimal(parameterIndex, new BigDecimal(value));
                    } else if (typeName.startsWith("INT"       )) {
                        ivalue = 0;
                        try {
                            ivalue = Integer.parseInt(value);
                        } catch(Exception exc) { // ignore
                        }
                        cstmt.setInt(parameterIndex, ivalue);
                    } else if (typeName.equals    ("TIME"      )) {
                        cstmt.setTime(parameterIndex, java.sql.Time.valueOf(value));
                    } else if (typeName.equals    ("TIMESTAMP" )) {
                        value = value
                                .replaceAll("T", " ")
                                .replaceAll("\\'", "");
                        cstmt.setTimestamp(parameterIndex, java.sql.Timestamp.valueOf(value));
                    } else { // STRING, CHAR etc.
                        typeName =                 "VARCHAR";
                        cstmt.setString(parameterIndex, value);
                    }
                } // in
                if (opt.startsWith("-out") || opt.startsWith("-inout")) { // -out
                    if (false) {
                    } else if (typeName.equals    ("CHAR"      )) {
                        cstmt.registerOutParameter(parameterIndex, Types.CHAR      );
                    } else if (typeName.equals    ("CLOB"      )) {
                        cstmt.registerOutParameter(parameterIndex, Types.CLOB      );
                    } else if (typeName.equals    ("DATE"      )) {
                        cstmt.registerOutParameter(parameterIndex, Types.DATE      );
                    } else if (typeName.equals    ("DECIMAL"   )) {
                        cstmt.registerOutParameter(parameterIndex, Types.DECIMAL   );
                    } else if (typeName.startsWith("INT"       )) {
                        cstmt.registerOutParameter(parameterIndex, Types.INTEGER   );
                    } else if (typeName.equals    ("TIME"      )) {
                        cstmt.registerOutParameter(parameterIndex, Types.TIME      );
                    } else if (typeName.equals    ("TIMESTAMP" )) {
                        cstmt.registerOutParameter(parameterIndex, Types.TIMESTAMP );
                    } else { // STRING, CHAR etc.
                        typeName                 = "VARCHAR";
                        cstmt.registerOutParameter(parameterIndex, Types.VARCHAR   );
                    }
                } // out
                column.setTypeName(typeName);
            } // while counting parameters

            boolean moreResults = cstmt.execute();
            // log.info("callSql=" + callSql.toString() + "\ncarg=" + carg + ", karg=" + karg + ", args.length=" + args.length + ", procedureName=" + procedureName);

            if (moreResults) { // ??? what about updateCounts? and output parameters mixed with result sets?
                while (moreResults) {
                    serializeQueryResults(new TableMetaData(), callSql.toString(), cstmt.getResultSet());
                    moreResults = cstmt.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
                } // while moreResults
            } else { // without ResultSet
                result = cstmt.getUpdateCount();
                boolean intoParm = tbMetaData.getAggregateIndex() == TableMetaData.AGGR_PARAMS;
                if (! intoParm) {
                    tbSerializer.startTable(procedureName, tbMetaData);
                }
                int htmlRowCount = 0; // aggregated, HTML data rows only
                int sqlRowCount  = 1; // aggregated, SQL  data rows only

                carg = karg;
                parameterIndex = 0;
                int colNo = 0; // counts output columns only
                while (carg < args.length) { // retrieve output values and print them as column of a table with one row
                    String opt = args[carg ++];
                    column = tbMetaData.getColumn(parameterIndex);
                    parameterIndex ++;
                    String typeName = "VARCHAR";
                    int cpos = opt.indexOf(':');
                    if (cpos >= 0) {
                        typeName = opt.substring(cpos + 1).toUpperCase(); // rest behind colon
                    }
                    if (opt.startsWith("-in")) { // valid for "-inout", too
                        value = args[carg ++]; // show -in value unchanged
                    }
                    if (opt.startsWith("-out") || opt.startsWith("-inout")) { // -out
                        if (false) {
                        } else if (typeName.equals    ("BLOB"      )) {
                            // ???
                        } else if (typeName.equals    ("CLOB"      )) {
                            Clob clob = cstmt.getClob(parameterIndex);
                            StringWriter writer = new StringWriter();
                            fetchClob(writer, clob);
                            value = writer.toString();
                        } else if (typeName.equals    ("DATE"      )) {
                            value = DATE_FORMAT.format(cstmt.getDate(parameterIndex));
                        } else if (typeName.equals    ("DECIMAL"   )) {
                            BigDecimal bvalue = cstmt.getBigDecimal(parameterIndex);
                            value = (bvalue == null) ? "0" : bvalue.toString();
                        } else if (typeName.startsWith("INT"       )) {
                            ivalue = cstmt.getInt(parameterIndex);
                            value = String.valueOf(ivalue);
                        } else if (typeName.equals    ("TIME"      )) {
                            value = TIME_FORMAT.format(cstmt.getTime(parameterIndex));
                        } else if (typeName.equals    ("TIMESTAMP" )) {
                            value = TIMESTAMP_FORMAT.format(cstmt.getTimestamp(parameterIndex));
                        } else {
                            typeName =                 "VARCHAR";
                            value = cstmt.getString(parameterIndex);
                        }
                    } // out
                    column.separateWrappedValue(value, targetEncoding, escapingRule, nullText);
                } // while counting parameters

                int aggregateChange = tbMetaData.getAggregateChange();
                switch (aggregateChange) {
                    case TableMetaData.AGGR_NOT_SET: // feature not set, print current row unconditionally
                        if (this.isWithHeaders() && (htmlRowCount <= 0 || tbMetaData.hasGroupChange())) { // print columns' labels
                            tbSerializer.writeGenericRow(BaseTable.RowType.HEADER, tbMetaData, tbMetaData.columnList);
                        } // withHeaders
                        tbSerializer.writeGenericRow(BaseTable.RowType.DATA , tbMetaData, tbMetaData.columnList);
                        tbSerializer.writeGenericRow(BaseTable.RowType.DATA2, tbMetaData, tbMetaData.columnList);
                        htmlRowCount ++;
                        break;
                    case TableMetaData.AGGR_VERTICAL: // 1 row vertical, with headers prefixed
                        tbSerializer.writeGenericRow(BaseTable.RowType.ROW1 , tbMetaData, tbMetaData.columnList);
                        htmlRowCount ++;
                        break;
                    case TableMetaData.AGGR_PARAMS:   // append column values to corresponding parameter arrays
                        tbSerializer.appendToParameters(htmlRowCount        , tbMetaData, tbMetaData.columnList);
                        htmlRowCount ++;
                        break;
                    default: // >= 0, no change in non-aggregate columns, must aggregate
                        // ignore - will not be reached
                        break;
                } // switch aggregateChange

                if (this.isWithHeaders()) { // print row count
                    tbSerializer.writeTableFooter(htmlRowCount, sqlRowCount >= fetchLimit, tbMetaData);
                } // withHeaders
                if (! intoParm) {
                    tbSerializer.endTable();
                }

            } // ! withResultSet
            cstmt.close ();
        } catch (SQLException exc) {
            result = 0;
            StringBuffer mess = new StringBuffer("callStoredProcedure[" + args.length + "]");
            int iarg = 0;
            while (iarg < args.length) {
                mess.append(iarg == 0 ? "(" : ", ");
                mess.append(args[iarg ++]);
            }
            mess.append(");");
            log.error(mess);
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
            tbSerializer.writeMarkup("<h3 class=\"error\">SQL Error in SQLAction.callStoredProcdure: "
                    + exc.getSQLState() + " "
                    + exc.getMessage() + "</h3><pre class=\"error\">");
            tbSerializer.writeMarkup(callSql.toString());
            tbSerializer.writeMarkup("</pre>");
            try {
                con.rollback(); // End Transaction 1
            } catch (Exception exc2) {
                log.error(exc2.getMessage(), exc2);
            }
            setCommitted(true); // avoid a final COMMIT
        } catch (Exception exc) {
            result = 0;
            log.error(exc.getMessage(), exc);
            tbSerializer.writeMarkup("<h3 class=\"error\">Error in SQLAction.callStoredProcdure: "
                    + exc.getMessage() + "</h3><pre class=\"error\">");
            tbSerializer.writeMarkup(callSql.toString());
            int iarg = 0;
            while (iarg < args.length) {
                tbSerializer.writeMarkup("<br />" + iarg + ": " + args[iarg ++]);
            }
            tbSerializer.writeMarkup("</pre>");
            try {
                con.rollback(); // End Transaction 1
            } catch (Exception exc2) {
                log.error(exc2.getMessage(), exc2);
            }
            setCommitted(true); // avoid a final COMMIT
        } // catch
        return result;
    } // callStoredProcedure

    /** Sets the string value of one column from a query result set,
     *  together with the href value if the column has a href or link attribute.
     *  @param column the table column with its properties
     *  @param stResults result set of a query
     *  @param icol column number in the table meta data, starting at 0;
     *  column numbers in the result set start at 1
     */
    private void setColumnResult(TableColumn column, ResultSet stResults, int icol) {
        String value = "";
        Object obj   = null;
        try {
            switch (column.getDataType()) {
                case Types.CLOB:
                case Types.LONGVARCHAR:
                    Clob clob = stResults.getClob(icol + 1);
                    if (clob == null) {
                        value = null;
                    } else {
                        StringWriter writer = new StringWriter((int) (clob.length() & 0x7fffffff));
                        fetchClob(writer, clob);
                        value = writer.toString();
                    }
                    break;
                case Types.BLOB:
                case Types.JAVA_OBJECT:
                case Types.LONGVARBINARY:
                    Blob blob = stResults.getBlob(icol + 1);
                    if (blob == null) {
                        value = null;
                    } else {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) (blob.length() & 0x7fffffff));
                        fetchBlob(bos, blob);
                        value = bos.toString(targetEncoding);
                    }
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                    value = stResults.getString(icol + 1);
                    if (value != null) {
                        switch(trimSides) {
                            case 0: // no trim
                                break;
                            case 1: // rtrim
                                value = (("x" + value).trim()).substring(1);
                                break;
                            default:
                            case 2: // both sides
                                value = value.trim();
                                break;
                        } // switch
                    }
                    break;
                case Types.DECIMAL:
                    obj = stResults.getBigDecimal(icol + 1);
                    value = (obj == null) ? null : ((BigDecimal) obj).toString();
                    break;
                case Types.DATE:
                    obj = stResults.getDate(icol + 1);
                    value = (obj == null) ? null : DATE_FORMAT.format(obj);
                    break;
                case Types.TIME:
                    obj = stResults.getTime(icol + 1);
                    value = (obj == null) ? null : TIME_FORMAT.format(obj);
                    break;
                case Types.TIMESTAMP:
                    obj = stResults.getTimestamp(icol + 1);
                    value = (obj == null) ? null : TIMESTAMP_FORMAT.format(obj);
                    break;
                default:
                    obj = stResults.getObject(icol + 1);
                    value = (obj == null) ? null : obj.toString();
                    break;
            } // switch Types
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
            setCommitted(true); // avoid a final COMMIT
        }
        column.separateWrappedValue(value, targetEncoding, escapingRule, nullText);
    } // setColumnResult

    /** Serializes the results of a previously executed SELECT statement
     *  for the configured output format.
     *  @param tbMetaData meta data for the table as far as they are alreay known
     *  @param selectSql SQL statement which returned the result set, for debugging
     *  @param stResults result set from JDBC execute
     */
    private void serializeQueryResults(TableMetaData tbMetaData, String selectSql, ResultSet stResults) {
        String value = "";
        TableColumn column = null;
        BaseTable tbSerializer  = config.getTableSerializer();
        escapingRule    = tbSerializer.getEscapingRule();
        targetEncoding  = tbSerializer.getTargetEncoding();
        if (tbSerializer instanceof SQLTable) { // write 'null' and not '"null"' for SQL derived formats
            setNullText(-1);
        }
        try {
            tbMetaData.putAttributes(stResults);
            int columnCount      = tbMetaData.getColumnCount();
            String showTableName = tbMetaData.getTableName();
            String tableId       = tbMetaData.getIdentifier();
            if (tableId != null) {
                showTableName = tableId;
            }
            String aggregateName = tbMetaData.getAggregationName();
            if (aggregateName != null) {
                tbMetaData.setAggregateColumn(aggregateName, tbMetaData.getAggregationSeparator());
            }
            boolean intoParm = tbMetaData.getAggregateIndex() == TableMetaData.AGGR_PARAMS;
            if (! intoParm) {
                tbSerializer.startTable(showTableName, tbMetaData);
            }
            int htmlRowCount = 0; // aggregated, HTML data rows only
            int sqlRowCount  = 0; // aggregated, SQL  data rows only
            while (sqlRowCount < fetchLimit && stResults.next()) {
                sqlRowCount ++;
                int icol = 0;
                while (icol < columnCount) { // #1
                    // we count from 0, but JDBC counts from 1 (c.f. "icol + 1" below)
                    column = tbMetaData.getColumn(icol);
                    setColumnResult(column, stResults, icol);
                    column.setValue(tbSerializer.getFlatValue(column));
                    icol ++;
                } // while icol #1
                int aggregateChange = tbMetaData.getAggregateChange();
                switch (aggregateChange) {
                    case TableMetaData.AGGR_CHANGED: // some change in non-aggregate columns, print old (aggregated) row
                        tbMetaData.writePreviousRow(tbSerializer, this.isWithHeaders(), htmlRowCount, columnCount);
                        htmlRowCount ++;
                        break;
                    case TableMetaData.AGGR_EMPTY: // first input row - remember it only (below)
                        if (tbMetaData.isPivot()) {
                            tbMetaData.addPivotColumn(tbSerializer);
                        }
                        break;
                    case TableMetaData.AGGR_NOT_SET: // feature not set, print current row unconditionally
                        if (this.isWithHeaders() && (htmlRowCount <= 0 || tbMetaData.hasGroupChange())) { // print columns' labels
                            tbSerializer.writeGenericRow(BaseTable.RowType.HEADER, tbMetaData, tbMetaData.columnList);
                        } // withHeaders
                        tbSerializer.writeGenericRow(BaseTable.RowType.DATA , tbMetaData, tbMetaData.columnList);
                        tbSerializer.writeGenericRow(BaseTable.RowType.DATA2, tbMetaData, tbMetaData.columnList);
                        htmlRowCount ++;
                        break;
                    case TableMetaData.AGGR_VERTICAL: // 1 row vertical, with headers prefixed
                        tbSerializer.writeGenericRow(BaseTable.RowType.ROW1 , tbMetaData, tbMetaData.columnList);
                        htmlRowCount ++;
                        break;
                    case TableMetaData.AGGR_PARAMS:   // append column values to corresponding parameter arrays
                        tbSerializer.appendToParameters(htmlRowCount        , tbMetaData, tbMetaData.columnList);
                        htmlRowCount ++;
                        break;
                    default: // >= 0, no change in non-aggregate columns, must aggregate
                        if (tbMetaData.isPivot()) {
                            tbMetaData.addPivotColumn(tbSerializer);
                        } else {
                            tbMetaData.aggregateColumn(tbSerializer);
                        }
                        break;
                } // switch aggregateChange
                tbMetaData.rememberRow(tbSerializer);
                if (sqlRowCount % maxCommit == 0) {
                    tbSerializer.writeCommit(sqlRowCount); // some modes insert a COMMIT statement here
                }
            } // while results

            if (tbMetaData.getAggregateChange() > -3) { // don't forget last row
                tbMetaData.writePreviousRow(tbSerializer, this.isWithHeaders(), htmlRowCount, columnCount);
                htmlRowCount ++;
            } // last row
            if (! intoParm) {
                if (this.isWithHeaders()) { // print row count
                    if (tbMetaData.isPivot()) {
                        htmlRowCount --; // first data row was really the header row
                    }
                    tbSerializer.writeTableFooter(htmlRowCount, sqlRowCount >= fetchLimit, tbMetaData);
                } // withHeaders
                tbSerializer.writeCommit(sqlRowCount);
                tbSerializer.endTable();
            } else { // intoParm - save rowCount in a new parameter
                String name = tbMetaData.getCounterDesc(1); // singular only, it is the parameter name anyway
                tbSerializer.setParameter(name, new String[] { String.valueOf(htmlRowCount) });
            } // intoParm
        } catch (Exception exc) {
            log.error(exc.getMessage() + ", SQL=\"" + selectSql + "\"", exc);
            printSQLError(exc);
            setCommitted(true); // avoid a final COMMIT
        }
    } // serializeQueryResults

    /** Set the value for a single placeholder in a prepared or callable statement
     *  @param pstmt statement which contains placeholders ("?")
     *  @param parameterIndex sequential number of the placeholder, starting at 1 (not 0!)
     *  @param typeName SQL type name, for example "VARCHAR", in upper case
     *  @param value string representation of the value, will be converted to the correct Java type
     */
    private void setPlaceholder(PreparedStatement pstmt, int parameterIndex, String typeName, String value) {
        try {
            // keep this switch in synch with the code in format.EchoSQL.writeEchoSQL
            if (false) {
            } else if (typeName.equals    ("CLOB"      )) {
                // not yet
            } else if (typeName.equals    ("DATE"      )) {
                pstmt.setDate(parameterIndex, java.sql.Date.valueOf(value));
            } else if (typeName.equals    ("DECIMAL"   )) {
                pstmt.setBigDecimal(parameterIndex, new BigDecimal(value));
            } else if (typeName.startsWith("INT"       )) {
                int ivalue = 0;
                try {
                    ivalue = Integer.parseInt(value);
                } catch(Exception exc) { // ignore
                }
                pstmt.setInt(parameterIndex, ivalue);
            } else if (typeName.equals    ("TIME"      )) {
                pstmt.setTime(parameterIndex, java.sql.Time.valueOf(value));
            } else if (typeName.equals    ("TIMESTAMP" )) {
                value = value
                        .replaceAll("T", " ")
                        .replaceAll("\\'", "");
                pstmt.setTimestamp(parameterIndex, java.sql.Timestamp.valueOf(value));
            } else { // STRING, CHAR etc.
                typeName =                 "VARCHAR";
                pstmt.setString(parameterIndex, value);
            }
        } catch(Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // setPlaceholder

    /** Set the values for all placeholders in a prepared or callable statement
     *  @param pstmt statement which contains placeholders (parameter markers, "?")
     *  @param variables array of string pairs (typeName, value)
     */
    private void setPlaceholders(PreparedStatement pstmt, ArrayList/*<1.5*/<String>/*1.5>*/ variables) {
        int parameterIndex = 1;
        int ivar = 0;
        int vlen = variables.size();
        while (ivar < vlen) {
            setPlaceholder(pstmt, parameterIndex, variables.get(ivar + 1), variables.get(ivar + 2));
            parameterIndex ++;
            ivar += 3;
        } // while ivar
    } // setPlaceholders

    /** Execute a single SQL statement.
     *  SQL Comments starting with "--" were removed previously by the caller.
     *  @param tbMetaData meta data for the table as far as they are alreay known
     *  @param sqlInstruction text of the SQL statement (SELECT, INSERT, UPDATE ...).
     *  @param parameterMap map containing HTTP request or CLI parameter settings
     */
    public void execSQLStatement(TableMetaData tbMetaData, String sqlInstruction
            , HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        execSQLStatement(tbMetaData, sqlInstruction
                , new ArrayList/*<1.5*/<String>/*1.5>*/ () // empty variables' list
                , parameterMap);
    } // execSQLStatement(3)

    /** Execute a single SQL statement.
     *  SQL Comments starting with "--" were removed previously by the caller.
     *  @param tbMetaData meta data for the table as far as they are already known
     *  @param sqlInstruction text of the SQL statement (SELECT, INSERT, UPDATE ...).
     *  @param variables pairs of types and values for variables to be filled
     *  into any placeholders ("?") in the prepared statement
     *  @param parameterMap map containing HTTP request or CLI parameter settings
     */
    public void execSQLStatement(TableMetaData tbMetaData, String sqlInstruction
            , ArrayList/*<1.5*/<String>/*1.5>*/ variables
            , HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        Connection con          = config.getOpenConnection();
        BaseTable  tbSerializer = config.getTableSerializer();
        int result = 0;
        PreparedStatement statement = null;
        try {
            if (sqlInstruction.length() > 0) { // statement non-empty
                if (con == null) { // not yet set
                    con = config.openConnection();
                }
                String[] words = sqlInstruction.split("\\s+", 3); // starts with verb - c.f. the trim() above
                String verb = words[0].toUpperCase();
                int updateCount = 0;
                if (false) {
                } else if (verb.equals("SELECT") || (verb.equals("WITH") && sqlInstruction.toUpperCase().indexOf("SELECT") >= 0)) {
                    statement = con.prepareStatement(sqlInstruction);
                    if (variables.size() > 0) {
                        setPlaceholders(statement, variables); // set the values of all placeholders
                    } // set placeholders
                    // statement.setQueryTimeout(120); // not supported by DB2 JDBC driver
                    tbSerializer.writeSQLInstruction(tbMetaData, sqlInstruction, 0, config.getVerbose(), variables);
                    ResultSet stResults = statement.executeQuery();
                    serializeQueryResults(tbMetaData, sqlInstruction, stResults);
                    stResults.close();
                    if (! config.hasAutoCommit()) { // for DB2 on z
                        con.commit();
                    }
                    statement.close();
                } else if (verb.equals("CALL")) {
                    String[] args = CommandTokenizer.tokenize(sqlInstruction);
                    updateCount = callStoredProcedure(con, tbMetaData, 1, args);
                    if (parameterMap != null) {
                        parameterMap.put(config.UPDATE_COUNT, new String[] { String.valueOf(updateCount) });
                    }
                    // log.info("callStoredProcedure with \"" + sqlInstruction + "\", updateCount=" + updateCount);
                    result = updateCount;
                } else if (verb.equals("COMMIT")) {
                    setCommitted(true); // avoid a final COMMIT
                    if (! config.hasAutoCommit()) { // for DB2 on z
                        con.commit();
                    }
                    instructionSum --; // will be incremented again below - do not count COMMIT
                } else { // DDL or DML: UPDATE, INSERT, CREATE ...
                    statement = con.prepareStatement(sqlInstruction);
                    if (variables.size() > 0) {
                        setPlaceholders(statement, variables); // set the values of all placeholders
                    } // set placeholders
                    tbSerializer.writeSQLInstruction(tbMetaData, sqlInstruction, 2, config.getVerbose(), variables);
                    updateCount = statement.executeUpdate();
                    if (parameterMap != null) {
                        parameterMap.put(config.UPDATE_COUNT, new String[] { String.valueOf(updateCount) });
                    }
                    result = updateCount;
                    statement.close();
                } // DDL or DML
                instructionSum ++;
                manipulatedSum += updateCount;
            } // statement non-empty
        } catch (SQLException exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
            tbSerializer.writeMarkup("<h3 class=\"error\">SQL Error in SQLAction.execSQLStatement: "
                    + exc.getSQLState() + " "
                    + exc.getMessage() + "</h3><pre class=\"error\">");
            tbSerializer.writeMarkup(sqlInstruction.toString());
            tbSerializer.writeMarkup("</pre>");
            try {
                if (statement != null) {
                    statement.close();
                }
                con.rollback(); // End Transaction 1
            } catch (Exception exc2) {
                log.error(exc2.getMessage(), exc2);
            }
            setCommitted(true); // avoid a final COMMIT
            if (parameterMap != null) {
                parameterMap.put(config.SQL_STATE, new String[] { exc.getSQLState() });
            }
        } catch (Exception exc) {
            log.error(exc.getMessage()); // , exc); // -  would cause a stack trace - not desired for -m probe
            tbSerializer.writeMarkup("<h3 class=\"error\">Error in SQLAction.execSQLStatement: "
                    + exc.getMessage() + "</h3><pre class=\"error\">");
            tbSerializer.writeMarkup(sqlInstruction.toString());
            tbSerializer.writeMarkup("</pre>");
            try {
                if (statement != null) {
                    statement.close();
                }
                con.rollback(); // End Transaction 1
            } catch (Exception exc2) {
                log.error(exc2.getMessage(), exc2);
            }
            setCommitted(true); // avoid a final COMMIT
        } // catch
    } // execSQLStatement

    /** Execute a single COMMIT statement.
     */
    public void execCommitStatement() {
        execSQLStatement(null, "COMMIT", null);
        setCommitted(true); // avoid a final COMMIT
    } // execCommitStatement

    /** Execute the stored batch, and commit it.
     *  @param statement statement for database operations
     */
    private void putBatch(Statement statement) {
        try {
            if (batchInsert) {
                int[] updateCounts = statement.executeBatch();
                int sum = 0;
                for (int iup = updateCounts.length - 1; iup >= 0; iup --) {
                    sum += updateCounts[iup];
                }
            } // batchInsert
            execCommitStatement();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            printSQLError(exc);
            // setCommitted(true); // avoid a final COMMIT
        }
    } // putBatch

    /** Read SQL statements from a file, and execute them.
     *  CALL statements have a proprietary, non-SQL syntax.
     *  SQL comments (starting with "--") at the end of any input line are handled ,
     *  but "--" should not occur in a literal value.
     *  @param tbMetaData properties for one SQL action
     *  @param uri URI of file which contains SQL statements to be processed,
     *  or "-" for STDIN
     *  @param parameterMap map containing HTTP request or CLI parameter settings
     */
    public void execSQLfromURI(TableMetaData tbMetaData, String uri
            , HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        String stmtSeparator = config.getProcSeparator();
        if (stmtSeparator == null) {
            stmtSeparator = ";";
        }
        String line; // a line read from 'lineReader'
        int lineNo = 0;
        StringBuffer fileSql = new StringBuffer(); // assembled SQL statement (several lines)
        try {
            URIReader lineReader = new URIReader(uri, config.getEncoding(0));
            while ((line = lineReader.readLine()) != null) { // read and process lines
                lineNo ++;
                line = line.trim();
                int dashDashPos = line.indexOf("--"); // bad if "--" occurs in a value
                if (dashDashPos >= 0) {
                    // remove SQL comment by unsafe heuristics, without parsing the whole line
                    if (dashDashPos == 0) { // remove at start of line
                        line = line.substring(0, dashDashPos);
                    } else if (line.charAt(dashDashPos - 1) == '\'') { // not in string constant
                    } else if (line.toUpperCase().startsWith("INSERT") && line.matches("\\)\\;\\s*\\-\\-")) {
                        line = line.substring(0, dashDashPos); // after INSERT ... (...);  --
                    } else { // and in all other cases
                        line = line.substring(0, dashDashPos);
                    }
                    line = line.trim(); // again, to catch the trailing semicolon below
                } // remove --
                if (line.length() > 0) { // line not empty
                    fileSql.append(line);
                    fileSql.append(" ");
                    if (line.endsWith(stmtSeparator)) { // end of statement
                        fileSql.setLength(fileSql.length() - 1 - stmtSeparator.length()); // remove "; " for Oracle
                        this.execSQLStatement(tbMetaData, fileSql.toString(), parameterMap);
                        tbMetaData = new TableMetaData(config);
                        fileSql.setLength(0);
                    }
                } // line not empty
            } // while not EOF
            if (fileSql.length() > 0) { // last statement may miss a trailing stmtSeparator
                this.execSQLStatement(tbMetaData, fileSql.toString(), parameterMap);
                tbMetaData = new TableMetaData(config);
            }
            lineReader.close();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            System.err.println("Exception: line no. = " + lineNo);
            printSQLError(exc);
            this.setCommitted(true); // avoid a final COMMIT
        }
    } // execSQLfromURI

    /** Load a table's rows from an URI (-r action).
     *  Lines may have fixed column widths (option -l),
     *  or they may be separated by a string (option -s) or
     *  by whitespace (no -s and no -l).
     *  <blockquote>
     *      Caution, other input formats are not yet implemented!
     *  </blockquote>
     *  The overall sequence of operations is as follows:
     *  <ol>
     *  <li>Open the database connection.</li>
     *  <li>Prepare an INSERT statement with an appropiate number of "?" placeholders.</li>
     *  <li>In the URI file reading loop:</li>
     *      <ol>
     *      <li>Clear Parameters.</li>
     *      <li>Set all values read into the prepared statement.</li>
     *      <li>If the column is a LOB, the value must be another URI for the content:
     *          read that URI, store it in the LOB, and set that in the prepared statement.</li>
     *      <li>When the whole input row is read, execute the prepared statement.</li>
     *      <li>COMMIT after a predefined number of rows.</li>
     *      </ol>
     *  <li>Cleanup statement, file and connection.</li>
     *  </ol>
     *  @param tbMetaData meta data for the target DB table to be loaded, with its name
     *  @param uri URI for the external file data
     */
    public void insertFromURI(TableMetaData tbMetaData, String uri) {
        int icol = 0;
        String rawTable = tbMetaData.getTableName();
        char formatCode = config.getFormatMode().charAt(0); // default: 't'sv resp. whitespace separated
        String line = ""; // a line read from STDIN
        PreparedStatement insertStmt = null;
        try {
            Connection con = config.getOpenConnection();
            DatabaseMetaData dbMetaData = con.getMetaData();
            tbMetaData.parseTableName(config.getDefaultSchema(), rawTable);
            tbMetaData.fillColumns(dbMetaData, tbMetaData.getSchema(), tbMetaData.getTableBaseName());
            int columnCount = tbMetaData.getColumnCount();
            StringBuffer sqlBuffer = new StringBuffer(2048); // build a statemtent (with placeholders) to be prepared
            sqlBuffer.append("INSERT INTO " + rawTable + " VALUES(?"); // there must be at least 1 column
            icol = 1; // not 0, because 1st question mark is already set
            while (icol < columnCount) {
                sqlBuffer.append(",?");
                icol ++;
            } // while appending question marks
            sqlBuffer.append(");");
            insertStmt = con.prepareStatement(sqlBuffer.toString());
            TableColumn column = null;
            int rowCount = 0; // number of rows, for COMMIT insertion
            String columnValues[] = new String[columnCount]; // columns' values

            URIReader lineReader = new URIReader(uri, config.getEncoding(0));
            while ((line = lineReader.readLine()) != null) { // read and process lines
                insertStmt.clearParameters();
                int rawCount = 0; // number of values split from 'line'
                switch (formatCode) {
                    case 'f': // "fix", with fixed column widths
                        columnValues = new String[columnCount];
                        int spos = 0; // starting position
                        while (rawCount < columnCount && spos < line.length()) {
                            column = tbMetaData.getColumn(rawCount);
                            String pseudo = column.getPseudo();
                            if (pseudo == null) {
                                int epos = spos + column.getWidth();
                                if (epos >= line.length()) {
                                    epos = line.length();
                                }
                                switch (trimSides) {
                                    case 0: // notrim
                                        columnValues[rawCount] = (      line.substring(spos, epos))                    ;
                                        break;
                                    case 1: // rtrim
                                        columnValues[rawCount] = ("x" + line.substring(spos, epos)).trim().substring(1);
                                        break;
                                    default:
                                    case 2:
                                        columnValues[rawCount] = (      line.substring(spos, epos)).trim()             ;
                                        break;
                                } // switch trimSides
                                spos = epos;
                            } // not pseudo
                            rawCount ++;
                        } // while rawCount
                        break;
                    case 'c': // "csv" with separator
                        columnValues = line.split(config.getSeparator());
                        rawCount = columnValues.length;
                        break;
                    case 't':
                    default: // "tsv" -> separated by whitespace
                        columnValues = line.split("\\s+", -1);
                        rawCount = columnValues.length;
                        break;
                } // switch formatCode

                if (debug >= 2) {
                    System.err.println("insertFromURI.rawCount=" + rawCount + ", columnCount=" + columnCount);
                }
                if (rawCount > 0) { // insert row only if line contained some field
                    icol = 0;
                    int scol = 1; // column number for SQL, starting at 1, not incremented for pseudo columns
                    while (icol < columnCount) {
                        // we count from 0, but JDBC counts from 1 (c.f. "icol + 1" below)
                        // process those from the 'line' (at most 'columnCount')
                        String value = icol < rawCount
                                ? escapeSQLValue(columnValues[icol])
                                : null;
                        column = tbMetaData.getColumn(icol);
                        String pseudo = column.getPseudo();
                        if (pseudo == null) {
                            int dataType = column.getDataType();
                            if (debug >= 2) {
                                System.err.println("insertFromURI, column " + icol + ", dataType " + dataType);
                            }
                            if (value != null) {
                            switch (dataType) {
                        // special
                                case Types.BOOLEAN:
                                    insertStmt.setBoolean
                                            (scol ++, ! value.matches("[nNfF\\-\\?].*")); // indicators for "no", "false"
                                    break;
                        // character
                                case Types.CHAR:
                                case Types.VARCHAR:
                                    insertStmt.setString
                                            (scol ++, value);
                                    break;
                        // numeric
                                case Types.DECIMAL:
                                    insertStmt.setBigDecimal
                                            (scol ++, new BigDecimal(value));
                                    break;
                        // time
                                case Types.DATE:
                                    insertStmt.setDate     (scol ++, java.sql.Date     .valueOf(value));
                                    break;
                                case Types.TIME:
                                    insertStmt.setTime     (scol ++, java.sql.Time     .valueOf(value));
                                    break;
                                case Types.TIMESTAMP:
                                    insertStmt.setTimestamp(scol ++, java.sql.Timestamp.valueOf(value));
                                    break;
                        // LOBs
                                case Types.CLOB:
                                case Types.LONGVARCHAR:
                                    if (debug >= 2) {
                                        System.err.println("insertFromURI, uri " + value);
                                    }
                                    insertStmt.setClob // set CharacterStream
                                            (scol ++, (new URIReader(value)).getCharReader());
                                    break;
                                case Types.BLOB:
                                case Types.JAVA_OBJECT:
                                case Types.LONGVARBINARY:
                                    break;
                        // all others
                                default:
                                    insertStmt.setObject
                                            (scol ++, value);
                                    break;
                            } // switch getDataType
                            } else {
                        // null
                                    insertStmt.setNull(scol ++, dataType);
                            }
                        } // not pseudo
                        icol ++;
                    } // while those from 'line'

                    int inserted = insertStmt.executeUpdate();

                    rowCount ++;
                    if (rowCount % maxCommit == 0) {
                        this.execCommitStatement();
                    }
                } // rawCount > 0
            } // while notEof
            lineReader.close();
            this.execCommitStatement();
            insertStmt.close();
        } catch (Exception exc) {
            System.err.println("** offending line: " + line);
            log.error(exc.getMessage(), exc);
            this.setCommitted(true); // avoid a final COMMIT
            printSQLError(exc);
            try {
                if (insertStmt != null) {
                    insertStmt.close();
                }
            } catch (Exception exc2) {
                log.error(exc2.getMessage(), exc2);
            }
        } finally {
            config.closeConnection();
        }
    } // insertFromURI

    //====================
    // Main method - Test
    //====================

    /** Test driver -
     *  call it with -h to display possible options and arguments.
     *  The result is printed to STDOUT.
     *  @param args command line arguments: options, strings, table- or filenames
     */
    public static void main(String[] args) {
        Logger log = Logger.getLogger(SQLAction.class.getName());
        try {
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        } finally {
        }
    } // main

} // SQLAction
