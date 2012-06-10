/*  Properties of fields in records
 *  @(#) $Id$
 *  2006-09-20, Dr. Georg Fischer
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

package org.teherba.xtrans;

/** Definition of fields in data records, with a fixed position and width.
 *  These fields are used in 
 *  {@link org.teherba.xtrans.BaseRecord BaseRecord} and its derived classes
 *  {@link org.teherba.xtrans.ByteRecord ByteRecord} and
 *  {@link org.teherba.xtrans.CharRecord CharRecord}. 
 *  Start position and field length (width) is always counted
 *  either in byte or character unit, starting at 0, and extending
 *  up to the size of the undelying record buffer.
 *  @author Dr. Georg Fischer
 */
public class Field  { 
    public final static String CVSID = "@(#) $Id$";

    /** Position (offset) of the first byte / character of this field
     *  in the underlying record buffer. Field positions start at 0.
     */
    public int start;
    
    /** Length (width) of field in byte or character units
     */
    public int len;
    
    /** Optional designation of the field
     */
    protected String name;
    
    /** Defines a field 
     *  with an empty name , offset 0 and length 1
     */
    public Field() {
        this.start  = 0;
        this.len    = 1;
        this.name   = "";
    } // Constructor 0
    
    /** Defines a field 
     *  with an empty name and length 1
     *  @param start position of the first byte of the field
     */
    public Field(int start) {
        this.start  = start;
        this.len    = 1;
        this.name   = "";
    } // Constructor 1
    
    /** Defines a field 
     *  with an empty name
     *  @param start position of the first byte of the field
     *  @param len length of the field 
     */
    public Field(int start, int len) {
        this.start  = start;
        this.len    = len;
        this.name   = "";
    } // Constructor 2
    
    /** Defines a field 
     *  with all properties
     *  @param start position of the first byte of the field
     *  @param len length of the field 
     *  @param name name of the field
     */
    public Field(int start, int len, String name) {
        this.start = start;
        this.len   = len;
        this.name  = name;
    } // Constructor 3
    
} // Field
