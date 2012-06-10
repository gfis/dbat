/*  Generator for a default specification XML file 
    @(#) $Id$
	2010-05-21: with HTML namespace prefix
    2010-02-25: charWriter.write -> .print
 	2010-02-23: copied from SQLTable
*/
/*
 * Copyright 2010 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.dbat.Configuration; 		// for DBAT_URI, HTML_URI
import  org.teherba.dbat.SpecificationHandler; 	// for ROOT_TAG
import  org.teherba.dbat.TableColumn;
import  org.teherba.dbat.format.XMLTable;
import  java.sql.DatabaseMetaData;
import  java.sql.Types;
import  java.util.ArrayList;
import  java.util.HashMap;

/** Generates a default XML specification file 
 *	which describes a hypothetic "SELECT *" for the table, and which can be fed into {@link SpecificationHandler}.
 *	This class can only be used for the descripription of a table, not for the
 *	representation of a result set. The output is a starting point for
 *	an application specific modification of a query.
 *  @author Dr. Georg Fischer
 */
public class DefaultSpecTable extends XMLTable {
    public final static String CVSID = "@(#) $Id$";

    /** local copy of the table's schema */
    private String schema;
    /** local copy of the table's name */
    private String tableName;
    
    /** No-args Constructor
     */
    public DefaultSpecTable() {
        super();
        setFormatCodes("spec");
		setDescription("en", "Default Spec. File");
		setDescription("de", "Default-Spezifikation");
    } // Constructor

	/** XML Namespace prefix for HTML tags */
	private static final String HTML_PREFIX = "ht";
	
