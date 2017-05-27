/*  Avoid loading of external DTDs
    @(#) $Id$
    2016-10-13, Georg Fischer: copied from
    https://stuartsierra.com/2008/05/08/stop-your-java-sax-parser-from-downloading-dtds
 */
/*
 * Copyright 2016 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  java.io.IOException;
import  java.io.StringReader;
import  org.xml.sax.EntityResolver;
import  org.xml.sax.InputSource;
import  org.xml.sax.SAXException;

/** A very tiny resolver which returns the empty String
 *  for any entity. It may not return null.
 *  @author Dr. Georg Fischer
 */
public class DummyEntityResolver implements EntityResolver {
    public final static String CVSID = "@(#) $Id$";
    
    /** Allows applications to map references to external entities into input sources,
     *  or tell the parser it should use conventional URI resolution.
     *  This method is only called for external entities which have been properly declared.
     *  @param publicId The public identifier of the external entity being referenced
     *  (normalized as required by the XML specification), or null if none was supplied.
     *  @param systemId The system identifier of the external entity being referenced;
     *  either a relative or absolute URI. This is never null when invoked by a SAX2 parser;
     *  only declared entities, and any external subset, are resolved by such parsers.
     *  @return An InputSource object describing the new input source to be used by the parser.
     *  Returning null directs the parser to resolve the system ID against the base URI and open a connection to resulting URI.
     *  @throws SAXException Any SAX exception, possibly wrapping another exception.
     *  @throws IOException Probably indicating a failure to create a new InputStream or Reader,
     *  or an illegal URL.
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        return new InputSource(new StringReader(""));
    } // resolveEntity

} // DummyEntityResolver
