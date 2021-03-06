/*  Pseudo format which generates a Java program with embedded SQLJ instructions - äöüÄÖÜß
    @(#) $Id$
 *  2017-05-27: javadoc
    2016-08-26: with getISOTimestamp()
    2012-06-16: copied from GenerateSQLJ
*/
/*
 * Copyright 2012 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  java.util.ArrayList;
import  java.util.HashMap;
import  java.util.Iterator;
import  java.util.LinkedHashMap;

/** This class generates a Java program with embedded SQLJ instructions
 *  from a Dbat specification. It is run offline like {@link ProbeSQL}.
 *  Preconditions are the <code>manner=</code> attribute in the root element
 *  of the specification, and a unique <code>id=methodName</code> attribute in
 *  each individual SQL instruction (SELECT, DELETE, INSERT, UPDATE, CALL) in the specification.
 *  @author Dr. Georg Fischer
 */
public class GenerateSQLJ extends BaseTable {
    public final static String CVSID = "@(#) $Id$";
    /** Debugging switch */
    private int debug = 0;
    /** a sequential number for methods which are not identified by an <em>id=</em> attribute of the SQL element */
    private int methodNo;
    /** buffer for all methods */
    private StringBuffer methodBuffer;
    /** buffer for all SQLJ iterators for FETCH cursors */
    private StringBuffer cursorBuffer;

    /** No-args Constructor
     */
    public GenerateSQLJ() {
        super();
        debug = 0;
        setFormatCodes("sqlj");
        setDescription("en", "Generate SQLJ");
        setDescription("de", "SQLJ-Generator");
        setDescription("fr", "Générateur pour SQLJ");
    } // Constructor