    /** Starts a file that may contain several table descriptions and/or a SELECT result sets
     *	@param params array of 0 or more (name, value) strings which specify features in the file header.
     *	@param parameterMap map of request parameters to values
     *	The following names are interpreted:
     *	<ul>
     *	<li>encoding - encoding to be used for the output stream</li>
     *	</ul>
     */
    public void writeStart(String[] params,  HashMap/*<1.5*/<String, String[]>/*1.5>*/ parameterMap) {
        try {
			String encoding = getTargetEncoding();
			int iparam = params.length;
			while (iparam > 0) {
				iparam -= 2;
				if (false) {
				} else if (params[iparam].equals("encoding")) {
					encoding = params[iparam + 1];
				}
			} // while iparam
            charWriter.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\" ?>");
            charWriter.println("<" + SpecificationHandler.ROOT_TAG + " encoding=\"" + encoding + "\"\n" 
					+ "\txmlns=\"" + Configuration.DBAT_URI + "\"\n"
					+ "\txmlns:" + HTML_PREFIX + "=\"" + Configuration.HTML_URI + "\"\n"
            		+ "\t>");
            xmlDeclared = true;
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // writeStart

    /** Remembers the table (and its schema).
     *  @param dbMetaData database metadata 
     *  as obtained with <code>con.getMetaData()</code>
     *  @param schema    name of the table's schema, or null if none
     *  @param tableName name of the table
     *  @param tableType type of the table: "TABLE", "VIEW", etc.
     */
    public void startDescription(DatabaseMetaData dbMetaData, String schema, String tableName, String tableType) {
        try {
        	this.schema 	= schema;
        	this.tableName 	= tableName;
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // startDescription

    /** Ends the description of a SELECT statement.
     */
    public void endDescription() {
        try {
			charWriter.println("\t</select>");
			charWriter.println("\t<counter />");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // endDescription

    /** Returns the definition string for a table column in XML format.
     *  @param colNo number of the column: 0, 1, 2
     *  @param column bean with the properties of the column
     */
    public void describeColumn(int colNo, TableColumn column) {
        StringBuffer buffer = new StringBuffer(128);
        try {
        	if (colNo == 0) {
				charWriter.println("<!--");
				charWriter.println("\t@(#) $Id$");
				charWriter.println("-->");
				charWriter.print("\t<" + HTML_PREFIX + ":h2>");
	            if (schema != null && ! schema.equals("")) {
	   		        charWriter.print(schema + ".");
	       		}
	            charWriter.println(tableName + "</" + HTML_PREFIX + ":h2>");
	            charWriter.println("\t<" + HTML_PREFIX + ":form method=\"get\">");
				charWriter.println("\t\t<" + HTML_PREFIX + ":input label=\"Prefix\" name=\"prefix\" maxsize=\"20\" size=\"10\" />");
	        	charWriter.println("\t\t<" + HTML_PREFIX + ":input type=\"submit\" value=\"Submit\" />");
	            charWriter.println("\t</" + HTML_PREFIX + ":form>");
	            charWriter.println("\t<select>");
        	}
            buffer.append("\t\t<col label=\"");
            buffer.append(column.getName());
            int    dataType = column.getDataType();
            if (false) {
            } else if (dataType == Types.CHAR	) {
            } else if (dataType == Types.VARCHAR) {
            } else if (dataType == Types.DECIMAL || dataType == Types.INTEGER) {
                buffer.append("\" align=\"right");
            }
            buffer.append("\">a.");
            buffer.append(column.getName());
            buffer.append("</col>");
            String label = column.getLabel();
            if (label != null && label.length() > 0) {
                buffer.append(" -- ");
                buffer.append(label);
            }
            charWriter.println(buffer.toString());
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
     *	@param primary whether to describe the primary key
     *  @param keyName name of the key ("" for primary)
     *  @param columnNames array of key column names, ordered by key sequence
     */
    public void describeKey(String tableName, boolean primary, String keyName, ArrayList/*<1.5*/<String>/*1.5>*/ columnNames) {
        try {
			if (primary) { // is called even if there the table has no primary key
	            charWriter.println("\t<!--");
        	    charWriter.println("\t\t<where>");
    	        charWriter.println("\t\t\t" + (columnNames.size() > 0 ? (String) columnNames.get(0) : "\'a\'")
            			+ " like \'<parm name=\"prefix\" />%\'");
	            charWriter.println("\t\t</where>");
    	        charWriter.println("\t-->");
        	    charWriter.print("\t\t<from>");
            	charWriter.println(tableName + " a"	+ "</from>");
	            if (columnNames.size() > 0) {
	                charWriter.print("\t\t<order by=\"");
    	            for (int ikey = 0; ikey < columnNames.size(); ikey ++) {
        	            charWriter.print(ikey <= 0 ? "" : ",");
            	        charWriter.print((String) columnNames.get(ikey));
	                } // for ikey
    	            charWriter.println("\" />");
	            } // with column names
			} // primary
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // describeKey

    /** Writes the description of the table's primary key.
     *  Here, we make use of the fact that <em>describePrimaryKey</em> 
     *	is always called at the end of  the column descriptions.
     *	A comment with a WHERE clause is inserted, and the FROM and
     *	ORDER by clauses are appended with their XML elements.
     *  @param tableName fully qualified name of the table
     *  @param pkCount number of primary key columns
     *  @param primaryKeyFields array of primary key column names, ordered by key sequence
     */
    public void describePrimaryKey(String tableName, int pkCount, String[] primaryKeyFields) {
        try {
            charWriter.println("\t<!--");
            charWriter.println("\t\t<where>");
            charWriter.println("\t\t\t" + (pkCount > 0 ? primaryKeyFields[1] : "\'a\'")
            		+ " like \'<parm name=\"prefix\" />%\'");
            charWriter.println("\t\t</where>");
            charWriter.println("\t-->");
            charWriter.print("\t\t<from>");
            charWriter.println(tableName + " a"	+ "</from>");
            if (pkCount > 0) {
                charWriter.print("\t\t<order by=\"");
                for (int ipk = 1; ipk <= pkCount; ipk ++) {
                    charWriter.print(ipk <= 1 ? "a." : ", a.");
                    charWriter.print(primaryKeyFields[ipk]);
                } // for ipk
                charWriter.println("\" />"); // </order>
            } // with primay key
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // describePrimaryKey

} // DefaultSpecTable
