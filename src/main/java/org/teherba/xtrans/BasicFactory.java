/*  Selects the applicable transformer - only for XML
    @(#) $Id$
    2011-04-05, Georg Fischer: derived from XtransFactory
*/
/*
 * Copyright 2011 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  org.teherba.xtrans.BaseTransformer;
import  org.teherba.xtrans.pseudo.LevelFilter;
import  org.teherba.xtrans.XMLTransformer;
import  org.apache.log4j.Logger;

/** Selects a specific transformer, and iterates over the descriptions
 *  of all transformers and their codes. The <em>main</em> method of this
 *  class generates package description files in all subdirectories
 *  for the javadoc API documentation.
 *  @author Dr. Georg Fischer
 */
public class BasicFactory extends XtransFactory{ 
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;
    
    public BasicFactory() {
        log = Logger.getLogger(BasicFactory.class.getName());
        allTransformers = new BaseTransformer[] { null // since this allows for "," on next source line
                // the order here defines the order in documentation.jsp,
                // should be: "... group by package order by package, name"
                , new LevelFilter               ()
                , new XMLTransformer            () // serializer for XML
                }; 
    } // Constructor

} // BasicFactory
