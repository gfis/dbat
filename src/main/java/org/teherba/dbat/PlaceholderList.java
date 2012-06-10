/*  PlaceholderList - array list of {@link Placholder}s and associated methods
    @(#) $Id$
    2012-05-07, Georg Fischer: copied from TableMetaData
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
package org.teherba.dbat;
import  org.teherba.dbat.Placeholder;
import  java.math.BigDecimal;
import  java.sql.CallableStatement;
import  java.sql.PreparedStatement;
import  java.sql.Types;
import  java.util.ArrayList;
import  org.apache.log4j.Logger;

/** Bean for an array list of {@link Placeholder}s to be replaced
 *	for the question marks in a prepared or callable statement template.
 *  @author Dr. Georg Fischer
 */
public class PlaceholderList { 
    public final static String CVSID = "@(#) $Id$";
    
    /** log4j logger (category) */
    protected Logger log;
    
	//======================================
	// Bean properties, getters and setters
	//======================================
    /** Base array for placeholders */
    public ArrayList/*<1.5*/<Placeholder>/*1.5>*/ placeholderList;

	//=============================
	// Construction
	//=============================

    /** No-args Constructor
     */
    public PlaceholderList() {
        log = Logger.getLogger(PlaceholderList.class.getName());
        placeholderList = new ArrayList/*<1.5*/<Placeholder>/*1.5>*/(16); // empty so far
	} // no-args Constructor

    /** Adds a new placeholder to the list
     *	@param dir direction indicator: 'i', 'o' or 'b'
     *  @param typeName SQL type of the placeholder
     *	@param value value of the column
     */
    public Placeholder add(char dir, String typeName, String value) {
    	Placeholder placeholder = new Placeholder(dir, typeName, value);
    	placeholderList.add(placeholder);
    	return placeholder;
    } // add(2)
    
    /** Adds a new (input) placeholder to the list
     *  @param typeName SQL type of the placeholder
     *	@param value value of the column
     */
    public Placeholder add(String typeName, String value) {
    	return this.add('i', typeName, value);
    } // add(2)
    
	//========================
	// Auxilliary methods
	//========================
    
    /** Remove all columns - not used
     */
    private void clear() {
		try {
			placeholderList.clear();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // clear

    /** Gets the number of filled placeholder elements
     *  @return size of {@link #placeholderList}, 0, 1, 2 ...
     */
    public int size() {
        return placeholderList.size();
    } // size
	
    /** Fills the placeholder properties from an array of commandline arguments 
     *  @param options commandline arguments of the form 
     *	<pre>
     *	"-" dir [":" type [value]] 
     *	</pre>
     */
    public void fillOptions(String[] options) {
    	int iopt = 0;
    	while (iopt < options.length) {
			String opt = options[iopt ++];
	    	if (opt.startsWith("-")) {
	    		opt = opt.substring(1);
		    	String[] dirType = opt.split(":");
				this.add( dirType[0].charAt(0)
						, (dirType.length > 1) ? dirType[1].toUpperCase() 	: "VARCHAR"
						, (dirType[0].startsWith("i")) ? options[iopt ++]	: ""
						);
	    	} else { // syntax error - ignore
	    		iopt ++; 
	    	}
	    } // while iopt
    } // fillOptions

    /** Fills the markers in a callable (or prepared) statement 
     *	with the values from <em>this</em> list.
     *  @param pstmt prepared or even callable statement
     */
    public void fillStatement(PreparedStatement pstmt) {
    	boolean isCallable = pstmt instanceof CallableStatement;
		CallableStatement cstmt = null;
		if (isCallable) {
			cstmt = (CallableStatement) pstmt;
		}
        try {
        	int iph = 0;
        	while (iph < placeholderList.size()) {                                   
        		char direction 	= placeholderList.get(iph).getDir();
        		String typeName = placeholderList.get(iph).getTypeName();
        		String value	= placeholderList.get(iph).getValue();
	        	if (direction != 'o') { // -in or -inout			
		        	if (false) {
					} else if (typeName.equals    ("CLOB"      )) {
						// not yet
					} else if (typeName.equals    ("DATE"      )) {
						pstmt.setDate(iph, java.sql.Date.valueOf(value));
					} else if (typeName.equals    ("DECIMAL"   )) {
						pstmt.setBigDecimal(iph, new BigDecimal(value));
					} else if (typeName.startsWith("INT"       )) {
						int ivalue = 0;
						try {
							ivalue = Integer.parseInt(value);
						} catch(Exception exc) { // ignore
						}
						pstmt.setInt(iph, ivalue);
					} else if (typeName.equals    ("TIME"      )) {
						pstmt.setTime(iph, java.sql.Time.valueOf(value));
					} else if (typeName.equals    ("TIMESTAMP" )) {
						value = value
								.replaceAll("T", " ")
								.replaceAll("\\'", "");
						pstmt.setTimestamp(iph, java.sql.Timestamp.valueOf(value));
					} else { // STRING, CHAR etc.
						typeName = 				   "VARCHAR";
						pstmt.setString(iph, value);
					}
	       		} // in
	       		if (direction != 'i' && isCallable) { // -inout or -out
					if (false) {
					} else if (typeName.equals    ("CHAR"      )) {
						cstmt.registerOutParameter(iph, Types.CHAR      );
					} else if (typeName.equals    ("CLOB"      )) {
						cstmt.registerOutParameter(iph, Types.CLOB      );
					} else if (typeName.equals    ("DATE"      )) {
						cstmt.registerOutParameter(iph, Types.DATE      );
					} else if (typeName.equals    ("DECIMAL"   )) {
						cstmt.registerOutParameter(iph, Types.DECIMAL   );
					} else if (typeName.startsWith("INT"       )) {
						cstmt.registerOutParameter(iph, Types.INTEGER   );
					} else if (typeName.equals    ("TIME"      )) {
						cstmt.registerOutParameter(iph, Types.TIME      );
					} else if (typeName.equals    ("TIMESTAMP" )) {
						cstmt.registerOutParameter(iph, Types.TIMESTAMP );
					} else { // STRING, CHAR etc.
						typeName 				 = "VARCHAR";
						cstmt.registerOutParameter(iph, Types.VARCHAR   );
					}
				} // out      
				iph ++;
			} // while iph
	    } catch (Exception exc) {
  	        log.error(exc.getMessage(), exc);
        } // catch
    } // fillStatement
    
} // PlaceholderList
