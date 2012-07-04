/** Contains Java classes for auxiliary pages in a Dbat web application.
 *  Previously, these were realized by Java Server Pages (JSPs), but
 *  JSPs were banned from Dbat in February 2012 because:
 *  <ul>
 *  <li>they need a special precompilation step which depends on the target environment, e.g. TomCat or Websphere,</li>
 *  <li>they need 5 Tomcat libraries when the .war file is to be included in Winstone, even though they are precompiled,</li>
 *  <li>they do not properly decode URLs which contain encoded UTF-8 characters.</li>
 *  </ul>
 */
package org.teherba.dbat.view;
// @(#) $Id$
