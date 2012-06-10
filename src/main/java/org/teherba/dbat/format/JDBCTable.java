/*  Generator for a JDBC table 
    @(#) $Id$
    2007-01-12: copied from BaseTable
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
import  org.teherba.dbat.format.SQLTable;

/** Generator for a JDBC table (INSERT statements for all rows).
 *  The output is the same as for SQL, except that dates, times and
 *  timestamps are written as JDBC escape sequences "{d", "{t" and "{ts" respectively. 
 *	These features depend on <em>isJDBC</em> and are implemented in {@link SQLTable}.
 *  @author Dr. Georg Fischer
 */
public class JDBCTable extends SQLTable {
    public final static String CVSID = "@(#) $Id$";

    /** No-args Constructor
     */
    public JDBCTable() {
        super("jdbc");
		setDescription("en", "SQL with JDBC escapes");
		setDescription("de", "SQL mit JDBC-Escapes");
        isJDBC = true;
    } // Constructor
    
} // JDBCTable