    /** Name of the specification file, with optional subdirectory, without extension ".xml|.xsl|.css|.js",
     *  any slash replaced by a dot, the first letter of the last component is uppercased,
     *  for example "test/xslt_brackets"
     */
    private String targetClassName;

    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *  @param params array of 0 or more (name, value) strings which specify features in the file header.
     *  @param parameterMap map of request parameters to values
     *  The following names are interpreted:
     *  <ul>
     *  <li>specname - name of the parent spec, which is turned into a suitable Java class name</li>
     *  </ul>
     */
    public void writeStart(String[] params,  HashMap<String, String[]> parameterMap) {
        methodBuffer = new StringBuffer(8192);
        cursorBuffer = new StringBuffer(2048);
        String targetPackageName = GenerateSQLJ.class.getName().replaceAll("\\.dbat\\..*", ".dbat.sqlj");
        String encoding = getTargetEncoding();
        methodNo = 0;
        try {
            int iparam = params.length;
            while (iparam > 0) {
                iparam -= 2;
                if (false) {
                } else if (params[iparam].equals("specname")) {
                    String[] components = params[iparam + 1].toLowerCase().split("[\\.\\/]");
                    int icomp = components.length - 1;
                    targetClassName = components[icomp --];
                    targetClassName = targetClassName.substring(0, 1).toUpperCase() + targetClassName.substring(1); // uc_first
                    while (icomp >= 0) {
                        targetPackageName += "." + components[icomp --];
                    } // while icomp
                } else { // skip all other pairs
                }
            } // while iparam

            charWriter.println("/*  Generated SQLJ class " + targetClassName + " - DO NOT EDIT here!                                  ");
            charWriter.println(" *  @(#) $Id$                                                                 ");
            charWriter.println(" *  " + BaseTable.getISOTimestamp() + " by " + GenerateSQLJ.class.getName());
            charWriter.println(" */ ");
            charWriter.println("package " + targetPackageName + ";"      );
            charWriter.println("import  org.teherba.dbat.SQLAction;     ");
            charWriter.println("import  org.teherba.dbat.TableMetaData; ");
            charWriter.println("import  java.math.BigDecimal;           ");
            charWriter.println("import  java.sql.Date;                  ");
            charWriter.println("import  java.sql.Time;                  ");
            charWriter.println("import  java.sql.Timestamp;             ");
            charWriter.println("import  sqlj.runtime.*;                 ");
            charWriter.println("import  sqlj.runtime.ref.*;             ");
            charWriter.println("                                        ");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeStart

    /** Ends a file that may contain several table descriptions and/or a SELECT result sets
     */
    public void writeEnd() {
        try {
            charWriter.print  (cursorBuffer.toString());
            charWriter.println();
            charWriter.println("/** This class was generated by {@link org.teherba.dbat.format.GenerateSQLJ}  ");
            charWriter.println(" *  for the SQL instructions in a Dbat specification. The individual methods  ");
            charWriter.println(" *  here are called if the specification is processed in the SQLJ manner.     ");
            charWriter.println(" */                                                                           ");
            charWriter.println("public class " + targetClassName + " {");
            charWriter.println("    public final static String CVSID = \"@(#) $" + "Id$\";                    ");
            charWriter.println("                                                                              ");
            charWriter.print  (methodBuffer  .toString());
            charWriter.println("} // " + targetClassName);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeEnd

    /** Converts a Dbat type name into a Java type (= class) name.
     *  @param typeName the original Dbat type name, for example "char"
     *  @return the converted Java type, for example "String"
     */
    public static String typeNameToJava(String typeName) {
        typeName = typeName.toUpperCase();
        if (false) {
        } else if (typeName.equals    ("DECIMAL"   )) {
            typeName = "BigDecimal";
        } else if (typeName.startsWith("INT"       )) {
            typeName = "int";
        } else if (typeName.equals    ("DATE"       )) {
            typeName = "java.sql.Date";
        } else if (typeName.equals    ("TIME"       )) {
            typeName = "java.sql.Time";
        } else if (typeName.equals    ("TIMESTAMP"  )) {
            typeName = "java.sql.Timestamp";
        } else { // DATE, TIME, STRING, CHAR etc.
            typeName = "String";
        } // switch typeName
        return typeName;
    } // typeNameToJava

    /** Gets a list of variable names,
     *  optionally with Java types,
     *  ordered ascending by names,
     *  separated by commas and
     *  enclosed in parentheses.
     *  @param vars ordered map of tuples (var name, var type)
     *  @param tupleSelect 2 bits: 0 = none, 1 = var names only, 2 = type names only, 3 = both
     *  @return the list
     */
    public String getVarList(LinkedHashMap<String, String> vars, int tupleSelect) {
        StringBuffer listBuffer = new StringBuffer(128);
        String separator = "(";
        if (vars.size() == 0) { // constant SQL
            listBuffer.append(separator);
        } else { // there a some variables
            Iterator<String> viter = vars.keySet().iterator();
            while (viter.hasNext()) { // process all cstRows
                String varName  = viter.next();
                String typeName = typeNameToJava(vars.get(varName).toUpperCase());
                listBuffer.append(separator);
                if ((tupleSelect & 2) > 0 ) {
                    listBuffer.append(typeName);
                } // with type
                if ((tupleSelect & 3) == 3) { // both
                    listBuffer.append(' ');
                } // both
                if ((tupleSelect & 1) > 0 ) {
                    listBuffer.append(varName);
                } // with name
                separator = ", ";
            } // while viter
        } // some variables
        listBuffer.append(')');
        return listBuffer.toString();
    } // getVarList

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
    		, ArrayList/*<1.5*/<String>/*1.5>*/ variables) {
        try {
			sqlInstruction = sqlInstruction + " "; // because of trailing PARAMETER_MARKER
            LinkedHashMap<String, String> vars = new LinkedHashMap<String, String>();
            methodNo ++;
            String idAttr = tbMetaData.getIdentifier();
            TableColumn column = null;
            String methodName = idAttr != null ? idAttr : ("method_" + String.valueOf(methodNo));
            int len = sqlInstruction.length();
            StringBuffer sqlBuffer = new StringBuffer(len);
            int nvar = variables.size();
            if (nvar > 0) { // placeholders must be replaced
                int ivar = 0;
                int foundPos = 0; // position of the PARAMETER_MARKER's first char
                int startPos = 0; // starting position in sqlBuffer
                while (ivar < nvar && startPos < len) {
                    foundPos = sqlInstruction.indexOf(PARAMETER_MARKER, startPos);
                    if (foundPos < 0) { // error - not enough parameter markers
                        foundPos = len - 1;
                    }
                    sqlBuffer.append(sqlInstruction.substring(startPos, foundPos + 1)); // copy the left space also
                    startPos = foundPos + PARAMETER_MARKER.length() - 1; // copy the right space also
                    String varName  = variables.get(ivar + 0);
                    String typeName = variables.get(ivar + 1).toUpperCase();
                    String value    = variables.get(ivar + 2);
                    vars.put(varName, typeName);
                    if (debug > 0) {
                        sqlBuffer.append('{');
                        sqlBuffer.append(String.valueOf(ivar));
                        sqlBuffer.append(',');
                        sqlBuffer.append(varName);
                        sqlBuffer.append('=');
                        sqlBuffer.append(typeName);
                        sqlBuffer.append(':');
                        sqlBuffer.append(value);
                        sqlBuffer.append('}');
                    } // debug
                    ivar += 3;
                    sqlBuffer.append(':');
                    sqlBuffer.append(varName); // insert the host variable instead of the placeholder "?"
                } // while ivar
                foundPos = len;
                if (startPos < foundPos) { // copy the rest behind the last marker
                    sqlBuffer.append(sqlInstruction.substring(startPos, foundPos));
                }
            } else { // no placeholders
                sqlBuffer.append(sqlInstruction);
            }
        //  methodBuffer.append("    /** Generated SQLJ method */\n");
            methodBuffer.append("    public void " + methodName);
            if (false) {
            } else if (action == 0) { // SELECT
                int columnCount      = tbMetaData.getColumnCount();
                String iteratorName = targetClassName + "_" + String.valueOf(methodNo);
                cursorBuffer.append("#sql iterator " + iteratorName); // 2 = types only
                String
                separator = "(";
                int
                icol = 0;
                while (icol < columnCount) { // #1 - print types
                    column = tbMetaData.getColumn(icol);
                    cursorBuffer.append(separator + typeNameToJava(column.getTypeName()));
                    separator = ", ";
                    icol ++;
                } // while icol #1
                cursorBuffer.append(");\n");

                methodBuffer.append("(SQLAction callBack, TableMetaData tbMetaData\n");
                methodBuffer.append("            , " + getVarList(vars, 3).substring(1) + " {\n"); // 3 = types and names
                methodBuffer.append("        try {\n");
                icol = 0;
                while (icol < columnCount) { // #2 - print declarations of all additional variables
                    column = tbMetaData.getColumn(icol);
                    String colName  = column.getName();
                    if (vars.get(colName) == null) { // not in vars
                        String typeName = typeNameToJava(column.getTypeName());
                        methodBuffer.append("            " + typeName + " " + column.getName() + " = ");
                        methodBuffer.append((typeName.startsWith("int") ? "0" : "null") + ";\n");
                    } // not in vars
                    icol ++;
                } // while icol #2
                String cursorName = "cursor_" + String.valueOf(methodNo);
                methodBuffer.append("            " + iteratorName + " " + cursorName + ";\n");
/*
        int deptnumb = 0;
        String deptname = "";
        int manager = 0;
        String division = "";
        String location = "";
        TbRead_Cursor0 cur0;
        #sql cur0 = {SELECT * FROM org};
        while (true) { // retrieve and display the result from the SELECT statement
            #sql {FETCH :cur0 INTO :deptnumb, :deptname, :manager, :division, :location};
            if (cur0.endFetch()) {
                break;
            }
        } // while
        cur0.close();
    } catch (Exception e) {
        SqljException sqljExc = new SqljException(e);
        sqljExc.handle();
    }
*/

                methodBuffer.append("            #sql " + cursorName + " = {\n");
                methodBuffer.append("                ");
                methodBuffer.append(sqlBuffer.toString().replaceAll("\n",
                                  "\n                "));
                methodBuffer.append("\n                };\n");
                methodBuffer.append("            while (true) { // fetch all rows\n");
                methodBuffer.append("               #sql { FETCH :" + cursorName + " INTO ");
                separator = "";
                icol = 0;
                while (icol < columnCount) { // #3 - print all column host variables
                    column = tbMetaData.getColumn(icol);
                    String colName  = column.getName();
                    methodBuffer.append(separator + ":" + column.getName());
                    separator = ", ";
                    icol ++;
                } // while icol #3
                methodBuffer.append(" };\n");
                methodBuffer.append("               if (" + cursorName + ".endFetch()) {\n");
                methodBuffer.append("                   break;\n");
                methodBuffer.append("               }\n");
                methodBuffer.append("            } // while fetching\n");
                methodBuffer.append("            " + cursorName + ".close();\n");
            } else { // DML
                methodBuffer.append(getVarList(vars, 3)); // types and names
                methodBuffer.append(" {\n");
                methodBuffer.append("        try {\n");
                methodBuffer.append("            #sql {\n");
                methodBuffer.append("\n                ");
                methodBuffer.append(sqlBuffer.toString().replaceAll("\n",
                                  "\n                "));
                methodBuffer.append("\n");
                methodBuffer.append("\n                };\n");
            }
            if (true) {
                methodBuffer.append("        } catch (Exception exc) {\n");
                methodBuffer.append("           log.error(exc.getMessage(), exc);\n");
                methodBuffer.append("        }\n");
                methodBuffer.append("    } // " + methodName + "\n\n");
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeSQLInstruction

    /** Writes a complete header, data or alternate data row with all tags and cell contents.
     *  Must be redefined here with no action, since the default implementation in {@link BaseTable#writeGenericRow}
     *  would output the query results.
     *  @param rowType type of the generic row
     *  @param tbMetaData meta data for the table
     *  @param columnList contains the row to be written
     */
    public void writeGenericRow(RowType rowType, TableMetaData tbMetaData, ArrayList/*<1.5*/<TableColumn>/*1.5>*/ columnList) {
        switch (rowType) {
            case DATA:
                break;
        } // switch rowType
    } // writeGenericRow

} // GenerateSQLJ
