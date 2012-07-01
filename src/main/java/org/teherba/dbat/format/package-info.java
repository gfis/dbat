/** Provides the Java classes for all output formats (modes) which can be generated by Dbat.
 *	All classes in this package inherit common methods and properties from 
 *	{@link org.teherba.dbat.format.BaseTable}. The selection of a specific format is
 *	done in {@link org.teherba.dbat.format.TableFactory}.
 *	<p />
 *  There are several groups of formats:
 *  <ul>
 *  <li>Simple, plain text formats: 
 *  {@link org.teherba.dbat.format.FixedWidthTable},
 *  {@link org.teherba.dbat.format.SeparatedTable},
 *  {@link org.teherba.dbat.format.TayloredTable}
 *  </li>
 *  <li>XML and markup oriented formats: 
 *  {@link org.teherba.dbat.format.HTMLTable},
 *  {@link org.teherba.dbat.format.XMLTable},
 *  {@link org.teherba.dbat.format.ExcelTable},
 *  {@link org.teherba.dbat.format.WikiTable},
 *  <ul>
 *  <li>XML Formats which can be further processed by XSLT:
 *  {@link org.teherba.dbat.format.TableGenerator},
 *  {@link org.teherba.dbat.format.TransformedTable}
 *  </li>
*   </ul>
 *  </li>
 *  <li>SQL oriented formats: 
 *  {@link org.teherba.dbat.format.SQLTable},
 *  {@link org.teherba.dbat.format.JDBCTable},
 *  {@link org.teherba.dbat.format.SQLUpdateTable}
 *  </li>
 *  <li>Pseudo formats which do not show the results of a query: 
 *  {@link org.teherba.dbat.format.DefaultSpecTable},
 *  {@link org.teherba.dbat.format.EchoSQL},
 *  {@link org.teherba.dbat.format.GenerateSQLJ},
 *  {@link org.teherba.dbat.format.ProbeSQL}
 *  </li>
 *  </ul>
 */
package org.teherba.dbat.format;
// @(#) $Id$
