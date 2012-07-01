/** Provides the basic classes for Dbat applications.
 *  <ol>
 *  <li>Entry points for the different Dbat activation patterns
 *      <ul>
 *      <li>the commandline class {@link org.teherba.dbat.Dbat},</li>
 *      <li>the web applications {@link org.teherba.dbat.DbatServlet},</li>
 *      <li>the SOAP service provider {@link org.teherba.dbat.DbatService},</li>
 *      </ul>
 *  </li>
 *  <li>"Work-horse" classes 
 *      <ul>
 *      <li>the SAX driver for XML specification files - {@link org.teherba.dbat.SpecificationHandler},</li>
 *      <li>the interface to the Relational Database System - {@link org.teherba.dbat.SQLAction},</li>
 *      </ul>
 *  </li>
 *  <li>Beans for 
 *      <ul>
 *      <li>configuration data and database connections - {@link org.teherba.dbat.Configuration},</li>
 *      <li>properties of a result table - {@link org.teherba.dbat.TableMetaData},</li>
 *      <li>properties of individual columns - {@link org.teherba.dbat.TableColumn}</li>
 *      <li>properties of parameter markers in prepared statements - 
 *          {@link org.teherba.dbat.Placeholder} and {@link org.teherba.dbat.PlaceholderList}</li>
 *      </ul>
 *  </li>
 *  <li>Several helper classes
 *  </li>
 *  </ol>
 */
package org.teherba.dbat;
// @(#) $Id$
