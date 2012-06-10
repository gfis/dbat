/*  Placeholder - bean with properties of an abstract parameter of a CallableStatement 
    @(#) $Id$
    2012-05-07, Georg Fischer: copied from TableColumn
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

/** Bean with properties of a parameter (marker, question mark) 
 *	to be replaced in a callable or prepared statement.
 *  @author Dr. Georg Fischer
 */
public class Placeholder { 
    public final static String CVSID = "@(#) $Id$";
   
    /** No-argument constructor 
     */
    public Placeholder() {
    	this("", "");
    } // constructor()
     
	/** Construct from direction, type and value
     *	@param dir direction indicator: 'i', 'o' or 'b'
     *	@param typeName SQL type name like 'VARCHAR', 'DECIMAL' and so on
     *	@param value string representation of the value
     */
    public Placeholder(char dir, String typeName, String value) {
    	setDir		(dir);
        setTypeName	(typeName);
        setValue	(value);
    } // constructor(3)
     
	/** Construct from type and value
     *	@param typeName SQL type name like 'VARCHAR', 'DECIMAL' and so on
     *	@param value string representation of the value
     */
    public Placeholder(String typeName, String value) {
    	this('i', typeName, value);
    } // constructor(2)
     
    //=================================
    // Properties, setters and getters
    //=================================
    
    /** Direction for parameters of Stored Procedures: "in" ('i'), "out" ('o') or "inout" ('b') */
    private char dir;
    /** Gets the direction for parameters of Stored Procedures
     *  @return one of "in" ('i'), "out" ('o') or "inout" ('b')
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
    		case 'i': result = "in";	break;
    		case 'o': result = "out";	break;
    		case 'b': result = "inout";	break;
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
        }
    } // setDirection
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

} // Placeholder
